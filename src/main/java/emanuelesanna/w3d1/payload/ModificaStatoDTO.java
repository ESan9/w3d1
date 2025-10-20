package emanuelesanna.w3d1.payload;

import emanuelesanna.w3d1.enums.StatoViaggio;
import jakarta.validation.constraints.NotNull;

public record ModificaStatoDTO(
        @NotNull(message = "Il nuovo stato è obbligatorio.")
        StatoViaggio nuovoStato) {
}
