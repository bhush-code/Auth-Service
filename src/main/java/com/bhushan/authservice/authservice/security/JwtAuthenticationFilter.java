package com.bhushan.authservice.authservice.security;

import com.bhushan.authservice.authservice.entity.Role;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.persistence.Cacheable;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.hibernate.sql.ast.tree.expression.Over;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter  extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final  JwtService jwtService;


   @Override
   protected boolean shouldNotFilter(HttpServletRequest request)
   {
        String path=request.getServletPath();
        return path.startsWith("/user/v1/") || path.startsWith("/auth/v1/login") || path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs");
   }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
    throws ServletException, IOException
    {
      try {
          final String authHeader=request.getHeader("Authorization");

          if(authHeader==null || !authHeader.startsWith("Bearer ")){

              throw new AuthenticationException("Authorization header is missing or invalid"){};

          }

          String token=authHeader.substring(7);
          if (!jwtService.isTokenValid(token)) {
              sendError(response, "Invalid JWT token", HttpStatus.UNAUTHORIZED);
              return;
          }


          String email=jwtService.extractUsername(token);
          List<String> roles= jwtService.extractRoles(token);
          UsernamePasswordAuthenticationToken authenticationToken=new UsernamePasswordAuthenticationToken(email,null,roles.stream().map(SimpleGrantedAuthority::new).toList());
          SecurityContextHolder.getContext().setAuthentication(authenticationToken);
          filterChain.doFilter(request,response);
      }catch(ExpiredJwtException exception)
      {
          sendError(response,"JWT Token Expired",HttpStatus.UNAUTHORIZED);
      }
      catch (MalformedJwtException exception)
      {
          sendError(response,"Invalid Jwt Exception",HttpStatus.BAD_REQUEST);
      }
      catch (JwtException exception)
      {
          sendError(response,"JWT Error",HttpStatus.UNAUTHORIZED);
      }
      catch (AuthenticationException exception)
      {
          sendError(response, exception.getMessage(),HttpStatus.UNAUTHORIZED);
      }

    }

    private void sendError(HttpServletResponse response, String message, HttpStatus status) throws IOException
    {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.getWriter().write(
                new ObjectMapper().writeValueAsString(Map.of(
                                 "timeStamp", Instant.now(),
                                "error",message,
                                "status",status.value()
                        ))
        );

    }

}
