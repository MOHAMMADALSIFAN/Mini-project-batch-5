package sg.edu.nus.iss.miniprojectbatch5.service;

import java.io.StringReader;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import sg.edu.nus.iss.miniprojectbatch5.model.UserProfile;

@Service
public class UserService{

    @Autowired
    @Qualifier("meal")
    private RedisTemplate<String, String> redisTemplate;

    private static final String USER_PREFIX = "userlogin:";


    public void processOAuthPostLogin(OAuth2User oAuth2User) {
        String userId = extractUserId(oAuth2User);
        String key = USER_PREFIX + userId + ":profile";
    
        // Create user profile JSON
        JsonObject userJson = Json.createObjectBuilder()
                .add("id", userId)
                .add("name", oAuth2User.getAttribute("name") != null ? 
                        oAuth2User.getAttribute("name").toString() : "")
                .add("email", oAuth2User.getAttribute("email") != null ? 
                        oAuth2User.getAttribute("email").toString() : "")
                .add("imageUrl", oAuth2User.getAttribute("picture") != null ? 
                        oAuth2User.getAttribute("picture").toString() : "")
                .build();
    
        // Store in Redis if not exists
        if (!redisTemplate.hasKey(key)) {
            redisTemplate.opsForValue().set(key, userJson.toString());
        }
    }


    public void updateUserProfile(String userId, UserProfile userProfile) {
        String key = USER_PREFIX + userId + ":profile";
        
        // Get existing profile
        String existingProfile = redisTemplate.opsForValue().get(key);
        JsonObject existing = null;
        if (existingProfile != null) {
            try (JsonReader jsonReader = Json.createReader(new StringReader(existingProfile))) {
                existing = jsonReader.readObject();
            }
        }

        // Create updated profile
        JsonObjectBuilder builder = Json.createObjectBuilder();
        if (existing != null) {
            for (Map.Entry<String, JsonValue> entry : existing.entrySet()) {
                builder.add(entry.getKey(), entry.getValue());
            }
        }
        
        // Update with new values
        builder.add("name", userProfile.getName());
        if (userProfile.getImageUrl() != null && !userProfile.getImageUrl().isEmpty()) {
            builder.add("imageUrl", userProfile.getImageUrl());
        }

        // Save to Redis
        redisTemplate.opsForValue().set(key, builder.build().toString());
    }


    public UserProfile getUserProfile(String userId) {
        String key = USER_PREFIX + userId + ":profile";
        String profileJson = redisTemplate.opsForValue().get(key);
        
        if (profileJson != null) {
            try (JsonReader jsonReader = Json.createReader(new StringReader(profileJson))) {
                JsonObject profileObj = jsonReader.readObject();
                UserProfile profile = new UserProfile();
                profile.setName(profileObj.getString("name", ""));
                profile.setImageUrl(profileObj.getString("imageUrl", ""));
                return profile;
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