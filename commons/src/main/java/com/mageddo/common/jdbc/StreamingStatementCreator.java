package com.mageddo.common.jdbc;

import org.springframework.jdbc.core.PreparedStatementCreator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static java.sql.ResultSet.CONCUR_READ_ONLY;
import static java.sql.ResultSet.TYPE_FORWARD_ONLY;

public class StreamingStatementCreator implements PreparedStatementCreator {

	private final String sql;
	private final int fetchSize;

	public StreamingStatementCreator(String sql) {
		this.sql = sql;
		this.fetchSize = 1000;
	}

	public StreamingStatementCreator(String sql, int fetchSize) {
		this.sql = sql;
		this.fetchSize = fetchSize;
	}

	@Override
	public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
		final PreparedStatement statement = connection.prepareStatement(sql, TYPE_FORWARD_ONLY, CONCUR_READ_ONLY);
		statement.setFetchSize(fetchSize);
		return statement;
	}
}
