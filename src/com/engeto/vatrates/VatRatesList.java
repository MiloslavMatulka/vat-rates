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

    public List<Country> importFromFile(String fileName)
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

    public void printItems(List<Country> listOfCountries) {
        listOfCountries
                .forEach(country ->
                        System.out.println(country.getDescription()));
    }

    public void printItemsVerbose(List<Country> listOfCountries) {
//        for (Country country : listOfCountries) {
//            System.out.println(country.getDescriptionVerbose());
//        }
//        System.out.println("====================");
//        System.out.println("Sazba VAT " + Settings.getVatLimit()
//                + " % nebo nižší nebo používají speciální sazbu: ");
        listOfCountries.stream()
                .filter(country ->
                        country.getVatStandard()
                                .compareTo(Settings.getVatLimit()) > 0
                        && !country.hasVatSpecial())
                .sorted(Comparator.comparing(Country::getVatStandard)
                        .reversed())
                .forEach(country ->
                        System.out.println(country.getDescriptionVerbose()));
        System.out.println("====================");
        String countryCodesFiltered = listOfCountries.stream().filter(country ->
                country.getVatStandard()
                        .compareTo(Settings.getVatLimit()) <= 0
                || country.hasVatSpecial())
                .map(Country::getCodeOfCountry)
        .sorted()
        .collect(Collectors.joining(", "));
        System.out.println("Sazba VAT " + Settings.getVatLimit()
                + " % nebo nižší nebo používají speciální sazbu: "
                + countryCodesFiltered);
    }
}
