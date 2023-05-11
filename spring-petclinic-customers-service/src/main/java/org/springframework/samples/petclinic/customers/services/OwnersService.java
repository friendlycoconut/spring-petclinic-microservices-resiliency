package org.springframework.samples.petclinic.customers.services;


import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.samples.petclinic.customers.model.OwnerRepository;
import org.springframework.stereotype.Service;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import static io.github.resilience4j.core.IntervalFunction.ofExponentialRandomBackoff;
import static org.springframework.samples.petclinic.customers.services.OwnersService.RetryProperties.*;

@Service
public class OwnersService {

    @Autowired
    private Environment env;

    static Logger log = LoggerFactory.getLogger(OwnersService.class);

    public String getAllArticlesURL() {
        return  env.getProperty("resilience4j.retry.instances.retryExp1.max-attempts");
    }

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RetryRegistry registry;

    private static final String SERVICE_NAME = "owner-service";

    public List<Owner> getAllOwners() {
        System.out.println(" Making a request to " + SERVICE_NAME + " at :" + LocalDateTime.now());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        List<Owner> ownersList = new ArrayList<>();
            ownersList = ownerRepository.findAll();
        return ownersList;
    }
//
   //@Retry(name="retryExp5")
    public Optional<Owner> getOwnerById(int ownerId) {
        System.out.println(" Making a request to " + SERVICE_NAME + " (id) "+ " at :" + LocalDateTime.now());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        final Optional<Owner> owner = ownerRepository.findById(ownerId);

        return owner;
    }

    @TimeLimiter(name = "timeLimiterExp2_6")
    public Optional<Owner> getOwnerByIdExperiment2(int ownerId) {
        System.out.println(" Making a request to " + SERVICE_NAME + " (id) "+ " at :" + LocalDateTime.now());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        final Optional<Owner> owner = ownerRepository.findById(ownerId);

        return owner;
    }




    @TimeLimiter(name = "timeLimiterExp2")
    @Retry(name = "retryExp3Delay_1")
    public CompletableFuture<Optional<Owner>> getOwnerByIdExperiment3(int ownerId){
        return CompletableFuture.supplyAsync(() -> {
            return  ownerRepository.findById(ownerId);
        });
    }

    @TimeLimiter(name = "timeLimiterExp4")
    @CircuitBreaker(name = "CircuitBreakerService")
    public CompletableFuture<Optional<Owner>> getOwnerByIdExperiment4(int ownerId){
        return CompletableFuture.supplyAsync(() -> {
            return  ownerRepository.findById(ownerId);
        });
    }

    @Retry(name="retryExp5")
    public Optional<Owner> getOwnerByIdExperiment5(int ownerId) {
        System.out.println(" Making a request to " + SERVICE_NAME + " (id) "+ " at :" + LocalDateTime.now());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        final Optional<Owner> owner = ownerRepository.findById(ownerId);

        return owner;
    }


    @Retry(name="retryExp7")
    public Optional<Owner> getOwnerByIdExperiment7(int ownerId) {
        System.out.println(" Making a request to " + SERVICE_NAME + " (id) "+ " at :" + LocalDateTime.now());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        final Optional<Owner> owner = ownerRepository.findById(ownerId);

        return owner;
    }




    public Optional<Owner> getOwnerByIdRetryExponential(int ownerId) {

        System.out.println(" Making a request to " + SERVICE_NAME + " at :" + LocalDateTime.now());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        final Optional<Owner> owner = ownerRepository.findById(ownerId);

        return owner;
    }




    public List<Owner> getDefaultOwners(Exception e) {
        System.out.println("Retry exception");
        return new ArrayList<>();
    }

    public Optional<Owner> getDefaultLoansGetOwnerId(Exception e) {
        System.out.println("Retry exception");
        Owner ownerNew = new Owner();
        Optional<Owner> ownerOptional= Optional.of(ownerNew);
        return ownerOptional;
    }

    @PostConstruct
    public void postConstruct() {
        registry
            .retry("retryExp7")
            .getEventPublisher()
            .onRetry(
                event -> {

                log.info( "Retry attemps:" +event.getNumberOfRetryAttempts());
                log.info( "Event call before change:" + registry.retry("retryExp7")
                    .getRetryConfig()
                    .getMaxAttempts());
                if(event.getNumberOfRetryAttempts()==3){
                    registry.replace("retryExp7", registry.retry("retryExp7_1"));
                }});


        registry
            .retry("retryExp7_1")
            .getEventPublisher()
            .onRetry(
                event -> {

                    log.info( "Retry attemps 7_1:" +event.getNumberOfRetryAttempts());
                    log.info( "Event call before change: 7_1" + registry.retry("retryExp7")
                        .getRetryConfig()
                        .getMaxAttempts());
                    });


    }




    static class RetryProperties {
        static final Long INITIAL_INTERVAL = 1000L;
        static final Double MULTIPLIER = 2.0D;
        static final Double RANDOMIZATION_FACTOR = 0.6D;
        static final Integer MAX_RETRIES = 3;
    }


}
