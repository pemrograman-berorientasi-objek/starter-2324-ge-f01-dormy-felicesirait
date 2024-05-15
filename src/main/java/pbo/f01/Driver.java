package pbo.f01;

/**
 * 12S22006 - Felice Anggie Sirait
 * 12S22018 - Jesica A M Siburian
 */

import java.sql.SQLException;
import java.util.Scanner;
import java.sql.ResultSet;
import java.sql.Statement;

import pbo.f01.driver.*;

public class Driver {
    public static void main(String[] args) {
        
        try {
            ContactDatabase database = new ContactDatabase("jdbc:h2:./db/dormy");

            Scanner input = new Scanner(System.in);
            String str;

            while(input.hasNextLine()){
                str = input.nextLine();
                if(str.equals("---")){
                    break;                    
                }

                String[] token = str.split("#");
                if (token[0].equals("student-add")){
                    database.addStudent(token[1], token[2], Integer.parseInt(token[3]), token[4] );
                }
                else if (token[0].equals("dorm-add")){
                    database.addDorm(token[1], Integer.parseInt(token[2]), token[3] );
                }
                else if (token[0].equals("assign")){
                    database.assign(token[1], token[2]);
                }
                else if (token[0].equals("display-all")){
                    database.displayAll();
                }      
            }
            database.shutdown();

        } catch (SQLException sqle) {
            System.out.println(sqle.getMessage());
        }
        
    }
}