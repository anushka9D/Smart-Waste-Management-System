package com.swms.model.citizen;

public enum CitizenRequestStatus {
    SUBMITTED("Submitted"),
    ASSIGNED("Assigned"),
    COLLECTION_SCHEDULED("Collection Scheduled"),
    OUT_FOR_COLLECTION("Out for collection"),
    COLLECTED("Collected"),
    RESOLVED("Resolved"),
    CANCELLED("Cancelled");

    private final String displayName;

    CitizenRequestStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}