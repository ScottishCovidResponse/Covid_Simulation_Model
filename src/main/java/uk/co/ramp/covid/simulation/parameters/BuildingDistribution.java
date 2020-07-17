package uk.co.ramp.covid.simulation.parameters;

/** Defines the number of types of building
 *
 *  Parameters such as populationToHospitalsRatio imply 1 hospital per populationToHospitalsRatio people
 *  Place size distributions give probability that a place is Small, Medium, or Large, where size
 *  controls the expected number of people that work there
 **/
public class BuildingDistribution {
    public Integer populationToHospitalsRatio = null;
    /** Every covidHospitalRatio hosptials (including the first) is a designated Covid hospital */
    public Integer covidHospitalRatio = null;
    public PlaceSizeDistribution hospitalSizeDistribution = null;

    public Integer populationToSchoolsRatio = null;
    public PlaceSizeDistribution schoolSizeDistribution = null;

    public Integer populationToShopsRatio = null;
    public PlaceSizeDistribution shopSizeDistribution = null;

    public Integer populationToOfficesRatio = null;
    public PlaceSizeDistribution officeSizeDistribution = null;

    public Integer populationToConstructionSitesRatio = null;
    public PlaceSizeDistribution constructionSiteSizeDistribution = null;

    public Integer populationToNurseriesRatio = null;
    public PlaceSizeDistribution nurserySizeDistribution = null;

    public Integer populationToRestaurantsRatio = null;
    public PlaceSizeDistribution restaurantSizeDistribution = null;

    public Integer populationToCareHomesRatio = null;
    public PlaceSizeDistribution careHomeSizeDistribution = null;

    public boolean isValid() {
        return populationToHospitalsRatio > 0
                && populationToConstructionSitesRatio > 0
                && populationToNurseriesRatio > 0
                && populationToOfficesRatio > 0
                && populationToRestaurantsRatio > 0
                && populationToSchoolsRatio > 0
                && populationToShopsRatio > 0
                && populationToCareHomesRatio > 0
                && hospitalSizeDistribution.isValid()
                && schoolSizeDistribution.isValid()
                && shopSizeDistribution.isValid()
                && officeSizeDistribution.isValid()
                && constructionSiteSizeDistribution.isValid()
                && nurserySizeDistribution.isValid()
                && careHomeSizeDistribution.isValid()
                && restaurantSizeDistribution.isValid();
    }
}
