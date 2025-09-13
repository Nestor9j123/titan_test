package nitchcorp.backend.titan.events.Application.service;

import nitchcorp.backend.titan.events.Application.dtos.request.TicketTemplateRequest;
import nitchcorp.backend.titan.events.Application.dtos.response.TicketTemplateResponse;

import java.util.List;
import java.util.UUID;

public interface TicketTemplateService {


    void delete(UUID trackingIdTicketTemplate) ;

    TicketTemplateResponse get(UUID trackingIdTicketTemplate) ;

    List<TicketTemplateResponse> getAll() ;

    TicketTemplateResponse create(TicketTemplateRequest request);

    TicketTemplateResponse update(TicketTemplateRequest request , UUID trackingIdTicket) ;


    List<TicketTemplateResponse> getAllForOneEvent(UUID trackingIdEvent) ;



}
