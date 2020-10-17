package com.fozat.stockprice.stockpriceservice.stocks.external.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockPrice {

    private String symbol;
    private Double price;
    private LocalDateTime time;

}