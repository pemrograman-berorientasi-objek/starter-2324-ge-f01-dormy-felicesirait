package pbo.f01.driver;

import java.sql.Connection; //untuk koneksi ke database
import java.sql.DriverManager; //untuk mengelola driver
import java.sql.SQLException; //untuk menangani error


public abstract class AbstractDatabase {

    protected String url = "jdbc:h2:./db/dormy";
    protected Connection connection = null; //untuk koneksi ke database
    
    public AbstractDatabase(String url) throws SQLException { 
        this.url = url;
        this.prepareTables();
    }

    protected Connection getConnection() throws SQLException {
        if (connection == null) {
            connection = DriverManager.getConnection(url);
        }
        return connection;
    }

    public void shutdown() throws SQLException { //untuk menutup koneksi ke database
        if (connection != null) {
            connection.close();
        }
    }
    
    protected abstract void prepareTables() throws SQLException;{
        this.createTables();
    }

    protected abstract void createTables() throws SQLException;{
        // Create tables
    }

}