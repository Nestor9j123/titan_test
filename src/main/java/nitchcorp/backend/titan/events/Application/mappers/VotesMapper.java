package nitchcorp.backend.titan.events.Application.mappers;


import io.jsonwebtoken.lang.Assert;
import nitchcorp.backend.titan.events.Application.dtos.request.VotesRequest;
import nitchcorp.backend.titan.events.Application.dtos.response.VotesResponse;
import nitchcorp.backend.titan.events.domain.model.Events;
import nitchcorp.backend.titan.events.domain.model.Options;
import nitchcorp.backend.titan.events.domain.model.Votes;
import nitchcorp.backend.titan.shared.securite.user.entities.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class VotesMapper {


    public VotesResponse toResponse(Votes entity){

        Assert.notNull(entity , "entity null  to  votes mapper");
        Assert.notNull(entity.getEvent() , "make sure that the event is not null");

        return VotesResponse.builder()
                .trackingId(entity.getTrackingId())
                .eventTrackingId(entity.getEvent().getTrackingId())
                .creatorId(entity.getCreator() != null ? entity.getCreator().getTrackingId() : null)
                .options(entity.getOptions())
                .question(entity.getQuestion())
                .statusVote(entity.isStatusVote())
                .build() ;
    }

    public Votes toEntity(VotesRequest request, Events events, User creator){
        Assert.notNull( request , "request null  to  votes mapper");
        Assert.notNull(events , "make sure that the event is not null");
        Assert.notNull(creator , "make sure that the creator is not null");

        return Votes.builder()
                .question(request.question())
                .event(events)
                .creator(creator)
                .options(
                        request.options()
                                .stream().map(option->
                                        Options.builder()
                                                .name(option.name())
                                                .trackingId(UUID.randomUUID())
                                                .build()
                                ).toList()
                )
                .statusVote(true)
                .build();
    }

    // Méthode de compatibilité pour les anciens appels sans User
    public Votes toEntity(VotesRequest request, Events events){
        Assert.notNull( request , "request null  to  votes mapper");
        Assert.notNull(events , "make sure that the event is not null");

        return Votes.builder()
                .question(request.question())
                .event(events)
                .options(
                        request.options()
                                .stream().map(option->
                                        Options.builder()
                                                .name(option.name())
                                                .trackingId(UUID.randomUUID())
                                                .build()
                                ).toList()
                )
                .statusVote(true)
                .build();
    }


    public List<VotesResponse> toResponseList(List<Votes> list){
        Assert.notNull(list  ,"list null  to votes mapper");
        return list.stream().map(this::toResponse).toList() ;
    }

    public Votes toEntityByUpdate(Votes votes , VotesRequest request , Events events){


            votes.getOptions().clear();

            List<Options> listNewOptions = request.options()
                    .stream()
                    .map(optionRequest ->
                        Options.builder()
                               .trackingId(UUID.randomUUID())
                               .name(optionRequest.name())
                               .build()
                    )
                    .toList() ;

            votes.setOptions(listNewOptions);
            votes.setEvent(events);

            return  votes ;

    }



}
