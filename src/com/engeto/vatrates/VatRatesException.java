package com.engeto.vatrates;

/**
 * User defined exception that better differentiates error messages from other
 * exceptions.
 */
public class VatRatesException extends Exception {
    public VatRatesException(String msg) {
        super(msg);
    }
}
