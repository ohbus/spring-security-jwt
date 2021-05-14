package xyz.subho.springjwt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import xyz.subho.springjwt.model.AuthenticationRequest;
import xyz.subho.springjwt.model.AuthenticationResponse;
import xyz.subho.springjwt.service.MyUserDetailsService;
import xyz.subho.springjwt.util.JwtUtil;

@RestController
public class AuthenticationController {
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private MyUserDetailsService myUserDetailsService;
	
	@Autowired
	private JwtUtil jwtUtil;

	@PostMapping("/authenticate")
	public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception	{
		
		try {
			authenticationManager
				.authenticate(
						new UsernamePasswordAuthenticationToken(
								authenticationRequest.getUsername(),
								authenticationRequest.getPassword()
						)
				);
		} catch (BadCredentialsException e) {
			throw new Exception("Incorrect username or password", e);
			
		}
		
		final var userDetails = myUserDetailsService.loadUserByUsername(authenticationRequest.getUsername());
		final String jwt = jwtUtil.generateToken(userDetails);
		
		return ResponseEntity.ok(new AuthenticationResponse(jwt));
		
	}
}
