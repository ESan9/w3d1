package emanuelesanna.w3d1.repositories;

import emanuelesanna.w3d1.entities.Dipendente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DipendenteRepository extends JpaRepository<Dipendente, UUID> {
    Optional<Dipendente> findByUsername(String username);

    Optional<Dipendente> findByEmail(String email);
}
