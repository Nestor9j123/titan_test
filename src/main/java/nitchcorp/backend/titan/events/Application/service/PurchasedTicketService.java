package nitchcorp.backend.titan.events.Application.service;

import nitchcorp.backend.titan.events.Application.dtos.request.PurchaseTicketRequest;
import nitchcorp.backend.titan.events.Application.dtos.response.PurchasedTicketResponse;
import nitchcorp.backend.titan.events.domain.enums.TicketStatus;

import java.util.List;
import java.util.UUID;

public interface PurchasedTicketService {
    PurchasedTicketResponse purchaseTicket(PurchaseTicketRequest request);

    List<PurchasedTicketResponse> getAllPurchasedTickets();

    List<PurchasedTicketResponse> getPurchasedTicketsByEvent(UUID eventTrackingId);

    List<PurchasedTicketResponse> getPurchasedTicketsByBuyer(UUID buyerId);

    PurchasedTicketResponse getPurchasedTicketByTrackingId(UUID ticketTrackingId);

    PurchasedTicketResponse updateTicketStatus(UUID ticketTrackingId, TicketStatus status);

    PurchasedTicketResponse getPurchasedTicketByVoucherCode(String voucherCode);

    void cancelTicket(UUID ticketTrackingId);

    PurchasedTicketResponse validateTicketByVoucherCode(String voucherCode);

    PurchasedTicketResponse validateTicketByTrackingId(UUID ticketTrackingId);
}