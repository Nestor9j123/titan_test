package nitchcorp.backend.titan.events.Application.service.ImplService;

import java.util.List;
import java.util.UUID;

import nitchcorp.backend.titan.events.domain.model.Options;
import nitchcorp.backend.titan.events.infrastructure.OptionsRepository;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.lang.Assert;
import nitchcorp.backend.titan.events.Application.dtos.request.OptionRequest;
import nitchcorp.backend.titan.events.Application.dtos.response.OptionResponse;
import nitchcorp.backend.titan.events.Application.mappers.OptionsMapper;
import nitchcorp.backend.titan.events.Application.service.OptionsService;


@Service
public class OptionsServiceImpl  implements OptionsService{

    private final OptionsMapper mapper ;

    private final OptionsRepository repository ;


    public OptionsServiceImpl(
            OptionsMapper mapper,
            OptionsRepository repository
    ){
        this.mapper = mapper ;
        this.repository = repository;
    }

    

    @Override
    public OptionResponse create(OptionRequest request) {

        Assert.notNull(request, "make sure that request is not null");

        Options option = this.mapper.toEntity(request);

        option.setTrackingId(UUID.randomUUID());
        this.repository.save(option);

        return  this.mapper.toResponse(option);
    }

    @Override
    public List<OptionResponse> getAll() {
        return  this.repository.getAll()
                .stream().map(this.mapper::toResponse)
                .toList() ;
    }

    @Override
    public void delete(UUID trackingIdOption) {
       Assert.notNull(trackingIdOption  , "make that  option tracking id is not null");

       Options result = this.repository.getByTrackingId(trackingIdOption)
               .orElseThrow(()-> new IllegalArgumentException("option not found or tracking id invalid"));

       this.repository.delete(result);
    }

    @Override
    public OptionResponse create(OptionRequest request, UUID trackingIdOption) {
        Assert.notNull(trackingIdOption , "make sure that option");
        Assert.notNull(request , "make sure that request is not null");

        Options option = this.mapper.toEntity(request) ;

        option.setTrackingId(UUID.randomUUID());

        option = this.repository.save(option) ;

        return this.mapper.toResponse(option) ;


    }

    @Override
    public OptionResponse get(UUID trackingIdOption) {
      Assert.notNull(trackingIdOption , "make sure that ");

      var option = this.repository.getByTrackingId(trackingIdOption)
              .orElseThrow(()-> new RuntimeException("option not found or tracking id invalid"));

      return this.mapper.toResponse(option);
    }

}