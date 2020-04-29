/**********************************
 * 
 * Class for writing outputs as csv files 
 * 
 ********************************/
package development_v1;

import java.io.*;
import java.util.*;


public class ReadWrite {
	public FileWriter fw;
	public BufferedWriter bw;

	public ReadWrite(String outFile) throws Exception{
		this.fw = new FileWriter(outFile);
		this.bw = new BufferedWriter(this.fw);
	}
	public static String newline = System.getProperty("line.separator");


	public void openWritemodel() throws Exception {
		
		this.bw.write("iter,day,H,L,A,P1,P2,D,R" + newline);
		this.bw.flush();
	}
	
	public void writemodel(int iter, String outS) throws Exception {
		this.bw.write(iter + ","+ outS);
		this.bw.flush();
	}


}