package org.springframework.samples.petclinic.customers.services;

import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.samples.petclinic.customers.model.OwnerRepository;
import org.springframework.stereotype.Service;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OwnersService {
    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private RestTemplate restTemplate;

    private static final String SERVICE_NAME = "owner-service";


    @Retry(name = "retryApi", fallbackMethod = "getDefaultLoans")
    public List<Owner> getAllOwners() {
        System.out.println(" Making a request to " + SERVICE_NAME + " at :" + LocalDateTime.now());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        List<Owner> ownersList = new ArrayList<>();
            ownersList = ownerRepository.findAll();
        return ownersList;
    }

    @Retry(name = "retryApi", fallbackMethod = "getDefaultLoans")
    public Optional<Owner> getOwnerById(int ownerId) {
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
}
