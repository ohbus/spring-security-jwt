package xyz.subho.springjwt.filer;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import xyz.subho.springjwt.service.MyUserDetailsService;
import xyz.subho.springjwt.util.JwtUtil;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
	
	@Autowired
	private MyUserDetailsService userDetailsService;
	
	@Autowired
	private JwtUtil jwtUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		final String authrizationHeader = request.getHeader("Authorization");
		
		String username = null;
		String jwt = null;
		
		if (authrizationHeader != null && authrizationHeader.startsWith("Bearer "))	{
			jwt = authrizationHeader.substring(7);
			username = jwtUtil.extractUsername(jwt);
		}
		
		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null)	{
			var userDetails = this.userDetailsService.loadUserByUsername(username);
			if (Boolean.TRUE.equals(jwtUtil.validateToken(jwt, userDetails)))	{
				
				var usernamePasswordAuthenticationToken
					= new UsernamePasswordAuthenticationToken(
							userDetails, null, userDetails.getAuthorities());
				usernamePasswordAuthenticationToken
					.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
			}
		}
		
		filterChain.doFilter(request, response);
	}
}
