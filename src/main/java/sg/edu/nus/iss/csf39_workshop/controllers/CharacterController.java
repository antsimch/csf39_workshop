package sg.edu.nus.iss.csf39_workshop.controllers;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
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
                    .map(v -> Json.createObjectBuilder()
                                    .add("id", v.getId())
                                    .add("name", v.getName())
                                    .add("description", v.getDescription())
                                    .add("thumbnail", v.getThumbnail())
                                    .build())
                    .forEach(v -> arrBuilder.add(v));

            return ResponseEntity.ok(arrBuilder.build().toString());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();            
        }
    }

    @GetMapping(path = "/character/{id}")
    public ResponseEntity<String> getCharacterById(@PathVariable String id) {

        JsonObject obj = charSvc.getCharacterById(id);
        System.out.println("\n\nobj >>>> " + obj + "\n\n");

        if (obj == null)
            return ResponseEntity.internalServerError().build();

        return ResponseEntity.ok(obj.toString());
    }
}
