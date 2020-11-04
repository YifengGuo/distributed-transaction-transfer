package com.yifeng.commons.constant;

public enum BankEnum {
    BANK_SERVICE_A("bank-service-A"),

    BANK_SERVICE_B("bank-service-B");

    private String name;

    BankEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
