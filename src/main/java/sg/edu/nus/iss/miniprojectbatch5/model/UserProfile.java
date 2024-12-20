package sg.edu.nus.iss.miniprojectbatch5.model;

import jakarta.validation.constraints.Size;

import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.NotBlank;

public class UserProfile {
  @NotBlank(message = "Name cannot be empty")
  @Size(min = 3, max = 200, message = "Name must be between 3 and 200 characters")
  private String name;

  @URL(message = "Please enter a valid URL")
    private String imageUrl;

    public UserProfile(String name,String imageUrl) {
      this.name = name;
      this.imageUrl = imageUrl;
    }

    public UserProfile() {
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getImageUrl() {
      return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
      this.imageUrl = imageUrl;
    }

}
