package nitchcorp.backend.titan.food.Application.mappers;

import nitchcorp.backend.titan.food.Application.dtos.request.NotePlatRequest;
import nitchcorp.backend.titan.food.Application.dtos.request.PlatRequest;
import nitchcorp.backend.titan.food.Application.dtos.response.NotePlatResponse;
import nitchcorp.backend.titan.food.Application.dtos.response.PlatResponse;
import nitchcorp.backend.titan.food.Domain.models.Plat;
import nitchcorp.backend.titan.food.Domain.models.Restaurant;
import nitchcorp.backend.titan.food.Domain.enums.CategoriePlat;
import nitchcorp.backend.titan.food.Infrastructure.RestaurantRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PlatMapper {
    private final RestaurantRepository restaurantRepository;

    public PlatMapper(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    public Plat toEntity(PlatRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("La requete est null");
        }

        Plat plat = new Plat();
        plat.setTrackingId(UUID.randomUUID());
        Restaurant restaurant = restaurantRepository.findByTrackingId(request.tracking_restaurantId())
                .orElseThrow(() -> new IllegalArgumentException("Restaurant non trouvé"));
        plat.setRestaurant(restaurant);
        plat.setCategorie(CategoriePlat.PLAT_PRINCIPAL);
        plat.setDescription(request.description());
        plat.setName(request.name());
        plat.setPrix(request.prix());
        plat.setTempsDePreparation(request.tempsDePreparation());
        plat.setRate(request.rate());
        plat.setPrixFinal(0.0);
        plat.setImagesurl(request.imagesurl());
        return plat;
    }

    public Plat toEntity(NotePlatRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("la note est null");
        }

        Plat plat = new Plat();
        plat.setNote(request.note());
        return plat;
    }

    public PlatResponse toResponse(Plat plat) {
        if (plat == null) {
            throw new IllegalArgumentException("Le plat est null");
        }
        double prixFinal = plat.getPrix() * (1 - plat.getRate());

        return new PlatResponse(
                plat.getTrackingId(),
                plat.getRestaurant().getTrackingId(),
                plat.getName(),
                plat.getDescription(),
                plat.getCategorie(),
                plat.getPrix(),
                plat.getTempsDePreparation(),
                plat.getRate(),
                prixFinal,
                plat.getImagesurl()
        );
    }

    public NotePlatResponse toNoteResponse(Plat plat){
        if(plat == null){
            throw new IllegalArgumentException("le plat est null");
        }
        return new NotePlatResponse(plat.getNote());
    }

    public static Plat toEntityFromResponse(PlatRequest response) {
        if (response == null) {
            throw new IllegalArgumentException("La demande de réclamation est null");
        }

        Plat plat = new Plat();
        plat.setTrackingId(response.tracking_restaurantId());
        plat.setCategorie(CategoriePlat.valueOf(response.categorie()));
        plat.setDescription(response.description());
        plat.setName(response.name());
        plat.setPrix(response.prix());
        plat.setTempsDePreparation(response.tempsDePreparation());
        plat.setRate(response.rate());
        plat.setImagesurl(response.imagesurl());
        return plat;
    }

    public void updateEntityFromRequest(Plat entityToUpdate, PlatRequest request) {
        if (request == null) {
            return;
        }

        if (request.name() != null) {
            entityToUpdate.setName(request.name());
        }
        if (request.description() != null) {
            entityToUpdate.setDescription(request.description());
        }
        if (request.categorie() != null) {
            entityToUpdate.setCategorie(CategoriePlat.valueOf(request.categorie()));
        }
        if (request.prix() != 0) {
            entityToUpdate.setPrix(request.prix());
        }
        if (request.tempsDePreparation() != null) {
            entityToUpdate.setTempsDePreparation(request.tempsDePreparation());
        }
        if (request.rate() != 0) {
            entityToUpdate.setRate(request.rate());
        }
        if (request.imagesurl() != null) {
            entityToUpdate.setImagesurl(request.imagesurl());
        }
    }

}
