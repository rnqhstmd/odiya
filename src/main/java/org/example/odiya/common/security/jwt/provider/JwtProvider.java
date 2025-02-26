package org.example.odiya.common.security.jwt.provider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.odiya.common.exception.UnauthorizedException;
import org.example.odiya.member.domain.Member;
import org.example.odiya.member.domain.MemberRole;
import org.example.odiya.member.service.MemberQueryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.example.odiya.common.exception.type.ErrorType.*;


@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final MemberQueryService memberqueryService;
    @Value("${security.jwt.token.access-secret-key}")
    private String key;
    @Value("${security.jwt.token.access-expire-length}")
    public int validityInMilliseconds;

    public String generateJwtToken(final String email) {
        Member member = memberqueryService.findExistingMemberByEmail(email);
        Claims claims = createClaims(email, member.getRole());
        Date now = new Date();
        long expiredDate = calculateExpirationDate(now);
        SecretKey secretKey = generateKey();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(expiredDate))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // JWT claims 생성
    private Claims createClaims(final String email, final MemberRole role) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(email));
        claims.put("role", role.name());
        return claims;
    }

    // JWT 만료 시간 계산
    private long calculateExpirationDate(final Date now) {
        return now.getTime() + validityInMilliseconds;
    }

    // Key 생성
    private SecretKey generateKey() {
        return Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
    }

    // 토큰의 유효성 검사
    public void isValidToken(final String jwtToken) {
        if (jwtToken == null || jwtToken.isEmpty()) {
            log.warn("토큰이 null이거나 비어 있습니다.");
            throw new UnauthorizedException(TOKEN_NOT_INCLUDED_ERROR);
        }

        try {
            SecretKey secretKey = generateKey();
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(jwtToken);

        } catch (ExpiredJwtException e) { // 어세스 토큰 만료
            throw new UnauthorizedException(TOKEN_EXPIRED_ERROR);
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            throw new UnauthorizedException(TOKEN_MALFORMED_ERROR, e.getMessage());
        }
    }

    // jwtToken 으로 Authentication 에 사용자 등록
    public void getAuthenticationFromToken(final String jwtToken) {
        Member loginMember = getUserByToken(jwtToken);
        setContextHolder(jwtToken, loginMember);
    }

    // token 으로부터 유저 정보 확인
    private Member getUserByToken(final String jwtToken) {
        String memberEmail = getUserEmailFromToken(jwtToken);
        return memberqueryService.findExistingMemberByEmail(memberEmail);
    }

    private void setContextHolder(String jwtToken, Member loginMember) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + loginMember.getRole().name()));
        log.debug("인증 정보 설정: email={}, role={}, authorities={}",
                loginMember.getEmail(), loginMember.getRole(), authorities);
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginMember, jwtToken, authorities);

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    // 토큰에서 user email 얻기
    public String getUserEmailFromToken(final String jwtToken) {
        SecretKey secretKey = generateKey();

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();

        return claims.getSubject();
    }

    // 토큰에서 role 얻기
    public MemberRole getRoleFromToken(final String jwtToken) {
        SecretKey secretKey = generateKey();

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();

        MemberRole role = MemberRole.valueOf(claims.get("role", String.class));
        log.info("role: {}", role);
        return role;
    }
}
