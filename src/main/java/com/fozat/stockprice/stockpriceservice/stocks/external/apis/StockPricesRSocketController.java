package com.fozat.stockprice.stockpriceservice.stocks.external.apis;

import com.fozat.stockprice.stockpriceservice.stocks.external.models.StockPrice;
import com.fozat.stockprice.stockpriceservice.stocks.external.models.StockPriceRSocketRequest;
import com.fozat.stockprice.stockpriceservice.stocks.internal.services.StockPricesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

@Log
@Controller
@RequiredArgsConstructor
public class StockPricesRSocketController {

    private final StockPricesService service;

    @PreAuthorize("hasRole('USER')")
    @MessageMapping("stockPrices")
    public Flux<StockPrice> prices(Flux<StockPriceRSocketRequest> requestFlux,
                                   @AuthenticationPrincipal UserDetails user) {
        return requestFlux.doOnNext(request -> log.info("Requested interval is " + request.getInterval() + " seconds."))
                          .doOnCancel(() -> log.warning("The client cancelled the channel."))
                          .switchMap(service::doProcessRequest);
    }

}
