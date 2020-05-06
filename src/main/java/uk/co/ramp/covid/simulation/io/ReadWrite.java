/**********************************
 *
 * Class for writing outputs as csv files 
 *
 ********************************/
package uk.co.ramp.covid.simulation.io;

// Test update

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;


public class ReadWrite {
    public static String newline = System.getProperty("line.separator");
    public FileWriter fw;
    public BufferedWriter bw;

    public ReadWrite(String outFile) throws Exception {
        File f = new File(outFile);

        f.getParentFile().mkdirs();
        f.createNewFile();

        this.fw = new FileWriter(outFile);
        this.bw = new BufferedWriter(this.fw);
    }

    public void openWritemodel() throws Exception {

        this.bw.write("iter,day,H,L,A,P1,P2,D,R" + newline);
        this.bw.flush();
    }

    public void writemodel(int iter, String outS) throws Exception {
        this.bw.write(iter + "," + outS + newline);
        this.bw.flush();
    }


}