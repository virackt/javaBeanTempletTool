package com.zxhd.exceltool.sql;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;
import com.zxhd.exceltool.util.AppConfig;

public class SQLConnector {
	
	public Connection getConn() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String url = AppConfig.getString("url");
			String username = AppConfig.getString("username");
			String password = AppConfig.getString("password");
			try {
				Connection con = (Connection) DriverManager.getConnection(url,
						username, password);
				return con;
			} catch (SQLException se) {
				System.out.println("数据库连接失败！");
				se.printStackTrace();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void sqlOperation(Connection conn, List<String> list){
		Statement st = null;
		try {
			conn.setAutoCommit(false);
			st = (Statement) conn.createStatement();
			String sql;
			int size = 20000;
			int count = 0;
			String firstSql = list.get(0);
			System.out.println("===开始执行sql，表名：" + firstSql.substring(firstSql.indexOf("`") + 1, firstSql.lastIndexOf("`")));
			for (int i = 0; i < list.size(); i++) {
				sql = list.get(i);
				count++;
				try {
					st.executeUpdate(sql);
				} catch (Exception e) {
					System.err.println("执行失败的：sql=" + sql);
					e.printStackTrace();
					try {
						st.executeUpdate(sql);
					} catch (Exception ex) {
						System.err.println("执行失败的：sql=" + sql);
						ex.printStackTrace();
						continue;
					}
				}
				if (count >= size) {
					count = 0;
					st.executeBatch();
					conn.commit();
				}
			}
			conn.commit();
//			list.clear();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			System.out.println("===结束执行sql");
			connectionRelease(conn, st);
		}
	}
	
	public void connectionRelease(Connection conn, Statement st){
		try {
			if(st != null){
				st.close();
				st = null;
			}
			if(conn != null){
				conn.close();
				conn = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
