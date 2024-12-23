package sg.edu.nus.iss.miniprojectbatch5.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import sg.edu.nus.iss.miniprojectbatch5.service.UserService;

@Controller
public class LoginController {
    
    @Autowired
    UserService userService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/")
    public String home(@AuthenticationPrincipal OAuth2User principal, Model model) {
        if (principal != null) {
            String userId = userService.extractUserId(principal);
            String userName = userService.getCurrentUserName(userId);
            model.addAttribute("name", userName != null ? userName : principal.getAttribute("name"));
        }
        return "index";
    }
}
