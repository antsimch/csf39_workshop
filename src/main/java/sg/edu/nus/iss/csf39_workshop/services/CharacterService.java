package sg.edu.nus.iss.csf39_workshop.services;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import sg.edu.nus.iss.csf39_workshop.models.MD5;
import sg.edu.nus.iss.csf39_workshop.models.MarvelCharacter;
import sg.edu.nus.iss.csf39_workshop.repositories.CharacterRepository;

@Service
public class CharacterService {

    private static final String MARVEL_API_GET_CHARACTERS = 
            "https://gateway.marvel.com:443/v1/public/characters";

    @Value("${marvel.api.key.public}")
    private String MARVEL_API_KEY_PUBLIC;

    @Value("${marvel.api.key.private}")
    private String MARVEL_API_KEY_PRIVATE;

    public CharacterRepository charRepo;

    public CharacterService(CharacterRepository charRepo) {
        this.charRepo = charRepo;
    }

    public List<MarvelCharacter> findCharacters(
            String name,
            int limit,
            int offset) throws Exception {
        long timeStamp = new Date().getTime();

        String apiUrl = UriComponentsBuilder
                .fromUriString(MARVEL_API_GET_CHARACTERS)
                .queryParam("ts", timeStamp)
                .queryParam("apikey", MARVEL_API_KEY_PUBLIC)
                .queryParam("hash", MD5.getMd5(
                        timeStamp + MARVEL_API_KEY_PRIVATE + 
                        MARVEL_API_KEY_PUBLIC))
                .queryParam("nameStartsWith", name)
                .queryParam("limit", limit)
                .queryParam("offset", offset)
                .toUriString();

        System.out.println("\n\napiUrl >>>> " + apiUrl + "\n\n");
        RequestEntity<Void> req = RequestEntity.get(apiUrl).build();
        RestTemplate template = new RestTemplate();

        ResponseEntity<String> resp = template.exchange(
                req, String.class);

        JsonObject obj = Json.createReader(new StringReader(resp.getBody()))
                .readObject();

        List<MarvelCharacter> marvelCharacters = new ArrayList<>();

        JsonArray arr = obj.getJsonObject("data")
                .getJsonArray("results");

        marvelCharacters = arr.stream().map(v -> (JsonObject) v)
                .map(v -> new MarvelCharacter(
                        v.getJsonNumber("id").intValue(),
                        v.getString("name"),
                        v.getString("description"),
                        v.getJsonObject("thumbnail").getString("path") 
                                + "." + v.getJsonObject("thumbnail")
                                .getString("extension")))
                .toList();

        marvelCharacters.stream()
                .forEach(v -> charRepo.saveCharacterToRedis(v));

        return marvelCharacters;
    }

    public JsonObject getCharacterById(String id) {
        Optional<String> opt = charRepo.getCharacterFromRedis(id);

        if (opt.isEmpty())
            return null;
        
        return Json.createReader(new StringReader(opt.get()))
                .readObject();
    }
}
