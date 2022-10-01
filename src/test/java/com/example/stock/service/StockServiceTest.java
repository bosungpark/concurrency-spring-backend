package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.facade.OptimisticLockStockFacade;
import com.example.stock.repository.StockRepsitory;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.junit.Assert.*;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
class StockServiceTest {

    @Autowired
    private StockService stockService;

    @Autowired
    private StockRepsitory stockRepsitory;

    @Autowired
    private PerssimisticLockStorkService perssimisticLockStorkService;

    @Autowired
    private OptimisticLockStockFacade optimisticLockStockFacade;

    @BeforeEach
    public void before(){
        Stock stock=new Stock(1L,100L);

        stockRepsitory.saveAndFlush(stock);
    }

    @AfterEach
    public void after(){
        stockRepsitory.deleteAll();
    }

    @Test
    public void stockDecrease(){
        stockService.decrease(1L,1L);
        Stock stock= stockRepsitory.findById(1L).orElseThrow();

        Assert.assertEquals(Optional.of(99L).get(), stock.getQuantity());
    }

    @Test
    public void concurrencyRequestWithSynchronized() throws InterruptedException {
//        synchronized 처리가 없을시:
//        expected:<Optional[0]> but was:<92>
//        필요:Optional[0]
//        실제   :92
//        이유: race condition 때문
        int threadCnt=100;
        ExecutorService executorService= Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCnt);

        for (int i=0; i<threadCnt; i++) {
            executorService.submit(()->{
                try {
                    stockService.decrease(1L,1L);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        Stock stock=stockRepsitory.findById(1L).orElseThrow();
        Assert.assertEquals(Optional.of(0L).get(),stock.getQuantity());
    }

    @Test
    public void concurrencyRequestWithPerssimisicLock() throws InterruptedException {
        int threadCnt=100;
        ExecutorService executorService= Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCnt);

        for (int i=0; i<threadCnt; i++) {
            executorService.submit(()->{
                try {
                    perssimisticLockStorkService.decrease(1L,1L);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        Stock stock=stockRepsitory.findById(1L).orElseThrow();
        Assert.assertEquals(Optional.of(0L).get(),stock.getQuantity());
    }

    @Test
    public void concurrencyRequestWithOptimisicLock() throws InterruptedException {
//        업데이트가 실패하면 재시도를 한다.
//        충돌이 적다면 성능이 좋은 편이지만, 빈번할 때는 성능이 매우 저하될 수 있다.
        int threadCnt=100;
        ExecutorService executorService= Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCnt);

        for (int i=0; i<threadCnt; i++) {
            executorService.submit(()->{
                try {
                    optimisticLockStockFacade.decrease(1L,1L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        Stock stock=stockRepsitory.findById(1L).orElseThrow();
        Assert.assertEquals(Optional.of(0L).get(),stock.getQuantity());
    }
}