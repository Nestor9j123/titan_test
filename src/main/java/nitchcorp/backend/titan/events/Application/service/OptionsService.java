package nitchcorp.backend.titan.events.Application.service;

import java.util.List;
import java.util.UUID;

import nitchcorp.backend.titan.events.Application.dtos.request.OptionRequest;
import nitchcorp.backend.titan.events.Application.dtos.response.OptionResponse;

public interface OptionsService {

    OptionResponse create(OptionRequest request) ; 

    List<OptionResponse> getAll() ;  

    void delete(UUID trackingIdOption) ;

    OptionResponse create(OptionRequest  request, UUID trackingIdOption) ;


    OptionResponse get(UUID trackingIdOptoin );

}
