package emanuelesanna.w3d1.controllers;

import emanuelesanna.w3d1.entities.Dipendente;
import emanuelesanna.w3d1.exceptions.ValidationException;
import emanuelesanna.w3d1.payload.LoginDTO;
import emanuelesanna.w3d1.payload.LoginResponseDTO;
import emanuelesanna.w3d1.payload.NewDipendenteDTO;
import emanuelesanna.w3d1.services.AuthService;
import emanuelesanna.w3d1.services.DipendenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private DipendenteService dipendenteService;

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginDTO body) {
        return new LoginResponseDTO(authService.checkCredentialsAndGenerateToken(body));
    }

    // 2 POST http://localhost:3001/dipendenti (+ payload) 201 CREATED
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Dipendente createDipendente(@RequestBody @Validated NewDipendenteDTO payload, BindingResult validationResult) {
        // @Validated serve per "attivare" la validazione
        // BindingResult è un oggetto che contiene tutti gli errori e anche dei metodi comodi da usare tipo .hasErrors()
        if (validationResult.hasErrors()) {

            throw new ValidationException(validationResult.getFieldErrors()
                    .stream().map(fieldError -> fieldError.getDefaultMessage()).toList());
        }
        return this.dipendenteService.saveDipendente(payload);
    }
}
