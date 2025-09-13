package nitchcorp.backend.titan.events.Application.mappers;


import io.jsonwebtoken.lang.Assert;
import nitchcorp.backend.titan.events.Application.dtos.request.TicketTemplateRequest;
import nitchcorp.backend.titan.events.Application.dtos.response.TicketTemplateResponse;
import nitchcorp.backend.titan.events.domain.model.Events;
import nitchcorp.backend.titan.events.domain.model.TicketTemplate;
import nitchcorp.backend.titan.shared.securite.user.entities.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TicketTemplateMapper {

    public TicketTemplateResponse toResponse(TicketTemplate entity){
        Assert.notNull(entity , "entity null  to ticketTemplate mapper");
        Assert.notNull(entity.getEvent() , "make sure that the event is also registered");

        return  TicketTemplateResponse.builder()
                .trackingId(entity.getTrackingId())
                .eventTrackingId(entity.getEvent().getTrackingId())
                .creatorId(entity.getCreator() != null ? entity.getCreator().getTrackingId() : null)
                .isAvailable(entity.isAvailable())
                .nombreTicketDisponible(entity.getNumberOfAvailableTickets())
                .nombreTicketRestant(entity.getNombreRestant())
                .nombreTicketVendu(entity.getNumberOfTicketsSold())
                .price(entity.getPrice())
                .type(entity.getType().toString())
                .build();

    }


    public TicketTemplate toEntityByUpdate(
            TicketTemplate ticket , 
            TicketTemplateRequest request , 
            Events events 
    ){

        ticket.setEvent(events);
        ticket.setPrice(request.price());
        ticket.setNumberOfAvailableTickets(request.nombreTicketDisponible());
        ticket.setType(request.type() == null ? ticket.getType() : request.type()) ; 

        return ticket ; 

    }

    public TicketTemplate toEntity(TicketTemplateRequest request, Events events, User creator){
        Assert.notNull(request , "request null  to ticketTemplate mapper");
        Assert.notNull(creator , "creator null  to ticketTemplate mapper");

        return  TicketTemplate.builder()
                .event(events)
                .creator(creator)
                .type(request.type())
                .price(request.price())
                .numberOfAvailableTickets(request.nombreTicketDisponible())
                .numberOfTicketsSold(0)
                .build();
    }

    // Méthode de compatibilité pour les anciens appels sans User
    public TicketTemplate toEntity(TicketTemplateRequest request, Events events){
        Assert.notNull(request , "request null  to ticketTemplate mapper");

        return  TicketTemplate.builder()
                .event(events)
                .type(request.type())
                .price(request.price())
                .numberOfAvailableTickets(request.nombreTicketDisponible())
                .numberOfTicketsSold(0)
                .build();
    }


    public List<TicketTemplateResponse> toResponsesList(List<TicketTemplate> list){
        Assert.notNull(list , "list null  in TicketTemplate  mapper");

        return  list.stream().map(this::toResponse).toList() ;
    }
}
