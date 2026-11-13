package com.piet.busmanagement.dao;

import com.piet.busmanagement.model.Notice;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class NoticeDAO extends BaseDAO {

    private static final String SELECT = "SELECT notice_id, title, description, notice_date, status FROM notices ";

    private Notice map(ResultSet rs) throws SQLException {
        return new Notice(rs.getInt("notice_id"), str(rs, "title"), str(rs, "description"),
                str(rs, "notice_date"), str(rs, "status"));
    }

    public List<Notice> search(String keyword, String status) throws SQLException {
        String k = like(keyword);
        return query(SELECT + "WHERE (title LIKE ? OR description LIKE ?) AND (? = 'All' OR status = ?) "
                + "ORDER BY notice_date DESC, notice_id DESC", this::map, k, k, status, status);
    }

    /** Latest ACTIVE notices (shown to students, drivers and on the admin dashboard). */
    public List<Notice> listActive(int limit) throws SQLException {
        return query(SELECT + "WHERE status = 'ACTIVE' ORDER BY notice_date DESC, notice_id DESC LIMIT ?",
                this::map, limit);
    }

    public Notice findById(int id) throws SQLException {
        return queryOne(SELECT + "WHERE notice_id = ?", this::map, id);
    }

    public void add(Notice n) throws SQLException {
        update("INSERT INTO notices (title, description, notice_date, status) VALUES (?, ?, ?, ?)",
                n.title(), n.description(), n.noticeDate(), n.status());
    }

    public void update(Notice n) throws SQLException {
        update("UPDATE notices SET title = ?, description = ?, notice_date = ?, status = ? WHERE notice_id = ?",
                n.title(), n.description(), n.noticeDate(), n.status(), n.id());
    }

    public void delete(int id) throws SQLException {
        update("DELETE FROM notices WHERE notice_id = ?", id);
    }
}
