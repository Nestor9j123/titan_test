package nitchcorp.backend.titan.events.Application.mappers;


import io.jsonwebtoken.lang.Assert;
import nitchcorp.backend.titan.events.Application.dtos.request.PurchaseTicketRequest;
import nitchcorp.backend.titan.events.Application.dtos.response.PurchasedTicketResponse;
import nitchcorp.backend.titan.events.domain.model.PurchasedTicket;
import nitchcorp.backend.titan.events.domain.model.TicketTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PurchasedTicketMapper {


    public PurchasedTicketResponse toResponse(PurchasedTicket entity){
        Assert.notNull(entity , "entity null  to PurchasedTicket mapper");


        return PurchasedTicketResponse.builder()
                .ticketTrackingId(entity.getTicketTrackingId())
                .eventTrackingId(entity.getTicketTrackingId())
                .buyerName(entity.getBuyer().getFirstName() + " " + entity.getBuyer().getLastName())
                .prixUnitaire(entity.getTicketTemplate().getPrice())
                .eventTrackingId(entity.getTicketTemplate().getEvent().getTrackingId())
                .nombreTicketAchete(entity.getNumberOfTicketsBought())
                .prixTotal(entity.getTicketTemplate().getPrice() * entity.getNumberOfTicketsBought())
                .type(entity.getTicketTemplate().getType().toString())
                .qrCodeUrl(entity.getQrCodeUrl())
                .voucherCode(entity.getVoucherCode())
                .status(entity.getStatus().name())

                .build();
    }


        public List<PurchasedTicketResponse> toResponseList(List<PurchasedTicket> list){
            Assert.notNull(list  , "list null  to PurchasedTicket list in his mapper");


            return  list.stream()
                    .map(this::toResponse)
                    .toList() ;
        }


}
