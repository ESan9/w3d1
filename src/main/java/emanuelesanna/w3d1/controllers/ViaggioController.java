package emanuelesanna.w3d1.controllers;

import emanuelesanna.w3d1.entities.Viaggio;
import emanuelesanna.w3d1.exceptions.ValidationException;
import emanuelesanna.w3d1.payload.ModificaStatoDTO;
import emanuelesanna.w3d1.payload.NewViaggioDTO;
import emanuelesanna.w3d1.services.ViaggioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/viaggi")
public class ViaggioController {

    @Autowired
    private ViaggioService viaggioService;

    // 1 PATCH http://localhost:3001/viaggi/id
    @PatchMapping("/{viaggioId}/stato")
    public Viaggio updateStato(@PathVariable UUID viaggioId, @RequestBody @Validated ModificaStatoDTO payload, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            throw new ValidationException(validationResult.getFieldErrors()
                    .stream().map(fieldError -> fieldError.getDefaultMessage()).toList());
        }
        return viaggioService.updateStato(viaggioId, payload.nuovoStato());
    }

    // 2 GET http://localhost:3001/viaggi 200 OK

    @GetMapping
    public Page<Viaggio> findAll(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 @RequestParam(defaultValue = "idViaggio") String sortBy) {
        return this.viaggioService.findAllViaggi(page, size, sortBy);
    }

    // 3 POST http://localhost:3001/viaggi (+ payload) 201 CREATED

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Viaggio createViaggio(@RequestBody @Validated NewViaggioDTO payload, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            throw new ValidationException(validationResult.getFieldErrors()
                    .stream().map(fieldError -> fieldError.getDefaultMessage()).toList());
        }
        return this.viaggioService.saveViaggio(payload);
    }

    // 4 GET http://localhost:3001/viaggi/{viaggioId} 200 OK

    @GetMapping("/{viaggioId}")
    public Viaggio findById(@PathVariable UUID viaggioId) {
        return this.viaggioService.findById(viaggioId);
    }

    // 5 PUT http://localhost:3001/viaggi/{viaggiId} + payload 200 OK

    @PutMapping("/{viaggioId}")
    public Viaggio findByIdAndUpdate(@PathVariable UUID viaggioId, @RequestBody @Validated NewViaggioDTO payload, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            throw new ValidationException(validationResult.getFieldErrors()
                    .stream().map(fieldError -> fieldError.getDefaultMessage()).toList());
        }
        return this.viaggioService.findByIdAndUpdate(viaggioId, payload);
    }

    // 6 DELETE http://localhost:3001/viaggi/{viaggioId} 204 NC

    @DeleteMapping("/{viaggioId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void findByIdAndDelete(@PathVariable UUID viaggioId) {
        this.viaggioService.findByIdAndDelete(viaggioId);
    }
}
