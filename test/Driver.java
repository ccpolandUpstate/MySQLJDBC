package test.test;

import java.io.*;
import java.util.Scanner;
import java.sql.*;

public class Driver {


    public static void main(String[] args) throws SQLException, IOException
	{
    	//connect to database server	
    	Connection conn;
		try
		{
			String url = "jdbc:mysql://localhost/test";  //test is the database name
			String user = "root"; //username
			String password = "t123456"; //root password

			conn = DriverManager.getConnection(url, user, password);
		    //print menu
			Scanner scan = new Scanner(System.in);
			SQLCommands SQL = new SQLCommands();
			int command;
			String input;
	        boolean keepGoing = true;
	        while (keepGoing)//
	        {
	            printmenu();
	            input = scan.nextLine();
	            command = Integer.parseInt(input);

				switch (command) {
					case 1 -> SQL.add_course(conn, scan);
					case 2 -> SQL.delete_course(conn, scan);
					case 3 -> SQL.show_courses(conn);
					case 4 -> SQL.add_student(conn, scan);
					case 5 -> SQL.delete_student(conn, scan);
					case 6 -> SQL.register_course(conn, scan);
					case 7 -> SQL.check_registration(conn, scan);
					case 8 -> SQL.upload_grades(conn, scan);
					case 0 -> {
						System.out.println("The Session has been ended, Thank you!");
						keepGoing = false;
						conn.close(); //close database connection
					}
				}
		    }
		}
		catch (SQLException ex)
		{
			System.out.println("An error occurred when connecting to the database server.");
			ex.printStackTrace();
		}				
  }
    public static void printmenu()
    {
		System.out.println();
		System.out.println("*********************************************************************");
		System.out.println("");
		System.out.println("***                                                               ***");
		System.out.println("");
		System.out.println("***              Welcome to Online Registration System            ***");
		System.out.println("");
		System.out.println("***                                                               ***");
		System.out.println("");
		System.out.println("*********************************************************************");
		System.out.println("1. Add a course");
		System.out.println("2. Delete a course");
		System.out.println("3. Show all course");
		System.out.println("4. Add a student");
		System.out.println("5. Delete a student");
		System.out.println("6. Register a course");
		System.out.println("7. Check for registration");
		System.out.println("8. Upload grades");

		System.out.println("0. Quit ");
		System.out.println();

		System.out.println("Please choose an option");
    }
}


