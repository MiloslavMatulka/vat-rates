package com.engeto.vatrates;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class VatRates {
    public static void print(List<Country> listOfCountries) {
        listOfCountries.forEach(country ->
                System.out.println(country.getDescription()));
    }

    public static void printByVat(List<Country> listOfCountries,
                                           BigDecimal vatStd) {
        List<Country> filteredList =
                VatRatesList.filterByVat(listOfCountries, vatStd);
        filteredList.forEach(country ->
                System.out.println(country.getDescription()));
    }

    public static void printByVatDescending(List<Country> listOfCountries,
                                  BigDecimal vatStd) {
        List<Country> filteredList =
                VatRatesList.filterByVat(listOfCountries, vatStd);
        List<Country> sortedListByVatStd =
                VatRatesList.sortByVatStdDescending(filteredList);
        sortedListByVatStd.forEach(country ->
                System.out.println(country.getDescription()));
    }

    public static void printByVatWithOthers(List<Country> listOfCountries,
                                            BigDecimal vatStd) {
        List<Country> filteredList =
                VatRatesList.filterByVat(listOfCountries, vatStd);
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

    public static void printByVatWithOthersAltn(
            Map<Boolean, List<Country>> mapOfCountries,
            BigDecimal vatStdLimit) {
        List<Country> listOverLimit = mapOfCountries.get(true);
        List<Country> sortedListOverLimitDescending =
                VatRatesList.sortByVatStdDescending(listOverLimit);
        sortedListOverLimitDescending.forEach(country ->
                System.out.println(country.getDescriptionVerbose()));

        List<Country> listOfOthers = mapOfCountries.get(false);
        List<Country> sortedListOfOthers =
                VatRatesList.sortByCode(listOfOthers);
        System.out.println("====================\n"
        + "Sazba VAT " + vatStdLimit + " % nebo nižší nebo používají "
        + "speciální sazbu: " +  sortedListOfOthers.stream()
                .map(Country::getCodeOfCountry)
                        .sorted()
                .collect(Collectors.joining(", ")));
    }

    public static void main(String[] args) {
        Logger logger = Logger.getLogger("VAT rates");
        try {
            List<Country> listOfCountries = VatRatesList
                    .importFromFile(Settings.getInputFile());
//            VatRatesList vatRatesList = new VatRatesList(listOfCountries);
            System.out.println("Všechny země:");
            print(listOfCountries);
            System.out.println("---");

            System.out.println("Země s DPH vyšší než 20 % a bez speciální "
                    + "sazby daně:");
            printByVat(listOfCountries, Settings.getVatLimit());
            System.out.println("---");

            System.out.println("Země s DPH vyšší než 20 % a bez speciální "
                    + "sazby daně, sestupně:");
            printByVatDescending(listOfCountries, Settings.getVatLimit());
            System.out.println("---");

            System.out.println("Země s DPH vyšší než 20 % a bez speciální "
                    + "sazby daně, sestupně, seznam zkratek, které ve výpisu "
                    + "nefigurují, vzestupně:");
            printByVatWithOthers(listOfCountries, Settings.getVatLimit());
            System.out.println("---");

            System.out.println("Země s DPH vyšší než 20 % a bez speciální "
                    + "sazby daně, sestupně, seznam zkratek, které ve výpisu "
                    + "nefigurují, vzestupně, státy rozděleny na 1 průchod:");
            printByVatWithOthersAltn(
                    VatRatesList.filterByVatOnePass(listOfCountries,
                            Settings.getVatLimit()),
                    Settings.getVatLimit());
            System.out.println("---");

            System.out.println("Exporting extract to file \""
                    + Settings.getResourcesPath() + "vat-over-20.txt\"");
            System.out.println("---");
            VatRatesList.exportToFile(listOfCountries, Settings.getVatLimit());
        } catch (VatRatesException e) {
            logger.log(Level.WARNING, e.getClass().getName() + ": "
                    + e.getLocalizedMessage());
        }
    }
}
