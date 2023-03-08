package com.engeto.vatrates;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class VatRatesList {
    private List<Country> listOfCountries;

    public VatRatesList(List<Country> listOfCountries) {
        this.listOfCountries = listOfCountries;
    }

    public List<Country> getListOfCountries() {
        return new ArrayList<>(listOfCountries);
    }

    public void setListOfCountries(List<Country> listOfCountries) {
        this.listOfCountries = listOfCountries;
    }

    public void clearListOfCountries(List<Country> listOfCountries) {
        listOfCountries.clear();
    }

    public static void exportToFile(List<Country> data, BigDecimal vatStdLimit)
            throws VatRatesException {
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(
                new FileWriter(Settings.getResourcesPath()
                        + "vat-over-" + vatStdLimit + ".txt")))) {
            Map<Boolean, List<Country>> mapOfCountries =
                    filterByVatOnePass(data, vatStdLimit);
            List<Country> listOverLimit = mapOfCountries.get(true);
            List<Country> sortedListOverLimitDescending =
                    sortByVatStdDescending(listOverLimit);
            sortedListOverLimitDescending.forEach(country ->
                    writer.println(country.getDescriptionVerbose()));

            List<Country> listOfOthers = mapOfCountries.get(false);
            List<Country> sortedListOfOthers = sortByCode(listOfOthers);
            writer.println(getStringOfOtherCountries(
                    sortedListOfOthers, vatStdLimit));
        } catch (IOException e) {
            throw new VatRatesException(e.getLocalizedMessage());
        }
    }

    public static List<Country> importFromFile(String file)
            throws VatRatesException {
        List<Country> list = new ArrayList<>();
        File input = new File(file);
        long lineNumber = 0L;
        try (Scanner scanner = new Scanner(input)) {
            while (scanner.hasNextLine()) {
                ++lineNumber;
                String record = scanner.nextLine();
                list.add(parseCountry(record));
            }
        } catch (FileNotFoundException e) {
            throw new VatRatesException(e.getLocalizedMessage());
        } catch (VatRatesException e) {
            throw new VatRatesException(e.getLocalizedMessage()
                    + ", řádek souboru č. " + lineNumber);
        }
        return list;
    }

    public static Country parseCountry(String data) throws VatRatesException {
        Scanner scanner = new Scanner(data);
        scanner.useLocale(Settings.getLocale());
        scanner.useDelimiter(Settings.getDelimiter());

        String codeOfCountry = scanner.next();
        String nameOfCountry = scanner.next();
        BigDecimal vatStandard = null;
        BigDecimal vatReduced = null;
        try {
            vatStandard = scanner.nextBigDecimal();
            vatReduced = scanner.nextBigDecimal();
        } catch (InputMismatchException e) {
            throw new VatRatesException("Neplatné číslo; "
                    + e.getLocalizedMessage());
        }
        boolean hasVatSpecial = scanner.nextBoolean();

        return new Country(codeOfCountry, nameOfCountry, vatStandard,
                vatReduced, hasVatSpecial);
    }

    /**
     * Filters a list of countries. Only countries over the submitted value
     * and without the special VAT are accepted.
     *
     * @param vatStdLimit The standard VAT value used for filtering the list.
     * @return Returns filtered list of countries.
     */
    public static List<Country> filterByVat(List<Country> listOfCountries,
                                            BigDecimal vatStdLimit) {
        return listOfCountries
                .stream()
                .filter(country ->
                        country.getVatStandard()
                                .compareTo(vatStdLimit) > 0
                        && !country.hasVatSpecial())
                .toList();
    }

    /**
     * Filters a list of countries in one pass. Partitions a list into two
     * lists. Boolean keys correspond to them in a map.
     *
     * @return Returns a map of partitioned lists.
     */
    public static Map<Boolean, List<Country>> filterByVatOnePass(
            List<Country> listOfCountries,
            BigDecimal vatStdLimit) {
        return listOfCountries
                .stream()
                .collect(Collectors.groupingBy(country ->
                        country.getVatStandard().compareTo(vatStdLimit) > 0
                        && !country.hasVatSpecial()));
    }

    public static List<Country> sortByCode(List<Country> listOfCountries) {
        return listOfCountries.stream()
                .sorted(Comparator.comparing(Country::getCodeOfCountry))
                .toList();
    }

    public static List<Country> sortByVatStdDescending(
            List<Country> listOfCountries) {
        return listOfCountries.stream()
                .sorted(Comparator.comparing(Country::getVatStandard)
                        .reversed()).toList();
    }

    public static List<Country> subtractFilteredVat(
            List<Country> listOfCountries, List<Country> subList) {
        return listOfCountries.stream()
                .filter(country -> !subList.contains(country))
                .toList();
    }

    protected static String getStringOfOtherCountries(
            List<Country> listOfCountries, BigDecimal vatStdLimit) {

        // Localize the number format
        String vatStdLimitToStr =
                Settings.getNumberFormat().format(vatStdLimit);
        return "====================\n"
                + "Sazba VAT " + vatStdLimitToStr + " % nebo nižší nebo "
                + "používají speciální sazbu: "
                +  listOfCountries.stream()
                .map(Country::getCodeOfCountry)
                .collect(Collectors.joining(", "));
    }
}
