package nitchcorp.backend.titan.food.Application.mappers;

import nitchcorp.backend.titan.food.Application.dtos.request.OptionPersonaliserRequest;
import nitchcorp.backend.titan.food.Application.dtos.response.OptionPersonaliserResponse;
import nitchcorp.backend.titan.food.Domain.models.OptionPersonaliser;
import nitchcorp.backend.titan.food.Domain.models.Plat;
import nitchcorp.backend.titan.food.Infrastructure.PlatRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OptionPersonaliserMapper {
    private PlatRepository platRepository;

    public OptionPersonaliserMapper(PlatRepository platRepository) {
        this.platRepository = platRepository;
    }

    public OptionPersonaliser toEntity(OptionPersonaliserRequest request){
        if(request == null){
            throw  new IllegalArgumentException(" La requete est null");
        }

        OptionPersonaliser optionPersonaliser = new OptionPersonaliser();
        optionPersonaliser.setTrackingId(UUID.randomUUID());
        optionPersonaliser.setName(request.name());
        optionPersonaliser.setDescription(request.description());
        optionPersonaliser.setPrix(request.prix());

        Plat plat = platRepository.findByTrackingId(request.tracking_platId()).
                orElseThrow(() -> new  IllegalArgumentException(" La plat n'existe pas"));

        optionPersonaliser.setPlat(plat);
        return  optionPersonaliser;
    }

    public OptionPersonaliserResponse toResponse(OptionPersonaliser option) {
        if (option == null) {
            new IllegalArgumentException(" L 'entite est null");
        }

        return new OptionPersonaliserResponse(
                option.getTrackingId(),
                option.getPlat().getTrackingId(),
                option.getName(),
                option.getDescription(),
                option.getPrix()
        );
    }

    public static OptionPersonaliser toEntityFromResponse(OptionPersonaliserResponse response){
        if(response == null){
            throw  new IllegalArgumentException("la commande est null");
        }

        OptionPersonaliser option = new OptionPersonaliser();

        option.setTrackingId(UUID.randomUUID());
        option.setName(response.name());
        option.setDescription(response.description());
        option.setPrix(response.prix());
        return option;
    }

}
