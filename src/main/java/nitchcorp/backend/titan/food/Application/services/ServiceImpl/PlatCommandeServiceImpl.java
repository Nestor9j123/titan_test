package nitchcorp.backend.titan.food.Application.services.ServiceImpl;


import jakarta.persistence.EntityNotFoundException;
import nitchcorp.backend.titan.food.Application.dtos.request.PlatCommandeRequest;
import nitchcorp.backend.titan.food.Application.dtos.response.OptionPersonaliserResponse;
import nitchcorp.backend.titan.food.Application.dtos.response.PlatCommandeResponse;
import nitchcorp.backend.titan.food.Application.mappers.PlatCommandeMapper;
import nitchcorp.backend.titan.food.Application.services.CommandeService;
import nitchcorp.backend.titan.food.Domain.events.CommandeRequestEvent;
import nitchcorp.backend.titan.food.Domain.models.Commande;
import nitchcorp.backend.titan.food.Domain.models.OptionPersonaliser;
import nitchcorp.backend.titan.food.Domain.models.PlatCommande;
import nitchcorp.backend.titan.food.Domain.models.Restaurant;
import nitchcorp.backend.titan.food.Infrastructure.CommandeRepository;
import nitchcorp.backend.titan.food.Infrastructure.OptionPersonaliserRepository;
import nitchcorp.backend.titan.food.Infrastructure.PlatCommandeRepository;
import nitchcorp.backend.titan.food.Application.services.OptionPersonaliserService;
import nitchcorp.backend.titan.food.Application.services.PlatCommandeService;
import nitchcorp.backend.titan.food.Infrastructure.PlatRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PlatCommandeServiceImpl implements PlatCommandeService {

    private final PlatCommandeRepository platCommandeRepository;
    private final PlatRepository platRepository;
    private final PlatCommandeMapper platCommandeMapper;
    private final OptionPersonaliserService optionPersonnaliserService;
    private  final OptionPersonaliserRepository optionPersonaliserRepository;
    private final CommandeRepository commandeRepository;
    private final ApplicationEventPublisher publisher;

    public PlatCommandeServiceImpl(
          PlatCommandeRepository platCommandeRepository,
          PlatCommandeMapper platCommandeMapper,
          OptionPersonaliserService optionPersonnaliserService,
          OptionPersonaliserRepository optionPersonaliserRepository,
          CommandeRepository commandeRepository,
          CommandeService commandeService,
          PlatRepository platRepository,
          ApplicationEventPublisher publisher
    ){
        this.commandeRepository = commandeRepository;
        this.platCommandeRepository = platCommandeRepository;
        this.platCommandeMapper = platCommandeMapper;
        this.optionPersonnaliserService = optionPersonnaliserService;
        this.optionPersonaliserRepository = optionPersonaliserRepository;
        this.platRepository = platRepository;
        this.publisher = publisher;
    }


    @Override
    @Transactional
    public PlatCommandeResponse createPlatCommande(PlatCommandeRequest request) {

        int count = 0;
        PlatCommande platCommande = platCommandeMapper.toEntity(request);
        List<OptionPersonaliser> options = new ArrayList<>();
        if (request.OptionIds() != null && !request.OptionIds().isEmpty()) {

            for( count = 0 ; count < platCommande.getOptionIdSize() ; count++  ){
                OptionPersonaliser option = optionPersonaliserRepository.findById(platCommande.getOptionIds().get(count))
                        .orElseThrow(() -> new EntityNotFoundException("l'option indexé n' a pas éte retrouver"));
                options.add(option);
            }

            for (OptionPersonaliser option : options) {
                platCommande.addOptionId(option.getId());
            }
        }

        PlatCommande savedPlatCommande = platCommandeRepository.save(platCommande);

        List<PlatCommande> platsDeLaCommande = platCommandeRepository.findAllByCommande(
                savedPlatCommande.getCommande().getTrackingId()
        );

        if (platsDeLaCommande.size() == 1) {
            Restaurant restaurant = platRepository.findRestaurantByPlatTrackingId(
                    savedPlatCommande.getPlat().getTrackingId()
            ).orElseThrow(() -> new EntityNotFoundException("Restaurant non trouvé"));


            Double latitude = restaurant.getLatitude() != null ? restaurant.getLatitude() : 0.0;
            Double longitude = restaurant.getLongitude() != null ? restaurant.getLongitude() : 0.0;

            publisher.publishEvent(new CommandeRequestEvent(
                    savedPlatCommande.getPlat().getRestaurant().getName(),
                    savedPlatCommande.getCommande().getTrackingId(),
                    savedPlatCommande.getCommande().getAddressLivaraison(),
                    latitude,
                    longitude
            ));
            System.out.println("CommandeRequestEvent published");
        }

        updateCommandeprice(platCommande.getCommande().getTrackingId());

        List<OptionPersonaliserResponse> allOptions = optionPersonnaliserService.getOptionsForPlatCommande(savedPlatCommande.getTrackingId());
        return platCommandeMapper.toResponse(savedPlatCommande,allOptions);
    }


    @Override
    @Transactional
    public PlatCommandeResponse updatePlatCommande(UUID trackingId, PlatCommandeRequest request) {
        PlatCommande existingPlatCommande = platCommandeRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new EntityNotFoundException("PlatCommande non trouvé avec l'ID : " + trackingId));

        existingPlatCommande.setQuantite(request.quantite());
        existingPlatCommande.setPrix(request.prix());
        existingPlatCommande.setTempsPreparation(request.tempsPreparation());

        if (existingPlatCommande.getPlat().getTrackingId()!= request.tracking_platId()||
                existingPlatCommande.getCommande().getTrackingId()!= request.tracking_commandeId() ) {


            PlatCommande updatedPlatCommande = platCommandeMapper.toEntity(request);
            updatedPlatCommande.setId(existingPlatCommande.getId());
            updatedPlatCommande.setTrackingId(existingPlatCommande.getTrackingId());

            PlatCommande savedPlatCommande = platCommandeRepository.save(updatedPlatCommande);
            return platCommandeMapper.toResponse(savedPlatCommande);
        }

        PlatCommande updatedPlatCommande = platCommandeRepository.save(existingPlatCommande);
        return platCommandeMapper.toResponse(updatedPlatCommande);
    }

    @Override
    @Transactional
    public void deletePlatCommande(UUID trackingId) {
        PlatCommande platCommande = platCommandeRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new EntityNotFoundException("PlatCommande non trouvé avec l'ID : " + trackingId));
        platCommandeRepository.delete(platCommande);
        updateCommandeprice(platCommande.getCommande().getTrackingId());
    }

    @Override
    @Transactional(readOnly = true)
    public PlatCommandeResponse getPlatCommande(UUID trackingId) {
        PlatCommande platCommande = platCommandeRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new EntityNotFoundException("PlatCommande non trouvé avec l'ID : " + trackingId));
        return platCommandeMapper.toResponse(platCommande);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlatCommandeResponse> getAllPlatCommandes() {
        return platCommandeRepository.findAll().stream()
                .map(platCommandeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PlatCommandeResponse> getAllPlatCommmandeByCommande(UUID trackingId) {
        List<PlatCommandeResponse> platCommandeResponses = new ArrayList<>();
        Commande commande = commandeRepository.findByTrackingId(trackingId)
                .orElseThrow(()-> new EntityNotFoundException("la commade n'a pas été retrouver dasn la base de donnée "));

        List<PlatCommande> plats = platCommandeRepository.findAllByCommande(trackingId);
        for(PlatCommande plat : plats){
            PlatCommandeResponse platCommandeResponse =  platCommandeMapper.toResponse(plat);
            platCommandeResponses.add(platCommandeResponse);
        }
        return platCommandeResponses;
    }

    @Override
    public PlatCommandeResponse addOptionToPlatCommande(UUID trackingId, UUID optionId) {
        double price = 0.0;
        PlatCommande platCommande = platCommandeRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new EntityNotFoundException("Plat commande non trouvé"));

        OptionPersonaliser option = optionPersonaliserRepository.findByTrackingId(optionId)
                .orElseThrow(() -> new EntityNotFoundException("l'option selectionner n'as pas ete selectionné "));

        platCommande.addOptionId(option.getId());

        price = platCommande.getPlat().getPrix() + option.getPrix();

        platCommande.setPrix(price);

        platCommandeRepository.save(platCommande);
        List<OptionPersonaliserResponse> allOptions = optionPersonnaliserService.getOptionsForPlatCommande(trackingId);
        return  platCommandeMapper.toResponse(platCommande,allOptions);

    }

    @Override
    public void deleteOptionToPlatCommande(UUID trackingId , UUID optionTrackingId) {
        PlatCommande platCommande = platCommandeRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new EntityNotFoundException("Plat commander Not found"));

        OptionPersonaliser option = optionPersonaliserRepository.findByTrackingId(optionTrackingId)
                .orElseThrow(() -> new EntityNotFoundException("l'option selectionner n'as pas ete selectionné "));

        optionPersonnaliserService.deleteOption(optionTrackingId);

        platCommande.removeOptionId(option.getId());
        platCommandeRepository.save(platCommande);
    }

    @Override
    public double getTotalPrice(PlatCommande platCommande) {

        double prix = platCommande.getPlat().getPrix();
        List<OptionPersonaliser> options = new ArrayList<>();
        int count = 0;
        if(platCommande.getOptionIdSize() != 0){
            for( count = 0 ; count < platCommande.getOptionIdSize() ; count++  ){
                OptionPersonaliser option = optionPersonaliserRepository.findById(platCommande.getOptionIds().get(count))
                        .orElseThrow(() -> new EntityNotFoundException("l'option indexé n' a pas éte retrouver"));

                options.add(option);
            }

            for(OptionPersonaliser option : options){
                prix = prix + option.getPrix();
            }
        }
        return prix;
    }


     private void updateCommandeprice(UUID trackingId) {
        double prix = 0;
        Commande commande = commandeRepository.findByTrackingId(trackingId)
                .orElseThrow(()-> new EntityNotFoundException("la commade n'a pas été retrouver dasn la base de donnée "));

        List<PlatCommande> plats = platCommandeRepository.findAllByCommande(trackingId);

        for(PlatCommande plat : plats){
            prix  += plat.getPrix();
        }

        commande.setPrixTotal(prix);
    }
}
