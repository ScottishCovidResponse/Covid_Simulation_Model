package uk.co.ramp.covid.simulation;

import com.google.gson.JsonParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uk.co.ramp.covid.simulation.lockdown.*;
import uk.co.ramp.covid.simulation.output.CsvOutput;
import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.output.LogConfig;
import uk.co.ramp.covid.simulation.output.network.ContactsWriter;
import uk.co.ramp.covid.simulation.output.network.PeopleWriter;
import uk.co.ramp.covid.simulation.parameters.ParameterIO;
import uk.co.ramp.covid.simulation.population.Population;
import uk.co.ramp.covid.simulation.util.*;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/** A uk.co.ramp.covid.simulation.Model represents a particular run of the model with some given parameters
 */
public class Model {
    private static final Logger LOGGER = LogManager.getLogger(Model.class);

    private boolean outputDisabled;
    private Integer populationSize = null;
    private Integer nInitialInfections = null;
    private Integer externalInfectionDays = null;
    private Integer nDays = null;
    private Integer nIters = null;
    private Integer rngSeed = null;
    private String outputDirectory = null;
    private String networkOutputDir = null;

    // transient stops serialisation to json
    private transient Path outPath;
    
    private final List<LockdownEvent> lockdownEvents = new ArrayList<>();
    private final List<LockdownEventGenerator> lockdownGenerators = new ArrayList<>();

    public Model() {}

    // Builder style interface
    public Model setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
        return this;
    }

    public Model setnInitialInfections(int nInitialInfections) {
        this.nInitialInfections = nInitialInfections;
        return this;
    }

    public Model setExternalInfectionDays(int n) {
        this.externalInfectionDays = n;
        return this;
    }

    public Model setnDays(int nDays) {
        this.nDays = nDays;
        return this;
    }

    public Model setIters(int iters) {
        this.nIters = iters;
        return this;
    }

    public Model setNoOutput() {
        this.outputDisabled = true;
        return this;
    }

    public Model setRNGSeed(int seed) {
        this.rngSeed = seed;
        return this;
    }
    
    public Model addLockdownEvent(LockdownEvent e) {
        lockdownEvents.add(e);
        return this;
    }

    public Model addLockdownGenerator(LockdownEventGenerator e) {
        lockdownGenerators.add(e);
        return this;
    }

    public Integer getRNGSeed() {
        return rngSeed;
    }

    public Model setOutputDirectory(String fname) {
        this.outputDirectory = fname;
        return this;
    }

    public Model setNetworkOutputDir(String path) {
        this.networkOutputDir = path;
        return this;
    }

    public boolean isValid() {
        boolean valid = true;

        if (populationSize == null) {
            LOGGER.warn("Uninitialised model parameter: populationSize");
            valid = false;
        }
        if (nIters == null) {
            LOGGER.warn("Uninitialised model parameter: nIters");
            valid = false;
        }
        if (nInitialInfections == null) {
            LOGGER.warn("Uninitialised model parameter: nInitialInfections");
            valid = false;
        }
        if (externalInfectionDays == null) {
            LOGGER.warn("Uninitialised model parameter: externalInfectionDays");
            valid = false;
        }
        if (nDays == null) {
            LOGGER.warn("Uninitialised model parameter: nDays");
            valid = false;
        }
        if (!outputDisabled) {
            if (outputDirectory == null) {
                LOGGER.warn("Uninitialised model parameter: outputDirectory");
                valid = false;
            }
        }

        return valid;
    }

    /** Runs the model with the given parameters. Returns null if the parameters are missing or invalid */
    public List<List<DailyStats>> run(int simulationID) {
        if (!isValid()) {
            throw new InvalidParametersException("Invalid model parameters");
        }

        if (!outputDisabled) {
            createOutputDirectory();
            LogConfig.configureLoggerRedirects(outPath);
        }

        // We need to log this after creating a model to ensure it goes to the file
        LOGGER.info(BuildConfig.NAME + " version " + BuildConfig.VERSION);
        LOGGER.info("Git hash: " + BuildConfig.GitHash);

        optionallyGenerateRNGSeed();
        RNG.seed(rngSeed + simulationID);

        List<List<DailyStats>> stats = new ArrayList<>(nIters);
        for (int i = 0; i < nIters; i++) {
            // As households/person types are determined probabilistically in some cases it can be
            // impossible to populate all houseolds, e.g. 50 ADULT households and only 49 ADULTS.
            // He we return an empty run to indicate that the parameters etc are okay, but we used an unlucky random
            // seed (this can be accounted for when processing the output).
            Population p;
            try {
                p = PopulationGenerator.genValidPopulation(populationSize);
            } catch (CannotGeneratePopulationException e) {
                LOGGER.error(e);
                break;
            }

            ContactsWriter contactsWriter = null;
            if (networkOutputDir != null) {
                try {
                    File peopleFile = new File(networkOutputDir, "people" + i + ".csv");
                    File contactsFile = new File(networkOutputDir, "contacts" + i + ".csv");
                    PeopleWriter.writePeople(new FileWriter(peopleFile), p.getAllPeople());
                    contactsWriter = new ContactsWriter(new FileWriter(contactsFile));
                } catch (IOException e) {
                    LOGGER.error("Error starting network generation", e);
                    break;
                }
            }

            p.getSeeder().setExternalInfectionDays(externalInfectionDays);
            p.getSeeder().forceNInfections(nInitialInfections);

            for (LockdownEventGenerator gen : lockdownGenerators) {
                gen.setPopulation(p);
                p.getLockdownController().addComponent(gen);
            }

            for (LockdownEvent c : lockdownEvents) {
                c.setPopulation(p);
                p.getLockdownController().addComponent(c);
            }

            List<DailyStats> iterStats = p.simulate(nDays, contactsWriter);
            for (DailyStats s : iterStats) {
                s.determineRValues(p);
                s.determineFutureDeaths(p);
            }

            
            stats.add(iterStats);

            if (contactsWriter != null) {
                contactsWriter.close();
            }
        }

        if (!outputDisabled) {
            writeOutput(simulationID, stats);
        }

        return stats;
    }



    private void createOutputDirectory() {
        if (outputDirectory.equals("")) {
            outPath = FileSystems.getDefault().getPath(".");
        } else {
            DateTimeFormatter tsFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_hhmmss");
            String timeStamp = tsFormatter.format(LocalDateTime.now());
            outPath = FileSystems.getDefault().getPath(outputDirectory, timeStamp);
            if (!outPath.toFile().mkdirs()) {
                LOGGER.error("Could not create output directory: " + outPath);
            }
        }

    }
    
    private void writeOutput(int iterId, List<List<DailyStats>> s) {
        // By here the output directory will be available
        CsvOutput output = new CsvOutput(outPath, iterId, s);
        output.writeOutput();

        ParameterIO.writeParametersToFile(outPath.resolve("population_params.json"));
        outputModelParams(outPath.resolve("model_params.json"));
    }

    private void outputModelParams(Path outF) {
        try (Writer writer = new FileWriter(outF.toFile())) {
            ParameterIO.getGson().toJson(this, writer);
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

    // Also allows reading from json file
    public static Model readModelFromFile(String path) throws IOException, JsonParseException {
        Reader file = new FileReader(path);
        return ParameterIO.getGson().fromJson(file, Model.class);
    }

    public void optionallyGenerateRNGSeed() {
        if (rngSeed == null) {
            rngSeed = RNG.generateRandomSeed();
            LOGGER.warn("Generated RNG seed: " + rngSeed);
        }
    }
}
