package org.example.odiya.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.odiya.auth.dto.request.LoginRequest;
import org.example.odiya.auth.dto.request.SigninRequest;
import org.example.odiya.auth.dto.response.LoginResponse;
import org.example.odiya.auth.service.AuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.example.odiya.common.util.CookieUtil.addCookie;
import static org.example.odiya.common.util.CookieUtil.clearCookies;

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

    @Operation(summary = "로그인 API", description = "사용자가 로그인을 요청합니다.")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request,
                                               HttpServletResponse response) {
        LoginResponse loginResponse = authService.login(request);
        addCookie(response, loginResponse.accessToken());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, response.getHeader("Set-Cookie"))
                .body(loginResponse);
    }

    @Operation(summary = "로그아웃 API", description = "사용자가 로그아웃을 요청합니다.")
    @GetMapping("/logout")
    public ResponseEntity<Void> logout(final HttpServletResponse response) {
        clearCookies(response);
        return ResponseEntity.ok().build();
    }
}
