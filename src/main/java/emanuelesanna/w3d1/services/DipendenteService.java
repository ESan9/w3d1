package emanuelesanna.w3d1.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import emanuelesanna.w3d1.entities.Dipendente;
import emanuelesanna.w3d1.entities.Prenotazione;
import emanuelesanna.w3d1.entities.Viaggio;
import emanuelesanna.w3d1.exceptions.BadRequestException;
import emanuelesanna.w3d1.exceptions.NotFoundException;
import emanuelesanna.w3d1.payload.NewDipendenteDTO;
import emanuelesanna.w3d1.repositories.DipendenteRepository;
import emanuelesanna.w3d1.repositories.PrenotazioneRepository;
import emanuelesanna.w3d1.repositories.ViaggioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class DipendenteService {

    //    Check per l'immagine
    private static final long MAX_SIZE = 5 * 1024 * 1024;
    private static final List<String> ALLOWED_TYPES = List.of("image/png", "image/jpeg");

    @Autowired
    private Cloudinary imageUp;

    @Autowired
    private DipendenteRepository dipendenteRepository;

    @Autowired
    private ViaggioRepository viaggioRepository;

    @Autowired
    private PrenotazioneRepository prenotazioneRepository;

    //    Non servirebbe esplicitamente ma lo faccio
    public Page<Dipendente> findAllDipendenti(int dipendentiNumber, int dipendentiSize, String sortBy) {
        if (dipendentiSize > 30) dipendentiSize = 30;
        Pageable pageable = PageRequest.of(dipendentiNumber, dipendentiSize, Sort.by(sortBy).ascending());
        return this.dipendenteRepository.findAll(pageable);
    }

    //    Salvo
    public Dipendente saveDipendente(NewDipendenteDTO payload) {
        this.dipendenteRepository.findByUsername(payload.username()).ifPresent(dipendente -> {
                    throw new BadRequestException("L'username " + dipendente.getUsername() + " è già in uso!");
                }
        );

        this.dipendenteRepository.findByEmail(payload.email()).ifPresent(dipendente -> {
            throw new BadRequestException("L'email " + dipendente.getEmail() + " è già registrata!");
        });

        Dipendente newDipendente = new Dipendente(payload.username(), payload.nome(), payload.cognome(), payload.email(), payload.password());
        newDipendente.setImmagineProfilo("https://ui-avatars.com/api/?name=" + payload.nome());

        Dipendente savedDipendente = this.dipendenteRepository.save(newDipendente);

        log.info("Il dipendente con id: " + savedDipendente.getIdDipendente() + " è stato salvato correttamente");
        return savedDipendente;
    }

    //    Cerco per id
    public Dipendente findById(UUID dipendenteId) {
        return this.dipendenteRepository.findById(dipendenteId).orElseThrow(() -> new NotFoundException(dipendenteId));
    }

    public Dipendente findByIdAndUpdate(UUID dipendenteId, NewDipendenteDTO payload) {

        Dipendente found = this.findById(dipendenteId);

        if (!found.getUsername().equals(payload.username())) {
            this.dipendenteRepository.findByUsername(payload.username()).ifPresent(dipendente -> {
                        throw new BadRequestException("L'username " + dipendente.getUsername() + " è già in uso!");
                    }
            );
        }

        if (!found.getEmail().equalsIgnoreCase(payload.email())) {
            this.dipendenteRepository.findByEmail(payload.email()).ifPresent(dipendente -> {

                if (!dipendente.getIdDipendente().equals(dipendenteId)) {
                    throw new BadRequestException("L'email " + payload.email() + " è già registrata!");
                }
            });
        }
        found.setUsername(payload.username());
        found.setNome(payload.nome());
        found.setCognome(payload.cognome());
        found.setEmail(payload.email());
        found.setImmagineProfilo("https://ui-avatars.com/api/?name=" + payload.nome());

        Dipendente modifiedDipendente = this.dipendenteRepository.save(found);

        log.info("Il dipendente con id " + modifiedDipendente.getIdDipendente() + " è stato modificato correttamente");

        return modifiedDipendente;
    }

    public void findByIdAndDelete(UUID dipendenteId) {
        Dipendente found = this.findById(dipendenteId);
        //    Essendo legati da relazioni questo è il metodo corretto che ho pensato
        List<Viaggio> viaggiFound = this.viaggioRepository.findByDipendenteIdDipendente(dipendenteId);
        for (Viaggio viaggio : viaggiFound) {
            List<Prenotazione> prenotazioniDaCancellare = this.prenotazioneRepository.findByViaggioIdViaggio(viaggio.getIdViaggio());

            if (!prenotazioniDaCancellare.isEmpty()) {
                this.prenotazioneRepository.deleteAll(prenotazioniDaCancellare);
            }
        }
        this.viaggioRepository.deleteAll(viaggiFound);
        this.dipendenteRepository.delete(found);
    }

    //    Upload avatar

    public Dipendente uploadAvatar(UUID dipendenteId, MultipartFile file) {

        if (file.isEmpty()) throw new BadRequestException("File vuoto!");
        if (file.getSize() > MAX_SIZE) throw new BadRequestException("La dimensione del file super quella massima");
        if (!ALLOWED_TYPES.contains(file.getContentType()))
            throw new BadRequestException("I formati permessi sono png e jpeg!");

        Dipendente foundDipendente = this.findById(dipendenteId);

        try {

            Map result = imageUp.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String imageURL = (String) result.get("url");

            foundDipendente.setImmagineProfilo(imageURL);
            Dipendente modifiedDipendente = this.dipendenteRepository.save(foundDipendente);

            log.info("L'avatar del dipendente con id {} è stato aggiornato correttamente. Nuovo URL: {}", dipendenteId, imageURL);

            return modifiedDipendente;

        } catch (IOException e) {

            log.error("Errore durante l'upload dell'immagine per il dipendente {}: {}", dipendenteId, e.getMessage());
            throw new BadRequestException("Errore del servizio di storage durante l'upload dell'immagine.");
        }
    }

    public Dipendente findByEmail(String email) {
        return this.dipendenteRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("L'utente con l'email " + email + " non è stato trovato"));
    }
}
