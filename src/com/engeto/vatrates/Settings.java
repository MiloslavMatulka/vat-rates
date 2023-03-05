package com.engeto.vatrates;

import java.math.BigDecimal;

public class Settings {
    private static final String DELIMITER = "\t";
    private static final String INPUT_FILE = getResourcesPath() + "vat-eu.csv";
    private static final String RESOURCES_PATH = "res/";
    private static final BigDecimal VAT_LIMIT = new BigDecimal(20);

    //region Getters
    public static String getDelimiter() {
        return DELIMITER;
    }

    public static String getInputFile() {
        return INPUT_FILE;
    }

    public static String getResourcesPath() {
        return RESOURCES_PATH;
    }

    public static BigDecimal getVatLimit() {
        return VAT_LIMIT;
    }
    //endregion
}
