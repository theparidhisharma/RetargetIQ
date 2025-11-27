package com.retargetiq.rloffer.service;

import com.retargetiq.rloffer.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OfferService {

    private final BanditEngine banditEngine;

    public OfferResponse generateOffer(OfferRequest request) {

        String topItemId = "unknown";
        if (request.getRankedItems() != null && !request.getRankedItems().isEmpty()) {
            topItemId = request.getRankedItems().get(0).getItemId();
        }

        String action = banditEngine.chooseAction(request.getUserFeatures());

        OfferResponse response = new OfferResponse();
        response.setUserId(request.getUserId());
        response.setItemId(topItemId);
        response.setOfferType(action);

        // Simple reward proxy
        response.setRewardScore(
                request.getUserFeatures().getPurchaseProbability() * 0.7 +
                request.getUserFeatures().getActivityScore() * 0.3
        );

        return response;
    }
}
