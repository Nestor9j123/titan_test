package nitchcorp.backend.titan.events.Application.mappers;


import io.jsonwebtoken.lang.Assert;
import nitchcorp.backend.titan.events.Application.dtos.request.OptionRequest;
import nitchcorp.backend.titan.events.Application.dtos.response.OptionResponse;
import nitchcorp.backend.titan.events.domain.model.Options;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OptionsMapper {

    public OptionResponse toResponse(Options entity){

        Assert.notNull(entity , "entity null  to Option mapper");

        return OptionResponse.builder()
                .name(entity.getName())
                .trackingId(entity.getTrackingId())
                .build();

    }

    public Options toEntity(OptionRequest request){
        Assert.notNull(request , "request null to Option mapper");

        return  Options.builder()
                .name(request.name())
                .build();
    }


    public List<OptionResponse> toResponseList(List<Options> list){
        Assert.notNull( list , "listEntity null  to Option mapper");

        return list.stream()
                .map(this::toResponse)
                .toList() ;
    }
}
