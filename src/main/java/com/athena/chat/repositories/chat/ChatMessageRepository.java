package com.athena.chat.repositories.chat;

import com.athena.chat.model.chat.ChatDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatMessageRepository extends MongoRepository<ChatDocument, String> {}
