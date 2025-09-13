package nitchcorp.backend.titan.food.Application.services.ServiceImpl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import nitchcorp.backend.titan.food.Application.dtos.request.CommandeRequest;
import nitchcorp.backend.titan.food.Application.dtos.response.CommandeResponse;
import nitchcorp.backend.titan.food.Application.mappers.CommandeMapper;
import nitchcorp.backend.titan.food.Domain.models.Commande;
import nitchcorp.backend.titan.food.Domain.models.PlatCommande;
import nitchcorp.backend.titan.food.Infrastructure.CommandeRepository;
import nitchcorp.backend.titan.food.Infrastructure.PlatCommandeRepository;
import nitchcorp.backend.titan.food.Application.services.CommandeService;
import nitchcorp.backend.titan.food.Infrastructure.PlatRepository;
import nitchcorp.backend.titan.food.Infrastructure.RestaurantRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CommandeServiceImpl implements CommandeService {

    private final RestaurantRepository restaurantRepository;
    private final PlatRepository platRepository;
    private PlatCommandeRepository platCommandeRepository;
    private final CommandeRepository commandeRepository;
    private final CommandeMapper commandeMapper;
    private final ApplicationEventPublisher publisher;

    public CommandeServiceImpl(CommandeRepository commandeRepository, PlatCommandeRepository platCommandeRepository, CommandeMapper commandeMapper, ApplicationEventPublisher publisher, RestaurantRepository restaurantRepository, PlatRepository platRepository) {
        this.commandeRepository = commandeRepository;
        this.platCommandeRepository = platCommandeRepository;
        this.commandeMapper = commandeMapper;
        this.publisher = publisher;
        this.restaurantRepository = restaurantRepository;
        this.platRepository = platRepository;
    }

    @Override
    public CommandeResponse createCommande(CommandeRequest request) {
        Commande commande = commandeMapper.toEntity(request);
        Commande saved = commandeRepository.save(commande);
    /*
        List<PlatCommande> platCommandes = platCommandeRepository.findAllByCommande(saved.getTrackingId());

        for (PlatCommande platCommande : platCommandes) {
            Plat plat = platCommandeRepository.findPlatByPlatCommandeTrackingId(platCommande.getTrackingId())
                    .orElseThrow(() -> new RuntimeException("Plat non trouvé avec l'ID : " + platCommande.getTrackingId()));

            Restaurant restaurant = platRepository.findRestaurantByPlatTrackingId(plat.getTrackingId())
                    .orElseThrow(() -> new RuntimeException("Restaurant non trouvé avec l'ID : " + plat.getTrackingId()));

            publisher.publishEvent(new CommandeRequestEvent(
                    plat.getRestaurant().getName(),
                    saved.getTrackingId(),
                    request.addressLivaraison(),
                    restaurant.getLatitude(),
                    restaurant.getLongitude()
            ));

            break;
        }
     */

        return commandeMapper.toResponse(saved);
    }


    @Override
    public CommandeResponse getCommandeByTrackingId(UUID trackingId) {
        Commande commande = commandeRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée avec l'ID : " + trackingId));
        return commandeMapper.toResponse(commande);
    }

    @Override
    public List<CommandeResponse> getAllCommandes() {
        return commandeRepository.findAll()
                .stream()
                .map(commandeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CommandeResponse updateCommande(UUID trackingId, CommandeRequest request) {
        Commande existingCommande = commandeRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée avec l'ID : " + trackingId));

        existingCommande.setDateCommande(request.dateCommande());
        existingCommande.setPrixTotal(request.prixTotal());
        existingCommande.setAddressLivaraison(request.addressLivaraison());


        Commande updated = commandeRepository.save(existingCommande);
        return commandeMapper.toResponse(updated);
    }

    @Override
    public void deleteCommande(UUID trackingId) {
        Commande commande = commandeRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée avec l'ID : " + trackingId));
        commandeRepository.delete(commande);
    }

    @Transactional
    public Double updateCommandePrix(UUID trackingId) {
        Commande commande = commandeRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new EntityNotFoundException("La commande n'a pas été retrouvée dans la base"));

        List<PlatCommande> plats = platCommandeRepository.findAllByCommande(trackingId);
        double prix = 0.0;

        for (PlatCommande plat : plats) {
            prix += plat.getPrix();
        }

        commande.setPrixTotal(prix);
        commandeRepository.save(commande);

        return prix;
    }

}
