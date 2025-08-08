package com.example.evooq.demo.demo;

import com.example.evooq.demo.kafka.consumer.ActionMessageConsumer;
import com.example.evooq.demo.kafka.model.ActionEventType;
import com.example.evooq.demo.kafka.model.ActionMessage;
import com.example.evooq.demo.services.cycle.ContextService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Simple demonstration of the Kafka service cycle functionality.
 * This shows how the cycle would work without requiring database connectivity.
 */
public class ServiceCycleDemo {
    
    public static void main(String[] args) {
        System.out.println("=== Kafka Service Cycle Demo ===");
        System.out.println();
        
        // Demonstrate Message Consumer
        demonstrateMessageConsumer();
        System.out.println();
        
        // Demonstrate Context Service 
        demonstrateContextService();
        System.out.println();
        
        System.out.println("=== Demo Complete ===");
        System.out.println("This demonstrates the core service cycle components:");
        System.out.println("1. Message gathering from Kafka (mock implementation)");
        System.out.println("2. Context management for action evaluation");
        System.out.println("3. Three-phase cycle architecture for consistency");
    }
    
    private static void demonstrateMessageConsumer() {
        System.out.println("--- Message Consumer Demo ---");
        
        ActionMessageConsumer consumer = new ActionMessageConsumer();
        
        // Add some mock messages
        consumer.addMockMessage(new ActionMessage(
            ActionEventType.ACTION_ADDED, 123L, "user1", LocalDateTime.now()
        ));
        consumer.addMockMessage(new ActionMessage(
            ActionEventType.ACTION_UPDATED, 456L, "user2", LocalDateTime.now()  
        ));
        consumer.addMockMessage(new ActionMessage(
            ActionEventType.ACTION_DELETED, 789L, "user3", LocalDateTime.now()
        ));
        
        System.out.println("Queue size before gathering: " + consumer.getQueueSize());
        
        // Gather messages (simulating Phase 1 of service cycle)
        List<ActionMessage> messages = consumer.gatherMessagesSinceLastCycle();
        
        System.out.println("Messages gathered: " + messages.size());
        for (ActionMessage msg : messages) {
            System.out.println("  " + msg);
        }
        
        System.out.println("Queue size after gathering: " + consumer.getQueueSize());
    }
    
    private static void demonstrateContextService() {
        System.out.println("--- Context Service Demo ---");
        
        ContextService contextService = new ContextService();
        
        // Get current context (simulating Phase 3 of service cycle)
        var context = contextService.getCurrentContext();
        
        System.out.println("Context generated with mock market data:");
        System.out.println("  Sample stock price (AAPL): " + 
            ((java.util.Map<String, Float>) context.get("STOCK_PRICES", java.util.HashMap.class)).get("AAPL"));
        System.out.println("  Sample crypto price (BTC): " + 
            ((java.util.Map<String, Float>) context.get("CRYPTO_PRICES", java.util.HashMap.class)).get("BTC"));
        System.out.println("  Market status (NYSE): " + 
            ((java.util.Map<String, String>) context.get("MARKET_STATUS", java.util.HashMap.class)).get("NYSE"));
    }
}