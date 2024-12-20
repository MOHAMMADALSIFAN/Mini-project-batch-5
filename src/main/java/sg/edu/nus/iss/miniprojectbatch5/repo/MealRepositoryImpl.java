package sg.edu.nus.iss.miniprojectbatch5.repo;

import java.io.StringReader;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

@Repository
public class MealRepositoryImpl {
    
    @Autowired
    @Qualifier("meal")
    private RedisTemplate<String, String> redisTemplate;

    private static final String KEY_PREFIX = "user:";
    private static final String KEY_SUFFIX = ":favorites";

    private String generateKey(String userId) {
        return KEY_PREFIX + userId + KEY_SUFFIX;
    }


    public void saveFavorite(String userId, String mealId, String mealJson) {
        String key = generateKey(userId);
        redisTemplate.opsForHash().put(key, mealId, mealJson);
    }


    public void deleteFavorite(String userId, String mealId) {
        String key = generateKey(userId);
        redisTemplate.opsForHash().delete(key, mealId);
    }


    public String getFavorites(String userId) {
        String key = generateKey(userId);
        List<Object> values = redisTemplate.opsForHash().values(key);
        
        // Create a JSON array from stored JSON strings
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (Object value : values) {
            try (JsonReader jsonReader = Json.createReader(new StringReader(value.toString()))) {
                JsonObject mealJson = jsonReader.readObject();
                arrayBuilder.add(mealJson);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return arrayBuilder.build().toString();
    }
}