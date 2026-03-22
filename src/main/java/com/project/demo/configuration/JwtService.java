package com.project.demo.configuration;


import com.project.demo.helpers.AppConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtService {
    private static final String secretKey = AppConstants.SECURITY_KEY;

    public String extractUserEmail(String JWT_TOKEN) {
        try {
            return getClaim(JWT_TOKEN, Claims::getSubject);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JWT token");
        }
    }

    public String generateNativeToken(UserDetails userDetails) {
        try {
            Map<String, Object> claims = new HashMap<>();
            Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
            claims.put("roles", authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
            return generateToken(claims, userDetails);
        } catch (Exception e) {
            throw new RuntimeException("Error generating token", e);
        }
    }

    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        try {
            return Jwts.builder()
                    .setClaims(extraClaims)
                    .setSubject(userDetails.getUsername())
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() +  1000 * 60 * 60 * 24))
                    .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception e) {
            throw new RuntimeException("Error generating token", e);
        }
    }

    public <T> T getClaim(String JWT_TOKEN, Function<Claims, T> claimsResolver) {
        try {
            final Claims claims = extractClaims(JWT_TOKEN);
            return claimsResolver.apply(claims);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error extracting claims from token", e);
        }
    }

    public boolean isTokenValid(String JWT_TOKEN, UserDetails userDetails) {
        try {
            final String userEmail = extractUserEmail(JWT_TOKEN);
            return (userEmail.equals(userDetails.getUsername())) && (!isTokenExpired(JWT_TOKEN));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid token", e);
        }
    }

    private boolean isTokenExpired(String JWT_TOKEN) {
        return extractExpiration(JWT_TOKEN).before(new Date());
    }

    private Date extractExpiration(String JWT_TOKEN) {
        return getClaim(JWT_TOKEN, Claims::getExpiration);
    }

    public Claims extractClaims(String JWT_TOKEN) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(JWT_TOKEN)
                    .getBody();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JWT token", e);
        }
    }

    private Key getSignInKey() {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(secretKey);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error decoding signing key", e);
        }
    }

   String extractAccountEmail( HttpServletRequest request){
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        final String jwt = authHeader.substring(7);
        final String userEmail = extractUserEmail(jwt);

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            return  userEmail;
        }else {
            return "";
        }
    }
}
