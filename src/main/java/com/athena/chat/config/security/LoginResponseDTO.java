package com.athena.chat.config.security;

import com.athena.chat.dto.simpledto.UserSimpleDTO;
import org.springframework.http.ResponseCookie;

public record LoginResponseDTO (UserSimpleDTO userDTO, String cookie) {}
