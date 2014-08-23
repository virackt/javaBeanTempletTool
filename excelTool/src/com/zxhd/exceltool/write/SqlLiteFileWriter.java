package com.zxhd.exceltool.write;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SqlLiteFileWriter {
	
	private BufferedWriter bw;
	
	public SqlLiteFileWriter(String fileName) {
		File f = new File(fileName);
		if(!f.exists()){
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			bw = new BufferedWriter(new FileWriter(f));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



	public void writeToFile(String value){
		try {
			bw.append(value);
			bw.newLine();
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void closeWriter(){
		if(bw != null){
			try {
				bw.flush();
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally{
				bw = null;
			}
		}
	}

}
