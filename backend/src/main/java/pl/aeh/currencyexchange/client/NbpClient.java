package pl.aeh.currencyexchange.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import pl.aeh.currencyexchange.dto.nbp.NbpTableDto;
import pl.aeh.currencyexchange.exception.ResourceNotFoundException;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class NbpClient {

    private final RestTemplate restTemplate;
    private static final String NBP_API_BASE_URL = "http://api.nbp.pl/api/exchangerates/tables/C";

    public NbpTableDto fetchCurrentRates() {
        String url = NBP_API_BASE_URL + "?format=json";
        return fetchRates(url);
    }

    public NbpTableDto fetchRatesByDate(LocalDate date) {
        String url = String.format("%s/%s?format=json", NBP_API_BASE_URL, date);
        return fetchRates(url);
    }

    private NbpTableDto fetchRates(String url) {
        try {
            log.debug("Fetching rates from NBP: {}", url);
            NbpTableDto[] response = restTemplate.getForObject(url, NbpTableDto[].class);
            
            if (response != null && response.length > 0) {
                return response[0];
            } else {
                throw new ResourceNotFoundException("No rates data received from NBP");
            }
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Rates not found for URL: {}", url);
            throw new ResourceNotFoundException("Rates not found for the specified criteria");
        } catch (ResourceAccessException e) {
            log.error("Network error while connecting to NBP API: {}", e.getMessage());
            throw new RuntimeException("Could not connect to NBP API", e);
        } catch (Exception e) {
            log.error("Error fetching rates from NBP: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch rates from NBP", e);
        }
    }
}
