package com.retargetiq.rloffer.service;

import com.retargetiq.rloffer.model.UserFeatures;
import org.springframework.stereotype.Component;

@Component
public class BanditEngine {

    public String chooseAction(UserFeatures user) {

        double purchaseIntent = user.getPurchaseProbability();
        double activity = user.getActivityScore();
        double recency = user.getRecencyScore();

        // High intent → show discount
        if (purchaseIntent > 0.7 && recency > 0.5) {
            return "DISCOUNT_10";
        }

        // Medium intent → send reminder
        if (purchaseIntent > 0.4 && activity > 0.4) {
            return "REMINDER";
        }

        // Low intent → no nudging
        return "NO_ACTION";
    }
}
