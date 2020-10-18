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

@Log
@Service
public class StockPricesService {

    private final Map<String, Flux<StockPrice>> pricesForStock = new ConcurrentHashMap<>();

    public Flux<StockPrice> doProcessRequest(final StockPriceRSocketRequest request) {
        return pricesForStock.computeIfAbsent(request.getStockName(), (stockName) -> createStockPriceFlux(request));
    }

    private Flux<StockPrice> createStockPriceFlux(final StockPriceRSocketRequest request) {
        return Flux.interval(Duration.ofSeconds(request.getInterval()))
                   .map(index -> mapToStockPrice(request.getStockName()));
    }

    private StockPrice mapToStockPrice(final String stockName) {
        log.info(String.format("New subscription for symbol %s.", stockName));
        return StockPrice.builder()
                         .price(randomStockPrice())
                         .time(LocalDateTime.now())
                         .stockName(stockName)
                         .build();
    }

    private Double randomStockPrice() {
        return ThreadLocalRandom.current()
                                .nextDouble(100.0);
    }

}
