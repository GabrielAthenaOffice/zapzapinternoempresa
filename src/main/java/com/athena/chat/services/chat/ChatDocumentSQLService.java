package com.athena.chat.services.chat;

import com.athena.chat.model.sql.ChatDocumentSQL;
import com.athena.chat.repositories.chat.ChatDocumentSQLRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChatDocumentSQLService {

    private final ChatDocumentSQLRepository repository;

    public ChatDocumentSQLService(ChatDocumentSQLRepository repository) {
        this.repository = repository;
    }

    public ChatDocumentSQL salvar(ChatDocumentSQL doc) {
        return repository.save(doc);
    }

    public List<ChatDocumentSQL> listar() {
        return repository.findAll();
    }

    public Optional<ChatDocumentSQL> buscarPorId(Long id) {
        return repository.findById(id);
    }
}
