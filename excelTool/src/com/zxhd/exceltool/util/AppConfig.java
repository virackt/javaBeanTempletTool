package com.zxhd.exceltool.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {
	private static Properties p;
	private static final String FILE_NAME="conf.properties";
	

	public static void init() {
		p = new Properties();
		InputStream is;
		try {
			is = new FileInputStream(new File(FILE_NAME));
		} catch (FileNotFoundException e) {
			is = AppConfig.class.getResourceAsStream(FILE_NAME);
		}
		if(is == null){
			try {
				is = new FileInputStream("./" + FILE_NAME);
			} catch (FileNotFoundException e) {
				is = AppConfig.class.getResourceAsStream("./" + FILE_NAME);
			}
		}
		if(is == null){
			try {
				is = new FileInputStream("/" + FILE_NAME);
			} catch (FileNotFoundException e) {
				is = AppConfig.class.getResourceAsStream("/" + FILE_NAME);
			}
		}
		try {
			p.load(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String getString(String key){
		return p.getProperty(key);
	}
	
	public static String getStringWithDefaultValue(String key, String defaultValue){
		return p.getProperty(key, defaultValue);
	}
	
	public static int getIntValue(String key){
		String value = getString(key);
		return Integer.parseInt(value);
	}

}
