package com.athena.chat.repositories.chat;

import com.athena.chat.model.sql.ChatDocumentSQL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatDocumentSQLRepository extends JpaRepository<ChatDocumentSQL, Long> {
}

