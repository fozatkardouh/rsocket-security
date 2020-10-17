package com.fozat.stockprice.stockpriceservice.stocks.external.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockPriceRSocketRequest {

    private Integer interval;
    private String stockName;

}
