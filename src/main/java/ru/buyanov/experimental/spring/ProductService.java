package ru.buyanov.experimental.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Sample service with two methods-transactions
 * First changes data, then
 */
@Service
public class ProductService {
    @Autowired
    ProductRepository repository;


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Product firstTransaction(Integer id, String dataToAdd) {
        Product p = repository.findOne(id);
        p.setName(p.getName() + dataToAdd);
        sleep(2000); // wait for second transaction to do all it's business before our commit
        return p;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Product secondTransaction(Integer id, String dataToAdd) {
        sleep(1000); // wait for first transaction to do all it's business before we start reading
        Product p = repository.findOne(id);
        p.setName(p.getName() + dataToAdd);
        return p;
    }

    public void sleep(long millis) {
        try {
            Thread.sleep(
                    millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
