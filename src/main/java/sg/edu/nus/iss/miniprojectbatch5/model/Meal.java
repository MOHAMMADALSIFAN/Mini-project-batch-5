package sg.edu.nus.iss.miniprojectbatch5.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.json.JsonObject;

public class Meal {

    private String idMeal;
    private String strMeal;
    private String strCategory;
    private String strArea;
    private String strInstructions;
    private String strMealThumb;
    private List<String> ingredients;
    private List<String> measurements;

    public Meal() {
    }

    public static Meal create(JsonObject jo) {
        Meal meal = new Meal();
        meal.setIdMeal(jo.getString("idMeal"));
        meal.setStrMeal(jo.getString("strMeal"));
        meal.setStrMealThumb(jo.getString("strMealThumb"));
        meal.setStrInstructions(jo.getString("strInstructions"));
        meal.setStrCategory(jo.getString("strCategory"));
        meal.setStrArea(jo.getString("strArea"));

        // Parse ingredients and measurements
        List<String> ingredients = new ArrayList<>();
        List<String> measurements = new ArrayList<>();

        // Loop through 20 possible ingredients
        for (int i = 1; i <= 20; i++) {
            String ingredientKey = "strIngredient" + i;
            String measureKey = "strMeasure" + i;

            // Check if ingredient exists and is not null or empty
            if (jo.containsKey(ingredientKey) && 
                !jo.isNull(ingredientKey) && 
                !jo.getString(ingredientKey).trim().isEmpty()) {
                
                String ingredient = jo.getString(ingredientKey);
                String measure = jo.containsKey(measureKey) && !jo.isNull(measureKey) 
                    ? jo.getString(measureKey) 
                    : "";

                ingredients.add(ingredient);
                measurements.add(measure);
            }
        }

        meal.setIngredients(ingredients);
        meal.setMeasurements(measurements);

        return meal;
    }

    public String getIdMeal() {
        return idMeal;
    }

    public void setIdMeal(String idMeal) {
        this.idMeal = idMeal;
    }

    public String getStrMeal() {
        return strMeal;
    }

    public void setStrMeal(String strMeal) {
        this.strMeal = strMeal;
    }

    public String getStrCategory() {
        return strCategory;
    }

    public void setStrCategory(String strCategory) {
        this.strCategory = strCategory;
    }

    public String getStrArea() {
        return strArea;
    }

    public void setStrArea(String strArea) {
        this.strArea = strArea;
    }

    public String getStrInstructions() {
        return strInstructions;
    }

    public void setStrInstructions(String strInstructions) {
        this.strInstructions = strInstructions;
    }

    public String getStrMealThumb() {
        return strMealThumb;
    }

    public void setStrMealThumb(String strMealThumb) {
        this.strMealThumb = strMealThumb;
    }

    public List<String> getIngredients() {
      return ingredients;
  }

  public void setIngredients(List<String> ingredients) {
      this.ingredients = ingredients;
  }

  public List<String> getMeasurements() {
      return measurements;
  }

  public void setMeasurements(List<String> measurements) {
      this.measurements = measurements;
  }

}
