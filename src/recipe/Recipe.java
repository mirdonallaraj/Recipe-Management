package recipe;

import java.util.ArrayList;
import java.util.Objects;

public class Recipe {
	
	private Recipe reference;
	private int id;
	private String title;
	private String description;
	private ArrayList<Ingredient> ingredients;
	private ArrayList<RecipePreparationStep> steps;

	public Recipe(Recipe reference, String title) {
		this(reference, title, null, null, null);
	}

	public Recipe(String description, Recipe reference) {
		this(reference, null, description, null, null);
	}

	public Recipe(String title, String description, ArrayList<Ingredient> ingredietns,
			ArrayList<RecipePreparationStep> steps) {
		this(null, title, description, ingredietns, steps);
	}
 
	public Recipe(Recipe reference, 
				  String title, 
				  String description, 
				  ArrayList<Ingredient> ingredietns,
				  ArrayList<RecipePreparationStep> steps) {
		this(0, reference, title, description, ingredietns, steps);
	}
	
	public Recipe(int id,
				  Recipe reference, 
				  String title, 
				  String description, 
				  ArrayList<Ingredient> ingredietns,
				  ArrayList<RecipePreparationStep> steps) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.ingredients = ingredietns;
		this.steps = steps; 
		this.reference = reference; 
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Recipe getReference() {
		return reference;
	}

	public ArrayList<Ingredient> getIngredietns() {
		return ingredients;
	}

	public ArrayList<Ingredient> getIngredients() {
		return ingredients;
	}

	public ArrayList<RecipePreparationStep> getPreparationSteps() {
		return steps;
	}

	public int getId() {
		return id;
	}

	public void removeIngredient(Ingredient ingredient) {
		if (existsIngredient(ingredient)) {
			ingredients.remove(ingredient);
		}
	}

	private boolean existsIngredient(Ingredient ingredient) {
		if (ingredient == null) {
			return false;
		}
		for (Ingredient e : ingredients) {
			if (e.equals(ingredient)) {
				return true;
			}
		}
		return false;
	}

	public void addIngredient(Ingredient ingredient) {
		if (ingredient != null) {
			ingredients.add(ingredient);
		}
	}

	public void addIngredients(Ingredient... i) {
		if (i != null) {
			for (Ingredient e : i) {
				if (e != null) {
					ingredients.add(e);
				}
			}
		}
	}

	public void addStep(RecipePreparationStep step) {
		if (step != null) {
			steps.add(step);
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(description, id, title);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Recipe other = (Recipe) obj;
		return Objects.equals(description, other.description) && id == other.id && Objects.equals(title, other.title);
	}

	@Override
	public String toString() {
		return "nine.exercises.Recipe [reference=" + reference + ", id=" + id + ", title=" + title + ", description="
				+ description + ", ingredietns=" + ingredients + ", steps=" + steps + "]";
	}

	public void setId(int id) {
		this.id = id;
	}

}
