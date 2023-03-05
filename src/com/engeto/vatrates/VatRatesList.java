package com.engeto.vatrates;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class VatRatesList {
    private List<Country> listOfCountries = new ArrayList<>();

    public List<Country> getListOfCountries() {
        return new ArrayList<>(listOfCountries);
    }

    public void setListOfCountries(List<Country> listOfCountries) {
        this.listOfCountries = listOfCountries;
    }

    /**
     * Filters a list of countries.  Only countries over the submitted value
     * and without the special VAT are accepted.
     *
     * @param vatStd The standard VAT value used for filtering the list.
     * @return Returns filtered list of countries.
     */
    public List<Country> filterVat(BigDecimal vatStd) {
        return listOfCountries
                .stream()
                .filter(country ->
                        country.getVatStandard()
                                .compareTo(vatStd) > 0
                                && !country.hasVatSpecial())
                .toList();
    }

    public static List<Country> importFromFile(String fileName)
            throws VatRatesException {
        List<Country> list = new ArrayList<>();
        File input = new File(fileName);
        long lineNumber = 0L;
        try (Scanner scanner = new Scanner(input)) {
//            scanner.useLocale(Locale.of("cs"));
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

    public void printCountries() {
        listOfCountries.forEach(country ->
                System.out.println(country.getDescription()));
    }

    public void printCountriesByVat(BigDecimal vatStd) {
        List<Country> filteredList = filterVat(vatStd);
        List<Country> sortedListByVatStd =
                sortByVatStdDescending(filteredList);
        sortedListByVatStd.forEach(country ->
                System.out.println(country.getDescriptionVerbose()));

        List<Country> subtractedList = subtractFilteredVat(sortedListByVatStd);
        List<Country> sortedListByCode = sortByCode(subtractedList);
        System.out.println("====================\n"
                + "Sazba VAT " + vatStd + " % nebo nižší nebo používají "
                + "speciální sazbu: " + sortedListByCode.stream()
                        .map(Country::getCodeOfCountry)
                        .collect(Collectors.joining(", ")));
    }

    public List<Country> sortByCode(List<Country> countryList) {
        return countryList.stream()
                .sorted(Comparator.comparing(Country::getCodeOfCountry))
                .toList();
    }

    public List<Country> sortByVatStdDescending(List<Country> countryList) {
        return countryList.stream()
                .sorted(Comparator.comparing(Country::getVatStandard)
                        .reversed()).toList();
    }

    public List<Country> subtractFilteredVat(List<Country> subList) {
        return listOfCountries.stream()
                .filter(country -> !subList.contains(country))
                .toList();
    }
}
