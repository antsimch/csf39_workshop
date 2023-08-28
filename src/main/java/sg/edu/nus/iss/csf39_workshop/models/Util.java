package sg.edu.nus.iss.csf39_workshop.models;

import org.bson.Document;

import jakarta.json.JsonObject;

public class Util {
    
    // Mapping Marvel API response "results" attribute object
    public static MarvelCharacter marvelApiJsonToMarvelCharacter(
            JsonObject obj)
    {
        return new MarvelCharacter(
            obj.getJsonNumber("id").intValue(),
            obj.getString("name"),
            obj.getString("description"),
            obj.getJsonObject("thumbnail").getString("path") 
                    + "." + obj.getJsonObject("thumbnail")
                    .getString("extension"));
    }

    // Mapping comment java object to document object
    public static Document commentToDocument(Comment comment) {
        Document document = new Document();
        document.put("char_id", comment.getCharacterId());
        document.put("text", comment.getText());
        return document;
    }
}
