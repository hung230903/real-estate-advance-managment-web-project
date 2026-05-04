package com.webapp.enums;

import java.util.HashMap;
import java.util.Map;

public enum TransactionType {
    CSKH("Customer Care"),
    DDX("Site Visit");

    private final String name;

    TransactionType(String name) {
        this.name = name;
    }

    public static Map<String, String> getTransactionTypes() {
        Map<String, String> types = new HashMap<>();
        for (TransactionType type : TransactionType.values()) {
            types.put(type.name(), type.name);
        }
        return types;
    }
}
