package com.efuture.product.util;

import lombok.Getter;

import java.math.BigDecimal;

public class Constants {
    public static final BigDecimal PREMIUM_PRODUCT_PRICE_LIMIT = new BigDecimal(500);
    public static final String KAFKA_BOOTSTRAP_SERVERS = "localhost:29092";
    public static final String CARD_EVENT_TOPIC = "product-topic";

    public static class ResponseCodes {
        public static final int STATUS_OK = 200;
        public static final int STATUS_ERROR = 500;
        public static final int STATUS_CREATED = 201;
        public static final int STATUS_FORBIDDEN = 403;
        public static final int STATUS_BAD_REQUEST = 400;
    }

    public static class ResponseMsg {
        public static final String SUCCESS = "success";
        public static final String ALREADY_DELETED = "already deleted";
    }

    public static class ErrorMsg {
        public static final String PRODUCT_NOT_FOUND = "The product is not found";
        public static final String INVALID_PRODUCT_ID = "The product id is invalid";
    }

    public static class ErrorDescription {
        public static final String PRODUCT_NOT_FOUND_DESCRIPTION = "The product is not found :: id %s";
        public static final String PRODUCT_ALREADY_DELETED = "The product is already deleted : %s";
    }

    @Getter
    public static enum ProductStatus{
        ACTIVE("A"),
        DELETED("D");

        private final String value;

        ProductStatus(String value) {
            this.value = value;
        }
    }
}
