package com.engeto.vatrates;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VatRates {
    public static void main(String[] args) {
        Logger logger = Logger.getLogger("VAT rates");
        VatRatesList listOfCountries = new VatRatesList();
        try {
            List<Country> list =
                    listOfCountries.importFromFile(Settings.getInputFile());
            listOfCountries.printItems(list);
            System.out.println("---");
            listOfCountries.printItemsVerbose(list);
        } catch (VatRatesException e) {
            logger.log(Level.WARNING, e.getClass().getName() + ": "
                    + e.getLocalizedMessage());
        }
    }
}
