package nitchcorp.backend.titan.food.Application.services.ServiceImpl;

import jakarta.persistence.EntityNotFoundException;
import nitchcorp.backend.titan.food.Application.dtos.request.NotePlatRequest;
import nitchcorp.backend.titan.food.Application.dtos.request.PlatRequest;
import nitchcorp.backend.titan.food.Application.dtos.response.NotePlatResponse;
import nitchcorp.backend.titan.food.Application.dtos.response.PlatResponse;
import nitchcorp.backend.titan.food.Application.mappers.PlatCommandeMapper;
import nitchcorp.backend.titan.food.Application.mappers.PlatMapper;
import nitchcorp.backend.titan.food.Domain.models.Plat;

import nitchcorp.backend.titan.food.Domain.enums.CategoriePlat;
import nitchcorp.backend.titan.food.Infrastructure.PlatCommandeRepository;
import nitchcorp.backend.titan.food.Infrastructure.PlatRepository;

import nitchcorp.backend.titan.food.Application.services.PlatService;

import nitchcorp.backend.titan.shared.minio.enums.FileType;
import nitchcorp.backend.titan.shared.minio.service.MinioService;
import nitchcorp.backend.titan.shared.securite.user.entities.User;
import nitchcorp.backend.titan.shared.securite.user.repositories.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PlatServiceImpl implements PlatService {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PlatServiceImpl.class);

    private final PlatRepository platRepository;
    private final PlatMapper platMapper;
    private final MinioService minioService;
    private final UserRepository userRepository;
    private final PlatCommandeRepository platCommandeRepository;
    private final PlatCommandeMapper platCommandeMapper;

    public PlatServiceImpl(PlatRepository platRepository, PlatMapper platMapper, MinioService minioService, UserRepository userRepository, PlatCommandeRepository platCommandeRepository, PlatCommandeMapper platCommandeMapper) {
        this.platRepository = platRepository;
        this.platMapper = platMapper;
        this.minioService = minioService;
        this.userRepository = userRepository;
        this.platCommandeRepository = platCommandeRepository;
        this.platCommandeMapper = platCommandeMapper;
    }

    @Override
    @Transactional
    public PlatResponse createPlat(PlatRequest request) {
        Plat plat = platMapper.toEntity(request);
        Plat savedPlat = platRepository.save(plat);
        return platMapper.toResponse(savedPlat);
    }
    @Override
    @Transactional
    public NotePlatResponse noterPlat(UUID trackingId, NotePlatRequest request) {
        Plat plat = platRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new RuntimeException("Plat non trouvé"));
        plat.setNote(request.note());
        platRepository.save(plat);
        return platMapper.toNoteResponse(plat);
    }

    @Override
    @Transactional
    public PlatResponse updatePlat(UUID trackingId, PlatRequest request) {
        Plat existingPlat = platRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new EntityNotFoundException("Plat non trouvé avec l'ID : " + trackingId));
        existingPlat.setName(request.name());
        existingPlat.setDescription(request.description());
        existingPlat.setCategorie(CategoriePlat.valueOf(request.categorie()));
        existingPlat.setPrix(request.prix());
        existingPlat.setTempsDePreparation(request.tempsDePreparation());

        Plat updatedPlat = platRepository.save(existingPlat);
        return platMapper.toResponse(updatedPlat);
    }

    @Override
    @Transactional
    public void deletePlat(UUID trackingId) {
        Plat plat = platRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new EntityNotFoundException("Plat non trouvé avec l'ID : " + trackingId));
        platRepository.delete(plat);
    }

    @Override
    @Transactional(readOnly = true)
    public PlatResponse getPlat(UUID trackingId) {
        Plat plat = platRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new EntityNotFoundException("Plat non trouvé avec l'ID : " + trackingId));
        return platMapper.toResponse(plat);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlatResponse> getAllPlats() {
        return platRepository.findAll().stream()
                .map(platMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlatResponse> getPlatsByCategorie(CategoriePlat categoriePlat) {
        return platRepository.findByCategoriePlat(categoriePlat).stream()
                .map(platMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public List<PlatResponse> recommendPlatsForUser(UUID trackingId, int limit) {
        User user = userRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        List<Plat> platsUser = platCommandeRepository.findTopPlatsByUser(user.getId(), PageRequest.of(0, limit));


        List<Plat> platsPopulaires = platCommandeRepository.findTopPlatsGlobally(PageRequest.of(0, limit));

        Set<Plat> recommandations = new LinkedHashSet<>();
        recommandations.addAll(platsUser);
        recommandations.addAll(platsPopulaires);

        return recommandations.stream()
                .limit(limit)
                .map(platMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Exemple pour Plat
    @Override
    @Transactional
    public PlatResponse createPlatWithPhotos(PlatRequest request, List<MultipartFile> photos) {
        log.info("Début création plat avec photos: {}", request);

        // Création du plat sans image
        PlatResponse plat = createPlat(request);
        log.info("Plat créé avec trackingId: {}", plat.trackingId());

        if (photos != null && !photos.isEmpty()) {
            log.info("Traitement de {} photo(s) pour le plat {}", photos.size(), plat.trackingId());

            Plat savedPlat = platRepository.findByTrackingId(plat.trackingId())
                    .orElseThrow(() -> new RuntimeException("Plat non trouvé avec trackingId: " + plat.trackingId()));

            try {
                // Upload des images
                var uploadResponses = minioService.uploadMultipleFiles(photos, FileType.IMAGE);
                List<String> imageUrls = uploadResponses.stream()
                        .map(response -> response.getFileUrl())
                        .collect(Collectors.toList());

                // Ajout des URLs dans l'entité
                savedPlat.setImagesurl(imageUrls);
                Plat updatedPlat = platRepository.save(savedPlat);

                return platMapper.toResponse(updatedPlat);
            } catch (Exception e) {
                log.error("Erreur upload images: {}", e.getMessage(), e);
                throw new RuntimeException("Échec upload images: " + e.getMessage(), e);
            }
        } else {
            log.info("Aucune image fournie pour le plat {}", plat.trackingId());
        }

        return plat;
    }
}
