package com.example.evooq.demo.db.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "actions")
public class ActionEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String userId;
  private String description;
  private String contextId;
  private String contextType;

  @Enumerated(EnumType.STRING)
  private ActionType actionType;

  private LocalDateTime createdAt;

  private Double targetValue;
  private Double stopLossValue;
  private Double amount;
  private String assetSymbol;

  public ActionEntity() {
    this.createdAt = LocalDateTime.now();
  }

  public ActionEntity(
      String userId,
      String description,
      String contextId,
      String contextType,
      ActionType actionType) {
    this.userId = userId;
    this.description = description;
    this.contextId = contextId;
    this.contextType = contextType;
    this.actionType = actionType;
    this.createdAt = LocalDateTime.now();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getContextId() {
    return contextId;
  }

  public void setContextId(String contextId) {
    this.contextId = contextId;
  }

  public String getContextType() {
    return contextType;
  }

  public void setContextType(String contextType) {
    this.contextType = contextType;
  }

  public ActionType getActionType() {
    return actionType;
  }

  public void setActionType(ActionType actionType) {
    this.actionType = actionType;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public Double getTargetValue() {
    return targetValue;
  }

  public void setTargetValue(Double targetValue) {
    this.targetValue = targetValue;
  }

  public Double getStopLossValue() {
    return stopLossValue;
  }

  public void setStopLossValue(Double stopLossValue) {
    this.stopLossValue = stopLossValue;
  }

  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  public String getAssetSymbol() {
    return assetSymbol;
  }

  public void setAssetSymbol(String assetSymbol) {
    this.assetSymbol = assetSymbol;
  }

  public void patch(ActionEntity source) {
    if (source == null) return;

    if (source.getDescription() != null) {
      this.description = source.getDescription();
    }

    switch (this.actionType) {
      case BUY_AUTOMATIC:
        if (source.getAmount() != null) {
          this.amount = source.getAmount();
        }
        if (source.getAssetSymbol() != null) {
          this.assetSymbol = source.getAssetSymbol();
        }
        break;
      case SELL_AUTOMATIC:
        if (source.getAmount() != null) {
          this.amount = source.getAmount();
        }
        if (source.getAssetSymbol() != null) {
          this.assetSymbol = source.getAssetSymbol();
        }
        if (source.getTargetValue() != null) {
          this.targetValue = source.getTargetValue();
        }
        break;
      case SET_STOP_LOSS:
        if (source.getStopLossValue() != null) {
          this.stopLossValue = source.getStopLossValue();
        }
        if (source.getAssetSymbol() != null) {
          this.assetSymbol = source.getAssetSymbol();
        }
        break;
      default:
        break;
    }
  }
}
