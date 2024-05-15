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
            "Name VARCHAR(30) NOT NULL PRIMARY KEY," +
            "Gender TEXT NOT NULL," +
            "Capacity INTEGER NOT NULL" +
            ")";

        String Student = "CREATE TABLE IF NOT EXISTS Student (" +
            "Id VARCHAR(30) PRIMARY KEY," + 
            "Name VARCHAR(30) NOT NULL," +
            "Gender TEXT NOT NULL," +
            "EntranceYear INTEGER NOT NULL," +
            "DormName VARCHAR(30)" + 
            ")";

        Statement statement = this.getConnection().createStatement();
        for (String sql : dropSQLs) {
            statement.execute(sql);
        }

        statement.execute(Dorm);
        statement.execute(Student);

        statement.close();
    }

    public void addStudent(String Id, String Name, int EntranceYear, String Gender) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM Student WHERE Id = ?";
        PreparedStatement checkStatement = this.getConnection().prepareStatement(checkSql);
        checkStatement.setString(1, Id);
        ResultSet resultSet = checkStatement.executeQuery();
        resultSet.next();
        if (resultSet.getInt(1) > 0) {
            checkStatement.close();
            return; 
        }
        checkStatement.close();

        String sql = "INSERT INTO Student (Id, Name, EntranceYear, Gender) VALUES (?, ?, ?, ?)";
        PreparedStatement statement = this.getConnection().prepareStatement(sql);
        statement.setString(1, Id);
        statement.setString(2, Name);
        statement.setInt(3, EntranceYear);
        statement.setString(4, Gender);
        statement.executeUpdate();
        statement.close();
    }

    public void addDorm(String Name, int Capacity, String Gender) throws SQLException {
        String sql = "INSERT INTO Dorm (Name, Capacity, Gender) VALUES (?, ?, ?)";
        PreparedStatement statement = this.getConnection().prepareStatement(sql);
        statement.setString(1, Name);
        statement.setInt(2, Capacity);
        statement.setString(3, Gender);
        statement.executeUpdate();
        statement.close();
    }

    public void assign(String Id, String DormName) throws SQLException {
        //UNTUK CEK GENDER STUDENT
        String studentGenderSql = "SELECT Gender FROM Student WHERE Id = ?";
        PreparedStatement studentGenderStatement = this.getConnection().prepareStatement(studentGenderSql);
        studentGenderStatement.setString(1, Id);
        ResultSet studentGenderResultSet = studentGenderStatement.executeQuery();
        if (!studentGenderResultSet.next()) {
            studentGenderStatement.close();
            return; 
        }
        String studentGender = studentGenderResultSet.getString("Gender");
        studentGenderStatement.close();

        //UNTUK CEK GENDER DORM SERTA CAPACITY
        String dormSql = "SELECT Gender, Capacity, (SELECT COUNT(*) FROM Student WHERE DormName = ?) AS Occupied FROM Dorm WHERE Name = ?";
        PreparedStatement dormStatement = this.getConnection().prepareStatement(dormSql);
        dormStatement.setString(1, DormName);
        dormStatement.setString(2, DormName);
        ResultSet dormResultSet = dormStatement.executeQuery();
        if (!dormResultSet.next()) {
            dormStatement.close();
            return;
        }
        String dormGender = dormResultSet.getString("Gender");
        int capacity = dormResultSet.getInt("Capacity");
        int occupied = dormResultSet.getInt("Occupied");
        dormStatement.close();

        //UNTUK CEK GENDER APAKAH SESUAI DENGAN DORM YANG MEMILIKI KAPASITAS
        if (!studentGender.equals(dormGender) || occupied >= capacity) {
            return; //kalau ga sesuai dan full dormnya, ga boleh di assign lagi
        }

        //UNTUK MEMASUKKAN STUDENT KE DORMNYA
        String sql = "UPDATE Student SET DormName = ? WHERE Id = ?";
        PreparedStatement statement = this.getConnection().prepareStatement(sql);
        statement.setString(1, DormName);
        statement.setString(2, Id);
        statement.executeUpdate();
        statement.close();
    }

    public void displayAll() throws SQLException {
        String dormSQL = "SELECT * FROM Dorm ORDER BY Name ASC";
        String studentSQL = "SELECT * FROM Student WHERE DormName = ? ORDER BY Name ASC";

        Statement dormStatement = this.getConnection().createStatement();
        ResultSet dormResultSet = dormStatement.executeQuery(dormSQL);

        while (dormResultSet.next()) {
            String dormName = dormResultSet.getString("Name");
            String dormGender = dormResultSet.getString("Gender");
            int dormCapacity = dormResultSet.getInt("Capacity");

            String dormDetail = dormName + "|" + dormGender + "|" + dormCapacity;

            PreparedStatement studentStatement = this.getConnection().prepareStatement(studentSQL);
            studentStatement.setString(1, dormName);
            ResultSet studentResultSet = studentStatement.executeQuery();
            
            int studentCount = 0;
            StringBuilder studentDetails = new StringBuilder();

            while (studentResultSet.next()) {
                String studentId = studentResultSet.getString("Id");
                String studentName = studentResultSet.getString("Name");
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