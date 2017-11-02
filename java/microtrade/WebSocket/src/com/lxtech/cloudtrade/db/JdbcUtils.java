package com.lxtech.cloudtrade.db;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * @ClassName: JdbcUtils
 * @Description: 数据库连接工具类
 *
 */
public class JdbcUtils {
	private final static Logger logger = LoggerFactory.getLogger(JdbcUtils.class);
	// 视频统计数据库
	private static ComboPooledDataSource ds = null;

	// 使用ThreadLocal存储当前线程中的Connection对象
	private static ThreadLocal<Connection> threadLocal = new ThreadLocal<Connection>();

	// 在静态代码块中创建数据库连接池
	static {
		ds = getDataSourceByConfig("/crawler.properties");
	}

	private static ComboPooledDataSource getDataSourceByConfig(String propertyFilePath) {
		ComboPooledDataSource ds = null;
		try {
			Properties properties = new Properties();
			Map<String, String> dbConfig = null;
			InputStream is = JdbcUtils.class.getResourceAsStream(propertyFilePath);
			properties.load(is);
			dbConfig = new HashMap<String, String>((Map) properties);
			String connUrl = String.format(
					"jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=utf-8&useSSL=false&autoReconnect=true&serverTimezone=Asia/Shanghai",
					dbConfig.get("host"), dbConfig.get("port"), dbConfig.get("dbname"));

			// 通过代码创建C3P0数据库连接池
			ds = new ComboPooledDataSource();
			ds.setDriverClass("com.mysql.jdbc.Driver");
			ds.setJdbcUrl(connUrl);
			ds.setUser(dbConfig.get("username"));
			ds.setPassword(dbConfig.get("password"));
			ds.setInitialPoolSize(10);
			ds.setMinPoolSize(5);
			ds.setMaxPoolSize(20);
			// 通过读取C3P0的xml配置文件创建数据源，C3P0的xml配置文件c3p0-config.xml必须放在src目录下
			// ds = new ComboPooledDataSource();//使用C3P0的默认配置来创建数据源
			// ds = new ComboPooledDataSource("MySQL");//使用C3P0的命名配置来创建数据源
		} catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		}
		return ds;
	}

	/**
	 * @Method: getConnection
	 * @Description: 从数据源中获取数据库连接
	 * @Anthor:孤傲苍狼
	 * @return Connection
	 * @throws SQLException
	 */
	public static Connection getConnection() throws SQLException {
		// 从当前线程中获取Connection
		Connection conn = threadLocal.get();
		if (conn == null) {
			// 从数据源中获取数据库连接
			conn = getDataSource().getConnection();
			// 将conn绑定到当前线程
			threadLocal.set(conn);
		}
		return conn;
	}

	/**
	 * @Method: startTransaction
	 * @Description: 开启事务
	 * @Anthor:孤傲苍狼
	 *
	 */
	public static void startTransaction() {
		try {
			Connection conn = threadLocal.get();
			if (conn == null) {
				conn = getConnection();
				// 把 conn绑定到当前线程上
				threadLocal.set(conn);
			}
			// 开启事务
			conn.setAutoCommit(false);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @Method: rollback
	 * @Description:回滚事务
	 * @Anthor:孤傲苍狼
	 *
	 */
	public static void rollback() {
		try {
			// 从当前线程中获取Connection
			Connection conn = threadLocal.get();
			if (conn != null) {
				// 回滚事务
				conn.rollback();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @Method: commit
	 * @Description:提交事务
	 * @Anthor:孤傲苍狼
	 *
	 */
	public static void commit() {
		try {
			// 从当前线程中获取Connection
			Connection conn = threadLocal.get();
			if (conn != null) {
				// 提交事务
				conn.commit();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @Method: close
	 * @Description:关闭数据库连接(注意，并不是真的关闭，而是把连接还给数据库连接池)
	 * @Anthor:孤傲苍狼
	 *
	 */
	public static void close() {
		try {
			// 从当前线程中获取Connection
			Connection conn = threadLocal.get();
			if (conn != null) {
				conn.close();
				// 解除当前线程上绑定conn
				threadLocal.remove();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @Method: getDataSource
	 * @Description: 获取数据源
	 * @return DataSource
	 */
	public static DataSource getDataSource() {
		// 从数据源中获取数据库连接
		return ds;
	}
}