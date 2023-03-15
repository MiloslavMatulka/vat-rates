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
 *
 * @author Miloslav Matulka (Discord tag Miloslav#8572)
 */
public class VatRates {
    public static BigDecimal inputVatStdLimit() throws VatRatesException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Zadej výši sazby DPH/VAT, podle které se má "
                + "filtrovat >> ");
        String input = scanner.nextLine();
        BigDecimal inputVatStdLimit = null;
        if (input.isBlank()) {
            inputVatStdLimit = Constants.getVatDefault();
        } else {
            try {
                if (input.contains(",")) {
                    Number inputToNumber =
                            Constants.getNumberFormat().parse(input);
                    inputVatStdLimit =
                            new BigDecimal(inputToNumber.toString());
                } else {
                    inputVatStdLimit = new BigDecimal(input);
                }
            } catch (ParseException | NumberFormatException e) {
                throw new VatRatesException("Neplatná vstupní hodnota: "
                        + e.getLocalizedMessage());
            }
        }
        return inputVatStdLimit;
    }

    /**
     * Prints the description of all countries imported from a file.
     *
     * @param listOfCountries list of countries
     */
    public static void print(List<Country> listOfCountries) {
        System.out.println("Vypiš šechny země:");
        listOfCountries.forEach(country ->
                System.out.println(country.getDescription()));
        System.out.println(Constants.getTaskSeparator());
    }

    /**
     * Prints the description of an unsorted list of countries filtered
     * by the passed standard VAT.
     *
     * @param listOfCountries list of countries
     * @param vatStdLimit standard VAT rate used for filtering the list
     */
    public static void printByVat(List<Country> listOfCountries,
                                           BigDecimal vatStdLimit) {
        System.out.println("Vypiš země s DPH vyšší než "
                + Constants.getNumberFormat().format(vatStdLimit)
                + " % a bez speciální sazby daně:");
        List<Country> filteredList =
                VatRatesList.filterByVat(listOfCountries, vatStdLimit);
        filteredList.forEach(country ->
                System.out.println(country.getDescription()));
        System.out.println(Constants.getTaskSeparator());
    }

    /**
     * Prints the description of a list of countries filtered by the passed
     * standard VAT, sorted in descending order.
     *
     * @param listOfCountries list of countries
     * @param vatStdLimit standard VAT rate used for filtering the list
     */
    public static void printByVatDescending(List<Country> listOfCountries,
                                  BigDecimal vatStdLimit) {
        System.out.println("Vypiš země s DPH vyšší než "
                + Constants.getNumberFormat().format(vatStdLimit)
                + " % a bez speciální sazby daně, sestupně:");
        List<Country> filteredList =
                VatRatesList.filterByVat(listOfCountries, vatStdLimit);
        List<Country> sortedListByVatStd =
                VatRatesList.sortByVatStdDescending(filteredList);
        sortedListByVatStd.forEach(country ->
                System.out.println(country.getDescription()));
        System.out.println(Constants.getTaskSeparator());
    }

    /**
     * Prints the description of a list of countries filtered by the passed
     * standard VAT, sorted in descending order, and a list of other countries
     * codes sorted in ascending order.
     * 2-pass implementation of partitioning of countries.
     *
     * @param listOfCountries list of countries
     * @param vatStdLimit standard VAT rate used for filtering the list
     */
    public static void printByVatWithOthers(List<Country> listOfCountries,
                                            BigDecimal vatStdLimit) {
        System.out.println("Vypiš země s DPH vyšší než "
                + Constants.getNumberFormat().format(vatStdLimit)
                + " % a bez speciální sazby daně, sestupně, "
                + "seznam zkratek, které ve výpisu nefigurují, "
                + "vzestupně:");
        List<Country> filteredList =
                VatRatesList.filterByVat(listOfCountries, vatStdLimit);
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
                sortedListByCode, vatStdLimit));
        System.out.println(Constants.getTaskSeparator());
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
        System.out.println("Vypiš země s DPH vyšší než "
                + Constants.getNumberFormat().format(vatStdLimit)
                + " % a bez speciální sazby daně, sestupně, "
                + "seznam zkratek, které ve výpisu nefigurují, vzestupně, "
                + "státy rozděleny na 1 průchod:");
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
        System.out.println(Constants.getTaskSeparator());
    }

    public static void printToFile(List<Country> listOfCountries,
                                   BigDecimal vatStdLimit)
            throws VatRatesException {
        System.out.println("Exportuj výpis do souboru \""
                + Constants.getResourcesPath()
                + "vat-over-" + vatStdLimit + ".txt\"");
        VatRatesList
                .exportToFile(listOfCountries, vatStdLimit);
        System.out.println(Constants.getTaskSeparator());

    }

    public static void main(String[] args) {
        Logger logger = Logger.getLogger("VAT rates");
        try {
            List<Country> importedList = VatRatesList
                    .importFromFile(Constants.getInputFile(),
                            Constants.getDelimiter());
            VatRatesList vatRatesList = new VatRatesList(importedList);
            List<Country> listOfCountries = vatRatesList.getListOfCountries();

            System.out.println("Třídění států EU podle DPH/VAT");
            print(listOfCountries);

            BigDecimal vatStdLimit = Constants.getVatDefault();
            printByVat(listOfCountries, vatStdLimit);

            printByVatDescending(listOfCountries, vatStdLimit);

            printByVatWithOthers(listOfCountries, vatStdLimit);

            printByVatWithOthersAltn(
                    VatRatesList.filterByVatOnePass(listOfCountries,
                            vatStdLimit),
                    vatStdLimit);

            printToFile(listOfCountries, vatStdLimit);

            vatStdLimit = inputVatStdLimit();
            printByVatWithOthersAltn(
                    VatRatesList.filterByVatOnePass(listOfCountries,
                            vatStdLimit),
                    vatStdLimit);

            printToFile(listOfCountries, vatStdLimit);
        } catch (VatRatesException e) {
            logger.log(Level.WARNING, e.getClass().getName() + ": "
                    + e.getLocalizedMessage());
        }
    }
}
