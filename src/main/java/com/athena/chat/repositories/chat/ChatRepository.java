package com.athena.chat.repositories.chat;


import com.athena.chat.model.chat.Chat;
import com.athena.chat.model.entities.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findByParticipantes_Id(Long userId);

    Optional<Chat> findByGrupo(Group grupo);
}

