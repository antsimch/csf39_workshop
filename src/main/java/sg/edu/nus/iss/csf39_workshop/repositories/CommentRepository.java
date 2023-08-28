package sg.edu.nus.iss.csf39_workshop.repositories;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CommentRepository {
    
    private MongoTemplate template;

    public CommentRepository(MongoTemplate template) {
        this.template = template;
    }

    
}
