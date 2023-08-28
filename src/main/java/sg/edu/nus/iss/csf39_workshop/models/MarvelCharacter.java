package sg.edu.nus.iss.csf39_workshop.models;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarvelCharacter {
    private Integer id;
    private String name;
    private String description;
    private String thumbnail;

    public static JsonObject toJson(MarvelCharacter character) {
        return Json.createObjectBuilder()
                .add("id", character.getId())
                .add("name", character.getName())
                .add("description", character.getDescription())
                .add("thumbnails", character.getThumbnail())
                .build();
    }
}
