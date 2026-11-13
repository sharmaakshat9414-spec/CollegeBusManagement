package com.piet.busmanagement.dao;

import com.piet.busmanagement.config.AppException;
import com.piet.busmanagement.config.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Small JDBC helpers shared by all DAO classes. Every query uses PreparedStatement. */
public abstract class BaseDAO {

    @FunctionalInterface
    public interface RowMapper<T> {
        T map(ResultSet rs) throws SQLException;
    }

    @FunctionalInterface
    protected interface TxWork {
        void run(Connection c) throws SQLException, AppException;
    }

    private static void bind(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
    }

    protected <T> List<T> query(String sql, RowMapper<T> mapper, Object... params) throws SQLException {
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                List<T> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(mapper.map(rs));
                }
                return list;
            }
        }
    }

    protected <T> T queryOne(String sql, RowMapper<T> mapper, Object... params) throws SQLException {
        List<T> list = query(sql, mapper, params);
        return list.isEmpty() ? null : list.get(0);
    }

    protected int count(String sql, Object... params) throws SQLException {
        Integer value = queryOne(sql, rs -> rs.getInt(1), params);
        return value == null ? 0 : value;
    }

    protected int update(String sql, Object... params) throws SQLException {
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, params);
            return ps.executeUpdate();
        }
    }

    // ---- variants that run inside an existing transaction ----

    protected static int updateTx(Connection c, String sql, Object... params) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, params);
            return ps.executeUpdate();
        }
    }

    protected static long insertTx(Connection c, String sql, Object... params) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bind(ps, params);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getLong(1) : 0;
            }
        }
    }

    protected static boolean existsTx(Connection c, String sql, Object... params) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    protected void inTransaction(TxWork work) throws SQLException, AppException {
        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                work.run(c);
                c.commit();
            } catch (SQLException | AppException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        }
    }

    protected static String like(String keyword) {
        return "%" + (keyword == null ? "" : keyword.trim()) + "%";
    }

    protected static String str(ResultSet rs, String column) throws SQLException {
        String v = rs.getString(column);
        return v == null ? "" : v;
    }
}
