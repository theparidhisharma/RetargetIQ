package com.retargetiq.rloffer.controller;

import com.retargetiq.rloffer.model.*;
import com.retargetiq.rloffer.service.OfferService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/offer")
@RequiredArgsConstructor
public class OfferController {

    private final OfferService offerService;

    @PostMapping("/select")
    public OfferResponse selectOffer(@RequestBody OfferRequest request) {
        return offerService.generateOffer(request);
    }
}
