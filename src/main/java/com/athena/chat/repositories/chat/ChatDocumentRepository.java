package com.athena.chat.repositories.chat;


import com.athena.chat.model.chat.ChatDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatDocumentRepository extends MongoRepository<ChatDocument, String> {
    List<ChatDocument> findByGroupId(Long groupId);
}
