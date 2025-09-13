package nitchcorp.backend.titan.events.Application.service.ImplService;


import io.jsonwebtoken.lang.Assert;
import nitchcorp.backend.titan.events.Application.dtos.request.VotesRequest;
import nitchcorp.backend.titan.events.Application.dtos.response.VotesResponse;
import nitchcorp.backend.titan.events.Application.mappers.VotesMapper;
import nitchcorp.backend.titan.events.domain.model.Events;
import nitchcorp.backend.titan.events.domain.model.Options;
import nitchcorp.backend.titan.events.domain.model.Votes;
import nitchcorp.backend.titan.events.Application.service.VotesService;
import nitchcorp.backend.titan.events.infrastructure.EventsRepository;
import nitchcorp.backend.titan.events.infrastructure.VotesRepository;
import nitchcorp.backend.titan.shared.securite.user.entities.User;
import nitchcorp.backend.titan.shared.securite.user.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class VotesServiceImpl implements VotesService {

    private final VotesRepository repository ;
    private final EventsRepository eventsRepository ;
    private final UserRepository userRepository;
    private  final VotesMapper mapper ;

    public VotesServiceImpl(VotesRepository repository, EventsRepository eventsRepository, UserRepository userRepository, VotesMapper mapper) {
        this.repository = repository;
        this.eventsRepository = eventsRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    public VotesResponse create(VotesRequest request) {

        Assert.notNull(request  , "make sure that  request is not null");
        Assert.notNull(request.eventId() , "make sure that  eventId is not null");
        Assert.notNull(request.creatorId() , "make sure that  creatorId is not null");

        Events events = this.eventsRepository.findByTrackingId(request.eventId())
                .orElseThrow(()-> new IllegalArgumentException("this events do not exist"));

        User creator = this.userRepository.findByTrackingId(request.creatorId())
                .orElseThrow(() -> new IllegalArgumentException("Creator user not found"));

        Votes vote = this.mapper.toEntity(request , events, creator);
        vote.setTrackingId(UUID.randomUUID());
        vote = this.repository.save(vote);

        return this.mapper.toResponse(vote);
    }

    @Override
   @Transactional
    public VotesResponse update(VotesRequest request, UUID trackingIdVote) {

        Assert.notNull(trackingIdVote , "make sure that  vote tracking_id is not null");

        Assert.notNull(request , "make sure that request is not null");


        Votes votes = this.repository.getVotesByTrackingId(trackingIdVote)
                .orElseThrow(()-> new RuntimeException("vote not found or invalid tracking_id")) ;


        Events events = this.eventsRepository.findByTrackingId(request.eventId())
                .orElseThrow(()-> new IllegalArgumentException("this events do not exist"));




        votes.getOptions().clear();

        votes.getOptions().addAll(
                request.options().stream()
                .map(options -> Options.builder()
                        .trackingId(UUID.randomUUID())
                        .name(options.name())
                        .build()
                ).toList()
        ) ;




       // votes.setOptions(listOption);
        votes.setQuestion(request.question());



        Votes voteUpdated = this.repository.save(votes);


        return  this.mapper.toResponse(voteUpdated);






    }

    @Override
    public void delete(UUID trackingIdVotes) {

        Assert.notNull(trackingIdVotes , "make sure that  vote tracking not null");

        var vote =  this.repository.getVotesByTrackingId(trackingIdVotes)
                .orElseThrow(()-> new RuntimeException("vote not found or alrealdy deleted"));

        this.repository.delete(vote);
    }

    @Override
    public List<VotesResponse> listVotes() {
        return Optional.ofNullable( this.repository.getAllVotes())
                .orElse(Collections.emptyList())
                .stream().map(this.mapper::toResponse)
                .toList();
    }

    @Override
    public List<VotesResponse> listForOneEvent(UUID trackingIdEvent) {

        Assert.notNull(trackingIdEvent , "make sure that  event tracking id is not null");

        Events events = this.eventsRepository.findByTrackingId(trackingIdEvent)
                .orElseThrow(()-> new RuntimeException("event not found or tracking id invalid"));


        return  Optional.ofNullable(this.repository.getAllVotesByTrackingIdEvent(events.getId()))
                .orElse(Collections.emptyList())
                .stream()
                .map(this.mapper::toResponse)
                .toList() ;
    }

    @Override
    public VotesResponse getByTrackingId(UUID trackingIdVote) {
        Assert.notNull(trackingIdVote  , "make sure that vote tracking id is not null");

        var result =  this.repository.getVotesByTrackingId(trackingIdVote)
                .orElse(null) ;

        return result == null ? null : this.mapper.toResponse(result) ;
    }
}
