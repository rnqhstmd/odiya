package org.example.odiya.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.odiya.auth.dto.SigninRequest;
import org.example.odiya.auth.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth API", description = "인증 관련 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입 API", description = "사용자가 회원가입을 요청합니다.")
    @PostMapping("/signup")
    public ResponseEntity<Void> signUp(@Valid @RequestBody SigninRequest request) {
        authService.signUp(request);
        return ResponseEntity.ok().build();
    }
}
