# Running On DiRAC

Follow these instructions to get an account (for the SCRC Project):

    https://github.com/ScottishCovidResponse/SCRCIssueTracking/wiki/DiRAC-Access

You should receive an email describing how to connect to one of the CSD3 login nodes,
with something like (replacing your-user-name with your username):

    ssh -l your-user-name login.hpc.cam.ac.uk

Load Java:

    module load jdk-8u141-b15-intel-17.0.4-3g6rhr2

John has downloaded gradle from https://gradle.org/release-nightly/ and unzipped it to `~/rds/rds-dirac-dc003/dc-nonw1/gradle`.  To use it (replacing [your-user-name]):

    export PATH=$PATH:/home/[your-user-name]/rds/rds-dirac-dc003/dc-nonw1/gradle/bin

Get the source code from GitHub, and build it with:

    git clone https://github.com/ScottishCovidResponse/Covid_Simulation_Model.git
    cd Covid_Simulation_Model
    gradle install

(Or use `git pull` instead of `clone` if you have already cloned the repository.)

Then have a look at the "TODO"s in the example `slurm_submit` file, which can be run with the command:

    sbatch slurm_submit

You can check the status of your job using:

    squeue -n covid-sim-model

And combine the output from multiple jobs into a single csv file using (replacing jobID, lastArrayIndex and outputFileName with the relevant values):

    combineArrayJobOutputs.sh jobID lastArrayIndex outputFileName
    
(After leaving your ssh session,) copy files back to your local machine by running scp from your local machine, e.g.:

    scp [your-user-name]@login.hpc.cam.ac.uk:/home/[your-user-name]/out.csv .
