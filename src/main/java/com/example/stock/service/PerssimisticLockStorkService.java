package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepsitory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PerssimisticLockStorkService {

    private StockRepsitory stockRepsitory;

    public PerssimisticLockStorkService(StockRepsitory stockRepsitory) {
        this.stockRepsitory = stockRepsitory;
    }

    @Transactional
    public void decrease(Long id, Long quantity){
        Stock stock=stockRepsitory.findByIdWithPessimistLock(id);

        stock.decrease(quantity);

        stockRepsitory.saveAndFlush(stock);
    }
}
