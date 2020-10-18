package com.fozat.stockprice.stockpriceservice.stocks.external.apis;

import com.fozat.stockprice.stockpriceservice.stocks.external.models.StockPrice;
import com.fozat.stockprice.stockpriceservice.stocks.external.models.StockPriceRSocketRequest;
import io.rsocket.metadata.WellKnownMimeType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.rsocket.context.LocalRSocketServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.security.rsocket.metadata.SimpleAuthenticationEncoder;
import org.springframework.security.rsocket.metadata.UsernamePasswordMetadata;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class StockPricesRSocketControllerTest {

    private static RSocketRequester requester;

    @BeforeAll
    public static void setupOnce(@Autowired RSocketRequester.Builder builder, @LocalRSocketServerPort Integer port) {

        UsernamePasswordMetadata credentials = new UsernamePasswordMetadata("user", "pass");
        MimeType mimeType = MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.getString());

        requester = builder.setupMetadata(credentials, mimeType)
                           .rsocketStrategies(b -> b.encoder(new SimpleAuthenticationEncoder()))
                           .connectTcp("localhost", port)
                           .block();
    }

    @Test
    public void testStreamGetsStream() {
        // Create first setting after 0 seconds. Server starts sending after 2 seconds.
        StockPriceRSocketRequest request1 = StockPriceRSocketRequest.builder()
                                                                    .interval(2)
                                                                    .stockName("TEST1")
                                                                    .build();
        Mono<StockPriceRSocketRequest> setting1 = Mono.just(request1)
                                                      .delayElement(Duration.ofSeconds(0));

        // Create next setting after 3 seconds. Server starts sending in after 1 second.
        StockPriceRSocketRequest request2 = StockPriceRSocketRequest.builder()
                                                                    .interval(1)
                                                                    .stockName("TEST2")
                                                                    .build();
        Mono<StockPriceRSocketRequest> setting2 = Mono.just(request2)
                                                      .delayElement(Duration.ofSeconds(3));

        // Bundle settings into a Flux
        Flux<StockPriceRSocketRequest> settings = Flux.concat(setting1, setting2);

        // Send a stream of request messages
        Flux<StockPrice> stream = requester.route("stockPrices")
                                           .data(settings)
                                           .retrieveFlux(StockPrice.class);

        // Verify that the response messages contain the expected data
        StepVerifier.create(stream)
                    .consumeNextWith(stockPrice -> {
                        assertThat(stockPrice.getStockName()).isEqualTo("TEST1");
                        assertThat(stockPrice.getPrice()).isBetween(0d, 100d);
                    })
                    .expectNextCount(0)
                    .consumeNextWith(stockPrice -> {
                        assertThat(stockPrice.getStockName()).isEqualTo("TEST2");
                    })
                    .thenCancel()
                    .verify();
    }

    @AfterAll
    public static void tearDownOnce() {
        requester.rsocket()
                 .dispose();
    }

}
