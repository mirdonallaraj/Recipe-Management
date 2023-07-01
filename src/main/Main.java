package main;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Scanner;

import management.system.RecipeSystem;
import user.User;

public class Main {
	
	public static void main(String[] args) {

		Scanner input = new Scanner(System.in);

		boolean exit = false;
		Connection mysqlConnection = RecipeSystem.getConnection();

		RecipeSystem.createTablesIfNotExist(mysqlConnection);

		boolean loggedIn = false;

		ArrayList<User> users = RecipeSystem.getUsers(mysqlConnection);

		String username = null;
		String password = null;

		while (!exit) {
			RecipeSystem.menu(loggedIn);
			String option = RecipeSystem.nextLine();
			if (loggedIn) {
				switch (option) {
				case "1":
					RecipeSystem.createRecipe(mysqlConnection, users, username);
					break;
				case "2":
					RecipeSystem.listMyRecipes(mysqlConnection, username);
					break;
				case "3":
					RecipeSystem.listAllRecipes(mysqlConnection);
					break;
				case "4":
					System.out.println("Write the id of recipe you want to modify: ");
					RecipeSystem.showRecipesIdAndTitles(mysqlConnection, username);
					int recipeId = input.nextInt();
					RecipeSystem.editRecipe(mysqlConnection, recipeId);
					break;
				case "5":
					exit = true;
					break;
				default:
				}
			} else {
				switch (option) {
				case "1":
					System.out.print("Username: ");
					username = input.nextLine().trim();
					System.out.print("Password: ");
					password = input.nextLine().trim();
					if (!RecipeSystem.existUser(mysqlConnection, username, password)) {
						System.out.println("Invalid credintials!");
					} else {
						loggedIn = true;
					}
					break;
				case "2":
					exit = true;
					break;
				default:
				}
			}
		}
		input.close();
	}
}
