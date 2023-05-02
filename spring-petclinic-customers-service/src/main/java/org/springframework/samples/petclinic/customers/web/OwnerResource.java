/*
 * Copyright 2002-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.customers.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.samples.petclinic.customers.model.OwnerRepository;
import org.springframework.samples.petclinic.customers.services.OwnersService;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.function.Supplier;

import static io.github.resilience4j.core.IntervalFunction.ofExponentialRandomBackoff;
import static java.util.Collections.nCopies;
import static java.util.concurrent.Executors.newFixedThreadPool;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 * @author Maciej Szarlinski
 */
@RequestMapping("/owners")
@RestController
@Timed("petclinic.owner")
@RequiredArgsConstructor
@Slf4j
class OwnerResource {

    private final OwnerRepository ownerRepository;

    private HashMap<String, ArrayList<String>> resultsByThread = new HashMap<String, ArrayList<String>>();
    private Gson g = new Gson();


    @Autowired
    private OwnersService ownersService;

    /**
     * Create Owner
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Owner createOwner(@Valid @RequestBody Owner owner) {
        return ownerRepository.save(owner);
    }

    /**
     * Read single Owner
     */
    // @Retry(name = "retryExp1")

    @GetMapping(value = "/{ownerId}")
    public Optional<Owner> findOwner(@PathVariable("ownerId") @Min(1) int ownerId) {
        /*
        CircuitBreakerConfig config = CircuitBreakerConfig
            .custom()
            .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
            .slidingWindowSize(10)
            .slowCallRateThreshold(70.0f)
            .slowCallDurationThreshold(Duration.ofSeconds(2))
            .build();
        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);
        CircuitBreaker circuitBreaker = registry.circuitBreaker("flightSearchService");

        Supplier<Optional<Owner>> ownerSupplier = circuitBreaker.decorateSupplier(() -> ownersService.getOwnerById(ownerId));


        return ownerSupplier.get();*/


        return ownersService.getOwnerById(ownerId);
    }

    /**
     * Read single Owner Retry pattern 2
     */

    @GetMapping(value = "/retryExponential/{ownerId}")
    public Optional<Owner> findOwnerRetryExponential(@PathVariable("ownerId") @Min(1) int ownerId) {
        return ownersService.getOwnerByIdRetryExponential(ownerId);
    }

    public String fallbackAfterRetry(Exception ex) {
        return "all retries have exhausted";
    }

    /**
     * Read List of Owners
     */

    @GetMapping
    public List<Owner> findAll() {
        return ownersService.getAllOwners();
    }

    /**
     * Update Owner
     */
    @PutMapping(value = "/{ownerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateOwner(@PathVariable("ownerId") @Min(1) int ownerId, @Valid @RequestBody Owner ownerRequest) {
        final Optional<Owner> owner = ownerRepository.findById(ownerId);
        final Owner ownerModel = owner.orElseThrow(() -> new ResourceNotFoundException("Owner " + ownerId + " not found"));

        // This is done by hand for simplicity purpose. In a real life use-case we should consider using MapStruct.
        ownerModel.setFirstName(ownerRequest.getFirstName());
        ownerModel.setLastName(ownerRequest.getLastName());
        ownerModel.setCity(ownerRequest.getCity());
        ownerModel.setAddress(ownerRequest.getAddress());
        ownerModel.setTelephone(ownerRequest.getTelephone());
        log.info("Saving owner {}", ownerModel);
        ownerRepository.save(ownerModel);
    }


    @GetMapping(value = "experiment/{ownerId}")
    public Optional<Owner> findOwnerExperiment(@PathVariable("ownerId") @Min(1) int ownerId) throws InterruptedException, ExecutionException {
        return getStringFromOwnersJob(ownerId);
    }

    @TimeLimiter(name = "timeLimiterExp2")
    @GetMapping(value="experiment2/{ownerId}")
    public CompletableFuture<Optional<Owner>> findOwnerExperiment2(@PathVariable("ownerId") @Min(1) int ownerId) throws InterruptedException, ExecutionException {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return getStringFromOwnersJob(ownerId);
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
    }


    @GetMapping(value="experiment3/{ownerId}")
    public CompletableFuture<Optional<Owner>> findOwnerExperiment3(@PathVariable("ownerId") @Min(1) int ownerId) throws InterruptedException, ExecutionException {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return getStringFromOwnersJob(ownerId);
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @GetMapping(value="experiment4/{ownerId}")
    @CircuitBreaker(name = "CircuitBreakerService")
    public CompletableFuture<Optional<Owner>> findOwnerExperiment4(@PathVariable("ownerId") @Min(1) int ownerId) throws InterruptedException, ExecutionException {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return getStringFromOwnersJob(ownerId);
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
    }





    private Optional<Owner> getStringFromOwnersJob( int ownerId) throws InterruptedException, ExecutionException {
        IntervalFunction intervalFn = ofExponentialRandomBackoff(RetryProperties.INITIAL_INTERVAL, RetryProperties.MULTIPLIER, RetryProperties.RANDOMIZATION_FACTOR);
        Function<Integer, Optional<Owner>> ownerIdFn = getOwnerIdFn(intervalFn);
        ExecutorService executors = newFixedThreadPool(3);

        List<Callable<Optional<Owner>>> tasks = nCopies(24, () -> ownerIdFn.apply(ownerId));

        List<Future<Optional<Owner>>> future = executors.invokeAll(tasks);
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String json = objectMapper.writeValueAsString(resultsByThread);
            JSONParser parser = new JSONParser();
            JSONObject jsonObj = (JSONObject) parser.parse(json);
            try (PrintWriter out = new PrintWriter(new FileWriter("testResults.json"))) {
                out.write(jsonObj.toJSONString());
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (JsonProcessingException | ParseException e) {
            e.printStackTrace();
        }

        return future.get(0).get();
    }





    private Function<Integer, String> getRetriableOwnerIdFn(IntervalFunction intervalFn) {
        RetryConfig retryConfig = RetryConfig.custom()
            .maxAttempts(1)
            .intervalFunction(intervalFn)
            .retryExceptions(Exception.class)
            .build();
        io.github.resilience4j.retry.Retry retry = io.github.resilience4j.retry.Retry.of("retryExp4", retryConfig);

        return io.github.resilience4j.retry.Retry.decorateFunction(retry, id -> {
            threadLogs();

            return ownersService.getOwnerById(id).toString();
        });
    }

    private Function<Integer, Optional<Owner>> getOwnerIdFn(IntervalFunction intervalFn) {
        return id -> {
            threadLogs();
            try {
                return ownersService.getOwnerByIdExperiment4(id).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private void threadLogs() {
        if (resultsByThread.containsKey(Thread.currentThread().getName())) {
            resultsByThread.get(Thread.currentThread().getName()).add(String.valueOf(LocalDateTime.now()));
        } else {
            ArrayList<String> newArrayList = new ArrayList<String>();
            newArrayList.add(String.valueOf(LocalDateTime.now()));
            resultsByThread.put(Thread.currentThread().getName(), newArrayList);
        }
        log.info("Invoked at {}", Thread.currentThread().getName() + "---" + LocalDateTime.now());
    }

    static class RetryProperties {
        static final Long INITIAL_INTERVAL = 1000L;
        static final Double MULTIPLIER = 2.0D;
        static final Double RANDOMIZATION_FACTOR = 0.6D;
        static final Integer MAX_RETRIES = 3;
    }

}
