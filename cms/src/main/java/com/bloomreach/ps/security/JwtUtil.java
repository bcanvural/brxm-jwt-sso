package com.bloomreach.ps.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;

public class JwtUtil {

    private String secret;

    public void setSecret(String secret) {
        this.secret = secret;
    }

    /**
     * Tries to parse specified String as a JWT token. If successful, returns User object with username, id and role prefilled (extracted from token).
     * If unsuccessful (token is invalid or not containing all required user properties), simply returns null.
     *
     * @param token the JWT token to parse
     * @return the User object extracted from specified token or null if a token is invalid.
     */
    public User getUserFromToken(String token) {
        try {
            final JwtParser parser = Jwts.parserBuilder().setSigningKey(secret).build();
            final Claims body = parser.parseClaimsJws(token).getBody();
            //TODO possibly extract more things from the token body and set it on a more custom user object. Depends on use case / project!
            return new User(body.get("email", String.class), "password", Collections.singletonList(new SimpleGrantedAuthority("dummyrole")));

        } catch (JwtException | ClassCastException e) {
            throw new InvalidJwtTokenException("Couldn't extract user from jwt token");
        }
    }

}