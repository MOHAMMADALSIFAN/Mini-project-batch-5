package sg.edu.nus.iss.miniprojectbatch5.model;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserProfile {

    @NotBlank(message = "Name cannot be empty")
    @Size(min = 3, max = 200, message = "Name must be between 3 and 200 characters")
    private String name;

    @NotBlank(message = "Please select your preferred cuisine")
    @Pattern(regexp = "^(Italian|Chinese|Indian|Japanese|Mexican|Thai|French|None)$",
            message = "Please select a valid cuisine")
    private String favoriteCuisine;

    @Pattern(regexp = "^(Beginner|Intermediate|Expert)$",
            message = "Cooking level must be Beginner, Intermediate, or Expert")
    private String cookingLevel;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Please enter a valid email address")
    private String email;

    @Min(value = 1, message = "Years of cooking experience must be at least 1")
    @Max(value = 70, message = "Please enter a valid years of experience")
    private Integer yearsOfCooking;

    @Pattern(regexp = "^(Vegetarian|Vegan|Non-vegetarian|Pescatarian|None)$",
            message = "Please select a valid dietary preference")
    private String dietaryPreference;

    @Size(min = 0, max = 5, message = "Please select up to 5 allergies")
    private List<String> allergies;

    public UserProfile() {
    }

    public UserProfile(
            @NotBlank(message = "Name cannot be empty") @Size(min = 3, max = 200, message = "Name must be between 3 and 200 characters") String name,
            @NotBlank(message = "Please select your preferred cuisine") String favoriteCuisine,
            @Pattern(regexp = "^(Beginner|Intermediate|Expert)$", message = "Cooking level must be Beginner, Intermediate, or Expert") String cookingLevel,
            @NotBlank(message = "Email cannot be empty") @Email(message = "Please enter a valid email address") String email,
            @Min(value = 1, message = "Years of cooking experience must be at least 1") @Max(value = 70, message = "Please enter a valid years of experience") Integer yearsOfCooking,
            @Pattern(regexp = "^(Vegetarian|Vegan|Non-vegetarian|Pescatarian|None)$", message = "Please select a valid dietary preference") String dietaryPreference,
            @Size(min = 0, max = 5, message = "Please select up to 5 allergies") List<String> allergies) {
        this.name = name;
        this.favoriteCuisine = favoriteCuisine;
        this.cookingLevel = cookingLevel;
        this.email = email;
        this.yearsOfCooking = yearsOfCooking;
        this.dietaryPreference = dietaryPreference;
        this.allergies = allergies;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFavoriteCuisine() {
        return favoriteCuisine;
    }

    public void setFavoriteCuisine(String favoriteCuisine) {
        this.favoriteCuisine = favoriteCuisine;
    }

    public String getCookingLevel() {
        return cookingLevel;
    }

    public void setCookingLevel(String cookingLevel) {
        this.cookingLevel = cookingLevel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getYearsOfCooking() {
        return yearsOfCooking;
    }

    public void setYearsOfCooking(Integer yearsOfCooking) {
        this.yearsOfCooking = yearsOfCooking;
    }

    public String getDietaryPreference() {
        return dietaryPreference;
    }

    public void setDietaryPreference(String dietaryPreference) {
        this.dietaryPreference = dietaryPreference;
    }

    public List<String> getAllergies() {
        return allergies;
    }

    public void setAllergies(List<String> allergies) {
        this.allergies = allergies;
    }
}