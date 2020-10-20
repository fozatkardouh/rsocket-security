package com.fozat.stockprice.stockpriceservice.stocks.external.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockPrice {

    private String stockName;
    private Double price;
    private LocalDateTime time;

}
