package nitchcorp.backend.titan.events.Application.service.ImplService;

import io.jsonwebtoken.lang.Assert;
import nitchcorp.backend.titan.events.Application.dtos.request.TicketTemplateRequest;
import nitchcorp.backend.titan.events.Application.dtos.response.TicketTemplateResponse;
import nitchcorp.backend.titan.events.Application.mappers.TicketTemplateMapper;
import nitchcorp.backend.titan.events.domain.model.Events;
import nitchcorp.backend.titan.events.domain.model.TicketTemplate;
import nitchcorp.backend.titan.events.Application.service.TicketTemplateService;
import nitchcorp.backend.titan.events.infrastructure.EventsRepository;
import nitchcorp.backend.titan.events.infrastructure.TicketTemplateRepository;
import nitchcorp.backend.titan.shared.securite.user.entities.User;
import nitchcorp.backend.titan.shared.securite.user.repositories.UserRepository;
import org.springframework.modulith.NamedInterface;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@NamedInterface
@Service
public class TicketTemplateServiceImpl implements TicketTemplateService {

    private final TicketTemplateRepository repository ;

    private final EventsRepository eventsRepository ; 

    private final UserRepository userRepository;

    private final TicketTemplateMapper mapper ;

    public TicketTemplateServiceImpl(TicketTemplateRepository repository, TicketTemplateMapper mapper, EventsRepository eventsRepository, UserRepository userRepository) {
        this.repository = repository;
        this.eventsRepository = eventsRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    public void delete(UUID trackingIdTicketTemplate) {
        Assert.notNull(trackingIdTicketTemplate  , "make sure that ticket_template tracking id is not null");

        TicketTemplate result  = this.repository.findByTrackingId(trackingIdTicketTemplate)
                .orElseThrow(()-> new RuntimeException("ticket_template not found or tracking id invalid"));

        this.repository.delete(result);

    }

    @Override
    public TicketTemplateResponse get(UUID trackingIdTicketTemplate) {
        Assert.notNull(trackingIdTicketTemplate  , "make sure that ticket_template tracking id is not null");

        TicketTemplate result =  this.repository.findByTrackingId(trackingIdTicketTemplate)
                .orElse(null);

        return  result == null ? null : this.mapper.toResponse(result);
    }

    @Override
    public List<TicketTemplateResponse> getAll() {
        return this.repository.getAll()
            .stream().map(this.mapper::toResponse)
            .toList() ; 
    }

    @Override
    public TicketTemplateResponse create(TicketTemplateRequest request) {

        Assert.notNull(request.creatorId(), "make sure that creatorId is not null");

        Events event = this.eventsRepository.findByTrackingId(request.eventTrackingId())
            .orElseThrow(()-> new RuntimeException("event not found or tracking id invalid")) ;
        
        User creator = this.userRepository.findByTrackingId(request.creatorId())
            .orElseThrow(() -> new IllegalArgumentException("Creator user not found"));
        
        TicketTemplate entity = this.mapper.toEntity(request, event, creator) ; 

        entity.setTrackingId(UUID.randomUUID());

        entity =  this.repository.save(entity) ; 

        return  this.mapper.toResponse(entity);
    }

    @Override
    public TicketTemplateResponse update(TicketTemplateRequest request, UUID trackingIdTicket) {

        Assert.notNull(trackingIdTicket  ,  "make sure that ticket tracking id is not null");
        Assert.notNull(request , "make sure that request is not null");

        TicketTemplate ticket = this.repository.findByTrackingId(trackingIdTicket)
            .orElseThrow(()-> new RuntimeException("ticket_template not found or tracking id invalid")) ; 
        
        Events events = this.eventsRepository.findByTrackingId(request.eventTrackingId())
            .orElseThrow( ()->new RuntimeException("envent not found or tracking id invalid")) ; 

        
        ticket = this.mapper.toEntityByUpdate(ticket, request, events) ; 

        var result = this.repository.save(ticket)  ; 
        return  this.mapper.toResponse(result);
    }

    @Override
    public List<TicketTemplateResponse> getAllForOneEvent(UUID trackingIdEvent) {

        Assert.notNull(trackingIdEvent , "make sure that  tracking id envent is not null");


        return  this.repository.findAllByEventTrackingId(trackingIdEvent)
            .stream()
            .map(this.mapper::toResponse)
            .toList();
    }
}
