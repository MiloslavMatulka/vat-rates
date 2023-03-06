package com.engeto.vatrates;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class Settings {
    private static final String DELIMITER = "\t";
    private static final String INPUT_FILE = getResourcesPath() + "vat-eu.csv";
    private static final Locale LOCALE = Locale.of("cs", "CZ");
    private static final NumberFormat NUMBER_FORMAT =
            NumberFormat.getInstance(getLocale());
    private static final String RESOURCES_PATH = "res/";
    private static final BigDecimal VAT_LIMIT = new BigDecimal(20);

    //region Getters
    public static String getDelimiter() {
        return DELIMITER;
    }

    public static String getInputFile() {
        return INPUT_FILE;
    }

    public static Locale getLocale() {
        return LOCALE;
    }

    public static NumberFormat getNumberFormat() {
        return NUMBER_FORMAT;
    }

    public static String getResourcesPath() {
        return RESOURCES_PATH;
    }

    public static BigDecimal getVatLimit() {
        return VAT_LIMIT;
    }
    //endregion
}
