package sg.edu.nus.iss.csf39_workshop.models;

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
}
