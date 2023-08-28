package sg.edu.nus.iss.csf39_workshop.repositories;

import java.time.Duration;
import java.util.Optional;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import jakarta.json.Json;
import sg.edu.nus.iss.csf39_workshop.models.MarvelCharacter;

@Repository
public class CharacterRepository {
    
    private RedisTemplate<String, String> template;

    public CharacterRepository(RedisTemplate<String, String> template) {
        this.template = template;
    }

    public void saveCharacterToRedis(MarvelCharacter character) {
        template.opsForValue().set(
                character.getId().toString(), 
                Json.createObjectBuilder()
                        .add("id", character.getId())
                        .add("name", character.getName())
                        .add("description", character.getDescription())
                        .add("thumbnail", character.getThumbnail())
                        .build()
                        .toString(), 
                Duration.ofHours(1));
    } 

    public Optional<String> getCharacterFromRedis(String id) {
        return Optional.ofNullable(template.opsForValue().get(id));
    }
}
