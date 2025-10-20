package emanuelesanna.w3d1.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Prenotazione {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private UUID idPrenotazione;
    private LocalDate dataRichiesta;
    private String preferenze;
    private String note;
    @ManyToOne
    @JoinColumn(name = "id_viaggio")
    private Viaggio viaggio;

    public Prenotazione(LocalDate dataRichiesta, String preferenze, String note) {
        this.dataRichiesta = dataRichiesta;
        this.preferenze = preferenze;
        this.note = note;
    }
}
