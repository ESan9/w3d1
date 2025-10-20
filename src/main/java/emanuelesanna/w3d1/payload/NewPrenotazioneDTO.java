package emanuelesanna.w3d1.payload;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record NewPrenotazioneDTO(
        @NotNull(message = "L'ID del viaggio è obbligatorio.")
        UUID viaggioId,

        @NotNull(message = "La data di richiesta è obbligatoria.")
        LocalDate dataRichiesta,

        @Size(max = 50, message = "Le preferenze non possono superare i 50 caratteri.")
        String preferenze,

        @Size(max = 100, message = "Le note non possono superare i 100 caratteri.")
        String note) {
}
