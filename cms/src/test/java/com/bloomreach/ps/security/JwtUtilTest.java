package com.bloomreach.ps.security;

import org.apache.tika.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

public class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    public void init() {
        jwtUtil = new JwtUtil();
        final String secret = "secretsecretsecretsecretsecretsecretsecretsecretsecretsecret";
        jwtUtil.setSecret(secret);
    }

    public String getMockToken() throws Exception {
        return IOUtils.toString(getClass().getResourceAsStream("/com/bloomreach/ps/security/mock-jwt.txt"));
    }

    @Test
    public void whenTokenValid_thenValidUserReturned() throws Exception {
        final String token = getMockToken();
        final User user = jwtUtil.getUserFromToken(token);
        assertThat(user.getUsername()).isEqualTo("bariscan.vural@bloomreach.com");
    }

    @Test
    public void whenTokenInvalid_thenInvalidJwtTokenExceptionThrown() {
        final String token = "invalidtoken";
        assertThatExceptionOfType(InvalidJwtTokenException.class).isThrownBy(() -> jwtUtil.getUserFromToken(token));
    }

    @Test
    public void whenTokenValidButSecretWrong_thenInvalidJwtTokenExceptionThrown() {
        final String secret = "WrongSecretWrongSecretWrongSecretWrongSecret";
        jwtUtil.setSecret(secret);
        assertThatExceptionOfType(InvalidJwtTokenException.class).isThrownBy(() -> jwtUtil.getUserFromToken(getMockToken()));
    }
}
