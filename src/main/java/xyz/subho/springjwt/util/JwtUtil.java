package xyz.subho.springjwt.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@SuppressWarnings("deprecation")
@Service
public class JwtUtil {
	
	private String SECRET_KEY = "secret";
	
	public String extractUsername(String token)	{
		return extractClaim(token, Claims::getSubject);
	}
	
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver)	{
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}
	
	public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
	
	public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
	
	public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }
	
	private String createToken(Map<String, Object> claims, String subject )	{
		
		return Jwts.builder()
				.setClaims(claims).setSubject(subject)
				.setIssuedAt(	new Date(System.currentTimeMillis()	) )
				.setExpiration(	new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 2) )
				.signWith(SignatureAlgorithm.PS512 , SECRET_KEY)
				.compact();
	}
	
	private boolean isTokenExpired(String token)	{
		return extractExpiration(token).before(new Date());
	}
	
	private Claims extractAllClaims(String token)	{
		return Jwts.parser()
				.setSigningKey(SECRET_KEY)
				.parseClaimsJws(token)
				.getBody();
	}

}
