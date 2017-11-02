package com.lxtx.util.db;

import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

public class DomainHandler {

	public static class DomainConfig {
		private long id;
		
		private String hostname;
		
		private short inuse;
		
		private Timestamp closetime;
		
		private short valid;

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public String getHostname() {
			return hostname;
		}

		public void setHostname(String hostname) {
			this.hostname = hostname;
		}

		public short getInuse() {
			return inuse;
		}

		public void setInuse(short inuse) {
			this.inuse = inuse;
		}

		public Timestamp getClosetime() {
			return closetime;
		}

		public void setClosetime(Timestamp closetime) {
			this.closetime = closetime;
		}

		public short getValid() {
			return valid;
		}

		public void setValid(short valid) {
			this.valid = valid;
		}
	}
	
	public static String getRedPacketActiveDomain() {
		try {
			String sql = "select hostname from business_domain_config where business = 'redpacket' and inuse = 1 and valid = 1";
			QueryRunner qr = new QueryRunner(JdbcUtils.getDomainDataSource());
			DomainConfig config;
			config = qr.query(sql, new BeanHandler<DomainConfig>(DomainConfig.class));
			if (config != null) {
				return config.getHostname();
			} else {
				return "";
			}
		} catch (SQLException e) {
			return "";
		}
	}
	
	public static void main(String[] args) {
		System.out.println(DomainHandler.getRedPacketActiveDomain());
	}
}
