package com.lsq.gen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * 文件功能说明：
 *       			
 * </p>
 * 
 * @Author linshiqin
 *         <p>
 *         <li>2019年5月20日-下午3:13:56</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>linshiqin：生成指定表的基础SQL</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class GenPointBaseSQLMysql {

	private String packageOutPathEntity = "com.lsq.db.entity";// 指定实体生成所在包的路径
	private String packageOutPath = "com.lsq.db.dao";// 指定实体生成所在包的路径
	private String authorName = "linshiq";// 作者名字
	private String tablename = "";// 表名

	// 数据库连接
	private static final String URL = "jdbc:mysql://localhost:3306/lsq";
	private static final String NAME = "root";
	private static final String PASS = "qqqq";
	private static final String DRIVER = "com.mysql.jdbc.Driver";
	
	/**
	 * 是否采用驼峰命名法 例:private String startDate;
	 * 
	 * 不采用则默认为 :private String start_date;
	 */
	private boolean isHumpNomenclature = true;

	/**
	 * 出口 TODO
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		List<String> tableNames =  new ArrayList<String>();
		
		tableNames.add("tp_person");
		
		new GenPointBaseSQLMysql(tableNames);

	}
	
	/*
	 * 构造函数
	 */
	public GenPointBaseSQLMysql(List<String> tableNames) {
		// 创建连接
		Connection con;

		PreparedStatement pStemt = null;
		try {
			try {
				Class.forName(DRIVER);
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			con = DriverManager.getConnection(URL, NAME, PASS);

			// 获取所有的表名
			DatabaseMetaData meta = con.getMetaData();
			ResultSet rs = meta.getTables(null, null, null, new String[] { "TABLE" });
			while (rs.next()) {
//				System.out.println("------------------------------");
//				System.out.println("表名：" + rs.getString(3));
//				System.out.println("表所属用户名：" + rs.getString(2));

				String tablename = rs.getString(3);
				
				// 是否是指定要生成的SQL表
				if(!tableNames.contains(tablename.toLowerCase())){
					continue;
				}else{
					System.out.println("存在"+tablename);
				}
								
				GenBaseSQLMysql genBaseSQLMysql = new GenBaseSQLMysql(isHumpNomenclature,tablename);
				
				genBaseSQLMysql.genBaseSQLMysqlProcess(con, pStemt);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// try {
			// con.close();
			// } catch (SQLException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
		}
	}

}