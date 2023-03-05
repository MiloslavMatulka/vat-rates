package com.engeto.vatrates;

import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VatRates {
    public static void main(String[] args) {
        Logger logger = Logger.getLogger("VAT rates");
        try {
            List<Country> listOfCountries = VatRatesList
                    .importFromFile(Settings.getInputFile());
            VatRatesList vatRatesList = new VatRatesList(listOfCountries);
            vatRatesList.setListOfCountries(listOfCountries);
            vatRatesList.printCountries();
            System.out.println("---");
            vatRatesList.printCountriesByVat(BigDecimal.valueOf(20));
        } catch (VatRatesException e) {
            logger.log(Level.WARNING, e.getClass().getName() + ": "
                    + e.getLocalizedMessage());
        }
    }
}
