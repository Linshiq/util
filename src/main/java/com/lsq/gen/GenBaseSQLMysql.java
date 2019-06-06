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
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.lsq.bean.GenEntityMysqlDTO;

public class GenBaseSQLMysql extends BaseSQL{
	
	/**
	 * @Author linshiqin
	 *         <p>
	 *         <li>2019年6月6日-下午4:19:53</li>
	 *         <li>功能说明：生成DB库中所有的表的基础SQL信息</li>
	 *         </p>
	 * @param genEntityMysqlDTO
	 */
	public void genAllDbTableSQLInfo(GenEntityMysqlDTO genEntityMysqlDTO){
		this.packageOutPathEntity = genEntityMysqlDTO.getPackageOutPathEntity();
		this.packageOutPath = genEntityMysqlDTO.getPackageOutPath();
		this.authorName = genEntityMysqlDTO.getAuthorName();
		this.URL = genEntityMysqlDTO.getDbUrl();
		this.NAME = genEntityMysqlDTO.getDbName();
		this.PASS = genEntityMysqlDTO.getDbPass();
		this.DRIVER = genEntityMysqlDTO.getDbDriver();
		this.isHumpNomenclature = genEntityMysqlDTO.isHumpNomenclature();

		publicProcess();// 生成数据库
	}
	
	/**
	 * @Author linshiqin
	 *         <p>
	 *         <li>2019年6月6日-下午4:19:53</li>
	 *         <li>功能说明：根据默认信息生成DB库中所有的表的基础SQL信息</li>
	 *         </p>
	 * @param genEntityMysqlDTO
	 */
	public void genAllDbTableSQLInfoByDefaultInfo(){
		
		publicProcess();// 生成数据库
	}
	
	/**
	 * @Author linshiqin
	 *         <p>
	 *         <li>2019年6月6日-下午4:23:16</li>
	 *         <li>功能说明：设置生成基础SQL所需的信息,不会调用生成方法,需要自己调用</li>
	 *         </p>
	 * @param genEntityMysqlDTO
	 * @param tablename
	 */
	public void setGenBaseSQLDefaultInfo(GenEntityMysqlDTO genEntityMysqlDTO,String tablename) {

		this.packageOutPathEntity = genEntityMysqlDTO.getPackageOutPathEntity();
		this.packageOutPath = genEntityMysqlDTO.getPackageOutPath();
		this.authorName = genEntityMysqlDTO.getAuthorName();
		this.URL = genEntityMysqlDTO.getDbUrl();
		this.NAME = genEntityMysqlDTO.getDbName();
		this.PASS = genEntityMysqlDTO.getDbPass();
		this.DRIVER = genEntityMysqlDTO.getDbDriver();
		this.isHumpNomenclature = genEntityMysqlDTO.isHumpNomenclature();
		
		this.tablename = tablename;
	}
	
	public void genBaseSQLMysqlByDefaultInfo(boolean isHumpNomenclature, String tablename) {
		this.isHumpNomenclature = isHumpNomenclature;
		this.tablename = tablename;
	}
	
	/**
	 * @Author linshiqin
	 *         <p>
	 *         <li>2019年6月6日-下午4:23:41</li>
	 *         <li>功能说明：执行生成基础SQL</li>
	 *         </p>
	 */
	private void publicProcess() {
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

				genBaseSQLMysqlProcess(con, pStemt);
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
	 *         <li>2019年5月29日-下午4:26:26</li>
	 *         <li>功能说明：生成基础SQL</li>
	 *         </p>
	 * @param con
	 * @param pStemt
	 */
	public void genBaseSQLMysqlProcess(Connection con, PreparedStatement pStemt) throws SQLException {

		// 获取所有的主键
		// oracle 语句 select
		// table_name,dbms_metadata.get_ddl('TABLE','TABLE_NAME')from
		// dual,user_tables where table_name='TABLE_NAME';
		// TABLE_NAME为具体的表名,要求大写
		String sql_table = "SHOW CREATE TABLE " + tablename;
		PreparedStatement pre = con.prepareStatement(sql_table);
		ResultSet rs1 = pre.executeQuery();
		if (rs1.next()) {

			// 正则匹配数据
			Pattern pattern = Pattern.compile("PRIMARY KEY \\(\\`(.*)\\`\\)");
			Matcher matcher = pattern.matcher(rs1.getString(2));
			matcher.find();
			String data = "";
			try {
				data = matcher.group();
			} catch (IllegalStateException e) {
				System.out.println("没主键");
				return;
			}

			// 过滤对于字符
			data = data.replaceAll("\\`|PRIMARY KEY \\(|\\)", "");
			// 拆分字符
			String[] stringArr = data.split(",");
			System.out.println("主键为:" + Arrays.toString(stringArr));

			System.out.println("------------------------------");

			// 查要生成实体类的表
			String sql = "select * from " + tablename;

			pStemt = con.prepareStatement(sql);
			ResultSetMetaData rsmd = pStemt.getMetaData();
			int size = rsmd.getColumnCount(); // 统计列
			String[] colnames = new String[size];
			String[] colTypes = new String[size];
			int[] colSizes = new int[size];
			for (int i = 0; i < size; i++) {
				colnames[i] = rsmd.getColumnName(i + 1).toLowerCase();
				colTypes[i] = rsmd.getColumnTypeName(i + 1);

				colSizes[i] = rsmd.getColumnDisplaySize(i + 1);
			}

			String content = parse(colnames, colTypes, colSizes, stringArr);

			try {
				File directory = new File("");
				// System.out.println("绝对路径："+directory.getAbsolutePath());
				// System.out.println("相对路径："+directory.getCanonicalPath());
//				String path = this.getClass().getResource("").getPath();
//
//				System.out.println(path);
//				System.out.println("src/?/" + path.substring(path.lastIndexOf("/com/", path.length())));
				// String outputPath = directory.getAbsolutePath()+
				// "/src/"+path.substring(path.lastIndexOf("/com/",
				// path.length()), path.length()) + initcap(tablename) +
				// ".java";

				String[] dire = packageOutPath.split("\\.");
				String createPath = directory.getAbsolutePath() + "/src/main/java";
				// 循环生成目录
				for (int i = 0; i < dire.length; i++) {
					createPath = createPath + "/" + dire[i];
					// 判断文件是否存在
					File directoryExist = new File(createPath);
					// 不存在则创建一个目录出来
					if (!directoryExist.exists()) {
						directoryExist.mkdir();
					}
				}

				String tableName = this.tablename;
				// 使用驼峰命名法
				if (isHumpNomenclature) {
					tableName = useHumpNomenclature(tableName);
				}

				String outputPath = directory.getAbsolutePath() + "/src/main/java/"
						+ this.packageOutPath.replace(".", "/") + "/" + initcap(tableName) + "Dao.java";

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
	}

	/**
	 * @Author linshiqin
	 *         <p>
	 *         <li>2019年5月29日-下午4:12:22</li>
	 *         <li>功能说明：将字段修改为驼峰命名法</li>
	 *         </p>
	 * @param str
	 * @return
	 */
	private String useHumpNomenclature(String str) {

		String[] strArr = str.split("_");

		if (strArr.length > 1) {
			StringBuffer sb = new StringBuffer();
			sb.append(strArr[0]);
			for (int j = 1; j < strArr.length; j++) {
				sb.append(initcap(strArr[j]));
			}
			return sb.toString();
		}

		return str;
	}

	/**
	 * 功能：生成实体类主体代码
	 * 
	 * @param colnames
	 * @param colTypes
	 * @param colSizes
	 * @param stringArr
	 * @return
	 */
	private String parse(String[] colnames, String[] colTypes, int[] colSizes, String[] stringArr) {

		boolean f_util = false; // 是否需要导入包java.util.*
		boolean f_sql = false; // 是否需要导入包java.sql.*
		boolean f_decimal = false; // 是否需要导入包java.sql.*
		boolean f_date = false; // 是否需要导入包java.sql.*

		// 确定导入包
		for (int i = 0; i < stringArr.length; i++) {

			for (int j = 0; j < colnames.length; j++) {

				if (colnames[j].equalsIgnoreCase(stringArr[i])) {

					if (colTypes[i].equalsIgnoreCase("datetime")) {
						f_util = true;
					}
					if (colTypes[i].equalsIgnoreCase("image") || colTypes[i].equalsIgnoreCase("text")) {
						f_sql = true;
					}

					String clone = colTypes[i].toLowerCase();

					if (clone.contains("decimal")) {
						f_decimal = true;
					}

					if (clone.equals("date") || clone.equals("time") || clone.equals("timestamp")) {
						f_date = true;
					}
				}

			}
		}

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

		String tableName = this.tablename;
		// 使用驼峰命名法
		if (isHumpNomenclature) {
			tableName = useHumpNomenclature(tableName);
		}

		sb.append("import java.util.List;\r\n");
		sb.append("import org.apache.ibatis.annotations.Delete;\r\n");
		sb.append("import org.apache.ibatis.annotations.Insert;\r\n");
		sb.append("import org.apache.ibatis.annotations.Select;\r\n");
		sb.append("import org.apache.ibatis.annotations.Update;\r\n");
		sb.append("import org.apache.ibatis.annotations.Mapper;\r\n");
		sb.append("import org.apache.ibatis.annotations.Param;\r\n");
		sb.append("import " + packageOutPathEntity + "." + initcap(tableName) + ";\r\n");
		sb.append("\r\n");

		// 注释部分
		sb.append("   /**\r\n");
		sb.append("    * " + tablename + " 基础SQL\r\n");
		sb.append("    * " + new Date() + " " + this.authorName + "\r\n");
		sb.append("    */ \r\n");
		// 实体部分
		sb.append("\r\n@Mapper");
		sb.append("\r\npublic interface " + initcap(tableName) + "Dao{\r\n");
		processDelete(sb, colnames, colTypes, colSizes, stringArr);
		sb.append("\r\n");
		processUpdate(sb, colnames, colTypes, colSizes, stringArr);
		sb.append("\r\n");
		processSelect(sb, colnames, colTypes, colSizes, stringArr);
		sb.append("\r\n");
		processInsert(sb, colnames, colTypes, colSizes, stringArr);

		sb.append("}\r\n");

		// System.out.println(sb.toString());
		return sb.toString();
	}

	/**
	 * @Author linshiqin
	 *         <p>
	 *         <li>2018年11月15日-下午4:33:28</li>
	 *         <li>功能说明：补充基础Insert</li>
	 *         </p>
	 * @param sb
	 * @param colnames
	 * @param colTypes
	 * @param colSizes
	 * @param stringArr
	 */
	private void processInsert(StringBuffer sb, String[] colnames, String[] colTypes, int[] colSizes,
			String[] stringArr) {

		StringBuffer insertInfoSb = new StringBuffer();

		for (int i = 0; i < colnames.length; i++) {// 补充insert into部分
			insertInfoSb.append(colnames[i].toLowerCase()).append(",");
		}
		insertInfoSb.delete(insertInfoSb.length() - 1, insertInfoSb.length());

		StringBuffer valuesSb = new StringBuffer(); // 补充values部分
		for (int j = 0; j < colnames.length; j++) {

			valuesSb.append("#{" + colnames[j] + "}").append(",");

		}

		valuesSb.delete(valuesSb.length() - 1, valuesSb.length());

		String tableName = this.tablename;
		// 使用驼峰命名法
		if (isHumpNomenclature) {
			tableName = useHumpNomenclature(tableName);
		}

		// 开始组装
		StringBuffer overParm = new StringBuffer();
		overParm.append("INSERT INTO " + tablename + " ( " + insertInfoSb.toString() + " ) ");
		overParm.append(" VALUES (" + valuesSb.toString() + ")");

		sb.append("\t" + "@Insert(\"" + overParm.toString() + "\")" + "\r\n");// 补充方法注解

		sb.append("\tint insert(" + initcap(tableName) + " " + tableName + ");\r\n");
		// System.out.println(parm);

	}

	/**
	 * @Author linshiqin
	 *         <p>
	 *         <li>2018年11月15日-下午4:33:26</li>
	 *         <li>功能说明：补充基础Select</li>
	 *         </p>
	 * @param sb
	 * @param colnames
	 * @param colTypes
	 * @param colSizes
	 * @param stringArr
	 */
	private void processSelect(StringBuffer sb, String[] colnames, String[] colTypes, int[] colSizes,
			String[] stringArr) {

		StringBuffer resultSb = new StringBuffer();

		for (int i = 0; i < colnames.length; i++) {
			resultSb.append(colnames[i].toLowerCase()).append(",");
		}
		resultSb.delete(resultSb.length() - 1, resultSb.length());

		StringBuffer inParm = new StringBuffer();
		StringBuffer overParm = new StringBuffer();
		// 补充排序
		StringBuffer orderBySb = new StringBuffer();
		orderBySb.append(" ORDER BY ");
		overParm.append("SELECT " + resultSb.toString() + " FROM " + tablename + " WHERE ");
		for (int i = 0; i < stringArr.length; i++) {

			for (int j = 0; j < colnames.length; j++) {

				if (colnames[j].equalsIgnoreCase(stringArr[i])) {
					inParm.append("@Param(\"" + stringArr[i] + "\") " + sqlType2JavaType(colTypes[i]) + " "
							+ stringArr[i] + ",");
					overParm.append(stringArr[i] + " = " + "#{" + stringArr[i] + "}").append(" and ");
					orderBySb.append(stringArr[i]).append(",");
				}

			}

		}

		String tableName = this.tablename;
		// 使用驼峰命名法
		if (isHumpNomenclature) {
			tableName = useHumpNomenclature(tableName);
		}

		orderBySb.delete(orderBySb.length() - 1, orderBySb.length());
		overParm.delete(overParm.length() - 5, overParm.length());
		// 补充selectbyPrimarykey
		sb.append("\t" + "@Select(\"" + overParm.toString() + orderBySb.toString() + "\")" + "\r\n");
		inParm.delete(inParm.length() - 1, inParm.length());
		sb.append("\t" + initcap(tableName) + " selectByPrimaryKey(" + inParm.toString() + ");\r\n");
		// System.out.println(parm);
		sb.append("\r\n");
		// 补充selectAll
		sb.append("\t" + "@Select(\"" + "SELECT " + resultSb.toString() + " FROM " + tablename + orderBySb.toString()
				+ "\")" + "\r\n");
		sb.append("\tList<" + initcap(tableName) + "> selectAll();\r\n");
		sb.append("\r\n");
		// 补充selectAllByPage
		sb.append("\t" + "@Select(\"" + "SELECT " + resultSb.toString() + " FROM " + tablename + orderBySb.toString()
				+ " LIMIT #{page},#{number}" + "\")" + "\r\n");
		sb.append("\tList<" + initcap(tableName)
				+ "> selectAllByPage(@Param(\"page\") long page,@Param(\"number\") long number);\r\n");
	}

	/**
	 * @Author linshiqin
	 *         <p>
	 *         <li>2018年11月15日-下午4:33:24</li>
	 *         <li>功能说明：补充基础Update</li>
	 *         </p>
	 * @param sb
	 * @param colnames
	 * @param colTypes
	 * @param colSizes
	 * @param stringArr
	 */
	private void processUpdate(StringBuffer sb, String[] colnames, String[] colTypes, int[] colSizes,
			String[] stringArr) {

		// 组装set部分
		StringBuffer setSb = new StringBuffer();

		List<String> mainKey = Arrays.asList(stringArr);

		for (int j = 0; j < colnames.length; j++) {

			if (mainKey.contains(colnames[j].toUpperCase()) || mainKey.contains(colnames[j].toLowerCase())) {
				continue;
			}

			setSb.append(colnames[j] + " = " + "#{" + colnames[j] + "}").append(",");

		}

		// 如果小于等于0说明都是主键
		if (setSb.length() <= 0) {

			return;
		}

		setSb.delete(setSb.length() - 1, setSb.length());

		// 组装where
		StringBuffer whereSb = new StringBuffer();

		for (int i = 0; i < stringArr.length; i++) {

			whereSb.append(stringArr[i] + " = " + "#{" + stringArr[i].toLowerCase() + "}").append(" and ");

		}

		whereSb.delete(whereSb.length() - 5, whereSb.length());

		String tableName = this.tablename;
		// 使用驼峰命名法
		if (isHumpNomenclature) {
			tableName = useHumpNomenclature(tableName);
		}

		StringBuffer overParm = new StringBuffer();
		overParm.append("UPDATE " + tablename + " SET " + setSb.toString() + " WHERE " + whereSb.toString());

		sb.append("\t" + "@Update(\"" + overParm.toString() + "\")" + "\r\n");

		sb.append("\tint updateByPrimaryKey(" + initcap(tableName) + " " + tableName + ");\r\n");

	}

	/**
	 * @Author linshiqin
	 *         <p>
	 *         <li>2018年11月15日-下午4:13:47</li>
	 *         <li>功能说明：补充基础删除方法</li>
	 *         </p>
	 * @param sb
	 */
	private void processDelete(StringBuffer sb, String[] colnames, String[] colTypes, int[] colSizes,
			String[] stringArr) {

		StringBuffer inParm = new StringBuffer();// 组装入参

		StringBuffer deleteSql = new StringBuffer();
		deleteSql.append("DELETE FROM " + tablename + " WHERE ");
		for (int i = 0; i < stringArr.length; i++) {

			for (int j = 0; j < colnames.length; j++) {

				if (colnames[j].equalsIgnoreCase(stringArr[i])) {
					inParm.append("@Param(\"" + stringArr[i] + "\") " + sqlType2JavaType(colTypes[i]) + " "
							+ stringArr[i] + ",");
					deleteSql.append(stringArr[i] + " = " + "#{" + stringArr[i] + "}").append(" and "); // 组装主键限制
				}

			}

		}

		deleteSql.delete(deleteSql.length() - 5, deleteSql.length());

		sb.append("\t" + "@Delete(\"" + deleteSql.toString() + "\")" + "\r\n");
		inParm.delete(inParm.length() - 1, inParm.length());
		sb.append("\t" + "int deleteByPrimaryKey(" + inParm.toString() + ");\r\n");
		// System.out.println(parm);

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

		// for (int i = 1; i < ch.length; i++) {
		//
		// if (ch[i] >= 'a' && ch[i] <= 'z') {
		// ch[i] = (char) (ch[i] + 32);
		// }
		// }

		return new String(ch);
	}

	/**
	 * 功能：获得列的数据类型
	 * 
	 * @param sqlType
	 * @return
	 */
	private String sqlType2JavaType(String sqlType) {
		// System.out.println(sqlType);
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
}