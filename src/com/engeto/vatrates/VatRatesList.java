package com.engeto.vatrates;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

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

    public static List<Country> importFromFile(String fileName)
            throws VatRatesException {
        List<Country> list = new ArrayList<>();
        File input = new File(fileName);
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
        scanner.useLocale(Locale.of("cs"));
        scanner.useDelimiter(Settings.getDelimiter());

        String codeOfCountry = scanner.next();
        String nameOfCountry = scanner.next();
        BigDecimal vatStandard = null;
        BigDecimal vatReduced = null;
        try {
        vatStandard = scanner.nextBigDecimal();
        vatReduced = scanner.nextBigDecimal();
        } catch (InputMismatchException e) {
            throw new VatRatesException("Nesprávný formát čísla; "
                    + e.getLocalizedMessage());
        }
        boolean hasVatSpecial = scanner.nextBoolean();

        return new Country(codeOfCountry, nameOfCountry, vatStandard,
                vatReduced, hasVatSpecial);
    }

    /**
     * Filters a list of countries.  Only countries over the submitted value
     * and without the special VAT are accepted.
     *
     * @param vatStd The standard VAT value used for filtering the list.
     * @return Returns filtered list of countries.
     */
    public static List<Country> filterVat(List<Country> listOfCountries,
                                          BigDecimal vatStd) {
        return listOfCountries
                .stream()
                .filter(country ->
                        country.getVatStandard()
                                .compareTo(vatStd) > 0
                                && !country.hasVatSpecial())
                .toList();
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
            List<Country> listOfCountries,List<Country> subList) {
        return listOfCountries.stream()
                .filter(country -> !subList.contains(country))
                .toList();
    }
}
