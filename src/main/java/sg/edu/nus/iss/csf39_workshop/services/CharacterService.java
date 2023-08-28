package sg.edu.nus.iss.csf39_workshop.services;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import sg.edu.nus.iss.csf39_workshop.models.Comment;
import sg.edu.nus.iss.csf39_workshop.models.MD5;
import sg.edu.nus.iss.csf39_workshop.models.MarvelCharacter;
import sg.edu.nus.iss.csf39_workshop.models.Util;
import sg.edu.nus.iss.csf39_workshop.repositories.CharacterRepository;
import sg.edu.nus.iss.csf39_workshop.repositories.CommentRepository;

@Service
public class CharacterService {

    private static final String MARVEL_API_GET_CHARACTERS = 
            "https://gateway.marvel.com:443/v1/public/characters";

    @Value("${marvel.api.key.public}")
    private String MARVEL_API_KEY_PUBLIC;

    @Value("${marvel.api.key.private}")
    private String MARVEL_API_KEY_PRIVATE;

    public CharacterRepository charRepo;
    public CommentRepository commentRepo;

    public CharacterService(
        CharacterRepository charRepo,
        CommentRepository commentRepo) 
    {
        this.charRepo = charRepo;
        this.commentRepo = commentRepo;
    }

    public List<MarvelCharacter> findCharacters(
            String name,
            int limit,
            int offset) throws Exception 
    {
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
                .map(v -> Util.marvelApiJsonToMarvelCharacter(v))
                .toList();

        marvelCharacters.stream()
                .forEach(v -> charRepo.saveCharacterToRedis(v));

        return marvelCharacters;
    }

    public JsonObject findCharacterById(String id) {
        Optional<String> opt = charRepo.getCharacterFromRedis(id);

        if (opt.isPresent()) {
            return Json.createReader(new StringReader(opt.get()))
                    .readObject();
        }

        long timeStamp = new Date().getTime();

        String apiUrl = UriComponentsBuilder
                .fromUriString(MARVEL_API_GET_CHARACTERS)
                .path("/")
                .path(id)
                .queryParam("ts", timeStamp)
                .queryParam("apikey", MARVEL_API_KEY_PUBLIC)
                .queryParam("hash", MD5.getMd5(
                        timeStamp + MARVEL_API_KEY_PRIVATE + 
                        MARVEL_API_KEY_PUBLIC))
                .toUriString();

        RequestEntity<Void> req = RequestEntity.get(apiUrl).build();
        RestTemplate template = new RestTemplate();

        ResponseEntity<String> resp = template.exchange(
                req, String.class);

        JsonObject obj = Json.createReader(new StringReader(resp.getBody()))
                .readObject()
                .getJsonObject("data")
                .getJsonArray("results")
                .getJsonObject(0);
        
        MarvelCharacter character = Util.marvelApiJsonToMarvelCharacter(obj);
        charRepo.saveCharacterToRedis(character);

        return MarvelCharacter.toJson(character);
    }

    public List<String> findCommentsByCharacterId(String id) {
        return commentRepo.findCommentsByCharacterId(id).stream()
                .map(v -> v.getString("text"))
                .toList();
    }

    public void insertComment(Comment comment) {
        Document document = Util.commentToDocument(comment);
        commentRepo.insertComment(document);
    }
}
