package com.travelPlanWithAccounting.service.entity;

import java.util.UUID;

public class TxResult {
    private final UUID poiId;
    private final boolean poiCreated;
    private final boolean langInserted;
    private final boolean alreadySaved;

    public TxResult(UUID poiId, boolean poiCreated, boolean langInserted, boolean alreadySaved) {
      this.poiId = poiId;
      this.poiCreated = poiCreated;
      this.langInserted = langInserted;
      this.alreadySaved = alreadySaved;
    }

    public UUID poiId() {
      return poiId;
    }

    public boolean poiCreated() {
      return poiCreated;
    }

    public boolean langInserted() {
      return langInserted;
    }

    public boolean alreadySaved() {
      return alreadySaved;
    }
  }
