package ru.buyanov.experimental.spring;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import java.util.concurrent.*;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SpringTransactionsIsolationApplication.class)
public class SpringTransactionsIsolationApplicationTests {
    public static final String BASE = "base";
    public static final String FIRST = "+first";
    public static final String SECOND = "+second";
    int productId;

    @Autowired
    ProductService service;

    @Autowired
    ProductRepository repository;


    @Before
    public void setUp() {
        Product product = new Product();
        product.setName(BASE);
        repository.save(product);
        productId = product.getId();
    }

    @After
    public void tearDown() {
        repository.deleteAll();
        productId = 0;
    }

    @Test
    public void contextLoads() {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        Future<Product> firstFuture = pool.submit(() -> service.firstTransaction(productId, FIRST));
        service.sleep(300); // not 100%, but in most cases this is enough for 1st transaction to start before 2nd
        Future<Product> secondFuture = pool.submit(() -> service.secondTransaction(productId, SECOND));
        try {
            Product firstProduct = firstFuture.get();
            Product secondProduct = secondFuture.get();
            Product productFromRepo = repository.findOne(productId);

            assertEquals(BASE + FIRST, firstProduct.getName());
            assertEquals(BASE + SECOND, secondProduct.getName());

            // There is only data from first transaction, update of second is lost
            assertEquals(productFromRepo.getName(), firstProduct.getName());
            // This means that there were no lock on data during first transaction
            // and second transaction commits without waiting
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
