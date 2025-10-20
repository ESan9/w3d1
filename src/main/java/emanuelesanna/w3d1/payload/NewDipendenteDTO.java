package emanuelesanna.w3d1.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NewDipendenteDTO(
        @NotBlank(message = "L'username è obbligatorio.")
        @Size(min = 2, max = 10, message = "L'username deve avere tra 2 e 10 caratteri.")
        String username,
        @NotBlank(message = "L'username è obbligatorio.")
        @Size(min = 2, max = 50, message = "Il nome deve avere tra 2 e 50 caratteri.")
        String nome,
        @NotBlank(message = "Il cognome è obbligatorio.")
        @Size(min = 2, max = 50, message = "Il cognome deve avere tra 2 e 50 caratteri.")
        String cognome,
        @NotBlank(message = "L'email è obbligatoria.")
        @Email(message = "Il formato dell'email non è valido.")
        @Size(max = 100, message = "L'email non può superare i 100 caratteri.")
        String email,
        @NotBlank(message = "La password è obbligatoria!")
        @Size(min = 4, message = "La password deve avere minimo 4 caratteri")
        String password) {
}
