package com.engeto.vatrates;

import java.math.BigDecimal;
import java.nio.file.FileSystems;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Collected constants of general utility.
 */
public class Constants {
    private static final String DELIMITER = "\t";
    private static final Locale LOCALE = Locale.of("cs", "CZ");
    private static final NumberFormat NUMBER_FORMAT =
            NumberFormat.getInstance(getLocale());
    private static final String PATH_SEPARATOR =
            FileSystems.getDefault().getSeparator();
    private static final String RESOURCES_PATH = "." + getPathSeparator()
            + "res" + getPathSeparator();
    private static final String INPUT_FILE = getResourcesPath() + "vat-eu.csv";
    private static final BigDecimal VAT_DEFAULT = new BigDecimal(20);

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

    public static String getPathSeparator() {
        return PATH_SEPARATOR;
    }

    public static String getResourcesPath() {
        return RESOURCES_PATH;
    }

    public static BigDecimal getVatDefault() {
        return VAT_DEFAULT;
    }
    //endregion
}
