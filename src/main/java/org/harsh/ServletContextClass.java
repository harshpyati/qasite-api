package org.harsh;

import org.harsh.utils.DBUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ServletContextClass implements ServletContextListener {
    public static Connection connection;

    public static void getConnection() {
        try {
            connection = DriverManager.getConnection(DBUtils.DB_URL, DBUtils.DB_UNAME, DBUtils.DB_PWD);
            System.out.println("Connected to database successfully");
        } catch (SQLException ex) {
            System.out.println(ex.getErrorCode());
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        getConnection();
        servletContextEvent.getServletContext().setAttribute(DBUtils.DB_CONTEXT, connection);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            System.out.println(ex.getErrorCode());
        }
    }
}
