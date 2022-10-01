package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepsitory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockService {

    private StockRepsitory stockRepsitory;

    public StockService(StockRepsitory stockRepsitory){
        this.stockRepsitory = stockRepsitory;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public synchronized void decrease(Long id, Long quantity){
        Stock stock=stockRepsitory.findById(id).orElseThrow();

        stock.decrease(quantity);

        stockRepsitory.saveAndFlush(stock);
    }
}
