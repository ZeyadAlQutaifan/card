package com.observability.card.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;

import java.util.Random;

@RestController
@RequestMapping("/api/cards")
@Slf4j
public class CardController {

    private final Counter cardRequestCounter;
    private final Random random = new Random();

    public CardController(MeterRegistry meterRegistry) {
        this.cardRequestCounter = Counter.builder("card.requests")
                .description("Total card requests")
                .register(meterRegistry);
    }

    @GetMapping("/{cardId}")
    public CardResponse getCard(@PathVariable String cardId) throws InterruptedException {
        log.info("Fetching card with ID: {}", cardId);
        cardRequestCounter.increment();

        // Simulate some processing time
        Thread.sleep(random.nextInt(100, 500));

        log.debug("Card {} retrieved successfully", cardId);
        
        return new CardResponse(
                cardId,
                "Visa",
                "**** **** **** 1234",
                "John Doe",
                "Active"
        );
    }

    @PostMapping
    public CardResponse createCard(@RequestBody CreateCardRequest request) throws InterruptedException {
        log.info("Creating new card for holder: {}", request.cardHolder());
        
        // Simulate processing
        Thread.sleep(random.nextInt(200, 800));
        
        String cardId = "CARD-" + System.currentTimeMillis();
        log.info("Card created with ID: {}", cardId);
        
        return new CardResponse(
                cardId,
                request.cardType(),
                "**** **** **** " + random.nextInt(1000, 9999),
                request.cardHolder(),
                "Active"
        );
    }

    @GetMapping("/error")
    public String simulateError() {
        log.error("Simulating an error scenario");
        throw new RuntimeException("Intentional error for testing observability");
    }
}

record CardResponse(
        String cardId,
        String cardType,
        String maskedNumber,
        String cardHolder,
        String status
) {}

record CreateCardRequest(
        String cardType,
        String cardHolder
) {}