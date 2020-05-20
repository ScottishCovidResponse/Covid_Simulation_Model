# Running On DiRAC

Load Java:

    module load jdk-8u141-b15-intel-17.0.4-3g6rhr2

John has downloaded gradle from https://gradle.org/release-nightly/ and unzipped it to `/home/dc-nonw1/gradle`.  To use it:

    export PATH=$PATH:/home/dc-nonw1/gradle/bin

Get the source code from GitHub, and build it with:

    gradle install

Then have a look at the example `slurm_submit` file, which can be run with the command:

    sbatch slurm_submit
