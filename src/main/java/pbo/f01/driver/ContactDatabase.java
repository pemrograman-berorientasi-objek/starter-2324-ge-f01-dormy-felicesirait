package pbo.f01.driver;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import pbo.f01.model.*;

public class ContactDatabase extends AbstractDatabase {

    public ContactDatabase(String url) throws SQLException {
        super(url);
    }

    protected void createTables() throws SQLException {
        String dropSQLs[] = {
            "DROP TABLE IF EXISTS Student",
            "DROP TABLE IF EXISTS Dorm",
        };
    
        String Dorm = "CREATE TABLE IF NOT EXISTS Dorm (" +
            "dormName VARCHAR(255) NOT NULL PRIMARY KEY," +
            "dormGender TEXT NOT NULL," +
            "dormCapacity INTEGER NOT NULL" +
            ")";
    
        String Student = "CREATE TABLE IF NOT EXISTS Student (" +
            "Id VARCHAR(30) PRIMARY KEY," + 
            "studentName VARCHAR(255) NOT NULL," +
            "studentGender TEXT NOT NULL," +
            "EntranceYear INTEGER NOT NULL," +
            "dormName TEXT " +
            ")";
    
        Statement statement = this.getConnection().createStatement(); //statement untuk membuat table
        //statement untuk mendrop table
        for (String sql : dropSQLs) {
            statement.execute(sql);
        }
    
        statement.execute(Dorm); 
        statement.execute(Student);
    
        statement.close();
    }

    //student-add#<id>#<name>#<year>#<gender>
    //METHOD STUDENT ADD
    public void addStudent(String Id, String Name, int EntranceYear, String Gender ) throws SQLException {
        String sql = "INSERT INTO Student (Id, studentName, EntranceYear, studentGender ) VALUES (?, ?, ?, ?)"; //query untuk menambahkan data ke table
        PreparedStatement statement = this.getConnection().prepareStatement(sql); //statement untuk menambahkan data ke table
        statement.setString(1, Id);
        statement.setString(2, Name);
        statement.setInt(3, EntranceYear);
        statement.setString(4, Gender);
        statement.executeUpdate();
        statement.close();
    
    String checkSql = "SELECT COUNT(*) FROM Student WHERE Id = ?";
    PreparedStatement checkStatement = this.getConnection().prepareStatement(checkSql);
    checkStatement.setString(1, Id);
    ResultSet resultSet = checkStatement.executeQuery();

    resultSet.next();
    if (resultSet.getInt(1) > 0) {
        checkStatement.close();
        return; // Jika ID sudah ada, tidak menambahkan mahasiswa baru
    }

    }

    //dorm-add#<name>#<capacity>#<gender>
    //METHOD DORM ADD
    public void addDorm(String Name, int Capacity , String Gender ) throws SQLException {
        String sql = "INSERT INTO Dorm (dormName, dormCapacity, dormGender) VALUES (?, ?, ?)";
        PreparedStatement statement = this.getConnection().prepareStatement(sql); 
        statement.setString(1, Name);
        statement.setInt(2, Capacity);
        statement.setString(3, Gender);
        statement.executeUpdate();
        statement.close();
    }

    //assign#<student-id>#<dorm-name>
    //METHOD ASSIGN
    public void assign(String Id, String dormName) throws SQLException {
        String sql = "UPDATE Student SET dormName = ? WHERE Id = ?";
        PreparedStatement statement = this.getConnection().prepareStatement(sql);
        statement.setString(1, dormName);
        statement.setString(2, Id);
        statement.executeUpdate();
        statement.close();

    // Periksa kapasitas asrama
    String capacitySql = "SELECT dormCapacity, (SELECT COUNT(*) FROM Student WHERE dormName = ?) AS Occupied FROM Dorm WHERE dormName = ?";
    PreparedStatement capacityStatement = this.getConnection().prepareStatement(capacitySql);
    capacityStatement.setString(1, dormName);
    capacityStatement.setString(2, dormName);
    ResultSet capacityResultSet = capacityStatement.executeQuery();
    if (capacityResultSet.next()) {
        int capacity = capacityResultSet.getInt("dormCapacity");
        int occupied = capacityResultSet.getInt("Occupied");
        if (occupied >= capacity) {
            capacityStatement.close();
            return; // Jika kapasitas penuh, tidak menempatkan mahasiswa
        }
    }
    capacityStatement.close();

    }

    //METHOD DISPLAY ALL
    public void displayAll() throws SQLException {
        String dormSQL = "SELECT * FROM Dorm ORDER BY dormName ASC";
        String studentSQL = "SELECT * FROM Student WHERE dormName = ? ORDER BY dormName ASC";

        Statement dormStatement = this.getConnection().createStatement();
        ResultSet dormResultSet = dormStatement.executeQuery(dormSQL);

        while (dormResultSet.next()) {
            String dormName = dormResultSet.getString("dormName");
            String dormGender = dormResultSet.getString("dormGender");
            int dormCapacity = dormResultSet.getInt("dormCapacity");

            String dormDetail = dormName + "|" + dormGender + "|" + dormCapacity;

            PreparedStatement studentStatement = this.getConnection().prepareStatement(studentSQL);
            studentStatement.setString(1, dormName);
            ResultSet studentResultSet = studentStatement.executeQuery();
            
            int studentCount = 0;
            StringBuilder studentDetails = new StringBuilder();

            while (studentResultSet.next()) {
                String studentId = studentResultSet.getString("Id");
                String studentName = studentResultSet.getString("studentName");
                int entranceYear = studentResultSet.getInt("EntranceYear");
                
                studentDetails.append("\n").append(studentId).append("|").append(studentName).append("|").append(entranceYear);
                studentCount++;
            }

            dormDetail += "|" + studentCount;
            System.out.println(dormDetail);
            System.out.println(studentDetails.toString().trim());

            studentStatement.close();
        }

        dormResultSet.close();
        dormStatement.close();
    }

    @Override
    public void prepareTables() {
        try {
            createTables();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}