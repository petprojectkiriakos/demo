package com.example.evooq.demo.resource.model;

import java.util.List;

public record ActionQueryResult(
    List<ActionResource> content,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean first,
    boolean last,
    boolean empty) {

  public static ActionQueryResult of(
      List<ActionResource> content, int page, int size, long totalElements) {
    int totalPages = (int) Math.ceil((double) totalElements / size);
    boolean first = page == 0;
    boolean last = page >= totalPages - 1;
    boolean empty = content.isEmpty();

    return new ActionQueryResult(
        content, page, size, totalElements, totalPages, first, last, empty);
  }
}
