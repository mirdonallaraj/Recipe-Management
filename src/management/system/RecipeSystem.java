package management.system;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

import com.mysql.cj.jdbc.MysqlDataSource;

import recipe.Ingredient;
import recipe.Recipe;
import recipe.RecipePreparationStep;
import user.Gender;
import user.User;

public class RecipeSystem {

	private static Scanner input = new Scanner(System.in);

	public static void menu(boolean loggedIn) {
		System.out.println("-------------------------------");
		if(loggedIn) {
			System.out.println("  1 - Create Recipe");
			System.out.println("  2 - List My Recipes");
			System.out.println("  3 - List All Recipes");
			System.out.println("  4 - Edit Recipe");
			System.out.println("  5 - Log Out");
		}
		else {
			System.out.println("  1 - Log In");
			System.out.println("  2 - Exit");
		}
		System.out.print(": ");
	}

	private static void showEditRecipeMenu() {
		System.out.println("Choose what you want to edit");
		System.out.println("  1 - Change title");
		System.out.println("  2 - Change description");
		System.out.println("  3 - Add ingredient");
		System.out.println("  4 - Add preparation step");
		System.out.println("  5 - Remove ingredient");
	}

	public static String nextLine() {
		String line = input.nextLine().trim();
		while(line.isEmpty()) {
			line = input.nextLine().trim();
		}
		return line;
	}
	
	public static Connection getConnection() {   
		try {
			MysqlDataSource mySqlDS = new MysqlDataSource(); 
			mySqlDS.setServerName("localhost");
			mySqlDS.setUser("root");
			mySqlDS.setPassword("root");
			mySqlDS.setDatabaseName("recipe_management");

			return mySqlDS.getConnection();
		} catch (SQLException e) { 
			e.printStackTrace();
		}
		return null;
	}

	public static ResultSet executeQuery(String query, Connection con) throws SQLException {
		return con.createStatement().executeQuery(query);
	}
	
	public static void executeUpdate(String query, Connection con) throws SQLException {
		con.createStatement().executeUpdate(query);
	}
	
	public static void createTablesIfNotExist(Connection mysqlConnection) {
		try {
			 
			executeUpdate("CREATE TABLE IF NOT EXISTS `user`("
						+ "`username` VARCHAR(64) NOT NULL ,"
						+ "`password` VARCHAR(32) NOT NULL, "
						+ "`name` VARCHAR(32) NOT NULL, "
						+ "`surname` VARCHAR(32) NOT NULL, "
						+ "`gender` VARCHAR(16), "
						+ "`birthdate` DATE, "
						+ "PRIMARY KEY (`username`)"
						+ ")", mysqlConnection);
			
			executeUpdate("CREATE TABLE IF NOT EXISTS `recipe`("
						+ "`username` VARCHAR(64) NOT NULL ,"
						+ "`original_recipe_id` INT NULL, "
						+ "`recipe_id` INT NOT NULL AUTO_INCREMENT, "
						+ "`title` VARCHAR(128) NOT NULL, "
						+ "`description` VARCHAR(2048) NOT NULL, "
						+ "PRIMARY KEY (`recipe_id`),"
						+ "UNIQUE INDEX `recipe_id_unique` (`recipe_id` ASC) VISIBLE,"
						+ "INDEX `username_idx` (`username` ASC) VISIBLE,"
						+ "CONSTRAINT `username`"
						+ " FOREIGN KEY (`username`)"
						+ " REFERENCES `user` (`username`)"
						+ " ON DELETE NO ACTION"
						+ " ON UPDATE NO ACTION,"
						+ "INDEX `original_recipe_id_idx` (`original_recipe_id` ASC) VISIBLE,"
						+ " CONSTRAINT `original_recipe_id`"
						+ "  FOREIGN KEY (`original_recipe_id`)"
						+ "  REFERENCES `recipe_management`.`recipe` (`recipe_id`)"
						+ "  ON DELETE NO ACTION"
						+ "  ON UPDATE NO ACTION"
						+ ")", mysqlConnection);
			
			
			executeUpdate("CREATE TABLE IF NOT EXISTS `recipe_ingredient`(" 
						+ "`recipe_ingredient_id` INT NOT NULL AUTO_INCREMENT, "
						+ "`name` VARCHAR(128) NOT NULL, "
						+ "`quantityPerPerson` DOUBLE(5, 2) NOT NULL, "
						+ "`recipe_id` INT NOT NULL, "
						+ "PRIMARY KEY (`recipe_ingredient_id`),"
						+ "UNIQUE INDEX `recipe_ingredient_id_unique` (`recipe_ingredient_id` ASC) VISIBLE,"
						+ "INDEX `recipe_ingredient_recipe_id_idx` (`recipe_id` ASC) VISIBLE,"
						+ "CONSTRAINT `recipe_ingredient_recipe_id`"
						+ " FOREIGN KEY (`recipe_id`)"
						+ " REFERENCES `recipe` (`recipe_id`)"
						+ " ON DELETE NO ACTION"
						+ " ON UPDATE NO ACTION"
						+ ")", mysqlConnection);

			executeUpdate("CREATE TABLE IF NOT EXISTS `recipe_preparation_step`(" 
						+ "`recipe_preparation_step_id` INT NOT NULL AUTO_INCREMENT, "
						+ "`number` INT NOT NULL, "
						+ "`title` VARCHAR(128) NOT NULL, "
						+ "`description` VARCHAR(2048) NOT NULL, " 
						+ "`recipe_id` INT NOT NULL, "
						+ "PRIMARY KEY (`recipe_preparation_step_id`),"
						+ "UNIQUE INDEX `recipe_preparation_step_id_unique` (`recipe_preparation_step_id` ASC) VISIBLE,"
						+ "INDEX `recipe_preparation_step_recipe_id_idx` (`recipe_id` ASC) VISIBLE,"
						+ "CONSTRAINT `recipe_preparation_step_recipe_id`"
						+ " FOREIGN KEY (`recipe_id`)"
						+ " REFERENCES `recipe` (`recipe_id`)"
						+ " ON DELETE NO ACTION"
						+ " ON UPDATE NO ACTION"
						+ ")", mysqlConnection);
		} catch (SQLException e) { 
			e.printStackTrace();
		}
	}

	public static void createRecipe(Connection mysqlConnection, 
									 ArrayList<User> users, 
									 String username) {
		Recipe newRecipe = readRecipe(mysqlConnection);
		try {
			if(newRecipe.getReference() != null){
				executeUpdate("INSERT INTO `recipe` "
						+ "       (`username`, `original_recipe_id`, `title`, `description`) "
						+ "VALUES ('" + username + "', " 
								+ "'" + newRecipe.getReference().getId() + "', "
								+ "'" + newRecipe.getTitle() + "', "
								+ "'" + newRecipe.getDescription() + "')", mysqlConnection);
			}
			else {
				executeUpdate("INSERT INTO `recipe` "
						+ "       (`username`, `title`, `description`) "
						+ "VALUES ('" + username + "', " 
								+ "'" + newRecipe.getTitle() + "', "
								+ "'" + newRecipe.getDescription() + "')", mysqlConnection);
			}
			newRecipe.setId(getLastInsertId(mysqlConnection, "recipe"));
			for(Ingredient ingredient : newRecipe.getIngredients()) {
				addIngredient(ingredient, newRecipe.getId(), mysqlConnection);
			}
			for(RecipePreparationStep step : newRecipe.getPreparationSteps()) {
				addPreparationStep(step, newRecipe.getId(), mysqlConnection);
			}
		} catch (SQLException e) { 
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}
	
	private static Recipe readRecipe(Connection mysqlConnection) {
		Recipe original = null;
		String title = null;
		String description = null;
		ArrayList<Ingredient> ingredients = new ArrayList<>();
		ArrayList<RecipePreparationStep> steps = new ArrayList<>(); 
		System.out.print("Reference id of the existing recipe (-1 if none): ");
		int originalId = input.nextInt();
		if(originalId != -1) {
			original = getRecipe(originalId, mysqlConnection);
		}
		System.out.print("Title: ");
		title = nextLine();
		System.out.print("Description: ");
		description = nextLine();
		System.out.print("Number of ingredients: ");
		int numberOfIngredients = input.nextInt();
		while(numberOfIngredients-- > 0) {
			ingredients.add(readIngredient());
		}
		System.out.print("Numer of preparation steps: ");
		int numberOfPreparationSteps = input.nextInt();
		while(numberOfPreparationSteps-- > 0) {
			steps.add(readPreparationStep());
		}
		return new Recipe(original , title, description, ingredients, steps);
	}

	private static RecipePreparationStep readPreparationStep() { 
		System.out.print("Step number: ");
		int number = input.nextInt();
		System.out.print("Title: ");
		String title = nextLine();
		System.out.print("Description: ");
		String description = nextLine();
		RecipePreparationStep step = new RecipePreparationStep(number, title, description);
		return step;
	}

	private static Ingredient readIngredient() { 
		System.out.print("Title: ");
		String title = nextLine();
		System.out.print("Quantity per person: ");
		double description = input.nextDouble();
		Ingredient ingredient = new Ingredient(title, description);
		return ingredient;
	}
	
	private static void addPreparationStep(RecipePreparationStep step, int id, Connection mysqlConnection) {
		try {
			executeUpdate("INSERT INTO `recipe_preparation_step`(`number`,"
					+ " `title`, `description`, `recipe_id`)"
					+ "VALUES('" + step.getNumber() + "',"
								+ "'" + step.getTitle() + "',"
								+ "'" + step.getDescription() + "',"
								+ "'" + id + "')", mysqlConnection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void addIngredient(Ingredient ingredient, int id, Connection mysqlConnection) {
			try {
				executeUpdate("INSERT INTO `recipe_ingredient`(`name`, `quantityPerPerson`, `recipe_id`)"
						+ "VALUES('" + ingredient.getName() + "',"
									+ "'" + ingredient.getQuantityPerPerson() + "',"
									+ "'" + id + "')", mysqlConnection);
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}

	private static int getLastInsertId(Connection mysqlConnection, String tableName) {
	    try (Statement statement = mysqlConnection.createStatement()) {
	        String query = "SELECT LAST_INSERT_ID() FROM `" + tableName + "`";
	        ResultSet rs = statement.executeQuery(query);
	        if (rs.next()) {
	            return rs.getInt(1);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return 0;
	}

	private static Recipe getRecipe(int recipeId, 
									Connection mysqlConnection) {
		try {
			ResultSet rs = executeQuery("SELECT * FROM `recipe` WHERE `recipe_id`=" + recipeId, mysqlConnection);
			if(rs.next()) {
				Integer originalRecipeId = rs.getInt(2);
				String title = rs.getString(4);
				String description = rs.getString(5);
				return new Recipe(recipeId,
								  getRecipe(originalRecipeId, mysqlConnection), 
								  title, 
								  description, 
								  getIngredients(recipeId, mysqlConnection), 
								  getPreparationSteps(recipeId, mysqlConnection));
			} 
		} catch (SQLException e) { 
			e.printStackTrace();
		} 
		return null;
	}

	private static ArrayList<RecipePreparationStep> getPreparationSteps(int recipeId, Connection mysqlConnection) {
		ArrayList<RecipePreparationStep> result = new ArrayList<>();
		try {
			ResultSet rs = executeQuery("SELECT * FROM `recipe_preparation_step` WHERE `recipe_id` =" + recipeId, mysqlConnection);
			while (rs.next()) {
				RecipePreparationStep step = new RecipePreparationStep(rs.getInt("recipe_preparation_step_id"),
																	   rs.getInt("number"),
																	   rs.getString("title"), 
																	   rs.getString("description"));
				result.add(step);
			}
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static ArrayList<Ingredient> getIngredients(int recipeId, Connection mysqlConnection) {
		ArrayList<Ingredient> result = new ArrayList<>();
		try {
			ResultSet rs = executeQuery("SELECT * FROM `recipe_ingredient` WHERE `recipe_id` =" + recipeId, mysqlConnection);
			while (rs.next()) {
				Ingredient ingredient = new Ingredient(rs.getInt("recipe_ingredient_id"), 
													   rs.getString("name"),
													   rs.getDouble("quantityPerPerson"));
				result.add(ingredient);
			}
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static void showRecipe(Recipe recipe, ArrayList<Ingredient> ingredients,
			ArrayList<RecipePreparationStep> steps) {
		System.out.println("Recipe: " + recipe.getTitle());
		System.out.println("Description: " + recipe.getDescription());
		System.out.println("\n\t Ingredients:");
		for(Ingredient ingredient : ingredients) {
			System.out.println(ingredient.getName() + "->" + ingredient.getQuantityPerPerson());
		}
		System.out.println("\n\t Steps to follow:");
		for (RecipePreparationStep step : steps) {
			System.out.println(step.getNumber() + ": " + step.getTitle());
			System.out.println(step.getDescription());
		}
	}

	public static void showRecipesIdAndTitles(Connection mysqlConnection, String username) {
		try {
			ResultSet rs = executeQuery("SELECT `recipe_id`, `title` FROM `recipe` WHERE `username` = '" + username + "'",
					mysqlConnection);
			while (rs.next()) {
				System.out.println("Id: " + rs.getInt("recipe_id") + " -> " + "Title: " + rs.getString("title"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void editRecipe(Connection mysqlConnection, int recipeId) {
			showEditRecipeMenu();
			String option = nextLine();
			switch (option) {
			case "1":
				changeTitle(recipeId, mysqlConnection);
				break;
			case "2":
				changeDescription(recipeId, mysqlConnection);
				break;
			case "3":
				Ingredient ingredient = readIngredient();
				addIngredient(ingredient, recipeId, mysqlConnection);
				break;
			case "4":
				RecipePreparationStep step = readPreparationStep();
				addPreparationStep(step, recipeId, mysqlConnection);
				break;
			case "5":
				removeIngredient(recipeId, mysqlConnection);
				break;
			}

	}

	private static void removeIngredient(int recipeId, Connection mysqlConnection) {
		System.out.print("Id of ingredient to remove: ");
		int ingredientId = input.nextInt();
		try {
			executeUpdate("DELETE FROM `recipe_ingredient` WHERE `recipe_ingredient_id` = '" + ingredientId + "'"
					+ "AND `recipe_id` = '" + recipeId + "'", mysqlConnection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void changeDescription(int recipeId, Connection mysqlConnection) {
		System.out.print("Description: ");
		String description = nextLine();
		try {
			executeUpdate("UPDATE `recipe` SET `description` = '" + description + "' WHERE `recipe_id` = " + recipeId, mysqlConnection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void changeTitle(int recipeId, Connection mysqlConnection) {
		System.out.print("Title: ");
		String title = nextLine();
		try {
			executeUpdate("UPDATE `recipe` SET `title` = '" + title + "' WHERE  `recipe_id` = " + recipeId, mysqlConnection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void listAllRecipes(Connection mysqlConnection) {
		try {
			ResultSet rs = executeQuery("SELECT `recipe_id` FROM `recipe`", mysqlConnection);
			if (!rs.next()) {
				System.out.println("There are no recipes yet!");
			}
			while (rs.next()) {
				showRecipe(getRecipe(rs.getInt("recipe_id"), mysqlConnection),
						getIngredients(rs.getInt("recipe_id"), mysqlConnection),
						getPreparationSteps(rs.getInt("recipe_id"), mysqlConnection));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void listMyRecipes(Connection mysqlConnection, String username) {
			try {
				ResultSet rs = executeQuery("SELECT `recipe_id` FROM `recipe` WHERE `username` = '" + username + "'", mysqlConnection);
				if (!rs.next()) {
					System.out.println("There are no recipes yet!");
				}
				while (rs.next()) {
					showRecipe(getRecipe(rs.getInt("recipe_id"), mysqlConnection), 
							   getIngredients(rs.getInt("recipe_id"), mysqlConnection), 
							   getPreparationSteps(rs.getInt("recipe_id"), mysqlConnection));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}

	public static boolean existUser(Connection mysqlConnection, String username, String password) {
		try {
			ResultSet rs = executeQuery("SELECT * FROM `user` WHERE `username`='" + username + "' AND `password`='" + password +"'", mysqlConnection);
			return rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
  
	public static ArrayList<User> getUsers(Connection mysqlConnection) {
		ResultSet rs;
		try {
			rs = executeQuery("SELECT * FROM `user`", mysqlConnection);
			ArrayList<User> users = new ArrayList<>();
			if (rs.next()) {
				String g = rs.getString("gender");
				Gender gender;
				if (g.equalsIgnoreCase("male")) {
					gender = Gender.MALE;
				} else if (g.equalsIgnoreCase("female")) {
					gender = Gender.FEMALE;
				} else {
					gender = Gender.OTHER;
				}
				Date date = rs.getDate("birthdate");
				User user = new User(rs.getString("username"), rs.getString("name"), rs.getString("surname"),
						rs.getString("password"), gender, date);
				users.add(user);
			}
			return users;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) throws SQLException {
		
		boolean exit = false;
		Connection mysqlConnection = getConnection(); 
		
		createTablesIfNotExist(mysqlConnection);
		
		boolean loggedIn = false;

		ArrayList<User> users = getUsers(mysqlConnection); 

		String username = null; 
		
		while(!exit) {
			menu(loggedIn);
			String option = nextLine();
			if(loggedIn) {
				switch(option) {
					case "1":
						createRecipe(mysqlConnection,
									 users,
									 username);
					break;
					case "2":
						listMyRecipes(mysqlConnection, username);
					break;
					case "3":
						listAllRecipes(mysqlConnection);
					break;
					case "4":
						System.out.println("Write the id of recipe you want to modify: ");
						showRecipesIdAndTitles(mysqlConnection, username);
						int recipeId = input.nextInt();
						editRecipe(mysqlConnection, recipeId);
					break;
					case "5":
						exit = true;
					break;
					default:
				}
			}
			else {
				switch(option) {
					case "1":
						System.out.print("Username: ");
						username = input.nextLine().trim();
						System.out.print("Password: ");
						String password = input.nextLine().trim();
						if(!existUser(mysqlConnection, username, password)) {
							System.out.println("Invalid credintials!");
						}
						else {
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
