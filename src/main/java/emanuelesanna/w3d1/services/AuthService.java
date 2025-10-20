package emanuelesanna.w3d1.services;

import emanuelesanna.w3d1.entities.Dipendente;
import emanuelesanna.w3d1.exceptions.UnauthorizedException;
import emanuelesanna.w3d1.payload.LoginDTO;
import emanuelesanna.w3d1.security.JWTTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private DipendenteService dipendenteService;
    @Autowired
    private JWTTools jwtTools;

    public String checkCredentialsAndGenerateToken(LoginDTO body) {
        // 1. Controllo credenziali
        // 1.1 Controllo nel DB se esiste un Dipendente con quell'indirizzo email (fornito nel body)
        Dipendente found = this.dipendenteService.findByEmail(body.email());

        // 1.2 Se esiste verifico che la sua password corrisponda a quella del body
        // 1.3 Se una delle 2 verifiche non va a buon fine --> 401
        if (found.getPassword().equals(body.password())) {
            // TODO: Migliorare gestione password
            // 2. Se credenziali OK --> Genero un access token
            // 3. Ritorno il token
            return jwtTools.createToken(found);
        } else {
            throw new UnauthorizedException("Credenziali errate!");
        }

    }
}
