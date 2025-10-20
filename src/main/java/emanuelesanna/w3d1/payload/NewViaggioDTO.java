package emanuelesanna.w3d1.payload;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record NewViaggioDTO(
        @NotBlank(message = "La destinazione è obbligatoria.")
        @Size(min = 3, max = 20, message = "La destinazione deve avere tra 3 e 20 caratteri.")
        String destinazione,
        @NotNull(message = "La data è obbligatoria.")
        @FutureOrPresent(message = "La data del viaggio non può essere nel passato.")
        LocalDate data,
        @NotNull(message = "L'ID del dipendente è obbligatorio.")
        UUID dipendenteId) {
}
