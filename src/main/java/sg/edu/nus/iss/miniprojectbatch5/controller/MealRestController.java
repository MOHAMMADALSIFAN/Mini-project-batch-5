package sg.edu.nus.iss.miniprojectbatch5.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import sg.edu.nus.iss.miniprojectbatch5.model.Meal;
import sg.edu.nus.iss.miniprojectbatch5.service.MealServiceImpl;

@RestController
@RequestMapping("/api")
public class MealRestController {

    @Autowired
    private MealServiceImpl mealService;

    @GetMapping("/meals/search")
    public ResponseEntity<List<Meal>> searchMeals(@RequestParam String query) {
        List<Meal> results = mealService.searchMeals(query);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/meals/{id}")
    public ResponseEntity<Meal> getMealById(@PathVariable String id) {
        Meal meal = mealService.getMealById(id);
        return ResponseEntity.ok(meal);
    }

    @GetMapping("/meals/random")
    public ResponseEntity<Meal> getRandomMeal() {
        Meal meal = mealService.getRandomMeal();
        return ResponseEntity.ok(meal);
    }
}
//   // API endpoints
// GET /api/meals/search?query={query}
// GET /api/meals/{id}
// GET /api/meals/random
  