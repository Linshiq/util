package com.lsq.bean;

/**
 * <p>
 * 文件功能说明：
 *       			
 * </p>
 * 
 * @Author linshiqin
 *         <p>
 *         <li>2019年6月6日-下午1:18:29</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>linshiqin：创建注释模板</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class GenEntityMysqlDTO {

	private String packageOutPathEntity = "com.lsq.db.entity";// 指定实体生成所在包的路径
	private String packageOutPath = "com.lsq.db.dao";// 指定实体生成所在包的路径
	private String authorName = "linshiq";// 作者名字

	// 数据库连接
	private String dbUrl = "jdbc:mysql://localhost:3306/lsq";
	private String dbName = "root";
	private String dbPass = "qqqq";
	private String dbDriver = "com.mysql.jdbc.Driver";
	
	/**
	 * 是否采用驼峰命名法 例:private String startDate;
	 * 
	 * 不采用则默认为 :private String start_date;
	 */
	private boolean isHumpNomenclature = true;

	public String getPackageOutPathEntity() {
		return packageOutPathEntity;
	}

	public void setPackageOutPathEntity(String packageOutPathEntity) {
		this.packageOutPathEntity = packageOutPathEntity;
	}

	public String getPackageOutPath() {
		return packageOutPath;
	}

	public void setPackageOutPath(String packageOutPath) {
		this.packageOutPath = packageOutPath;
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public boolean isHumpNomenclature() {
		return isHumpNomenclature;
	}

	public void setHumpNomenclature(boolean isHumpNomenclature) {
		this.isHumpNomenclature = isHumpNomenclature;
	}

	public String getDbUrl() {
		return dbUrl;
	}

	public void setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getDbPass() {
		return dbPass;
	}

	public void setDbPass(String dbPass) {
		this.dbPass = dbPass;
	}

	public String getDbDriver() {
		return dbDriver;
	}

	public void setDbDriver(String dbDriver) {
		this.dbDriver = dbDriver;
	}
	
	
}
