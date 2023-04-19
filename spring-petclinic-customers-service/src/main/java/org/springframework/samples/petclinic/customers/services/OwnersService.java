package org.springframework.samples.petclinic.customers.services;

import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.annotation.PostConstruct;
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
import java.util.concurrent.TimeoutException;

@Service
public class OwnersService {

    @Autowired
    private Environment env;

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


    @Retry(name = "retryExp1", fallbackMethod = "getDefaultLoans")
    public List<Owner> getAllOwners() {
        System.out.println(" Making a request to " + SERVICE_NAME + " at :" + LocalDateTime.now());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        List<Owner> ownersList = new ArrayList<>();
            ownersList = ownerRepository.findAll();
        return ownersList;
    }

    @Retry(name = "retryExp1", fallbackMethod = "getDefaultLoansGetOwnerId")
    public Optional<Owner> getOwnerById(int ownerId) {
        System.out.println(" Making a request to " + SERVICE_NAME + " at :" + LocalDateTime.now());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        final Optional<Owner> owner = ownerRepository.findById(ownerId);

        return owner;
    }

    @Retry(name = "retryExp2", fallbackMethod = "getDefaultLoansGetOwnerId")
    public Optional<Owner> getOwnerByIdRetryExponential(int ownerId) {

        System.out.println(" Making a request to " + SERVICE_NAME + " at :" + LocalDateTime.now());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        final Optional<Owner> owner = ownerRepository.findById(ownerId);

        return owner;
    }


    public List<Owner> getDefaultLoans(Exception e) {
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

        RetryConfig config = RetryConfig.custom()
            .maxAttempts(6)
            .waitDuration(Duration.ofMillis(100))
            .retryExceptions(IOException.class, TimeoutException.class)
            .build();
        RetryRegistry registryChanged = RetryRegistry.of(config);

        registry
            .retry("retryExp2")
            .getEventPublisher()
            .onRetry(event -> {
                System.out.println( "Retry attemps:" +event.getNumberOfRetryAttempts());
                System.out.println( "Event call before change:" + registry.retry("retryExp2").getRetryConfig().getMaxAttempts());
                if(event.getNumberOfRetryAttempts()==2){registry = registryChanged;

            System.out.println( "Event call:" + registry.retry("retryExp2").getRetryConfig().getMaxAttempts());
            }});



    }
}
