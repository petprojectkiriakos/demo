package com.example.evooq.demo.domain.context;

import java.util.HashMap;
import java.util.Map;

public class ActionContext {
  private final Map<String, Object> values = new HashMap<>();

  public <T> void set(String key, T value) {
    values.put(key, value);
  }

  public <T> T get(String key, Class<T> type) {
    return type.cast(values.get(key));
  }
}
