package nitchcorp.backend.titan.food.Application.services;

import nitchcorp.backend.titan.food.Application.dtos.request.OptionPersonaliserRequest;
import nitchcorp.backend.titan.food.Application.dtos.response.OptionPersonaliserResponse;

import java.util.List;
import java.util.UUID;

public interface OptionPersonaliserService {

    OptionPersonaliserResponse createOption(OptionPersonaliserRequest request);

    OptionPersonaliserResponse updateOption(UUID trackingId, OptionPersonaliserRequest request);

    void deleteOption(UUID trackingId);

    OptionPersonaliserResponse getOption(UUID trackingId);

    List<OptionPersonaliserResponse> getAllOptions();

    List<OptionPersonaliserResponse> getOptionsForPlatCommande(UUID platCommandeTrackingId);

}
