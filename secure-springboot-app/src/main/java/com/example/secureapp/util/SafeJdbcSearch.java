package com.example.secureapp.util;

import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class SafeJdbcSearch {

    private final DataSource ds;
    public SafeJdbcSearch(DataSource ds) { this.ds = ds; }

    public List<Map<String,Object>> searchByUsernameLike(String term) throws SQLException {
        String sql = "SELECT id, username FROM app_users WHERE username ILIKE ? LIMIT 50";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + term + "%");
            try (ResultSet rs = ps.executeQuery()) {
                List<Map<String,Object>> out = new ArrayList<>();
                while (rs.next()) {
                    out.add(Map.of("id", rs.getLong("id"), "username", rs.getString("username")));
                }
                return out;
            }
        }
    }
}
