package emanuelesanna.w3d1.payload;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorWithListDTO(
        String message,
        LocalDateTime timestamp,
        List<String> errorsList) {
}
