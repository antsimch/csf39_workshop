package sg.edu.nus.iss.csf39_workshop.controllers;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import sg.edu.nus.iss.csf39_workshop.models.Comment;
import sg.edu.nus.iss.csf39_workshop.models.MarvelCharacter;
import sg.edu.nus.iss.csf39_workshop.services.CharacterService;


@RestController
@RequestMapping(path = "/api")
public class CharacterController {

    private CharacterService charSvc;

    public CharacterController(CharacterService charSvc) {
        this.charSvc = charSvc;
    }
    
    @GetMapping(path = "/characters", 
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getCharacters(
        @RequestParam String name,
        @RequestParam(defaultValue = "20") int limit,
        @RequestParam(defaultValue = "0") int offset) 
    {
        try {
            JsonArrayBuilder arrBuilder = Json.createArrayBuilder();
            charSvc.findCharacters(name, limit, offset).stream()
                    .map(v -> MarvelCharacter.toJson(v))
                    .forEach(v -> arrBuilder.add(v));

            return ResponseEntity.ok(arrBuilder.build().toString());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();            
        }
    }

    @GetMapping(path = "/character/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getCharacterById(@PathVariable String id) {

        JsonObject obj = Json.createObjectBuilder()
                .add("character", charSvc.findCharacterById(id))
                .add("comments", Json.createArrayBuilder(
                        charSvc.findCommentsByCharacterId(id)))
                .build();

        return ResponseEntity.ok(obj.toString());
    }

    @PostMapping(path = "/character/{id}")
    public ResponseEntity<String> createComment(@RequestBody Comment comment) {
        charSvc.insertComment(comment);
        // TODO: check if insert successful 
        return ResponseEntity.ok().build();
    }
}
