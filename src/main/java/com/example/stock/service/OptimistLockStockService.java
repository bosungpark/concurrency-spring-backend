package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepsitory;
import org.springframework.stereotype.Service;

@Service
public class OptimistLockStockService {

    private StockRepsitory stockRepsitory;

    public OptimistLockStockService(StockRepsitory stockRepsitory) {
        this.stockRepsitory = stockRepsitory;
    }

    public void decrease(Long id, Long quantity){
        Stock stock=stockRepsitory.findByIdWithOptimistLock(id);
        stock.decrease(quantity);
        stockRepsitory.saveAndFlush(stock);
    }
}
