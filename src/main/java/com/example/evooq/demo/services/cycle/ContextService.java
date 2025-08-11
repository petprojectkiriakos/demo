package com.example.evooq.demo.services.cycle;

import com.example.evooq.demo.domain.context.ActionContext;
import com.example.evooq.demo.domain.context.ContextRegistry;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service that provides current market context data for action evaluation. In a production
 * environment, this would fetch real-time data from market data providers, databases, or other
 * external services.
 *
 * <p>For demonstration purposes, this service provides mock data that simulates real market
 * conditions.
 */
@Service
public class ContextService {
  private static final Logger logger = LoggerFactory.getLogger(ContextService.class);
  private final Random random = new Random();

  /**
   * Returns the current action context with all available market data. This context is used by
   * actions to evaluate their conditions.
   *
   * <p>In production, this would: - Fetch real-time stock/crypto/forex prices - Get current market
   * volumes and order books - Check market status (open/closed) - Handle data source failures
   * gracefully
   */
  public ActionContext getCurrentContext() {
    logger.debug("Building current action context");

    ActionContext context = new ActionContext();

    try {
      // Mock stock prices
      Map<String, Float> stockPrices = new HashMap<>();
      stockPrices.put("AAPL", 150.0f + random.nextFloat() * 20.0f);
      stockPrices.put("GOOGL", 2800.0f + random.nextFloat() * 200.0f);
      stockPrices.put("TSLA", 800.0f + random.nextFloat() * 100.0f);
      context.set(ContextRegistry.STOCK_PRICES.name(), stockPrices);

      // Mock crypto prices
      Map<String, Float> cryptoPrices = new HashMap<>();
      cryptoPrices.put("BTC", 45000.0f + random.nextFloat() * 10000.0f);
      cryptoPrices.put("ETH", 3000.0f + random.nextFloat() * 1000.0f);
      cryptoPrices.put("ADA", 1.2f + random.nextFloat() * 0.5f);
      context.set(ContextRegistry.CRYPTO_PRICES.name(), cryptoPrices);

      // Mock forex prices
      Map<String, Float> forexPrices = new HashMap<>();
      forexPrices.put("EUR/USD", 1.08f + random.nextFloat() * 0.05f);
      forexPrices.put("GBP/USD", 1.25f + random.nextFloat() * 0.05f);
      forexPrices.put("USD/JPY", 148.0f + random.nextFloat() * 5.0f);
      context.set(ContextRegistry.FOREX_PRICES.name(), forexPrices);

      // Mock ETF prices
      Map<String, Float> etfPrices = new HashMap<>();
      etfPrices.put("SPY", 420.0f + random.nextFloat() * 20.0f);
      etfPrices.put("QQQ", 380.0f + random.nextFloat() * 30.0f);
      context.set(ContextRegistry.EFT_PRICES.name(), etfPrices);

      // Mock volumes
      Map<String, Long> volumes = new HashMap<>();
      volumes.put("AAPL", 1000000L + random.nextLong(500000L));
      volumes.put("BTC", 50000L + random.nextLong(25000L));
      context.set(ContextRegistry.VOLUMES.name(), volumes);

      // Mock market status
      Map<String, String> marketStatus = new HashMap<>();
      marketStatus.put("NYSE", "OPEN");
      marketStatus.put("NASDAQ", "OPEN");
      marketStatus.put("CRYPTO", "24/7");
      context.set(ContextRegistry.MARKET_STATUS.name(), marketStatus);

      logger.debug("Successfully built action context with mock data");
      return context;

    } catch (Exception e) {
      logger.error("Failed to build action context", e);
      // Return empty context rather than null to avoid null pointer exceptions
      return new ActionContext();
    }
  }

  /**
   * Returns mock context data for a specific registry type. Used for testing purposes or when only
   * specific data is needed.
   */
  public Map<String, Object> getContextData(ContextRegistry registry) {
    logger.debug("Getting context data for registry: {}", registry);

    ActionContext fullContext = getCurrentContext();
    return fullContext.get(registry.name(), HashMap.class);
  }
}
