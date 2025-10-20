package emanuelesanna.w3d1.security;

import emanuelesanna.w3d1.exceptions.UnauthorizedException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTFilter extends OncePerRequestFilter {
    // Estendendo OncePerRequestFilter sto dicendo che questa classe sarà compatibile con la FilterChain
    // Una caratteristica importante dei filtri è che hanno accesso alle richieste che arrivano e inoltre possono anche mandare delle risposte
    @Autowired
    private JWTTools jwtTools;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Questo metodo verrà eseguito ad ogni richiesta. Sarà lui a dover verificare il token

        // PIANO DI BATTAGLIA
        // 1. Verifichiamo se nella request esiste un header che si chiama Authorization, verifichiamo anche che sia fatto con il formato giusto
        // (Authorization: "Bearer 123j21389912391283..."). Se non c'è oppure se è nel formato sbagliato --> 401
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            throw new UnauthorizedException("Inserire il token nell'authorization header nel formato giusto!");

        // 2. Se l'header esiste, estraiamo il token da esso
        // "Bearer 123j21389912391283..."
        String accessToken = authHeader.replace("Bearer ", "");

        // 3. Verifichiamo se il token è valido, cioè controlleremo se è stato modificato oppure no, se non è scaduto e se non è malformato
        jwtTools.verifyToken(accessToken);

        // 4. Se tutto è OK passiamo la richiesta al prossimo (che può essere o un altro filtro o direttamente il controller)
        filterChain.doFilter(request, response); // .doFilter chiama il prossimo elemento della catena (o un altro filtro o il controller direttamente)

        // 5. Se qualcosa non va con il token --> 401
    }

    // Tramite l'override di questo metodo posso disabilitare il lavoro del filtro per determinati tipi di richieste
    // Ad esempio, posso disabilitare tutte le richieste dirette al controller /auth
    // Quindi tutte le richieste tipo /auth/login oppure /auth/register non richiederanno alcun token
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return new AntPathMatcher().match("/auth/**", request.getServletPath());
        // Tutti gli endpoint nel controller "/auth/" non verranno controllati dal filtro
    }
}
