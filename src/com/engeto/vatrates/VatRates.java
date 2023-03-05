package com.engeto.vatrates;

import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class VatRates {
    public static void printCountries(List<Country> listOfCountries) {
        listOfCountries.forEach(country ->
                System.out.println(country.getDescription()));
    }

    public static void printCountriesByVat(List<Country> listOfCountries,
                                    BigDecimal vatStd) {
        List<Country> filteredList =
                VatRatesList.filterVat(listOfCountries, vatStd);
        List<Country> sortedListByVatStd =
                VatRatesList.sortByVatStdDescending(filteredList);
        sortedListByVatStd.forEach(country ->
                System.out.println(country.getDescriptionVerbose()));

        List<Country> subtractedList =
                VatRatesList.subtractFilteredVat(listOfCountries,
                        sortedListByVatStd);
        List<Country> sortedListByCode =
                VatRatesList.sortByCode(subtractedList);
        System.out.println("====================\n"
                + "Sazba VAT " + vatStd + " % nebo nižší nebo používají "
                + "speciální sazbu: " + sortedListByCode.stream()
                .map(Country::getCodeOfCountry)
                .collect(Collectors.joining(", ")));
    }

    public static void main(String[] args) {
        Logger logger = Logger.getLogger("VAT rates");
        try {
            List<Country> listOfCountries = VatRatesList
                    .importFromFile(Settings.getInputFile());
//            VatRatesList vatRatesList = new VatRatesList(listOfCountries);
            printCountries(listOfCountries);
            System.out.println("---");
            printCountriesByVat(listOfCountries, BigDecimal.valueOf(20));
        } catch (VatRatesException e) {
            logger.log(Level.WARNING, e.getClass().getName() + ": "
                    + e.getLocalizedMessage());
        }
    }
}
