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

public class GenEntityMysql {

	private String packageOutPath = "com.lsq.db.entity";// 指定实体生成所在包的路径
	private String authorName = "linshiq";// 作者名字
	private String tablename = "dp_consumer_details";// 表名
	private String[] colnames; // 列名数组
	private String[] colTypes; // 列名类型数组
	private int[] colSizes; // 列名大小数组
	private boolean f_util = false; // 是否需要导入包java.util.*
	private boolean f_sql = false; // 是否需要导入包java.sql.*
	private boolean f_decimal = false; // 是否需要导入包java.sql.*
	private boolean f_date = false;// 是否需要导入包java.util.Date
	// 数据库连接
	private static final String URL = "jdbc:mysql://localhost:3306/bms";
	private static final String NAME = "root";
	private static final String PASS = "qqqq";
	private static final String DRIVER = "com.mysql.jdbc.Driver";

	/*
	 * 构造函数
	 */
	public GenEntityMysql() {
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
				System.out.println("------------------------------");
				System.out.println("表名：" + rs.getString(3));
				System.out.println("表所属用户名：" + rs.getString(2));
				
				String tablename = rs.getString(3);
				this.tablename = tablename;	
				
				// 获取表中字段的所有注释
				PreparedStatement pStemt1 = con.prepareStatement("SELECT * FROM "+tablename);
				ResultSet commentRs = pStemt1.executeQuery("show full columns from " + tablename);
	            List<String> columnComments = new ArrayList<>();//列名注释集合
	            while (commentRs.next()) {
	                columnComments.add(commentRs.getString("Comment"));
	            }
				System.out.println(columnComments);
				
				// TODO:
				
				// 获取所有的主键
				// oracle 语句  select table_name,dbms_metadata.get_ddl('TABLE','TABLE_NAME')from dual,user_tables where table_name='TABLE_NAME'; 
				// TABLE_NAME为具体的表名,要求大写
				String sql_table="SHOW CREATE TABLE "+tablename;
				PreparedStatement pre=con.prepareStatement(sql_table);
				ResultSet rs1=pre.executeQuery();
				if(rs1.next()){
	 
					//正则匹配数据
					Pattern pattern = Pattern.compile("PRIMARY KEY \\(\\`(.*)\\`\\)");
					Matcher matcher =pattern.matcher(rs1.getString(2));
					matcher.find();
					String data="";
					try {
						data = matcher.group();
					} catch (IllegalStateException e) {
						System.out.println("没主键");
					}
					
					//过滤对于字符
					data=data.replaceAll("\\`|PRIMARY KEY \\(|\\)", "");
					//拆分字符
					String [] stringArr= data.split(",");
					System.out.println("主键为:"+Arrays.toString(stringArr));
				}
				System.out.println("------------------------------");		
				
				// 查要生成实体类的表
				String sql = "select * from " + tablename;

				pStemt = con.prepareStatement(sql);
				ResultSetMetaData rsmd = pStemt.getMetaData();
				int size = rsmd.getColumnCount(); // 统计列
				colnames = new String[size];
				colTypes = new String[size];
				colSizes = new int[size];
				for (int i = 0; i < size; i++) {
					colnames[i] = rsmd.getColumnName(i + 1).toLowerCase();
					colTypes[i] = rsmd.getColumnTypeName(i + 1);
					
					if (colTypes[i].equalsIgnoreCase("datetime")) {
						f_util = true;
					}
					if (colTypes[i].equalsIgnoreCase("image") || colTypes[i].equalsIgnoreCase("text")) {
						f_sql = true;
					}

					String clone = colTypes[i].toLowerCase();

				//	System.out.println("clone:" + clone);

					if (clone.contains("decimal")) {
						f_decimal = true;
					}
					
					if (clone.equals("date") || clone.equals("time") || clone.equals("timestamp")) {
						f_date = true;
					}
					
					colSizes[i] = rsmd.getColumnDisplaySize(i + 1);
				}

				String content = parse(colnames, colTypes, colSizes,columnComments);

				try {
					File directory = new File("");
					// System.out.println("绝对路径："+directory.getAbsolutePath());
					// System.out.println("相对路径："+directory.getCanonicalPath());
					String path = this.getClass().getResource("").getPath();

					System.out.println(path);
					System.out.println("src/?/" + path.substring(path.lastIndexOf("/com/", path.length())));
					// String outputPath = directory.getAbsolutePath()+
					// "/src/"+path.substring(path.lastIndexOf("/com/",
					// path.length()), path.length()) + initcap(tablename) +
					// ".java";
					
					String[] dire = packageOutPath.split("\\.");
					String createPath = directory.getAbsolutePath() + "/src/main/java";
					// 循环生成目录
					for (int i = 0; i < dire.length; i++) {
						createPath = createPath +"/" +dire[i];
						// 判断文件是否存在
						File directoryExist = new File(createPath);
						// 不存在则创建一个目录出来
						if(!directoryExist.exists()){
							directoryExist.mkdir();
						}
					}
					
					String outputPath = directory.getAbsolutePath() + "/src/main/java/"
							+ this.packageOutPath.replace(".", "/") + "/" + initcap(tablename) + ".java";
					FileWriter fw = new FileWriter(outputPath);
					System.out.println(outputPath);
					PrintWriter pw = new PrintWriter(fw);
					pw.println(content);
					pw.flush();
					pw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
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

	/**
	 * @Author linshiqin
	 *         <p>
	 *         <li>2018年11月6日-下午5:21:56</li>
	 *         <li>功能说明：获取所有的表名</li>
	 *         </p>
	 * @param con
	 */
	public String getTableNameByCon(Connection con) throws Exception {

		return null;
	}

	/**
	 * 功能：生成实体类主体代码
	 * 
	 * @param colnames
	 * @param colTypes
	 * @param colSizes
	 * @param columnComments 
	 * @return
	 */
	private String parse(String[] colnames, String[] colTypes, int[] colSizes, List<String> columnComments) {
		StringBuffer sb = new StringBuffer();

		sb.append("package " + this.packageOutPath + ";\r\n");
		sb.append("\r\n");
		// 判断是否导入工具包
		if (f_util) {
			sb.append("import java.util.Date;\r\n");
		}
		if (f_sql) {
			sb.append("import java.sql.*;\r\n");
		}
		if (f_decimal) {
			sb.append("import java.math.BigDecimal;\r\n");
		}
		if (f_date) {
			sb.append("import java.util.Date;\r\n");
		}
		// 注释部分
		sb.append("   /**\r\n");
		sb.append("    * " + tablename + " 实体类\r\n");
		sb.append("    * " + new Date() + " " + this.authorName + "\r\n");
		sb.append("    */ \r\n");
		// 实体部分
		sb.append("\r\n\r\npublic class " + initcap(tablename) + "{\r\n");
		processAllAttrs(sb);// 属性
		processAllMethod(sb,columnComments);// get set方法
		sb.append("}\r\n");

		// System.out.println(sb.toString());
		return sb.toString();
	}

	/**
	 * 功能：生成所有属性
	 * 
	 * @param sb
	 */
	private void processAllAttrs(StringBuffer sb) {

		for (int i = 0; i < colnames.length; i++) {
			sb.append("\tprivate " + sqlType2JavaType(colTypes[i]) + " " + colnames[i] + ";\r\n");
		}

	}

	/**
	 * 功能：生成所有方法
	 * 
	 * @param sb
	 * @param columnComments 
	 */
	private void processAllMethod(StringBuffer sb, List<String> columnComments) {
		
		/**
		  * fiche no
		  * 卡片号
		  */
		
		for (int i = 0; i < colnames.length; i++) {
			
			sb.append("\t/**").append("\r\n");// 注释部分
			sb.append("\t  * "+colnames[i]).append("\r\n");// 注释部分
			sb.append("\t  * "+columnComments.get(i)).append("\r\n");// 注释部分
			sb.append("\t  */").append("\r\n");// 注释部分
			
			sb.append("\tpublic void set" + initcap(colnames[i]) + "(" + sqlType2JavaType(colTypes[i]) + " "
					+ colnames[i] + "){\r\n");
			sb.append("\t\tthis." + colnames[i] + "=" + colnames[i] + ";\r\n");
			sb.append("\t}\r\n");
			
			sb.append("\t/**").append("\r\n");// 注释部分
			sb.append("\t  * "+colnames[i]).append("\r\n");// 注释部分
			sb.append("\t  * "+columnComments.get(i)).append("\r\n");// 注释部分
			sb.append("\t  */").append("\r\n");// 注释部分
			sb.append("\tpublic " + sqlType2JavaType(colTypes[i]) + " get" + initcap(colnames[i]) + "(){\r\n");
			sb.append("\t\treturn " + colnames[i] + ";\r\n");
			sb.append("\t}\r\n");
		}

	}

	/**
	 * 功能：将输入字符串的首字母改成大写
	 * 
	 * @param str
	 * @return
	 */
	private String initcap(String str) {

		char[] ch = str.toCharArray();
		if (ch[0] >= 'a' && ch[0] <= 'z') {
			ch[0] = (char) (ch[0] - 32);
		}
		
//		for (int i = 1; i < ch.length; i++) {
//			
//			if (ch[i] >= 'a' && ch[i] <= 'z') {
//				ch[i] = (char) (ch[i] + 32);
//			}
//		}
		
		return new String(ch);
	}

	/**
	 * 功能：获得列的数据类型
	 * 
	 * @param sqlType
	 * @return
	 */
	private String sqlType2JavaType(String sqlType) {
		//System.out.println(sqlType);
		if (sqlType.equalsIgnoreCase("bit")) {
			return "boolean";
		} else if (sqlType.equalsIgnoreCase("tinyint")) {
			return "byte";
		} else if (sqlType.equalsIgnoreCase("smallint")) {
			return "short";
		} else if (sqlType.equalsIgnoreCase("int")) {
			return "int";
		} else if (sqlType.equalsIgnoreCase("bigint")) {
			return "long";
		} else if (sqlType.equalsIgnoreCase("float")) {
			return "float";
		} else if (sqlType.equalsIgnoreCase("numeric") || sqlType.equalsIgnoreCase("real")) {
			return "double";
		} else if (sqlType.equalsIgnoreCase("decimal") || sqlType.equalsIgnoreCase("money")
				|| sqlType.equalsIgnoreCase("smallmoney")) {
			return "BigDecimal";
		} else if (sqlType.equalsIgnoreCase("varchar") || sqlType.equalsIgnoreCase("char")
				|| sqlType.equalsIgnoreCase("nvarchar") || sqlType.equalsIgnoreCase("nchar")
				|| sqlType.equalsIgnoreCase("text")) {
			return "String";
		} else if (sqlType.equalsIgnoreCase("datetime") || sqlType.equalsIgnoreCase("timestamp")) {
			return "Date";
		} else if (sqlType.equalsIgnoreCase("image")) {
			return "Blod";
		} else {

			String clone = sqlType.toLowerCase();

			if (clone.contains("decimal")) {
				return "BigDecimal";
			}

			return null;
		}

	}

	/**
	 * 出口 TODO
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		new GenEntityMysql();

	}

}