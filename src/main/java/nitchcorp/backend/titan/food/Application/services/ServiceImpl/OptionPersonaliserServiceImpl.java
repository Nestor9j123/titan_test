package nitchcorp.backend.titan.food.Application.services.ServiceImpl;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import nitchcorp.backend.titan.food.Application.dtos.request.OptionPersonaliserRequest;
import nitchcorp.backend.titan.food.Application.dtos.response.OptionPersonaliserResponse;
import nitchcorp.backend.titan.food.Application.mappers.OptionPersonaliserMapper;
import nitchcorp.backend.titan.food.Domain.models.OptionPersonaliser;
import nitchcorp.backend.titan.food.Domain.models.PlatCommande;
import nitchcorp.backend.titan.food.Infrastructure.OptionPersonaliserRepository;
import nitchcorp.backend.titan.food.Infrastructure.PlatCommandeRepository;
import nitchcorp.backend.titan.food.Application.services.OptionPersonaliserService;
import nitchcorp.backend.titan.shared.minio.service.MinioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OptionPersonaliserServiceImpl implements OptionPersonaliserService {

    private final OptionPersonaliserRepository optionRepository;
    private final OptionPersonaliserMapper optionMapper;
    private final PlatCommandeRepository platCommandeRepository;
    private final MinioService minioService;

    public OptionPersonaliserServiceImpl(
            OptionPersonaliserRepository optionRepository,
            OptionPersonaliserMapper optionMapper,
            PlatCommandeRepository platCommandeRepository,
            MinioService minioService
    ){
        this.optionMapper = optionMapper;
        this.platCommandeRepository = platCommandeRepository;
        this.optionRepository = optionRepository;
        this.minioService  = minioService;
    }

    @Override
    @Transactional
    public OptionPersonaliserResponse createOption(OptionPersonaliserRequest request) {
        OptionPersonaliser option = optionMapper.toEntity(request);
        OptionPersonaliser savedOption = optionRepository.save(option);
        return optionMapper.toResponse(savedOption);
    }

    @Override
    @Transactional
    public OptionPersonaliserResponse updateOption(UUID trackingId, OptionPersonaliserRequest request) {
        OptionPersonaliser existingOption = optionRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new EntityNotFoundException("Option non trouvée avec l'ID : " + trackingId));


        existingOption.setName(request.name());
        existingOption.setDescription(request.description());
        existingOption.setPrix(request.prix());

        OptionPersonaliser updatedOption = optionRepository.save(existingOption);
        return optionMapper.toResponse(updatedOption);
    }

    @Override
    @Transactional
    public void deleteOption(UUID trackingId) {
        OptionPersonaliser option = optionRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new EntityNotFoundException("Option non trouvée avec l'ID : " + trackingId));
        optionRepository.delete(option);
    }

    @Override
    @Transactional(readOnly = true)
    public OptionPersonaliserResponse getOption(UUID trackingId) {
        OptionPersonaliser option = optionRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new EntityNotFoundException("Option non trouvée avec l'ID : " + trackingId));
        return optionMapper.toResponse(option);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OptionPersonaliserResponse> getAllOptions() {
        return optionRepository.findAll().stream()
                .map(optionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OptionPersonaliserResponse> getOptionsForPlatCommande(UUID platCommandeTrackingId) {
        PlatCommande platCommande = platCommandeRepository.findByTrackingId(platCommandeTrackingId)
                .orElseThrow(() -> new EntityNotFoundException("Plat commande non trouvé avec trackingId: " + platCommandeTrackingId));


        return platCommande.getOptionIds().stream()
                .map(optionId -> optionRepository.findById(optionId)
                        .map(optionMapper::toResponse)
                        .orElse(null))
                .filter(opt -> opt != null)
                .collect(Collectors.toList());
    }


}
