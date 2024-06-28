package com.Eddy.security_one.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

import static javax.crypto.Cipher.SECRET_KEY;

@Service
public class JwtService {

    private static  final  String SECRET_KEY = "WEpKvzpzdrZzo99SbwW1VUNEmVj1J3he7ViFY7/7OxzU+bC/OEL" +
            "/3Uole+9bG34J56PP/Xf1IIpdA3leWHnJjx/XE8xN5DvLSvkLBfw1PF2W/4Q1QNj8nemVWsUWFA95LZygRKoiz7G" +
            "lmiNhRp7zqca+jU4N83x0ZGOZxPceLmblugIOTMCEQAd5uC2GI1k9usRCcFIjchiyBqfECsf98sUNYSH6hCo1OZWFDn" +
            "wxYZUMCFxLFxWwgNt2Kvv1ZhQuIeWoYO0poI/xlrgytLl8PjXT7ZYG9HrWE/UcGrVNPivWZ59L/nm36Db9zcqsGfz5a1" +
            "Vg2OOoqDBd9WLV5TW5zncw25J/BAGyWodsHYtdCpU=";

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(
            Map<String, Objects> extractClaims,
            UserDetails userDetails
    ){
       return Jwts
               .builder()
               .setClaims(extractClaims)
               .setSubject(userDetails.getUsername())
               .setIssuedAt(new Date(System.currentTimeMillis()))
               .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60* 24))
               .signWith(getSignInKey(), SignatureAlgorithm.HS256)
               .compact();
    }


    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {

        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {

        return extractClaim(token, Claims::getExpiration);
    }


    private Claims extractAllClaims (String token){
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte [] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
