package recipe;

import java.util.Objects;

public class Ingredient {
	
	private int recipeIngredientId;
	private String name;
	private double quantityPerPerson;

	public Ingredient(String name, 
					  double quantityPerPerson) {
		this(0, name, quantityPerPerson);
	}

	public Ingredient(int recipeIngredientId, 
					  String name, 
					  double quantityPerPerson) {
		super();
		this.recipeIngredientId = recipeIngredientId;
		this.name = name;
		this.quantityPerPerson = quantityPerPerson;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getQuantityPerPerson() {
		return quantityPerPerson;
	}

	public void setQuantityPerPerson(double quantityPerPerson) {
		this.quantityPerPerson = quantityPerPerson;
	}

	public int getRecipeIngredientId() {
		return recipeIngredientId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(recipeIngredientId);
	}

	public void setRecipeIngredientId(int recipeIngredientId) {
		this.recipeIngredientId = recipeIngredientId;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Ingredient other = (Ingredient) obj;
		return recipeIngredientId == other.recipeIngredientId;
	}

}
