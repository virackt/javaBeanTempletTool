package com.zxhd.exceltool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.mysql.jdbc.Connection;
import com.zxhd.exceltool.model.BeanModel;
import com.zxhd.exceltool.reader.ExcelReader;
import com.zxhd.exceltool.sql.SQLConnector;
import com.zxhd.exceltool.sql.SqlLiteConnector;
import com.zxhd.exceltool.util.AppConfig;
import com.zxhd.exceltool.write.SqlLiteFileWriter;

public class InitTool {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AppConfig.init();
		String filePath = AppConfig.getString("filePath");
		String outputPath = AppConfig.getString("outputPath");
		String mysqlFilePath = AppConfig.getString("mysqlFilePath");
		File file = new File(filePath);
		if (file.isFile()) {
			List<String> mySqlList = new ArrayList<String>();
			List<String> sqlLiteList = new ArrayList<String>();
			BeanModel model = ExcelReader.readExcel("", filePath, mySqlList,
					sqlLiteList);
			if(model == null){
				return;
			}
			if (mySqlList.size() > 0) {
				SQLConnector sqlc = new SQLConnector();
				sqlc.sqlOperation(sqlc.getConn(), mySqlList);
				SqlLiteFileWriter writer = new SqlLiteFileWriter(mysqlFilePath);
				for (String value : mySqlList) {
					if (value.contains("DROP") || value.contains("drop")) {
						System.out.println("开始生成表【" + value + "】的mysql文件");
					}
					writer.writeToFile(value);
				}
				writer.closeWriter();
			}
			if (sqlLiteList.size() > 0) {
				try {
					SqlLiteConnector sqlite = new SqlLiteConnector();
					sqlite.getConn();
					sqlite.sqlOperation(sqlite.getConn(), sqlLiteList);
				} catch (Exception e) {
					e.printStackTrace();
				}
				SqlLiteFileWriter sfWriter = new SqlLiteFileWriter(outputPath);
				for (String value : sqlLiteList) {
					if (value.contains("DROP") || value.contains("drop")) {
						System.out.println("开始生成表【"
								+ value.substring(value.indexOf("main") + 2)
								+ "】的sqllite文件");
					}
					sfWriter.writeToFile(value);
				}
				sfWriter.closeWriter();
			}
			ExcelReader.velocityTemplet(model, "D:\\doc2\\");
		} else {
			String[] fileNames = file.list();
			SqlLiteFileWriter sfWriter = new SqlLiteFileWriter(outputPath);
			SqlLiteFileWriter writer = new SqlLiteFileWriter(mysqlFilePath);
			List<String> mySqlList = new ArrayList<String>();
			List<String> sqlLiteList = new ArrayList<String>();
			for (String fileName : fileNames) {
				mySqlList.clear();
				sqlLiteList.clear();
				BeanModel model = ExcelReader.readExcel(filePath, fileName,
						mySqlList, sqlLiteList);
				if(model == null){
					continue;
				}
				ExcelReader.velocityTemplet(model, "D:\\doc2\\");
				if (mySqlList.size() > 0) {
//					SQLConnector sqlc = new SQLConnector();
//					Connection conn = sqlc.getConn();
//					sqlc.sqlOperation(conn, mySqlList);
					for (String value : mySqlList) {
						if (value.contains("DROP") || value.contains("drop")) {
							System.out.println("开始生成表【"
									+ value.substring(value.indexOf('`') + 1,
											value.length() - 2) + "】的mysql文件");
						}
						writer.writeToFile(value);
					}
				}
				if (sqlLiteList.size() > 0) {
					try {
//						SqlLiteConnector sqlite = new SqlLiteConnector();
//						java.sql.Connection conn = sqlite.getConn();
//						sqlite.sqlOperation(conn, sqlLiteList);
					} catch (Exception e) {
						e.printStackTrace();
					}
					for (String value : sqlLiteList) {
						if (value.contains("DROP") || value.contains("drop")) {
							System.out.println("开始生成sqllite文件【"
									+ value.substring(
											value.indexOf("main") + 7,
											value.length() - 2) + "】");
						}
						sfWriter.writeToFile(value);
					}
					System.out.println("结束生成sqllite文件");
				}
			}
			writer.closeWriter();
			sfWriter.closeWriter();
		}
		System.out.println("-----------------所有操作执行完毕！！！");
	}

}
