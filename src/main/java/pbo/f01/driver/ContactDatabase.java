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

    // static final String USER = "root";
    // static final String PASS = "dodohyun/135";

    public ContactDatabase(String url) throws SQLException {
        super(url);
    }

    protected void createTables() throws SQLException {
    
        String dropSQLs[] = {
            "DROP TABLE IF EXISTS Student",
            "DROP TABLE IF EXISTS Dorm",
        };
    
        String Dorm = "CREATE TABLE IF NOT EXISTS Dorm (" +
            "Name VARCHAR(255) NOT NULL PRIMARY KEY," +
            "Gender TEXT NOT NULL," +
            "Capacity INTEGER NOT NULL" +
            ")";
    
        String Student = "CREATE TABLE IF NOT EXISTS Student (" +
            "Id VARCHAR(30) PRIMARY KEY," + 
            "Name VARCHAR(255) NOT NULL," +
            "Gender TEXT NOT NULL," +
            "EntranceYear INTEGER NOT NULL," +
            "DormName VARCHAR(255)," +
            "FOREIGN KEY (DormName) REFERENCES Dorm(Name)" +
            ")";
    
        Statement statement = this.getConnection().createStatement(); //statement untuk membuat table
        //statement untuk mendrop table
        for (String sql : dropSQLs) {
            statement.execute(sql);
        }
    
        statement.execute(Dorm); // Create Dorm table first
        statement.execute(Student); // Then create Student table
    
        statement.close();
    }

    //student-add#<id>#<name>#<year>#<gender>
    //METHOD STUDENT ADD
    public void addStudent(String Id, String Name, int EntranceYear, String Gender ) throws SQLException {
        String sql = "INSERT INTO Student (Id, Name, EntranceYear, Gender ) VALUES (?, ?, ?, ?)"; //query untuk menambahkan data ke table
        PreparedStatement statement = this.getConnection().prepareStatement(sql); //statement untuk menambahkan data ke table
        statement.setString(1, Id);
        statement.setString(2, Name);
        statement.setInt(3, EntranceYear);
        statement.setString(4, Gender);
        statement.executeUpdate();
        statement.close();
    }

    //dorm-add#<name>#<capacity>#<gender>
    //METHOD DORM ADD
    public void addDorm(String Name, int Capacity , String Gender ) throws SQLException {
        String sql = "INSERT INTO Dorm (Name, Capacity, Gender) VALUES (?, ?, ?)"; //query untuk menambahkan data ke table
        PreparedStatement statement = this.getConnection().prepareStatement(sql); //statement untuk menambahkan data ke table
        statement.setString(1, Name);
        statement.setInt(2, Capacity);
        statement.setString(3, Gender);
        statement.executeUpdate();
        statement.close();
    }

    //assign#<student-id>#<dorm-name>
    //METHOD ASSIGN
    public void assign(String Id, String DormName) throws SQLException {
        String sql = "UPDATE Student SET DormName = ? WHERE Id = ?";
        PreparedStatement statement = this.getConnection().prepareStatement(sql);
        statement.setString(1, Id);
        statement.setString(2, DormName);
        statement.executeUpdate();
        statement.close();
    }

    //METHOD DISPLAY ALL
    public void displayAll() throws SQLException {
        String sql = "SELECT * FROM Dorm ORDER BY Name ASC";
        Statement statement = this.getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
    
        while (resultSet.next()) {
            String dormName = resultSet.getString("Name");
            String gender = resultSet.getString("Gender");
            int capacity = resultSet.getInt("Capacity");
    
            String studentSql = "SELECT * FROM Student WHERE DormName = ? ORDER BY Name ASC";
            PreparedStatement studentStatement = this.getConnection().prepareStatement(studentSql);
            studentStatement.setString(1, dormName);
            ResultSet studentResultSet = studentStatement.executeQuery();
    
            List<String> students = new ArrayList<>();
            while (studentResultSet.next()) {
                String studentId = studentResultSet.getString("Id");
                String studentName = studentResultSet.getString("Name");
                int entranceYear = studentResultSet.getInt("EntranceYear");
    
                students.add(studentId + "|" + studentName + "|" + entranceYear);
            }
    
            System.out.println(dormName + "|" + gender + "|" + capacity + "|" + students.size());
            for (String student : students) {
                System.out.println(student);
            }
    
            studentResultSet.close();
            studentStatement.close();
        }
    
        resultSet.close();
        statement.close();
    }
    





    @Override
    public void prepareTables() {
        // Implement the prepareTables() method here
    }
}