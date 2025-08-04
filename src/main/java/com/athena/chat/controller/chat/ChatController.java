package com.athena.chat.controller.chat;

import com.athena.chat.dto.chat.ChatCreateDTO;
import com.athena.chat.dto.chat.ChatDTO;
import com.athena.chat.dto.mapper.ChatMapper;
import com.athena.chat.model.chat.Chat;
import com.athena.chat.model.entities.User;
import com.athena.chat.repositories.UserRepository;
import com.athena.chat.services.chat.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/{id}")
    public ResponseEntity<ChatDTO> buscarPorId(@PathVariable Long id) {
        ChatDTO chat = chatService.buscarPorId(id);

        return new ResponseEntity<>(chat, HttpStatus.FOUND);
    }

    @GetMapping
    public ResponseEntity<List<ChatDTO>> listarTodos() {
        List<ChatDTO> chat = chatService.listarTodos();

        return new ResponseEntity<>(chat, HttpStatus.OK);
    }
}
