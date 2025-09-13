package nitchcorp.backend.titan.shared.securite.exception.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    private Boolean status;
    private String message;
}