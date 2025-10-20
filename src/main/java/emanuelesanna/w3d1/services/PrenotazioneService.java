package emanuelesanna.w3d1.services;

import emanuelesanna.w3d1.entities.Prenotazione;
import emanuelesanna.w3d1.entities.Viaggio;
import emanuelesanna.w3d1.exceptions.NotFoundException;
import emanuelesanna.w3d1.payload.NewPrenotazioneDTO;
import emanuelesanna.w3d1.repositories.PrenotazioneRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class PrenotazioneService {

    @Autowired
    private PrenotazioneRepository prenotazioneRepository;

    @Autowired
    private ViaggioService viaggioService;

    public Prenotazione savePrenotazione(NewPrenotazioneDTO payload) {

        Viaggio viaggio = viaggioService.findById(payload.viaggioId());

        Prenotazione newPrenotazione = new Prenotazione(
                payload.dataRichiesta(),
                payload.preferenze(),
                payload.note()
        );

        newPrenotazione.setViaggio(viaggio);

        Prenotazione savedPrenotazione = prenotazioneRepository.save(newPrenotazione);

        log.info("Prenotazione con id: {} salvata per il viaggio {}",
                savedPrenotazione.getIdPrenotazione(), viaggio.getIdViaggio());

        return savedPrenotazione;
    }

    public Prenotazione findById(UUID prenotazioneId) {
        return this.prenotazioneRepository.findById(prenotazioneId).orElseThrow(() -> new NotFoundException(prenotazioneId));
    }

    public Page<Prenotazione> findAllPrenotazioni(int page, int size, String sortBy) {
        if (size > 30) size = 30;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return this.prenotazioneRepository.findAll(pageable);
    }

    public Prenotazione findByIdAndUpdate(UUID prenotazioneId, NewPrenotazioneDTO payload) {
        Prenotazione trovata = this.findById(prenotazioneId);
        Viaggio nuovoViaggio = trovata.getViaggio();
        if (!trovata.getViaggio().getIdViaggio().equals(payload.viaggioId())) {
            nuovoViaggio = viaggioService.findById(payload.viaggioId());
        }
        trovata.setDataRichiesta(payload.dataRichiesta());
        trovata.setPreferenze(payload.preferenze());
        trovata.setNote(payload.note());
        trovata.setViaggio(nuovoViaggio);

        Prenotazione modificata = this.prenotazioneRepository.save(trovata);
        log.info("Prenotazione con id {} aggiornata per il viaggio {}.", prenotazioneId, nuovoViaggio.getIdViaggio());
        return modificata;
    }


    public void findByIdAndDelete(UUID prenotazioneId) {
        Prenotazione found = this.findById(prenotazioneId);
        this.prenotazioneRepository.delete(found);
        log.info("Prenotazione con ID {} cancellata.", prenotazioneId);
    }
}


