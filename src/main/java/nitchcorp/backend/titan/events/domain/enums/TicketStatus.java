package nitchcorp.backend.titan.events.domain.enums;

import org.springframework.modulith.NamedInterface;

@NamedInterface
public enum TicketStatus {
    ACTIVE,
    USED,
    CANCELLED,
    EXPIRED
}