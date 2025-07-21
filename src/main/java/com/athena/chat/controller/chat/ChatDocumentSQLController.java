package com.athena.chat.controller.chat;

import com.athena.chat.dto.chat.ChatDocumentDTO;
import com.athena.chat.dto.mapper.ChatDocumentMapper;
import com.athena.chat.model.sql.ChatDocumentSQL;
import com.athena.chat.repositories.GroupRepository;
import com.athena.chat.repositories.UserRepository;
import com.athena.chat.services.chat.ChatDocumentSQLService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class ChatDocumentSQLController {

    private final ChatDocumentSQLService service;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    @GetMapping
    public List<ChatDocumentDTO> listar() {
        return service.listar()
                .stream()
                .map(ChatDocumentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @PostMapping("/upload")
    public ResponseEntity<ChatDocumentDTO> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("senderId") Long senderId,
            @RequestParam("groupId") Long groupId
    ) throws IOException {
        String uploadDir = "uploads/";
        Path path = Paths.get(uploadDir + file.getOriginalFilename());
        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes());

        ChatDocumentSQL doc = new ChatDocumentSQL();
        doc.setFileName(file.getOriginalFilename());
        doc.setFileType(file.getContentType());
        doc.setFileUrl(path.toString());
        doc.setSender(userRepository.findById(senderId).orElse(null));
        doc.setGroup(groupRepository.findById(groupId).orElse(null));

        return ResponseEntity.ok(ChatDocumentMapper.toDTO(service.salvar(doc)));
    }
}
