package com.cineclub.schedulers;

import com.cineclub.services.HoldService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class HoldExpirationScheduler {
    private final HoldService holdService;

    /**
     * Ejecuta cada minuto para expirar holds que hayan superado su TTL
     * El cron expression: cada minuto
     */
    @Scheduled(cron = "0 * * * * *")
    public void expireOldHolds() {
        log.debug("Ejecutando tarea de expiración de holds...");
        
        int expiredCount = holdService.expireOldHolds();
        
        if (expiredCount > 0) {
            log.info("Se expiraron {} holds", expiredCount);
        }
    }
}
