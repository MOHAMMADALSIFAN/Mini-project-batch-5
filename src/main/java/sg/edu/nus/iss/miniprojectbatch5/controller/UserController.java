package sg.edu.nus.iss.miniprojectbatch5.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;
import sg.edu.nus.iss.miniprojectbatch5.model.UserProfile;
import sg.edu.nus.iss.miniprojectbatch5.service.UserService;

@Controller
@RequestMapping("/profile")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String showProfile(@AuthenticationPrincipal OAuth2User principal, Model model) {
        if (principal != null) {
            String userId = userService.extractUserId(principal);
            UserProfile userProfile = userService.getUserProfile(userId);

            if (userProfile == null) {
                userProfile = new UserProfile();
                userProfile.setName(principal.getAttribute("name"));
            }

            // Add all the lists with new options
            List<String> availableAllergies = Arrays.asList("Nuts", "Dairy", "Shellfish", "Eggs", "Soy", "Wheat", "Fish", "No Allergies");
            List<String> availableDietaryPreferences = Arrays.asList("Vegetarian", "Vegan", "Non-vegetarian", "Pescatarian", "None");
            List<String> availableCuisines = Arrays.asList("Italian", "Chinese", "Indian", "Japanese", "Mexican", "Thai", "French", "None");
            List<String> cookingLevels = Arrays.asList("Beginner", "Intermediate", "Expert");

            model.addAttribute("userProfile", userProfile);
            model.addAttribute("name", userProfile.getName());
            model.addAttribute("availableAllergies", availableAllergies);
            model.addAttribute("availableDietaryPreferences", availableDietaryPreferences);
            model.addAttribute("availableCuisines", availableCuisines);
            model.addAttribute("cookingLevels", cookingLevels);
        }
        return "profile";
    }

    @PostMapping("/update")
    public String updateProfile(
            @AuthenticationPrincipal OAuth2User principal,
            @Valid @ModelAttribute UserProfile userProfile,
            BindingResult bindingResult,
            Model model) {

        // Add the lists regardless of validation result
        List<String> availableAllergies = Arrays.asList("Nuts", "Dairy", "Shellfish", "Eggs", "Soy", "Wheat", "Fish", "No Allergies");
        List<String> availableDietaryPreferences = Arrays.asList("Vegetarian", "Vegan", "Non-vegetarian", "Pescatarian", "None");
        List<String> availableCuisines = Arrays.asList("Italian", "Chinese", "Indian", "Japanese", "Mexican", "Thai", "French", "None");
        List<String> cookingLevels = Arrays.asList("Beginner", "Intermediate", "Expert");

        model.addAttribute("availableAllergies", availableAllergies);
        model.addAttribute("availableDietaryPreferences", availableDietaryPreferences);
        model.addAttribute("availableCuisines", availableCuisines);
        model.addAttribute("cookingLevels", cookingLevels);

        if (bindingResult.hasErrors()) {
            model.addAttribute("name", principal.getAttribute("name"));
            model.addAttribute("error", "Please fix the validation errors");
            return "profile";
        }

        try {
            String userId = userService.extractUserId(principal);
            userService.updateUserProfile(userId, userProfile);
            model.addAttribute("success", "Profile updated successfully!");
            model.addAttribute("name", userProfile.getName());
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Failed to update profile. Please try again.");
            model.addAttribute("name", principal.getAttribute("name"));
        }

        return "profile";
    }
}
