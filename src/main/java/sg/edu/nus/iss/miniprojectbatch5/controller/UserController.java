package sg.edu.nus.iss.miniprojectbatch5.controller;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
                userProfile.setImageUrl(principal.getAttribute("picture"));
            }
            
            model.addAttribute("userProfile", userProfile);
            model.addAttribute("name", userProfile.getName());
        }
        return "profile";
    }

    @PostMapping("/update")
    public String updateProfile(
            @AuthenticationPrincipal OAuth2User principal,
            @Valid @ModelAttribute UserProfile userProfile,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
    
        if (bindingResult.hasErrors()) {
            model.addAttribute("name", principal.getAttribute("name"));
            return "profile";
        }
    
        try {
            String userId = userService.extractUserId(principal);
            userService.updateUserProfile(userId, userProfile);
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update profile. Please try again.");
        }
    
        return "redirect:/profile";
    }
}