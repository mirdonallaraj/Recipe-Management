package recipe;

import java.util.Objects;

public class RecipePreparationStep {
	
	private int recipePreparationStepId;
	
	private int number;
	private String title;
	private String description;

	public RecipePreparationStep(int recipePreparationStepId,
							     int number, 
							     String title, 
							     String description) {
		super();
		this.recipePreparationStepId = recipePreparationStepId;
		this.number = number;
		this.title = title;
		this.description = description;
	}

	public RecipePreparationStep(int number, String title, String description) {
		this(0, number, title, description);
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
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

	public int getRecipePreparationStepId() {
		return recipePreparationStepId;
	}
	
	public void setRecipePreparationStepId(int recipePreparationStepId) {
		this.recipePreparationStepId = recipePreparationStepId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(recipePreparationStepId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RecipePreparationStep other = (RecipePreparationStep) obj;
		return recipePreparationStepId == other.recipePreparationStepId;
	}
	
}
