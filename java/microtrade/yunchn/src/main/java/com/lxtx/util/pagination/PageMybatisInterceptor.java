package com.lxtx.util.pagination;

/**
 * @author mybatis分页拦截器插件的实现，基于CFOP提供的源码做了少量改动.
 */

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;

import com.lxtx.util.pagination.common.DataSection;
import com.lxtx.util.pagination.common.PageParameter;

/**
 * 通过拦截<code>StatementHandler</code>的<code>prepare</code>方法，重写sql语句实现物理分页。
 * 老规矩，签名里要拦截的类型只能是接口。
 * 
 * @author wjbj 20140530
 * 
 */
@Intercepts({ @Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class }) })
public class PageMybatisInterceptor implements Interceptor {
	private static final Log logger = LogFactory.getLog(PageMybatisInterceptor.class);
	private static final ObjectFactory DEFAULT_OBJECT_FACTORY = new DefaultObjectFactory();
	private static final ObjectWrapperFactory DEFAULT_OBJECT_WRAPPER_FACTORY = new DefaultObjectWrapperFactory();
	private static String defaultDialect = "mysql"; // 数据库类型(默认为mysql)
	private static String defaultPageSqlId = "queryForPage"; // 需要拦截的ID(正则匹配)，默认为select开头
	private static String dialect = ""; // 数据库类型(默认为mysql)
	private static String pageSqlId = ""; // 需要拦截的ID(正则匹配)

	public Object intercept(Invocation invocation) throws Throwable {
		StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
		MetaObject metaStatementHandler = MetaObject.forObject(statementHandler, DEFAULT_OBJECT_FACTORY,
				DEFAULT_OBJECT_WRAPPER_FACTORY);
		// 分离代理对象链(由于目标类可能被多个拦截器拦截，从而形成多次代理，通过下面的两次循环可以分离出最原始的的目标类)
		while (metaStatementHandler.hasGetter("h")) {
			Object object = metaStatementHandler.getValue("h");
			metaStatementHandler = MetaObject.forObject(object, DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY);
		}
		// 分离最后一个代理对象的目标类
		while (metaStatementHandler.hasGetter("target")) {
			Object object = metaStatementHandler.getValue("target");
			metaStatementHandler = MetaObject.forObject(object, DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY);
		}
		Configuration configuration = (Configuration) metaStatementHandler.getValue("delegate.configuration");
		dialect = configuration.getVariables().getProperty("dialect");
		// 获取用户设置的数据库类型，如果未设置，则使用默认值mysql
		if (null == dialect || "".equals(dialect)) {
			logger.warn("Property dialect is not setted,use default 'mysql' ");
			dialect = defaultDialect;
		}
		// 获取需要拦截的sqlid的正则表达式，如果未设置，则使用默认值“select.*”
		pageSqlId = configuration.getVariables().getProperty("pageSqlId");
		if (null == pageSqlId || "".equals(pageSqlId)) {
			logger.warn("Property pageSqlId is not setted,use default 'select.*' ");
			pageSqlId = defaultPageSqlId;
		}
		MappedStatement mappedStatement = (MappedStatement) metaStatementHandler.getValue("delegate.mappedStatement");
		PageParameter page = null;
		// 获取并判断是否传递了page参数，如果是则考虑分页，否则不需要考虑分页
		try {
			page = (PageParameter) DataSection.getInput("pages");
		} catch (Exception e) {
			page = null;
		}
		// 获取并判断sqlid是否满足拦截器设置的正则表达式规则
		boolean regMatched = false;
		String sqlId = mappedStatement.getId();
		// 去除包路径，只对id进行匹配
		String idOnly = sqlId.substring(sqlId.lastIndexOf(".") + 1);
		if (null != sqlId && idOnly.matches(pageSqlId)) {
			regMatched = true;
		}
		// 满足上面两个条件，则需要处理分页问题
		if (null != page && regMatched) {
			page.setStart(page.getPagenum() * page.getPagesize());
			BoundSql boundSql = (BoundSql) metaStatementHandler.getValue("delegate.boundSql");
			Object parameterObject = boundSql.getParameterObject();
			/*
			 * if (parameterObject == null) { // throw new NullPointerException(
			 * "parameterObject is null!"); } else {
			 */
			String sql = boundSql.getSql();
			// 重写sql
			String pageSql;
			pageSql = buildPageSql(sql, page);
			metaStatementHandler.setValue("delegate.boundSql.sql", pageSql);
			// 采用物理分页后，就不需要mybatis的内存分页了，所以重置下面的两个参数
			metaStatementHandler.setValue("delegate.rowBounds.offset", RowBounds.NO_ROW_OFFSET);
			metaStatementHandler.setValue("delegate.rowBounds.limit", RowBounds.NO_ROW_LIMIT);
			Connection connection = (Connection) invocation.getArgs()[0];
			// 重设分页参数里的总页数等
			setPageParameter(sql, connection, mappedStatement, boundSql, page);
			// }
		}
		// 将执行权交给下一个拦截器
		return invocation.proceed();
	}

	/**
	 * 从数据库里查询总的记录数并计算总页数，回写进分页参数<code>PageParameter</code>,这样调用者就可用通过 分页参数
	 * <code>PageParameter</code>获得相关信息。
	 * 
	 * @param sql
	 * @param connection
	 * @param mappedStatement
	 * @param boundSql
	 * @param page
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 */
	private void setPageParameter(String sql, Connection connection, MappedStatement mappedStatement, BoundSql boundSql,
			PageParameter page)
			throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		// 记录总记录数
		String countSql = "select count(0) from (" + sql + ")  total";
		PreparedStatement countStmt = null;
		ResultSet rs = null;
		try {
			countStmt = connection.prepareStatement(countSql);
			BoundSql countBS = new BoundSql(mappedStatement.getConfiguration(), countSql,
					boundSql.getParameterMappings(), boundSql.getParameterObject());

			Field metaParamsField = ReflectUtil.getFieldByFieldName(boundSql, "metaParameters");
			if (metaParamsField != null) {
				MetaObject mo = (MetaObject) ReflectUtil.getValueByFieldName(boundSql, "metaParameters");
				ReflectUtil.setValueByFieldName(countBS, "metaParameters", mo);
			}

			setParameters(countStmt, mappedStatement, countBS, boundSql.getParameterObject());
			rs = countStmt.executeQuery();
			int totalCount = 0;
			if (rs.next()) {
				totalCount = rs.getInt(1);
			}
			page.setTotalrecords(totalCount);
			int totalpage = totalCount / page.getPagesize() + ((totalCount % page.getPagesize() == 0) ? 0 : 1);
			page.setTotalpage(totalpage);

		} catch (SQLException e) {
			logger.error("Ignore this exception", e);
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				logger.error("Ignore this exception", e);
			}
			try {
				countStmt.close();
			} catch (SQLException e) {
				logger.error("Ignore this exception", e);
			}
		}

	}

	/**
	 * 对SQL参数(?)设值
	 * 
	 * @param ps
	 * @param mappedStatement
	 * @param boundSql
	 * @param parameterObject
	 * @throws SQLException
	 */
	private void setParameters(PreparedStatement ps, MappedStatement mappedStatement, BoundSql boundSql,
			Object parameterObject) throws SQLException {
		try {
			ParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, parameterObject, boundSql);
			parameterHandler.setParameters(ps);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据数据库类型，生成特定的分页sql
	 * 
	 * @param sql
	 * @param page
	 * @return
	 */
	private String buildPageSql(String sql, PageParameter page) {
		if (page != null) {
			StringBuilder pageSql = new StringBuilder();
			if ("mysql".equals(dialect)) {
				pageSql = buildPageSqlForMysql(sql, page);
			} else if ("oracle".equals(dialect)) {
				pageSql = buildPageSqlForOracle(sql, page);
			} else {
				return sql;
			}
			return pageSql.toString();
		} else {
			return sql;
		}
	}

	/**
	 * mysql的分页语句
	 * 
	 * @param sql
	 * @param page
	 * @return String
	 */
	public StringBuilder buildPageSqlForMysql(String sql, PageParameter page) {
		StringBuilder pageSql = new StringBuilder();
		// 处理排序问题
		if (null != page.getSortdatafield() && StringUtils.isNotEmpty(page.getSortdatafield())) {
			// 如果没有设置排序方式，默认为asc
			String order = page.getSortorder();
			if (null == order) {
				order = "asc";
			}
			String[] sqlSegs = sql.split("order\\s+by");
			pageSql.append(sqlSegs[0] + " order by " + page.getSortdatafield() + " " + order);
			// 如果原sql中已存在order by，则将其作为第二排序字段
			if (sqlSegs.length > 1) {
				pageSql.append(" , " + sqlSegs[1]);
			}
		} else {
			// 如果没有排序问题，则不需处理
			pageSql.append(sql);
		}
		// limit需要放置在最后面
		pageSql.append(" limit " + page.getStart() + "," + page.getPagesize());

		return pageSql;
	}

	/**
	 * 参考hibernate的实现完成oracle的分页
	 * 
	 * @param sql
	 * @param page
	 * @return String
	 */
	public StringBuilder buildPageSqlForOracle(String sql, PageParameter page) {
		StringBuilder pageSql = new StringBuilder(100);
		int endrow = page.getStart() + page.getPagesize();

		pageSql.append("select * from ( select temp.*, rownum row_id from ( ");

		// add by swh;
		if (null != page.getSortdatafield() && null != page.getSortorder()) {

			pageSql.append(sql.toLowerCase().split("order\\s+by")[0]);
			pageSql.append(" order by ").append(page.getSortdatafield()).append(" " + page.getSortorder());
		} else {
			pageSql.append(sql);
		}
		pageSql.append(" ) temp where rownum <= ").append(endrow);
		pageSql.append(") where row_id > ").append(page.getStart());
		return pageSql;
	}

	public Object plugin(Object target) {
		// 当目标类是StatementHandler类型时，才包装目标类，否者直接返回目标本身,减少目标被代理的次数
		if (target instanceof StatementHandler) {
			return Plugin.wrap(target, this);
		} else {
			return target;
		}
	}

	public void setProperties(Properties properties) {
	}

}
