package com.example.evooq.demo.resource.model;

public record ActionResource(
    String userId,
    String description,
    ActionType type,
    Double targetPrice,
    Double divergenceTolerance,
    Boolean priceIsLessThanTarget,
    String contextId) {}
