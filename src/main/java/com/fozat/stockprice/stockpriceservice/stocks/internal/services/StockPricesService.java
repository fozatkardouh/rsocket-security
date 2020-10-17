package com.fozat.stockprice.stockpriceservice.stocks.internal.services;

import com.fozat.stockprice.stockpriceservice.stocks.external.models.StockPrice;
import com.fozat.stockprice.stockpriceservice.stocks.external.models.StockPriceRSocketRequest;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

@Log
@Service
public class StockPricesService {

    private final Map<String, Flux<StockPrice>> pricesForStock = new ConcurrentHashMap<>();

    public Flux<StockPrice> doProcessRequest(StockPriceRSocketRequest request) {
        return pricesForStock.computeIfAbsent(request.getStockName(), createStockPriceFlux(request));
    }

    private Function<String, Flux<StockPrice>> createStockPriceFlux(final StockPriceRSocketRequest request) {
        return fun -> Flux.interval(Duration.ofSeconds(request.getInterval()))
                          .map(index -> mapToStockPrice(request))
                          .log();
    }

    private StockPrice mapToStockPrice(StockPriceRSocketRequest request) {
        log.info(String.format("New subscription for symbol %s.", request.getStockName()));
        return new StockPrice(request.getStockName(), randomStockPrice(), LocalDateTime.now());
    }

    private Double randomStockPrice() {
        return ThreadLocalRandom.current()
                                .nextDouble(100.0);
    }

}
