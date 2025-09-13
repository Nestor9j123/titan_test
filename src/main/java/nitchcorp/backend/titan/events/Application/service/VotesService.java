package nitchcorp.backend.titan.events.Application.service;

import nitchcorp.backend.titan.events.Application.dtos.request.VotesRequest;
import nitchcorp.backend.titan.events.Application.dtos.response.VotesResponse;
import org.springframework.modulith.NamedInterface;

import java.util.List;
import java.util.UUID;

@NamedInterface
public interface VotesService {


    VotesResponse create(VotesRequest request)  ;

    VotesResponse update(VotesRequest request   , UUID trackingIdVote) ;


    void delete(UUID trackingIdVote) ;

    List<VotesResponse> listVotes();

    List<VotesResponse> listForOneEvent(UUID trackingIdEvent) ;

    VotesResponse getByTrackingId(UUID trackingIdVote) ;
}
