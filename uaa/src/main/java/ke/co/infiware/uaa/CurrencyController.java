package ke.co.infiware.uaa;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * @author Denis Gitonga
 */
@RestController
@RequestMapping("/currencies")
public class CurrencyController {
    private final WebClient.Builder webClient;

    public CurrencyController(WebClient.Builder webClient) {
        this.webClient = webClient;
    }


    @GetMapping()
    public Flux<CurrencySymbol> getCurrencies() {
        return webClient.baseUrl("lb://boma-service")
                .build()
                .get()
                .uri("/currency/symbols")
                .retrieve()
                .bodyToFlux(CurrencySymbol.class)
                .log();
    }
}
