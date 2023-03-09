package com.engeto.vatrates;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Application that extracts information about VAT rates of countries defined
 * in a file.
 */
public class VatRates {

    /**
     * Prints the description of all countries imported from a file.
     *
     * @param listOfCountries list of countries
     */
    public static void print(List<Country> listOfCountries) {
        listOfCountries.forEach(country ->
                System.out.println(country.getDescription()));
    }

    /**
     * Prints the description of an unsorted list of countries filtered
     * by the passed standard VAT.
     *
     * @param listOfCountries list of countries
     * @param vatStd standard VAT rate used for filtering the list
     */
    public static void printByVat(List<Country> listOfCountries,
                                           BigDecimal vatStd) {
        List<Country> filteredList =
                VatRatesList.filterByVat(listOfCountries, vatStd);
        filteredList.forEach(country ->
                System.out.println(country.getDescription()));
    }

    /**
     * Prints the description of a list of countries filtered by the passed
     * standard VAT, sorted in descending order.
     *
     * @param listOfCountries list of countries
     * @param vatStd standard VAT rate used for filtering the list
     */
    public static void printByVatDescending(List<Country> listOfCountries,
                                  BigDecimal vatStd) {
        List<Country> filteredList =
                VatRatesList.filterByVat(listOfCountries, vatStd);
        List<Country> sortedListByVatStd =
                VatRatesList.sortByVatStdDescending(filteredList);
        sortedListByVatStd.forEach(country ->
                System.out.println(country.getDescription()));
    }

    /**
     * Prints the description of a list of countries filtered by the passed
     * standard VAT, sorted in descending order, and a list of other countries
     * codes sorted in ascending order.
     * 2-pass implementation of partitioning of countries.
     *
     * @param listOfCountries list of countries
     * @param vatStd standard VAT rate used for filtering the list
     */
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

    /**
     * Prints the description of a list of countries filtered by the passed
     * standard VAT, sorted in descending order, and a list of other countries
     * codes sorted in ascending order.
     * Alternative 1-pass implementation of partitioning of countries.
     *
     * @param mapOfCountries list of countries
     * @param vatStdLimit standard VAT rate used for filtering the list
     */
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
        System.out.println(VatRatesList.getStringOfOtherCountries(
                sortedListOfOthers, vatStdLimit));
    }

    public static void main(String[] args) {
        Logger logger = Logger.getLogger("VAT rates");
        try {
            List<Country> importedList = VatRatesList
                    .importFromFile(Constants.getInputFile());
            VatRatesList vatRatesList = new VatRatesList(importedList);
            List<Country> listOfCountries = vatRatesList.getListOfCountries();

            System.out.println("Všechny země:");
            print(listOfCountries);
            System.out.println("---");

            System.out.println("Země s DPH vyšší než 20 % a bez speciální "
                    + "sazby daně:");
            printByVat(listOfCountries, Constants.getVatDefault());
            System.out.println("---");

            System.out.println("Země s DPH vyšší než 20 % a bez speciální "
                    + "sazby daně, sestupně:");
            printByVatDescending(listOfCountries, Constants.getVatDefault());
            System.out.println("---");

            System.out.println("Země s DPH vyšší než 20 % a bez speciální "
                    + "sazby daně, sestupně, seznam zkratek, které ve výpisu "
                    + "nefigurují, vzestupně:");
            printByVatWithOthers(listOfCountries, Constants.getVatDefault());
            System.out.println("---");

            System.out.println("Země s DPH vyšší než 20 % a bez speciální "
                    + "sazby daně, sestupně, seznam zkratek, které ve výpisu "
                    + "nefigurují, vzestupně, státy rozděleny na 1 průchod:");
            printByVatWithOthersAltn(
                    VatRatesList.filterByVatOnePass(listOfCountries,
                            Constants.getVatDefault()),
                    Constants.getVatDefault());
            System.out.println("---");

            System.out.println("Exporting an extract to file \""
                    + Constants.getResourcesPath() + "vat-over-20.txt\"");
            VatRatesList.exportToFile(listOfCountries, Constants.getVatDefault());
            System.out.println("---");

            Scanner scanner = new Scanner(System.in);
            System.out.print("Zadej výši sazby DPH/VAT, podle které se má "
                    + "filtrovat >> ");
            String input = scanner.nextLine();
            BigDecimal inputVatLimit = null;
            if (input.isBlank()) {
                inputVatLimit = Constants.getVatDefault();
            } else {
                try {
                    if (input.contains(",")) {
                        Number inputToNumber =
                                Constants.getNumberFormat().parse(input);
                        inputVatLimit =
                                new BigDecimal(inputToNumber.toString());
                    } else {
                        inputVatLimit = new BigDecimal(input);
                    }
                } catch (ParseException | NumberFormatException e) {
                    throw new VatRatesException("Neplatná vstupní hodnota: "
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
