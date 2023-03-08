package com.engeto.vatrates;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        System.out.println(VatRatesList.getStringOfOtherCountries(
                sortedListByCode, vatStd));
    }

    public static void printByVatWithOthersAltn(
            Map<Boolean, List<Country>> mapOfCountries,
            BigDecimal vatStdLimit) throws VatRatesException {
        List<Country> listOverLimit = mapOfCountries.get(true);
        List<Country> sortedListOverLimitDescending =
                VatRatesList.sortByVatStdDescending(listOverLimit);
        sortedListOverLimitDescending.forEach(country ->
                System.out.println(country.getDescriptionVerbose()));

        List<Country> listOfOthers = mapOfCountries.get(false);
        List<Country> sortedListOfOthers =
                VatRatesList.sortByCode(listOfOthers);
        System.out.println(VatRatesList.getStringOfOtherCountries(
                sortedListOfOthers, vatStdLimit));
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
            printByVat(listOfCountries, Settings.getVatDefault());
            System.out.println("---");

            System.out.println("Země s DPH vyšší než 20 % a bez speciální "
                    + "sazby daně, sestupně:");
            printByVatDescending(listOfCountries, Settings.getVatDefault());
            System.out.println("---");

            System.out.println("Země s DPH vyšší než 20 % a bez speciální "
                    + "sazby daně, sestupně, seznam zkratek, které ve výpisu "
                    + "nefigurují, vzestupně:");
            printByVatWithOthers(listOfCountries, Settings.getVatDefault());
            System.out.println("---");

            System.out.println("Země s DPH vyšší než 20 % a bez speciální "
                    + "sazby daně, sestupně, seznam zkratek, které ve výpisu "
                    + "nefigurují, vzestupně, státy rozděleny na 1 průchod:");
            printByVatWithOthersAltn(
                    VatRatesList.filterByVatOnePass(listOfCountries,
                            Settings.getVatDefault()),
                    Settings.getVatDefault());
            System.out.println("---");

            System.out.println("Exporting an extract to file \""
                    + Settings.getResourcesPath() + "vat-over-20.txt\"");
            VatRatesList.exportToFile(listOfCountries, Settings.getVatDefault());
            System.out.println("---");

            Scanner scanner = new Scanner(System.in);
            System.out.print("Zadej výši sazby DPH/VAT, podle které se má "
                    + "filtrovat >> ");
            String input = scanner.nextLine();
            BigDecimal inputVatLimit = null;
            if (input.isEmpty()) {
                inputVatLimit = Settings.getVatDefault();
            } else {
                try {
                    if (input.contains(",")) {
                        Number inputToNumber =
                                Settings.getNumberFormat().parse(input);
                        inputVatLimit = new BigDecimal(inputToNumber.toString());
                    } else {
                        inputVatLimit = new BigDecimal(input);
                    }
                } catch (ParseException | NumberFormatException e) {
                    throw new VatRatesException("Neplatná vstupní hodnota; "
                            + e.getLocalizedMessage());
                }
            }
            printByVatWithOthersAltn(
                    VatRatesList.filterByVatOnePass(listOfCountries,
                            inputVatLimit),
                    inputVatLimit);
            VatRatesList.exportToFile(listOfCountries, inputVatLimit);
        } catch (VatRatesException e) {
            logger.log(Level.WARNING, e.getClass().getName() + ": "
                    + e.getLocalizedMessage());
        }
    }
}
