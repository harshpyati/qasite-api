package org.harsh.daos;

import org.harsh.domain.IdType;
import org.harsh.utils.db.DBUtils;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.sql.*;

public class CommonDao {
    public void executeUpdate(String sql) {
        try (Connection con = DBUtils.getDBConnection()) {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
            throw new WebApplicationException("failed to update", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    public Long executeUpdateAndReturnId(String sql) {
        try (Connection con = DBUtils.getDBConnection()) {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            int rows = ps.executeUpdate();

            if (rows == 1) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            throw new WebApplicationException("failed to insert", Response.Status.INTERNAL_SERVER_ERROR);
        }

        return null;
    }

    public int getCount(String sql) {
        int count = -1;
        try (Connection con = DBUtils.getDBConnection(); Statement statement = con.createStatement()) {
            ResultSet rs = statement.executeQuery(sql);
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
            throw new WebApplicationException("failed to fetch count", Response.Status.INTERNAL_SERVER_ERROR);
        }

        return count;
    }
}
