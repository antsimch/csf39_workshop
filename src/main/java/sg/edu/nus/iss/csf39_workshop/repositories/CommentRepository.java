package sg.edu.nus.iss.csf39_workshop.repositories;

import java.util.List;

import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class CommentRepository {
    
    private MongoTemplate template;

    public CommentRepository(MongoTemplate template) {
        this.template = template;
    }

    public List<Document> findCommentsByCharacterId(String id) {
        Query query = Query.query(
                Criteria.where("char_id").is(id));
        return template.find(
                query, 
                Document.class, 
                "comment");
    }

    public void insertComment(Document document) {
        Document newDoc = template.insert(document, "comment");
        System.out.println("\n\n" + "Object Id for comment >>>" + 
                newDoc.get("_id") + "\n\n");
    }
}
