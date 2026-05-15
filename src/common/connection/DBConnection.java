package common.connection;

import java.sql.*;

public class DBConnection {

    public static Connection getConnection(DBType dbType) {

        Connection conn = null;

        try {
            switch (dbType) {
                case ORACLE:
                    conn = DriverManager.getConnection("jdbc:oracle:thin:@192.168.2.27:1521/XEPDB1", "HF05", "1004");
                    break;
                case MARIADB:
                    conn = DriverManager.getConnection("jdbc:mariadb://localhost:3307/sampledb", "kosa", "1004");
                    break;
                case LOCALDB:
                    conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521/XEPDB1", "HF05_SUB", "1004");
                    break;
            }
        } catch (Exception e) {
            System.out.println("connection Factory : " + e.getMessage());
        }

        return conn;
    }

    public static Connection getConnection(DBType dbType, String id, String pwd) {

        Connection conn = null;

        try {
            switch (dbType) {
                case ORACLE:
                    conn = DriverManager.getConnection("jdbc:oracle:thin:@192.168.2.27:1521/XEPDB1", id, pwd);
                    break;
                case MARIADB:
                    conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/sampledb", id, pwd);
                    break;
                case LOCALDB:
                    conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521/XEPDB1", id, pwd);
                    break;
            }
        } catch (Exception e) {
            System.out.println("connection Factory : " + e.getMessage());
        }

        return conn;
    }

    // ?먯썝?댁젣
    public static void close(Connection conn) {
        if(conn != null) {
            try {
                conn.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static void close(ResultSet rs) {
        if(rs != null) {
            try {
                rs.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static void close(Statement stmt) {
        if(stmt != null) {
            try {
                stmt.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static void close(PreparedStatement pstmt) {
        if(pstmt != null) {
            try {
                pstmt.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}

