package user;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import com.mysql.cj.jdbc.MysqlDataSource;

public class RegisterUser {

	private static Scanner input = new Scanner(System.in);
	
	public static ResultSet executeQuery(String query) throws SQLException {
		return getConnection().createStatement().executeQuery(query);
	}
	
	public static void executeUpdate(String query) throws SQLException {
		getConnection().createStatement().executeUpdate(query);
	}
	
	public static Connection getConnection() throws SQLException {
		MysqlDataSource mySqlDS = new MysqlDataSource();
		
		mySqlDS.setServerName("localhost");
		mySqlDS.setUser("root");
		mySqlDS.setPassword("root");
		mySqlDS.setDatabaseName("recipe_management");
		
		return mySqlDS.getConnection();
	}
	
	@SuppressWarnings("deprecation")
	private static void registerUsers(int n) throws SQLException {
		for (int i = 0; i < n; i++) {

			System.out.print("Name : ");
			String name = input.nextLine();

			System.out.print("Surname: ");
			String surname = input.nextLine();

			System.out.print("Username: ");
			String username = input.nextLine();

			System.out.print("Gender(m/f/o): ");
			String g = input.nextLine();
			Gender gender;
			if (g.trim().equalsIgnoreCase("m")) {
				gender = Gender.MALE;
			} else if (g.trim().equalsIgnoreCase("f")) {
				gender = Gender.FEMALE;
			} else {
				gender = Gender.OTHER;
			}

			System.out.print("Birthday(YYYY/MM/DD): ");
			String b = input.nextLine();
			String[] birthdate = b.split("/");
			Date date = new Date(Integer.parseInt(birthdate[0]), 
								Integer.parseInt(birthdate[1]) - 1, 
								Integer.parseInt(birthdate[2]));

			System.out.print("Password: ");
			String password = input.nextLine();
			System.out.println();
			
			User user = new User(username, name, surname, password, gender, date);
			if (!existsUser(username)) {
				executeUpdate("INSERT INTO `user` (`username`,`name`, `surname`, `password`, `gender`, `birthdate` ) "
							+ "VALUES('" + user.getUsername() +  "',"
								   + "'" + user.getName() +  "',"
								   + "'" + user.getSurname() +  "',"
								   + "'" + user.getPassword() + "',"
								   + "'" + user.getGender().getName() + "',"
								   + "'" + user.getBirthdate() + "')");
			}
		}
	}

	private static boolean existsUser(String username) {
		ResultSet rs;
		try {
			rs = executeQuery("SELECT COUNT(*) AS cnt " + "FROM `user` " + "WHERE `user`.`username` = '" + username + "'");
			if (rs.next()) {
				int userCount = rs.getInt("cnt");
				if (userCount == 1) {
					return true;
				}else {
					return false;
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void main(String[] args) throws SQLException {

		System.out.print("Write the number of users you want to register: ");

		int n = input.nextInt();
		input.nextLine();

		registerUsers(n);
		
		input.close();
	}

}
