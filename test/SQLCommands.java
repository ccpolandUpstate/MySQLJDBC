package test.test;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class SQLCommands {


	// CONSTRAINTS:
	//•	Students cannot register for a class twice for the same year and semester. // Active
	//•	If a course is deleted, all registrations of that course need to be deleted too. (delete from registration table first, then delete it from the course table.)  // Active
	//•	If a student is deleted from the system, all registrations of that student need to be deleted too. (delete from registration table first, then delete it from the student table.) // Active
	// END CONSTRAINTS
	
	public void add_course(Connection conn, Scanner keyboard) throws SQLException, IOException
	{
		Statement st = conn.createStatement();
		System.out.println("Add a course");
		System.out.println("Please input course code: ");
		String courseCode = keyboard.nextLine().toUpperCase().trim();
		System.out.println("Please input course title: ");
		String courseTitle = keyboard.nextLine().trim();
		String query = "select code from Course Where code = '" + courseCode + "'";
		ResultSet rs = st.executeQuery(query);
		if (rs.next()) 
		{
			System.out.println("Course already exists");
			return;
		}
		query = "Insert into Course (code, title) values ('" + courseCode + "', '" + courseTitle + "')";
		try 
		{
			st.executeUpdate(query);
		} 
		catch (SQLException e) 
		{
			System.out.println("Message: " + e.getMessage());
		
		}
		rs.close();
		st.close();
		System.out.println("A new course is added.");
	}
	public void delete_course(Connection conn, Scanner keyboard) throws SQLException, IOException {
		Statement st = conn.createStatement();
		System.out.println("Delete a course");
		System.out.println("Please enter the course code");
		String courseCode = keyboard.nextLine().toUpperCase().trim();
		String query = "select code from Course Where code = '" + courseCode + "'";
		ResultSet rs = st.executeQuery(query);
		if (rs.next()) { // If course is found....

			// Calls to remove all students from the course
			remove_from_course(conn, courseCode);

			query = "DELETE FROM Course Where code = '" + courseCode + "'";
			try {
				st.execute(query);
			}
			catch (SQLException e)
			{
				System.out.println("Message: " + e.getMessage());
			}
			System.out.println("The course has successfully been deleted.");
		}
		else { // If code doesn't exist...
			System.out.println("The code you entered does not exist");
			return;
		}
		rs.close();
		st.close();
	}
	public void add_student(Connection conn, Scanner keyboard) throws SQLException, IOException {
		Statement st = conn.createStatement();

		// Student(ssn, name, address, major)
		System.out.println("Add a student");
		System.out.println("Enter their SSN: ");
		int SSN = Integer.parseInt(keyboard.nextLine());
		System.out.println("Enter their name: ");
		String studentName = keyboard.nextLine();
		System.out.println("Enter their address: ");
		String studentAddress = keyboard.nextLine();
		System.out.println("Enter their major: ");
		String studentMajor = keyboard.nextLine();

		// Students cannot have same SSN, although the rest can be the same.
		String query = "SELECT ssn from Student WHERE ssn = '" + SSN + "'";
		ResultSet rs = st.executeQuery(query);
		if(rs.next()) { // If that SSN exists.. end.
			System.out.println("A Student already exists with that SSN");
			return;
		}
		else { // If SSN doesn't exist… Add Student
			query = "INSERT INTO Student (ssn, name, address, major) values ('" + SSN + "', '" + studentName + "', '" + studentAddress + "', '" + studentMajor + "')";
			try{
				st.execute(query);
			}
			catch (SQLException e) {
				System.out.println("Message: " + e.getMessage());
			}
			System.out.println("Student: " + studentName + " has been successfully added.");
		}
		rs.close();
		st.close();
	}
	public void delete_student(Connection conn, Scanner keyboard) throws SQLException, IOException {
		Statement st = conn.createStatement();

		System.out.println("Delete Student");
		System.out.println("Enter the student's SSN");
		int studentSSN = Integer.parseInt(keyboard.nextLine());
		String query = "SELECT ssn from Student WHERE ssn = '" + studentSSN + "'";
		ResultSet rs = st.executeQuery(query);
		if(rs.next()) { // If a student with that SSN exists, delete it

			// Before we delete student
			// We remove student from all registered courses.
			remove_from_course(conn, studentSSN); // Calls the method to remove from courses

			query = "DELETE from student where SSN = '" + studentSSN + "'";
			try {
				st.execute(query);
			}
			catch (SQLException e) {
				System.out.println("Message: " + e.getMessage());
			}
			System.out.println("Student was successfully removed.");
		}
		else {
			System.out.println("No student with that SSN exists.");
			return;
		}
		rs.close();
		st.close();

	}
	// Overloaded the methods.
	public void remove_from_course(Connection conn, String courseCode) throws SQLException, IOException {
		Statement st = conn.createStatement();
		String query = "select code from Registered Where code = '" + courseCode + "'";
		ResultSet rs = st.executeQuery(query);
		// If the course exists...
		if(rs.next()) {
			// Delete the course
			query = "DELETE FROM registered Where Code = '" + courseCode + "'";
			try {
				st.execute(query);
			}
			catch (SQLException e) {
				System.out.println("Message: " + e.getMessage());
			}
			System.out.println("Successfully removed all students from: " + courseCode + ".");
		}
		else {
			System.out.println("No students were registered for this course.");
			return;
		}
		rs.close();
		st.close();
	}
	public void remove_from_course(Connection conn, int ssn) throws SQLException, IOException {
		Statement st = conn.createStatement();
		String query = "select ssn from Registered Where ssn = '" + ssn + "'";
		ResultSet rs = st.executeQuery(query);
		// If the student is registered for any classes...
		if(rs.next()) {
			// Remove that student from registered.
			query = "DELETE FROM registered where ssn = '" + ssn + "'";
			try {
				st.execute(query);
			}
			catch (SQLException e) {
				System.out.println("Message: " + e.getMessage());
			}
			System.out.println("Student has been removed from all registered courses.");
		}
		else {
			System.out.println("Student wasn't registered for any courses.");
			return;
		}
		rs.close();
		st.close();
	}

	public void register_course(Connection conn, Scanner keyboard) throws SQLException, IOException {
		// Registered(ssn, code, year, semester, grade) , exclude grade in this case.
		Statement st = conn.createStatement();

		System.out.println("Registering a Course");
		System.out.println("Student's SSN: ");
		String studentSSN = keyboard.nextLine();
		System.out.println("Enter the course code: ");
		String courseCode = keyboard.nextLine();
		System.out.println("Which school year?");
		int schoolYear = Integer.parseInt(keyboard.nextLine().trim());
		System.out.println("Which semester? (Spring / Fall");
		String semester = keyboard.nextLine();
		semester = semester.substring(0,1).toUpperCase() + semester.substring(1).toLowerCase();

		// Must check if SSN exists… Then must check there is no duplicate registration for year/semester.
		String query = "SELECT ssn from student where ssn = '" + studentSSN + "'";
		ResultSet rs = st.executeQuery(query);
		if(rs.next()) {
			// If the SSN does exist... Check that that SSN doesn't
			// have the same course code for inputted year & semester.
			query = "SELECT ssn from registered where ssn = '" + studentSSN + "'" +
					"AND code = '" + courseCode + "'" +
					"AND year = '" + schoolYear + "'" +
					"AND semester = '" + semester + "'";
			rs = st.executeQuery(query);
			if(rs.next()) {
				// IF it does contain all of these…
				// Then tell them that they cannot register for that course twice.
				System.out.println("Unable to register for course.");
				System.out.println("Student already registered for given year & semester.");
				return;
			}
			else {
				query = "INSERT INTO Registered(ssn, code, year, semester) values ('" +
						"" + studentSSN + "', '" +
						"" + courseCode + "', '" +
						"" + schoolYear + "', '" +
						"" + semester + "')";
				try {
					st.execute(query);
				}
				catch (SQLException e) {
					System.out.println("Message: " + e.getMessage());
				}
				System.out.println("Student successfully registered for: " + courseCode + ".");
			}
		}
		else {
			System.out.println("There are no students with that SSN.");
		}
		st.close();
		rs.close();
	}
	public void check_registration(Connection conn, Scanner keyboard) throws SQLException, IOException {
		// Registered(ssn, code, year, semester, grade) , exclude grade in this case.
		// Show course code + year + semester
		Statement st = conn.createStatement();
		System.out.println("Check Registration");
		System.out.println("Enter student's SSN");
		String studentSSN = keyboard.nextLine();

		String query = "SELECT * from registered where ssn = '" + studentSSN + "'";
		try {

			ResultSet rs = st.executeQuery(query);
			while (rs.next()) {
				// While the SSN is found...
				// List course code + year + semester
				String code = rs.getString("code");
				int year = Integer.parseInt(rs.getString("year"));
				String semester = rs.getString("semester");
				System.out.println(
						"Code: " + code
								+ " Year: " + year
								+ " Semester: " + semester
								+ " ");
			}
			rs.close();
			st.close();
		}
		catch (SQLException e) {
			System.out.println("Message: " + e.getMessage());
			}
		}
	public void upload_grades(Connection conn, Scanner keyboard) throws SQLException, IOException {
		// Registered(ssn, code, year, semester, grade)
		// User Supplies code, year, semester
		// Prompt for every student in the course
		Statement st = conn.createStatement();

		// List to store every ssn within the course
		ArrayList<String> listOfSSN = new ArrayList<>(); // Technically should be <Integer> list

		System.out.println("Upload Grades");
		System.out.println("Please enter course code: ");
		String courseCode = keyboard.nextLine();
		System.out.println("Please enter course year: ");
		int courseYear = Integer.parseInt(keyboard.nextLine().trim());
		System.out.println("Please enter course semester: ");
		String courseSemester = keyboard.nextLine();
		courseSemester = courseSemester.substring(0,1).toUpperCase() + courseSemester.substring(1).toLowerCase();

		String query = "SELECT * from registered where code = '" + courseCode + "'" +
				"AND year = '" + courseYear + "'" +
				"AND semester = '" + courseSemester + "'";
		ResultSet rs = st.executeQuery(query);

		// If student found within course... append ssn to list.
		while (rs.next()) {
			String ssn = rs.getString("ssn");
			listOfSSN.add(ssn);
		}
		// Loop that iterates through every student found within the course
		// Updates their grade based on user inputted value char(1)
		for (String ssnStudent : listOfSSN) {
			System.out.println("Enter a letter grade for student with SSN: " + ssnStudent);
			String grade = keyboard.nextLine();
			if(grade.length() > 1) {
				System.out.println("Invalid Grade Input");
				return;
			}
			query = "UPDATE registered SET grade = '" + grade + "' WHERE ssn = '" + ssnStudent + "'" +
					"AND code = '" + courseCode + "'" +
					"AND year = '" + courseYear + "'" +
					"AND semester = '" + courseSemester + "'";
			try {
				// Sends query to DB
				st.execute(query);
			}
			catch (SQLException e) {
				System.out.println("Message: " + e.getMessage());
			}
		}
		System.out.println("Grades uploaded successfully.");
		st.close();
		rs.close();
	}
	public void show_courses(Connection conn) throws SQLException, IOException
	{
		try
		(
			Statement st = conn.createStatement();

		)
		{
			String query = "select * from Course";
			ResultSet rs = st.executeQuery(query);
			while (rs.next()) 
			{
				 String code = rs.getString("code");
				 String title = rs.getString("title");
			     System.out.println("Code: " + code + "\tTitle: " + title);
			}
			rs.close();
			st.close();
		}
		catch (SQLException e) 
		{
			System.out.println("Message: " + e.getMessage());
		
		}
	}
	
}
