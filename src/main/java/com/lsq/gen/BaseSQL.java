package com.lsq.gen;

/**
 * <p>
 * 文件功能说明：
 *       			
 * </p>
 * 
 * @Author linshiqin
 *         <p>
 *         <li>2019年6月6日-下午4:12:05</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>linshiqin：创建注释模板</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public abstract class BaseSQL {

	protected String packageOutPathEntity = "com.lsq.db.entity";// 指定实体生成所在包的路径
	protected String packageOutPath = "com.lsq.db.dao";// 指定实体生成所在包的路径
	protected String authorName = "linshiq";// 作者名字
	protected String tablename = "";// 表名
	
	// 数据库连接
	protected String URL = "jdbc:mysql://localhost:3306/lsq";
	protected String NAME = "root";
	protected String PASS = "qqqq";
	protected String DRIVER = "com.mysql.jdbc.Driver";
	
	/**
	 * 是否采用驼峰命名法 例:private String startDate;
	 * 
	 * 不采用则默认为 :private String start_date;
	 */
	protected boolean isHumpNomenclature = true;
}
