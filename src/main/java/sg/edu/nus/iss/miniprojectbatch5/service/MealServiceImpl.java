package sg.edu.nus.iss.miniprojectbatch5.service;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import sg.edu.nus.iss.miniprojectbatch5.model.Meal;
import sg.edu.nus.iss.miniprojectbatch5.repo.MealRepositoryImpl;

@Service
public class MealServiceImpl {

    private static final String MEALDB_API_URL = "https://www.themealdb.com/api/json/v1/1";
    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private MealRepositoryImpl mealRepository;

    public List<Meal> searchMeals(String query) {
        String url = UriComponentsBuilder
                .fromUriString(MEALDB_API_URL)
                .path("/search.php")
                .queryParam("s", query)
                .toUriString();

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        String jsonStr = response.getBody();

        try (StringReader sr = new StringReader(jsonStr)) {
            JsonReader jReader = Json.createReader(sr);
            JsonObject result = jReader.readObject();
            if (result.containsKey("meals") && !result.isNull("meals")) {
                return result.getJsonArray("meals").stream()
                        .map(JsonValue::asJsonObject)
                        .map(Meal::create)
                        .collect(Collectors.toList());
            }
            return Collections.emptyList();
        }
    }

    public Meal getMealById(String id) {
        String url = UriComponentsBuilder
                .fromUriString(MEALDB_API_URL)
                .path("/lookup.php")
                .queryParam("i", id)
                .toUriString();

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        String jsonStr = response.getBody();

        try (StringReader sr = new StringReader(jsonStr)) {
            JsonReader jReader = Json.createReader(sr);
            JsonObject result = jReader.readObject();
            if (result.containsKey("meals") && !result.isNull("meals")) {
                return Meal.create(result.getJsonArray("meals").getJsonObject(0));
            }
            return null;
        }
    }

    public Meal getRandomMeal() {
        String url = UriComponentsBuilder
                .fromUriString(MEALDB_API_URL)
                .path("/random.php")
                .toUriString();

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        String jsonStr = response.getBody();

        try (StringReader sr = new StringReader(jsonStr)) {
            JsonReader jReader = Json.createReader(sr);
            JsonObject result = jReader.readObject();
            if (result.containsKey("meals") && !result.isNull("meals")) {
                return Meal.create(result.getJsonArray("meals").getJsonObject(0));
            }
            return null;
        }
    }

    public void addToFavorites(String userId, String mealId) {
        validateRedisOperation(userId);
        Meal meal = getMealById(mealId);
        if (meal != null) {
            // Convert meal to JSON before storing
            JsonObject mealJson = Json.createObjectBuilder()
                    .add("idMeal", meal.getIdMeal())
                    .add("strMeal", meal.getStrMeal())
                    .add("strMealThumb", meal.getStrMealThumb())
                    .add("strCategory", meal.getStrCategory())
                    .add("strArea", meal.getStrArea())
                    .add("strInstructions", meal.getStrInstructions())
                    .build();
            mealRepository.saveFavorite(userId, mealId, mealJson.toString());
        }
    }

    public void removeFromFavorites(String userId, String mealId) {
        validateRedisOperation(userId);
        mealRepository.deleteFavorite(userId, mealId);
    }

    public String getFavorites(String userId) {
        validateRedisOperation(userId);
        String favorites = mealRepository.getFavorites(userId);
        if (favorites == null || favorites.isEmpty()) {
            return "[]";
        }
        return favorites;
    }

    private void validateRedisOperation(String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new RuntimeException("Invalid user ID");
        }
    }

    public List<Meal> parseFavorites(String favoritesJson) {
        if (favoritesJson == null || favoritesJson.isEmpty()) {
            return Collections.emptyList();
        }

        try (StringReader sr = new StringReader(favoritesJson)) {
            JsonReader jReader = Json.createReader(sr);
            JsonArray jsonArray = jReader.readArray();
            return jsonArray.stream()
                    .map(JsonValue::asJsonObject)
                    .map(Meal::create)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<String> getAllCategories() {
        String url = UriComponentsBuilder
                .fromUriString(MEALDB_API_URL)
                .path("/list.php")
                .queryParam("c", "list")
                .toUriString();

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        String jsonStr = response.getBody();

        try (StringReader sr = new StringReader(jsonStr)) {
            JsonReader jReader = Json.createReader(sr);
            JsonObject result = jReader.readObject();
            if (result.containsKey("meals") && !result.isNull("meals")) {
                return result.getJsonArray("meals").stream()
                        .map(JsonValue::asJsonObject)
                        .map(jo -> jo.getString("strCategory"))
                        .collect(Collectors.toList());
            }
            return Collections.emptyList();
        }
    }

    public List<String> getAllAreas() {
        String url = UriComponentsBuilder
                .fromUriString(MEALDB_API_URL)
                .path("/list.php")
                .queryParam("a", "list")
                .toUriString();

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        String jsonStr = response.getBody();

        try (StringReader sr = new StringReader(jsonStr)) {
            JsonReader jReader = Json.createReader(sr);
            JsonObject result = jReader.readObject();
            if (result.containsKey("meals") && !result.isNull("meals")) {
                return result.getJsonArray("meals").stream()
                        .map(JsonValue::asJsonObject)
                        .map(jo -> jo.getString("strArea"))
                        .collect(Collectors.toList());
            }
            return Collections.emptyList();
        }
    }

    public List<Meal> filterByArea(String area) {
        String url = UriComponentsBuilder
                .fromUriString(MEALDB_API_URL)
                .path("/filter.php")
                .queryParam("a", area)
                .toUriString();

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        String jsonStr = response.getBody();

        try (StringReader sr = new StringReader(jsonStr)) {
            JsonReader jReader = Json.createReader(sr);
            JsonObject result = jReader.readObject();
            if (result.containsKey("meals") && !result.isNull("meals")) {
                JsonArray meals = result.getJsonArray("meals");
                List<Meal> filteredMeals = new ArrayList<>();

                for (JsonValue mealValue : meals) {
                    JsonObject mealObj = mealValue.asJsonObject();
                    String mealId = mealObj.getString("idMeal");
                    // Fetch full meal details for each meal
                    Meal fullMeal = getMealById(mealId);
                    if (fullMeal != null) {
                        filteredMeals.add(fullMeal);
                    }
                }
                return filteredMeals;
            }
            return Collections.emptyList();
        }
    }

    public List<Meal> filterByCategory(String category) {
        String url = UriComponentsBuilder
                .fromUriString(MEALDB_API_URL)
                .path("/filter.php")
                .queryParam("c", category)
                .toUriString();

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        String jsonStr = response.getBody();

        try (StringReader sr = new StringReader(jsonStr)) {
            JsonReader jReader = Json.createReader(sr);
            JsonObject result = jReader.readObject();
            if (result.containsKey("meals") && !result.isNull("meals")) {
                JsonArray meals = result.getJsonArray("meals");
                List<Meal> filteredMeals = new ArrayList<>();

                for (JsonValue mealValue : meals) {
                    JsonObject mealObj = mealValue.asJsonObject();
                    String mealId = mealObj.getString("idMeal");
                    // Fetch full meal details for each meal
                    Meal fullMeal = getMealById(mealId);
                    if (fullMeal != null) {
                        filteredMeals.add(fullMeal);
                    }
                }
                return filteredMeals;
            }
            return Collections.emptyList();
        }
    }

    public String extractUserId(OAuth2User principal) {
        // Try getting GitHub ID
        Object githubId = principal.getAttribute("id");
        if (githubId != null) {
            return githubId.toString();
        }

        // Try getting Google sub (subject) ID
        String googleId = principal.getAttribute("sub");
        if (googleId != null) {
            return googleId;
        }

        // If neither found, use email as fallback
        String email = principal.getAttribute("email");
        if (email != null) {
            return email;
        }

        return null;
    }
}
