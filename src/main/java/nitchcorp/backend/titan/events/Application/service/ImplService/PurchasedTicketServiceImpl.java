package nitchcorp.backend.titan.events.Application.service.ImplService;

import io.jsonwebtoken.lang.Assert;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nitchcorp.backend.titan.events.Application.dtos.request.PurchaseTicketRequest;
import nitchcorp.backend.titan.events.Application.dtos.response.PurchasedTicketResponse;
import nitchcorp.backend.titan.events.Application.mappers.PurchasedTicketMapper;
import nitchcorp.backend.titan.events.domain.enums.TicketStatus;
import nitchcorp.backend.titan.events.domain.model.PurchasedTicket;
import nitchcorp.backend.titan.events.domain.model.TicketTemplate;
import nitchcorp.backend.titan.events.Application.service.PurchasedTicketService;
import nitchcorp.backend.titan.events.infrastructure.PurchasedTicketRepository;
import nitchcorp.backend.titan.events.infrastructure.TicketTemplateRepository;
import nitchcorp.backend.titan.shared.securite.user.entities.User;
import nitchcorp.backend.titan.shared.securite.user.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service

@Transactional
@Slf4j
public class PurchasedTicketServiceImpl  implements PurchasedTicketService {

    private final PurchasedTicketRepository purchasedTicketRepository;
    private final TicketTemplateRepository templateRepository;
    private final PurchasedTicketMapper purchasedTicketMapper;
    private final QrCodeServiceImpl qrCodeService;
    private final UserRepository userRepository;

    public PurchasedTicketServiceImpl(PurchasedTicketRepository purchasedTicketRepository, TicketTemplateRepository templateRepository, PurchasedTicketMapper purchasedTicketMapper, QrCodeServiceImpl qrCodeService, UserRepository userRepository) {
        this.purchasedTicketRepository = purchasedTicketRepository;
        this.templateRepository = templateRepository;
        this.purchasedTicketMapper = purchasedTicketMapper;
        this.qrCodeService = qrCodeService;
        this.userRepository = userRepository;
    }

    @Override
    public PurchasedTicketResponse purchaseTicket(PurchaseTicketRequest request) {
        try {
            log.info("Achat de ticket par: {} pour le template: {}",
                    request.buyerId(), request.templateTrackingId());

            // Find the buyer user
            User buyer = userRepository.findByTrackingId(request.buyerId())
                    .orElseThrow(() -> new EntityNotFoundException("Buyer not found with ID: " + request.buyerId()));

            TicketTemplate template = templateRepository.findByTrackingId(request.templateTrackingId())
                    .orElseThrow(() -> new EntityNotFoundException("Template non trouvé: " + request.templateTrackingId()));


            if (!template.isAvailable()) {
                throw new IllegalStateException("Plus de tickets disponibles pour ce type");
            }

            if (template.getNumberOfAvailableTickets() != -1 &&
                    template.getNombreRestant() < request.nombreTicketAchete()) {
                throw new IllegalStateException("Seulement " + template.getNombreRestant() + " tickets disponibles");
            }

            // Créer le ticket acheté
            PurchasedTicket purchasedTicket = new PurchasedTicket();
            purchasedTicket.setTicketTrackingId(UUID.randomUUID());
            purchasedTicket.setBuyer(buyer);
            purchasedTicket.setNumberOfTicketsBought(request.nombreTicketAchete());
            purchasedTicket.setTicketTemplate(template);

            // Sauvegarder d'abord pour avoir l'entité complète
            PurchasedTicket savedTicket = purchasedTicketRepository.save(purchasedTicket);

            //Générer QR Code et Code Voucher
            QrCodeServiceImpl.TicketCodesResult codes = qrCodeService.generateTicketCodes(savedTicket);
            savedTicket.setQrCodeUrl(codes.getQrCodeUrl());
            savedTicket.setVoucherCode(codes.getVoucherCode());

            // Sauvegarder avec les codes générés
            savedTicket = purchasedTicketRepository.save(savedTicket);

            // Mettre à jour le compteur du template
            template.setNumberOfTicketsSold(template.getNumberOfTicketsSold() + request.nombreTicketAchete());
            templateRepository.save(template);

            log.info("Ticket acheté avec succès: {} - Voucher: {}",
                    savedTicket.getTicketTrackingId(), savedTicket.getVoucherCode());

            return purchasedTicketMapper.toResponse(savedTicket);

        } catch (Exception e) {
            log.error("Erreur lors de l'achat du ticket: {}", e.getMessage(), e);
            throw e;
        }
    }



    @Override
    public List<PurchasedTicketResponse> getAllPurchasedTickets() {
        return purchasedTicketRepository.findAll()
                .stream()
                .map(purchasedTicketMapper::toResponse)
                .toList();
    }

    @Override
    public List<PurchasedTicketResponse> getPurchasedTicketsByEvent(UUID eventTrackingId) {
        return purchasedTicketRepository.findAllByTicketTemplateEventTrackingId(eventTrackingId)
                .stream()
                .map(purchasedTicketMapper::toResponse)
                .toList();
    }
    @Override
    public List<PurchasedTicketResponse> getPurchasedTicketsByBuyer(UUID buyerId) {
        return purchasedTicketRepository.findAllByBuyerTrackingId(buyerId)
                .stream()
                .map(purchasedTicketMapper::toResponse)
                .toList();
    }

    @Override
    public PurchasedTicketResponse getPurchasedTicketByTrackingId(UUID ticketTrackingId) {

        Assert.notNull(ticketTrackingId , "make sure that tracking id not null");

        return null;
    }

    @Override
    public PurchasedTicketResponse updateTicketStatus(UUID ticketTrackingId, TicketStatus status) {
        PurchasedTicket ticket = purchasedTicketRepository.findByTicketTrackingId(ticketTrackingId)
                .orElseThrow(() -> new EntityNotFoundException("Ticket non trouvé: " + ticketTrackingId));

        ticket.setStatus(status);
        PurchasedTicket updatedTicket = purchasedTicketRepository.save(ticket);
        return purchasedTicketMapper.toResponse(updatedTicket);
    }

    @Override
    public PurchasedTicketResponse getPurchasedTicketByVoucherCode(String voucherCode) {
        return purchasedTicketRepository.findByVoucherCode(voucherCode)
                .map(purchasedTicketMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Ticket non trouvé avec le code voucher: " + voucherCode));
    }

    @Override
    public void cancelTicket(UUID ticketTrackingId) {
        PurchasedTicket ticket = purchasedTicketRepository.findByTicketTrackingId(ticketTrackingId)
                .orElseThrow(() -> new EntityNotFoundException("Ticket non trouvé: " + ticketTrackingId));

        // Supprimer le QR code du disque
       /* if (ticket.getQrCodeUrl() != null) {
            qrCodeService.deleteQrCode(ticket.getQrCodeUrl());
        }*/

        // Remettre les tickets dans le stock disponible
        TicketTemplate template = ticket.getTicketTemplate();
        template.setNumberOfTicketsSold(Math.max(0, template.getNumberOfTicketsSold() - ticket.getNumberOfTicketsBought()));
        templateRepository.save(template);

        // Marquer comme annulé
        ticket.setStatus(TicketStatus.CANCELLED);
        purchasedTicketRepository.save(ticket);
    }

    @Override
    public PurchasedTicketResponse validateTicketByVoucherCode(String voucherCode) {
        PurchasedTicket ticket = purchasedTicketRepository.findByVoucherCode(voucherCode)
                .orElseThrow(() -> new EntityNotFoundException("Ticket non trouvé avec le code voucher: " + voucherCode));

        if (ticket.getStatus() == TicketStatus.CANCELLED) {
            throw new IllegalStateException("Ticket annulé");
        }



        if(ticket.getStatus() == TicketStatus.USED) {
            throw new IllegalStateException("Ticket utilisé");
        }

        if(ticket.getStatus() == TicketStatus.EXPIRED) {
            throw new IllegalStateException("Ticket expiré");
        }

        ticket.setStatus(TicketStatus.USED);
        PurchasedTicket validatedTicket = purchasedTicketRepository.save(ticket);
        return purchasedTicketMapper.toResponse(validatedTicket);
    }

    @Override
    public PurchasedTicketResponse validateTicketByTrackingId(UUID ticketTrackingId) {
        PurchasedTicket ticket = purchasedTicketRepository.findByTicketTrackingId(ticketTrackingId)
                .orElseThrow(() -> new EntityNotFoundException("Ticket non trouvé: " + ticketTrackingId));

        if (ticket.getStatus() == TicketStatus.CANCELLED) {
            throw new IllegalStateException("Ticket annulé");
        }

        if(ticket.getStatus() == TicketStatus.USED) {
            throw new IllegalStateException("Ticket utilisé");
        }

        if(ticket.getStatus() == TicketStatus.EXPIRED) {
            throw new IllegalStateException("Ticket expiré");
        }

        ticket.setStatus(TicketStatus.USED);
        PurchasedTicket validatedTicket = purchasedTicketRepository.save(ticket);
        return purchasedTicketMapper.toResponse(validatedTicket);
    }
}
