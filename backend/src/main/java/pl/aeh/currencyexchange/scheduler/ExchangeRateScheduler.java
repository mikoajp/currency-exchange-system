package pl.aeh.currencyexchange.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.aeh.currencyexchange.service.ExchangeRateService;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExchangeRateScheduler {

    private final ExchangeRateService exchangeRateService;

    // Run at 12:00 PM every weekday (Mon-Fri)
    @Scheduled(cron = "0 0 12 * * MON-FRI")
    public void syncRates() {
        log.info("Starting scheduled exchange rate synchronization");
        try {
            exchangeRateService.syncRatesFromNbp();
            log.info("Scheduled synchronization completed successfully");
        } catch (Exception e) {
            log.error("Scheduled synchronization failed: {}", e.getMessage());
        }
    }
}
