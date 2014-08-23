package com.zxhd.exceltool.model;

import java.util.List;

/**
 * 属性
 * @author vic
 *
 */
public class FieldModel {
	// java字段类型
	private String javaType;
	// sql字段类型
	private String sqlType;
	// 字段名
	private String paramName;
	// 主键
	private boolean isPrimary;
	// 注释
	private String comment;
	// 模板表的值
	private List<Object> values;
	
	private String getMethodName;
	private String setMethodName;
	
	public FieldModel() {
		super();
	}
	public FieldModel(String sqlType, String paramName,
			boolean isPrimary, String comment) {
		super();
		setSqlType(sqlType);
		this.paramName = paramName;
		this.isPrimary = isPrimary;
		this.comment = comment;
		this.getMethodName = "get" + paramName.substring(0,1).toUpperCase() + paramName.substring(1) + "()";
		this.setMethodName = "set" + paramName.substring(0,1).toUpperCase() + paramName.substring(1) + "(" + javaType + " " + paramName + ")";
	}
	
	public FieldModel(String javaType, String sqlType, String paramName,
			boolean isPrimary, String comment, List<Object> values) {
		super();
		this.javaType = javaType;
		this.sqlType = sqlType;
		this.paramName = paramName;
		this.isPrimary = isPrimary;
		this.comment = comment;
		this.values = values;
	}
	public List<Object> getValues() {
		return values;
	}
	public void setValues(List<Object> values) {
		this.values = values;
	}
	public String getJavaType() {
		return javaType;
	}
	public void setJavaType(String javaType) {
		this.javaType = javaType;
	}
	public String getSqlType() {
		return sqlType;
	}
	public void setSqlType(String sqlType) {
		this.sqlType = sqlType;
		String sqlLower = sqlType.toLowerCase();
		if(sqlLower.contains("int")){
			this.javaType = "int";
		} else if(sqlLower.contains("varchar")){
			this.javaType = "String";
		} else if(sqlLower.contains("long")){
			this.javaType = "long";
		}
	}
	public String getParamName() {
		return paramName;
	}
	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
	public boolean isPrimary() {
		return isPrimary;
	}
	public void setPrimary(boolean isPrimary) {
		this.isPrimary = isPrimary;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getGetMethodName() {
		return getMethodName;
	}
	public void setGetMethodName(String getMethodName) {
		this.getMethodName = getMethodName;
	}
	public String getSetMethodName() {
		return setMethodName;
	}
	public void setSetMethodName(String setMethodName) {
		this.setMethodName = setMethodName;
	}
	
	
	
}
