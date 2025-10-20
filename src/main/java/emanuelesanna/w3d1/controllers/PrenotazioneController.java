package emanuelesanna.w3d1.controllers;

import emanuelesanna.w3d1.entities.Prenotazione;
import emanuelesanna.w3d1.exceptions.ValidationException;
import emanuelesanna.w3d1.payload.NewPrenotazioneDTO;
import emanuelesanna.w3d1.services.PrenotazioneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/prenotazioni")
public class PrenotazioneController {

    @Autowired
    private PrenotazioneService prenotazioneService;

    // 1 GET http://localhost:3001/prenotazioni 200 OK

    @GetMapping
    public Page<Prenotazione> findAll(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size,
                                      @RequestParam(defaultValue = "idPrenotazione") String sortBy) {
        return this.prenotazioneService.findAllPrenotazioni(page, size, sortBy);
    }

    // 2 POST http://localhost:3001/prenotazioni (+ payload) 201 CREATED

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Prenotazione createPrenotazione(@RequestBody @Validated NewPrenotazioneDTO payload, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            throw new ValidationException(validationResult.getFieldErrors()
                    .stream().map(fieldError -> fieldError.getDefaultMessage()).toList());
        }
        return this.prenotazioneService.savePrenotazione(payload);
    }

    // 3 GET http://localhost:3001/prenotazioni/{prenotazioneId} 200 OK

    @GetMapping("/{prenotazioneId}")
    public Prenotazione findById(@PathVariable UUID prenotazioneId) {
        return this.prenotazioneService.findById(prenotazioneId);
    }

    // 4 PUT http://localhost:3001/prenotazioni/{prenotazioneId} + payload 200 OK

    @PutMapping("/{prenotazioneId}")
    public Prenotazione findByIdAndUpdate(@PathVariable UUID prenotazioneId, @RequestBody @Validated NewPrenotazioneDTO payload, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            throw new ValidationException(validationResult.getFieldErrors()
                    .stream().map(fieldError -> fieldError.getDefaultMessage()).toList());
        }
        return this.prenotazioneService.findByIdAndUpdate(prenotazioneId, payload);
    }

    // 5 DELETE http://localhost:3001/prenotazioni/{prenotazioneId} 204 NC

    @DeleteMapping("/{prenotazioneId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void findByIdAndDelete(@PathVariable UUID prenotazioneId) {
        this.prenotazioneService.findByIdAndDelete(prenotazioneId);
    }
}
