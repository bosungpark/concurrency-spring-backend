package com.example.stock.facade;

import com.example.stock.service.OptimistLockStockService;
import org.springframework.stereotype.Service;

@Service
public class OptimisticLockStockFacade {

    private OptimistLockStockService optimistLockStockService;

    public OptimisticLockStockFacade(OptimistLockStockService optimistLockStockService) {
        this.optimistLockStockService = optimistLockStockService;
    }

    public void decrease(Long id, Long quantity) throws InterruptedException {
        while (true){
            try {
                optimistLockStockService.decrease(id, quantity);
                break;
            }catch (Exception e){
                Thread.sleep(50);
            }
        }
    }
}
