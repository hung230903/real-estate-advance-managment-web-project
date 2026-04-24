package com.webapp.enums;

import java.util.HashMap;
import java.util.Map;

public enum TransactionType {
    CSKH("Chăm sóc khách hàng"),
    DDX("Dẫn đi xem");

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
