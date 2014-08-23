package com.zxhd.exceltool.model;

import java.util.List;

/**
 * 实体类
 * 
 * @author vic
 * 
 */
public class BeanModel {
	// 实体名
	private String className;
	// 包名
	private String packageName;
	// 字段
	private List<FieldModel> fieldList;

	public BeanModel() {
		super();
	}

	public BeanModel(String className, String packageName,
			List<FieldModel> fieldList) {
		super();
		this.className = className;
		this.packageName = packageName;
		this.fieldList = fieldList;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public List<FieldModel> getFieldList() {
		return fieldList;
	}

	public void setFieldList(List<FieldModel> fieldList) {
		this.fieldList = fieldList;
	}
	/**
	 * 生成mysql建表语句
	 * @return
	 */
	public List<String> getMysqlStr(){
		return null;
	}
	
	/**
	 * 生成sqllite建表语句
	 * @return
	 */
	public List<String> getSqlLiteStr(){
		return null;
	}
	

}
