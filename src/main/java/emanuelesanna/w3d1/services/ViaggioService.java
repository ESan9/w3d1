package emanuelesanna.w3d1.services;

import emanuelesanna.w3d1.entities.Dipendente;
import emanuelesanna.w3d1.entities.Prenotazione;
import emanuelesanna.w3d1.entities.Viaggio;
import emanuelesanna.w3d1.enums.StatoViaggio;
import emanuelesanna.w3d1.exceptions.BadRequestException;
import emanuelesanna.w3d1.exceptions.NotFoundException;
import emanuelesanna.w3d1.payload.NewViaggioDTO;
import emanuelesanna.w3d1.repositories.PrenotazioneRepository;
import emanuelesanna.w3d1.repositories.ViaggioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ViaggioService {

    @Autowired
    private ViaggioRepository viaggioRepository;

    @Autowired
    private PrenotazioneRepository prenotazioneRepository;

    @Autowired
    private DipendenteService dipendenteService;

    public Viaggio saveViaggio(NewViaggioDTO payload) {

        Dipendente dipendente = dipendenteService.findById(payload.dipendenteId());

        List<Viaggio> sovrapposizioni = this.viaggioRepository.findByDipendenteIdDipendenteAndData(
                dipendente.getIdDipendente(),
                payload.data()
        );

        if (!sovrapposizioni.isEmpty()) {
            throw new BadRequestException("Il dipendente " + dipendente.getUsername() +
                    " ha già un viaggio programmato per la data " + payload.data() + ".");
        }

        Viaggio newViaggio = new Viaggio(payload.destinazione(), payload.data(), StatoViaggio.IN_PROGRAMMA);
        newViaggio.setDipendente(dipendente);

        Viaggio savedViaggio = this.viaggioRepository.save(newViaggio);
        log.info("Viaggio con id: {} salvato per il dipendente {}", savedViaggio.getIdViaggio(), dipendente.getUsername());
        return savedViaggio;
    }

    //    Cerco per id
    public Viaggio findById(UUID viaggioId) {
        return this.viaggioRepository.findById(viaggioId).orElseThrow(() -> new NotFoundException(viaggioId));
    }

    public void findByIdAndDelete(UUID viaggioId) {
        Viaggio found = this.findById(viaggioId);
        //    Essendo legati da relazioni questo è il metodo corretto che ho pensato
        List<Prenotazione> prenotazioniDaCancellare = this.prenotazioneRepository.findByViaggioIdViaggio(found.getIdViaggio());

        if (!prenotazioniDaCancellare.isEmpty()) {
            this.prenotazioneRepository.deleteAll(prenotazioniDaCancellare);
        }
        this.viaggioRepository.delete(found);
        log.info("Viaggio con ID {} cancellato.", viaggioId);
    }

    public Page<Viaggio> findAllViaggi(int page, int size, String sortBy) {
        if (size > 30) size = 30;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return this.viaggioRepository.findAll(pageable);
    }

    public Viaggio updateStato(UUID viaggioId, StatoViaggio nuovoStato) {
        Viaggio foundViaggio = this.findById(viaggioId);

        if (foundViaggio.getStato() == StatoViaggio.COMPLETATO) {
            throw new BadRequestException("Non è possibile modificare lo stato di un viaggio che è già 'COMPLETATO'.");
        }

        foundViaggio.setStato(nuovoStato);
        Viaggio updatedViaggio = this.viaggioRepository.save(foundViaggio);
        log.info("Stato del viaggio {} aggiornato a: {}", viaggioId, nuovoStato);
        return updatedViaggio;
    }

    public Viaggio findByIdAndUpdate(UUID viaggioId, NewViaggioDTO payload) {
        Viaggio foundViaggio = this.findById(viaggioId);
        Dipendente nuovoDipendente = dipendenteService.findById(payload.dipendenteId());
//        Allora devo verificare se la data o il dipendente sono cambiati
        if (!foundViaggio.getData().equals(payload.data()) || !foundViaggio.getDipendente().getIdDipendente().equals(payload.dipendenteId())) {
            List<Viaggio> sovrapposizioni = this.viaggioRepository.findByDipendenteIdDipendenteAndData(
                            nuovoDipendente.getIdDipendente(),
                            payload.data()
                    )
                    .stream()
                    .filter(viaggio -> !viaggio.getIdViaggio().equals(viaggioId)).toList();
//            Se la lista non è vuota lancio l'eccezione
            if (!sovrapposizioni.isEmpty()) {
                throw new BadRequestException("Il dipendente " + nuovoDipendente.getUsername() +
                        " ha già un viaggio programmato per la data " + payload.data() + ".");
            }
        }

        foundViaggio.setDestinazione(payload.destinazione());
        foundViaggio.setData(payload.data());
        foundViaggio.setDipendente(nuovoDipendente);

        Viaggio updatedViaggio = this.viaggioRepository.save(foundViaggio);
        log.info("Viaggio con id {} modificato e riassegnato a {}.", viaggioId, nuovoDipendente.getUsername());
        return updatedViaggio;
    }
}

