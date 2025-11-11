package co.edu.uniquindio.stayNow.security;

import co.edu.uniquindio.stayNow.services.implementation.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter{

    private final JWTUtils jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        // Obtener el token del header de la solicitud
        String token = getToken(request);
        System.out.println("🔍 Cookie JWT recibida: " + token);
        // Si no hay token, continuar con la cadena de filtros
        if (token == null) {
            chain.doFilter(request, response);
            return;
        }

        try {
            // Validar el token y obtener el payload
            Jws<Claims> payload = jwtUtil.parseJwt(token);
            String username = payload.getPayload().getSubject();

            // Si el usuario no está autenticado, crear un nuevo objeto de autenticación
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Crear un objeto UserDetails con el nombre de usuario y el rol
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Crear un objeto de autenticación y establecerlo en el contexto de seguridad
                UsernamePasswordAuthenticationToken authentication = new
                        UsernamePasswordAuthenticationToken(

                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println("USERNAME FROM JWT: " + username);
                System.out.println("AUTHORITIES: " + userDetails.getAuthorities());

            }
        } catch (Exception e) {
            // Si el token no es válido, enviar un error 401
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            return;
        }

        // Continuar con la cadena de filtros
        chain.doFilter(request, response);
    }

    private String getToken(HttpServletRequest request) {
        // 1️⃣ Buscar token en cookies
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        // 2️⃣ Si no hay cookie, intentar obtenerlo del header Authorization
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }

        return null;
    }
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        // ⛔ NO aplicar el filtro a las rutas públicas
        return path.startsWith("/api/auth");
    }
}