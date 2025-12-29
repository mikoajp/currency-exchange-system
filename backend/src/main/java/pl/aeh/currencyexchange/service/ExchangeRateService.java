package pl.aeh.currencyexchange.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.aeh.currencyexchange.client.NbpClient;
import pl.aeh.currencyexchange.dto.ExchangeRateDto;
import pl.aeh.currencyexchange.dto.nbp.NbpRateDto;
import pl.aeh.currencyexchange.dto.nbp.NbpTableDto;
import pl.aeh.currencyexchange.exception.ResourceNotFoundException;
import pl.aeh.currencyexchange.model.ExchangeRate;
import pl.aeh.currencyexchange.repository.ExchangeRateRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExchangeRateService {

    private final NbpClient nbpClient;
    private final ExchangeRateRepository exchangeRateRepository;

    /**
     * Fetch current rates from NBP, save to DB, and return them.
     * Uses cache to avoid frequent calls to NBP.
     */
    @Transactional
    @Cacheable(value = "currentRates")
    public List<ExchangeRateDto> fetchCurrentRates() {
        log.info("Fetching current exchange rates from NBP");
        try {
            NbpTableDto nbpTable = nbpClient.fetchCurrentRates();
            return processNbpTable(nbpTable);
        } catch (Exception e) {
            log.error("Failed to fetch current rates from NBP: {}", e.getMessage());
            // Fallback to latest rates from DB if NBP fails
            List<ExchangeRate> latestRates = exchangeRateRepository.findAllLatest();
            if (latestRates.isEmpty()) {
                throw e; // Rethrow if DB is also empty
            }
            log.info("Returning latest rates from DB due to NBP failure");
            return latestRates.stream()
                    .map(this::mapToDto)
                    .collect(Collectors.toList());
        }
    }

    /**
     * Fetch rates for specific date.
     * First checks DB, if missing fetches from NBP.
     */
    @Transactional
    public List<ExchangeRateDto> fetchRatesByDate(LocalDate date) {
        log.debug("Fetching rates for date: {}", date);
        
        List<ExchangeRate> existingRates = exchangeRateRepository.findAllByRateDate(date);
        if (!existingRates.isEmpty()) {
            return existingRates.stream()
                    .map(this::mapToDto)
                    .collect(Collectors.toList());
        }

        log.info("Rates for date {} not found in DB, fetching from NBP", date);
        try {
            NbpTableDto nbpTable = nbpClient.fetchRatesByDate(date);
            return processNbpTable(nbpTable);
        } catch (ResourceNotFoundException e) {
            log.warn("Rates for date {} not found in NBP", date);
            throw e;
        }
    }

    /**
     * Get rate history for a currency within date range.
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "rateHistory", key = "#currency + #from + #to")
    public List<ExchangeRateDto> getRateHistory(String currency, LocalDate from, LocalDate to) {
        log.debug("Fetching rate history for {} from {} to {}", currency, from, to);
        return exchangeRateRepository.findAllByCurrencyAndRateDateBetween(currency, from, to).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get latest rate for a specific currency.
     */
    @Transactional
    public ExchangeRateDto getCurrentRate(String currency) {
        return exchangeRateRepository.findLatestByCurrency(currency)
                .map(this::mapToDto)
                .orElseGet(() -> {
                    // Try to sync if not found
                    fetchCurrentRates();
                    return exchangeRateRepository.findLatestByCurrency(currency)
                            .map(this::mapToDto)
                            .orElseThrow(() -> new ResourceNotFoundException("Rate not found for currency: " + currency));
                });
    }

    /**
     * Sync rates from NBP (manually triggered or scheduled).
     * Evicts cache.
     */
    @Transactional
    @CacheEvict(value = {"currentRates", "rateHistory"}, allEntries = true)
    public void syncRatesFromNbp() {
        log.info("Starting manual synchronization of exchange rates");
        fetchCurrentRates();
        log.info("Synchronization completed");
    }

    private List<ExchangeRateDto> processNbpTable(NbpTableDto nbpTable) {
        LocalDate rateDate = nbpTable.getEffectiveDate(); // or tradingDate? effectiveDate is usually what we want
        
        return nbpTable.getRates().stream()
                .map(nbpRate -> {
                    // Check if exists
                    if (exchangeRateRepository.existsByCurrencyAndRateDate(nbpRate.getCode(), rateDate)) {
                        return exchangeRateRepository.findByCurrencyAndRateDate(nbpRate.getCode(), rateDate).get();
                    }
                    
                    ExchangeRate entity = ExchangeRate.builder()
                            .currency(nbpRate.getCode())
                            .currencyName(nbpRate.getCurrency())
                            .rateDate(rateDate)
                            .bid(nbpRate.getBid())
                            .ask(nbpRate.getAsk())
                            .build();
                    
                    return exchangeRateRepository.save(entity);
                })
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private ExchangeRateDto mapToDto(ExchangeRate entity) {
        return ExchangeRateDto.builder()
                .id(entity.getId())
                .currency(entity.getCurrencyName())
                .code(entity.getCurrency())
                .bid(entity.getBid())
                .ask(entity.getAsk())
                .midRate(entity.getMidRate())
                .rateDate(entity.getRateDate())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
