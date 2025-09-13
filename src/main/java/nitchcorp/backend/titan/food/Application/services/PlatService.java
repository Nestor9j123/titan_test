package nitchcorp.backend.titan.food.Application.services;

import nitchcorp.backend.titan.food.Application.dtos.request.NotePlatRequest;
import nitchcorp.backend.titan.food.Application.dtos.request.PlatRequest;
import nitchcorp.backend.titan.food.Application.dtos.response.NotePlatResponse;
import nitchcorp.backend.titan.food.Application.dtos.response.PlatResponse;
import nitchcorp.backend.titan.food.Domain.enums.CategoriePlat;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface PlatService {

    PlatResponse createPlat(PlatRequest request);

    NotePlatResponse noterPlat(UUID trackingId, NotePlatRequest request) ;

    PlatResponse updatePlat(UUID trackingId, PlatRequest request);

    void deletePlat(UUID trackingId);

    PlatResponse getPlat(UUID trackingId);

    List<PlatResponse> getAllPlats();

    PlatResponse createPlatWithPhotos(PlatRequest request, List<MultipartFile> photos) ;

    List<PlatResponse> recommendPlatsForUser(UUID trackingId, int limit);

    List<PlatResponse> getPlatsByCategorie(CategoriePlat categoriePlat);
}
