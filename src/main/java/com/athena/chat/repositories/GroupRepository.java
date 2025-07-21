package com.athena.chat.repositories;

import com.athena.chat.model.entities.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long> {}
