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
                    .importFromFile(Constants.getInputFile(),
                            Constants.getDelimiter());
            VatRatesList vatRatesList = new VatRatesList(importedList);
            List<Country> listOfCountries = vatRatesList.getListOfCountries();

            System.out.println("T????d??n?? st??t?? EU podle DPH/VAT");
            System.out.println("1. Vypi?? ??echny zem??:");
            print(listOfCountries);
            String taskSeparator = "---";
            System.out.println(taskSeparator);

            System.out.println("2. Vypi?? zem?? s DPH vy?????? ne?? "
                    + Constants.getVatDefault()
                    + " % a bez speci??ln?? sazby dan??:");
            printByVat(listOfCountries, Constants.getVatDefault());
            System.out.println(taskSeparator);

            System.out.println("3. Vypi?? zem?? s DPH vy?????? ne?? "
                    + Constants.getVatDefault()
                    + " % a bez speci??ln?? sazby dan??, sestupn??:");
            printByVatDescending(listOfCountries, Constants.getVatDefault());
            System.out.println(taskSeparator);

            System.out.println("4. Vypi?? zem?? s DPH vy?????? ne?? "
                    + Constants.getVatDefault()
                    + " % a bez speci??ln?? sazby dan??, sestupn??, "
                    + "seznam zkratek, kter?? ve v??pisu nefiguruj??, "
                    + "vzestupn??:");
            printByVatWithOthers(listOfCountries, Constants.getVatDefault());
            System.out.println(taskSeparator);

            System.out.println("5. Vypi?? zem?? s DPH vy?????? ne?? "
                    + Constants.getVatDefault()
                    + " % a bez speci??ln?? sazby dan??, sestupn??, "
                    + "seznam zkratek, kter?? ve v??pisu nefiguruj??, vzestupn??, "
                    + "st??ty rozd??leny na 1 pr??chod:");
            printByVatWithOthersAltn(
                    VatRatesList.filterByVatOnePass(listOfCountries,
                            Constants.getVatDefault()),
                    Constants.getVatDefault());
            System.out.println(taskSeparator);

            System.out.println("6. Exportuj v??pis do souboru \""
                    + Constants.getResourcesPath()
                    + "vat-over-" + Constants.getVatDefault() + ".txt\"");
            VatRatesList
                    .exportToFile(listOfCountries, Constants.getVatDefault());
            System.out.println(taskSeparator);

            Scanner scanner = new Scanner(System.in);
            System.out.print("7. Zadej v????i sazby DPH/VAT, podle kter?? se m?? "
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
                    throw new VatRatesException("Neplatn?? vstupn?? hodnota: "
                            + e.getLocalizedMessage());
                }
            }
            printByVatWithOthersAltn(
                    VatRatesList.filterByVatOnePass(listOfCountries,
                            inputVatLimit),
                    inputVatLimit);
            System.out.println(taskSeparator);

            System.out.println("8. Exportuj v??pis do souboru \""
                    + Constants.getResourcesPath()
                    + "vat-over-" + inputVatLimit + ".txt\"");
            VatRatesList.exportToFile(listOfCountries, inputVatLimit);
        } catch (VatRatesException e) {
            logger.log(Level.WARNING, e.getClass().getName() + ": "
                    + e.getLocalizedMessage());
        }
    }
}
