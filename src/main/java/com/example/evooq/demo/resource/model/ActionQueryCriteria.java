package com.example.evooq.demo.resource.model;

import java.time.LocalDateTime;
import java.util.List;

public record ActionQueryCriteria(
    Integer page,
    Integer size,
    String userId,
    ActionType type,
    LocalDateTime createdFrom,
    LocalDateTime createdTo,
    List<String> sortBy,
    String sortDirection) {

  public ActionQueryCriteria {
    if (page != null && page < 0) {
      throw new IllegalArgumentException("Page must be non-negative");
    }
    if (size != null && (size < 1 || size > 100)) {
      throw new IllegalArgumentException("Size must be between 1 and 100");
    }
    if (sortDirection != null
        && !sortDirection.equalsIgnoreCase("ASC")
        && !sortDirection.equalsIgnoreCase("DESC")) {
      throw new IllegalArgumentException("Sort direction must be ASC or DESC");
    }
  }

  public int getPageOrDefault() {
    return page != null ? page : 0;
  }

  public int getSizeOrDefault() {
    return size != null ? size : 20;
  }

  public String getSortDirectionOrDefault() {
    return sortDirection != null ? sortDirection.toUpperCase() : "DESC";
  }

  public List<String> getSortByOrDefault() {
    return sortBy != null && !sortBy.isEmpty() ? sortBy : List.of("createdAt");
  }
}
