# Running On DiRAC

Follow these instructinos to get an account (for the SCRC Project):

    https://github.com/ScottishCovidResponse/SCRCIssueTracking/wiki/DiRAC-Access

You should receive an email describing how to connect to one of the CSD3 login nodes,
with something like (replacing your-user-name with your username):

    ssh -l your-user-name login.hpc.cam.ac.uk

Load Java:

    module load jdk-8u141-b15-intel-17.0.4-3g6rhr2

John has downloaded gradle from https://gradle.org/release-nightly/ and unzipped it to `/home/dc-nonw1/gradle`.  To use it:

    export PATH=$PATH:/home/dc-nonw1/gradle/bin

Get the source code from GitHub, and build it with:

    git clone https://github.com/ScottishCovidResponse/Covid_Simulation_Model.git
    cd Covid_Simulation_Model
    gradle install

Then have a look at the "TODO"s in the example `slurm_submit` file, which can be run with the command:

    sbatch slurm_submit

