package sg.edu.nus.iss.miniprojectbatch5.service;

import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import sg.edu.nus.iss.miniprojectbatch5.model.UserProfile;

@Service
public class UserService {

    @Autowired
    @Qualifier("meal")
    private RedisTemplate<String, String> redisTemplate;

    private static final String USER_PREFIX = "userlogin:";

    public void processOAuthPostLogin(OAuth2User oAuth2User) {
        String userId = extractUserId(oAuth2User);
        String key = USER_PREFIX + userId + ":profile";

        // Create user profile JSON without imageUrl
        JsonObject userJson = Json.createObjectBuilder()
                .add("id", userId)
                .add("name", oAuth2User.getAttribute("name") != null
                        ? oAuth2User.getAttribute("name").toString() : "")
                .add("email", oAuth2User.getAttribute("email") != null
                        ? oAuth2User.getAttribute("email").toString() : "")
                // REMOVED: .add("imageUrl", ...)
                .build();

        // Store in Redis if not exists
        if (!redisTemplate.hasKey(key)) {
            redisTemplate.opsForValue().set(key, userJson.toString());
        }
    }

    public void updateUserProfile(String userId, UserProfile userProfile) {
        String key = USER_PREFIX + userId + ":profile";
        String existingProfile = redisTemplate.opsForValue().get(key);
        JsonObject existing = null;

        if (existingProfile != null) {
            try (JsonReader jsonReader = Json.createReader(new StringReader(existingProfile))) {
                existing = jsonReader.readObject();
            }
        }

        JsonObjectBuilder builder = Json.createObjectBuilder();
        if (existing != null) {
            for (Map.Entry<String, JsonValue> entry : existing.entrySet()) {
                builder.add(entry.getKey(), entry.getValue());
            }
        }

        builder.add("id", userId)
                .add("name", userProfile.getName())
                .add("email", userProfile.getEmail())
                .add("favoriteCuisine", userProfile.getFavoriteCuisine())
                .add("cookingLevel", userProfile.getCookingLevel())
                // Ensure yearsOfCooking is added as a number
                .add("yearsOfCooking", userProfile.getYearsOfCooking() != null
                        ? userProfile.getYearsOfCooking()
                        : 0)
                .add("dietaryPreference", userProfile.getDietaryPreference());

        // Handle allergies list
        JsonArrayBuilder allergyBuilder = Json.createArrayBuilder();
        if (userProfile.getAllergies() != null) {
            userProfile.getAllergies().forEach(allergyBuilder::add);
        }
        builder.add("allergies", allergyBuilder);

        redisTemplate.opsForValue().set(key, builder.build().toString());
    }

    public UserProfile getUserProfile(String userId) {
        String key = USER_PREFIX + userId + ":profile";
        String profileJson = redisTemplate.opsForValue().get(key);

        if (profileJson != null) {
            try (JsonReader jsonReader = Json.createReader(new StringReader(profileJson))) {
                JsonObject profileObj = jsonReader.readObject();
                UserProfile profile = new UserProfile();

                // Safe retrieval with default values
                profile.setName(profileObj.getString("name", ""));
                profile.setEmail(profileObj.getString("email", ""));
                profile.setFavoriteCuisine(profileObj.getString("favoriteCuisine", ""));
                profile.setCookingLevel(profileObj.getString("cookingLevel", ""));
                profile.setDietaryPreference(profileObj.getString("dietaryPreference", ""));

                // Safely handle yearsOfCooking
                JsonValue yearsOfCookingValue = profileObj.get("yearsOfCooking");
                if (yearsOfCookingValue != null && !yearsOfCookingValue.toString().isEmpty()) {
                    try {
                        // Try parsing as an integer, default to 0 if parsing fails
                        profile.setYearsOfCooking(
                                yearsOfCookingValue instanceof JsonNumber
                                        ? ((JsonNumber) yearsOfCookingValue).intValue()
                                        : Integer.parseInt(yearsOfCookingValue.toString())
                        );
                    } catch (Exception e) {
                        // Log the error and set a default value
                        System.err.println("Error parsing yearsOfCooking: " + e.getMessage());
                        profile.setYearsOfCooking(0);
                    }
                } else {
                    // Set a default value if no years of cooking is found
                    profile.setYearsOfCooking(0);
                }

                // Handle allergies array
                if (profileObj.containsKey("allergies")) {
                    List<String> allergies = profileObj.getJsonArray("allergies").stream()
                            .map(JsonValue::toString)
                            .map(s -> s.replace("\"", ""))
                            .collect(Collectors.toList());
                    profile.setAllergies(allergies);
                }

                return profile;
            } catch (Exception e) {
                // Log the full error for debugging
                e.printStackTrace();
                return null;
            }
        }
        return null;
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

        // Fallback to email
        String email = principal.getAttribute("email");
        if (email != null) {
            return email;
        }

        return null;
    }

    public String getCurrentUserName(String userId) {
        String key = "user:" + userId + ":profile";
        String profileJson = redisTemplate.opsForValue().get(key);

        if (profileJson != null) {
            try (JsonReader jsonReader = Json.createReader(new StringReader(profileJson))) {
                JsonObject profile = jsonReader.readObject();
                return profile.getString("name");
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}
