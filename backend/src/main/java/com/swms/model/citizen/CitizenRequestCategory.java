package com.swms.model.citizen;

public enum CitizenRequestCategory {
    OVERFLOWING_BIN("Overflowing Bin"),
    DAMAGED_BIN("Damaged Bin"),
    MISSING_BIN("Missing Bin"),
    ILLEGAL_DUMPING("Illegal Dumping"),
    REGULAR_PICKUP_REQUEST("Regular Pickup Request"),
    OTHER("Other");

    private final String displayName;

    CitizenRequestCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}