package sg.edu.nus.iss.miniprojectbatch5.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import sg.edu.nus.iss.miniprojectbatch5.model.Meal;
import sg.edu.nus.iss.miniprojectbatch5.service.MealService;
import sg.edu.nus.iss.miniprojectbatch5.service.UserService;

@Controller
public class MealController {

    @Autowired
    MealService mealService;

    @Autowired
    UserService userService;

    @GetMapping("/search")
    public String searchPage(@AuthenticationPrincipal OAuth2User principal, Model model) {
        if (principal != null) {
            String userId = userService.extractUserId(principal);
            String userName = userService.getCurrentUserName(userId);
            model.addAttribute("name", userName != null ? userName : principal.getAttribute("name"));
        }

        // Add the random meals
        List<Meal> randomMeals = mealService.getRandomMealsList();
        model.addAttribute("randomMeals", randomMeals);
        
        model.addAttribute("categories", mealService.getAllCategories());
        model.addAttribute("areas", mealService.getAllAreas());
        return "search";
    }

    @GetMapping("/search/results")
    public String searchResults(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String area,
            @AuthenticationPrincipal OAuth2User principal,
            Model model) {
        
        if (principal != null) {
            String userId = userService.extractUserId(principal);
            String userName = userService.getCurrentUserName(userId);
            model.addAttribute("name", userName != null ? userName : principal.getAttribute("name"));
        }

        // Add categories and areas for filters
        model.addAttribute("categories", mealService.getAllCategories());
        model.addAttribute("areas", mealService.getAllAreas());

        List<Meal> results;
        if (query != null && !query.isEmpty()) {
            results = mealService.searchMeals(query);
        } else if (category != null && !category.isEmpty()) {
            results = mealService.filterByCategory(category);
        } else if (area != null && !area.isEmpty()) {
            results = mealService.filterByArea(area);
        } else {
            results = Collections.emptyList();
        }

        // Add random meals to results page as well
        List<Meal> randomMeals = mealService.getRandomMealsList();
        model.addAttribute("randomMeals", randomMeals);
        model.addAttribute("results", results);
        model.addAttribute("searchQuery", query);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedArea", area);
        return "search";
    }

    @GetMapping("/meal/{id}")
    public String getMealDetails(@PathVariable String id,@AuthenticationPrincipal OAuth2User principal,Model model) {
        if (principal != null) {
            String userId = userService.extractUserId(principal);
            String userName = userService.getCurrentUserName(userId);
            model.addAttribute("name", userName != null ? userName : principal.getAttribute("name"));
        }
        Meal meal = mealService.getMealById(id);
        model.addAttribute("meal", meal);
        return "meal-details";
    }

    @GetMapping("/favorites")
    public String getFavorites(@AuthenticationPrincipal OAuth2User principal,Model model) {
        if (principal != null) {
            String userId = userService.extractUserId(principal);
            String userName = userService.getCurrentUserName(userId);
            model.addAttribute("name", userName != null ? userName : principal.getAttribute("name"));
            if (userId != null) {
                String favoritesJson = mealService.getFavorites(userId);
                List<Meal> favorites = mealService.parseFavorites(favoritesJson);
                model.addAttribute("favorites", favorites);
            }
        }
        return "favorites";
    }

    @PostMapping("/favorites/add")
    public String addToFavorites(@RequestParam String mealId,@AuthenticationPrincipal OAuth2User principal) {
        if (principal != null) {
            String userId = userService.extractUserId(principal);
            if (userId != null) {
                mealService.addToFavorites(userId, mealId);
            }
        }
        return "redirect:/meal/" + mealId;
    }

    @PostMapping("/favorites/remove")
    public String removeFromFavorites(@RequestParam String mealId,@AuthenticationPrincipal OAuth2User principal) {
        if (principal != null) {
            String userId = userService.extractUserId(principal);
            if (userId != null) {
                mealService.removeFromFavorites(userId, mealId);
            }
        }
        return "redirect:/favorites";
    }

    @GetMapping("/random")
    public String getRandomMeal(@AuthenticationPrincipal OAuth2User principal, Model model) {
        if (principal != null) {
            String userId = userService.extractUserId(principal);
            String userName = userService.getCurrentUserName(userId);
            model.addAttribute("name", userName != null ? userName : principal.getAttribute("name"));
        }
        Meal randomMeal = mealService.getRandomMeal();
        model.addAttribute("meal", randomMeal);
        return "meal-details"; 
    }

}
