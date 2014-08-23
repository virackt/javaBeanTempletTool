package com.zxhd.exceltool.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import com.zxhd.exceltool.model.BeanModel;
import com.zxhd.exceltool.model.FieldModel;

public class ExcelReader {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		readExcel("d:/config/","crossboxtagtemplet.xlsx", new ArrayList<String>(), new ArrayList<String>());
	}

	public static BeanModel readExcel(String path, String fileName, List<String> mysqlList, List<String> sqlLiteList) {
		if(!fileName.endsWith("xlsx")){
			System.out.println("非正常xlsx文件 fileName = " + fileName);
			return null;
		}
		List<String> sqlList = new ArrayList<String>();
		XSSFWorkbook hssfWB;
		InputStream is = null;
		try {
			is = new FileInputStream(new File(path + fileName));
			hssfWB = new XSSFWorkbook(is);
			XSSFSheet sheet = hssfWB.getSheetAt(0);
			Iterator<Row> it = sheet.rowIterator();
			int rowNum = 0;
			int cellNum = 0;
			String className = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.lastIndexOf(".xlsx"));
			className = className.substring(0,1).toUpperCase() + className.substring(1);
			String tableName = className.toLowerCase();
			List<String> commentList = new ArrayList<String>();
			List<String> typeList = new ArrayList<String>();
			List<String> paramNameList = new ArrayList<String>();
			List<Integer> keyList = new ArrayList<Integer>();
			while (it.hasNext()) {
				Row row = it.next();
				if (row != null) {
					StringBuffer sb = new StringBuffer();
					rowNum++;
					if (rowNum == 1) {// comment
						Iterator<Cell> cellIt = row.cellIterator();
						while (cellIt.hasNext()) {
							XSSFCell cell = (XSSFCell) cellIt.next();
							if (cell != null) {
								commentList.add(cell.getStringCellValue().trim());
							} else {
								break;
							}
						}
					} else if (rowNum == 2) {//字段名
						Iterator<Cell> cellIt = row.cellIterator();
						while (cellIt.hasNext()) {
							XSSFCell cell = (XSSFCell) cellIt.next();
							if (cell != null) {
								cellNum++;
								paramNameList.add(cell.getStringCellValue());
							} else {
								break;
							}
						}
					} else if (rowNum == 3) {// 字段类型
						for (int i = 0; i < cellNum; i++) {
							XSSFCell cell = (XSSFCell) row.getCell(i);
							if (cell == null) {
								throw new RuntimeException("字段类型为空："
										+ fileName + "第3行第" + (i + 1) + "列");
							}
							String cellValue = cell.getStringCellValue();
							if (!cellValue.contains("int")
									&& !cellValue.contains("varchar")
									&& !cellValue.contains("float")) {
								throw new RuntimeException("有特殊字段存在："
										+ cellValue);
							} else {
								typeList.add(cellValue);
							}

						}
					} else if (rowNum == 4) {
						for (int i = 0; i < cellNum; i++) {
							XSSFCell cell = (XSSFCell) row.getCell(i);
							String cellValue = cell.getStringCellValue();
							if (cellValue.equals("key")) {
								keyList.add(i);
							}
						}
					} else if (rowNum > 4) {
						sb.append("insert into ");
						sb.append(tableName);
						sb.append(" values(");
						for (int i = 0; i < cellNum; i++) {
							XSSFCell cell = (XSSFCell) row.getCell(i);
							if (typeList.get(i).contains("int")) {
								if(cell == null){
									sb.append(0);
								} else {
									try {
										sb.append((int) cell.getNumericCellValue());
									} catch (Exception e) {
										sb.append(Integer.parseInt(cell.getStringCellValue()));
									}
								}
								if (i != cellNum - 1) {
									sb.append(",");
								}
							} else if (typeList.get(i).contains("varchar")) {
								String cellValue = "";
								if(cell != null){
									try {
										cellValue = cell.getStringCellValue();
									} catch (Exception e) {
										cellValue = String.valueOf((int) cell
												.getNumericCellValue());
									}
								}
								if (StringUtils.isEmpty(cellValue)) {
									sb.append("'" + "'");
								} else {
									sb.append("'");
									sb.append(cellValue);
									sb.append("'");
								}
								if (i != cellNum - 1) {
									sb.append(",");
								}
							} else if (typeList.get(i).contains("float")) {
								sb.append((float)cell.getNumericCellValue());
								if (i != cellNum - 1) {
									sb.append(",");
								}
							}
						}
						sb.append(");");
						sqlList.add(sb.toString());
					}
				} else {
					break;
				}
			}
			String createTbSql = createTableSql(tableName, commentList,
					paramNameList, typeList, keyList);
			mysqlList.add("DROP TABLE IF EXISTS `" + tableName + "`;");
			sqlLiteList.add("DROP TABLE IF EXISTS `main`.`" + tableName + "`;");
			String sqlLiteCreateSql = createTableSqlLite(tableName, paramNameList, typeList, keyList).replaceAll("int", "Integer").replaceAll("float", "REAL").replaceAll("varchar", "TEXT");
			sqlLiteList.add(sqlLiteCreateSql);
			sqlLiteList.addAll(sqlList);
			mysqlList.add(createTbSql);
			mysqlList.addAll(sqlList);
			return createBeanModel(className, null, commentList, typeList, paramNameList, keyList);
		} catch (Exception e) {
			System.out.println("出错文件：" + fileName);
			e.printStackTrace();
		} finally {
			if(is != null){
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				is = null;
			}
		}
		return null;
	}

	/**
	 * 建表语句生成
	 * 
	 * @param tableName
	 * @param paramNameList
	 * @param typeList
	 * @param keyList
	 * @return
	 */
	public static String createTableSql(String tableName,
			List<String> commentList, List<String> paramNameList,
			List<String> typeList, List<Integer> keyList) {
		StringBuffer sb = new StringBuffer();
		sb.append("create table " + tableName + "(");
		boolean isCommentCanUse = (commentList.size() == paramNameList.size());
		for (int i = 0; i < paramNameList.size(); i++) {
			String paramName = paramNameList.get(i);
			String typeName = typeList.get(i);
			sb.append("`" + paramName + "` " + typeName);
			if (isCommentCanUse) {
				sb.append(" comment '" + commentList.get(i) + "',");
			} else {
				sb.append(",");
			}
		}
		sb.append("primary key(");
		for (int index : keyList) {
			sb.append("`" + paramNameList.get(index) + "`,");
		}
		String rtStr = sb.toString();
		return rtStr.substring(0, rtStr.length() - 1)
				+ ")) ENGINE=InnoDB DEFAULT CHARSET=utf8;";
	}
	
	/**
	 * 建表语句生成
	 * 
	 * @param tableName
	 * @param paramNameList
	 * @param typeList
	 * @param keyList
	 * @return
	 */
	public static String createTableSqlLite(String tableName, List<String> paramNameList,
			List<String> typeList, List<Integer> keyList) {
		StringBuffer sb = new StringBuffer();
		sb.append("create table " + tableName + "(");
		for (int i = 0; i < paramNameList.size(); i++) {
			String paramName = paramNameList.get(i);
			String typeName = typeList.get(i);
			sb.append("`" + paramName + "` " + typeName + ",");
		}
		sb.append("primary key(");
		for (int index : keyList) {
			sb.append("`" + paramNameList.get(index) + "`,");
		}
		String rtStr = sb.toString();
		return rtStr.substring(0, rtStr.length() - 1)
				+ "));";
	}
	
	/**
	 * 生成日志的各种协议
	 * @param mo
	 * @param outpath
	 */
	public static void velocityTemplet(BeanModel mo, String outpath){
		 //获取模板引擎
       VelocityEngine ve = new VelocityEngine();
       //模板文件所在的路径
       String path = "./conf/";
       //设置参数
       ve.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, path);
       //处理中文问题
       ve.setProperty(Velocity.INPUT_ENCODING,"utf-8");
       ve.setProperty(Velocity.OUTPUT_ENCODING,"utf-8");
       try 
       {
           //初始化模板
           ve.init();
           //获取模板(hello.html)
           //Velocity模板的名称
           Template template = ve.getTemplate("templet.vm");
           //获取上下文
           VelocityContext root = new VelocityContext();
           //把数据填入上下文
           root.internalPut("templet", mo);
           //输出
           Writer mywriter = new PrintWriter(new FileOutputStream(new File(outpath + mo.getClassName() + ".java"))); 
           System.out.println(outpath);
           template.merge(root, mywriter);
           mywriter.flush();           
       } 
       catch (Exception e) 
       {
           e.printStackTrace();
       }
	}
	
	/**
	 * 生成实体类
	 * @param commentList
	 * @param typeList
	 * @param paramNameList
	 * @param keyList
	 * @return
	 */
	private static BeanModel createBeanModel(String className, String packageName, List<String> commentList, List<String> typeList, List<String> paramNameList, List<Integer> keyList){
		int size = typeList.size();
		List<FieldModel> fieldList = new ArrayList<FieldModel>();
		for(int j = 0; j <= size - commentList.size(); j++){
			commentList.add(null);
		}
		for(int i = 0; i < size; i++){
			String sqlType = typeList.get(i);
			String comment = commentList.get(i);
			String fieldName = paramNameList.get(i);
			boolean isPrimary = false;
			if(keyList.contains(i)){
				isPrimary = true;
			}
			FieldModel model = new FieldModel(sqlType, fieldName, isPrimary, comment);
			fieldList.add(model);
		}
		return new BeanModel(className, packageName, fieldList);
	}


}
