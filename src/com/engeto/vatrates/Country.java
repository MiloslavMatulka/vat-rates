package com.engeto.vatrates;

import java.math.BigDecimal;

public class Country {

    //region Attributes
    private String codeOfCountry;
    private boolean hasVatSpecial;
    private String nameOfCountry;
    private BigDecimal vatReduced;
    private BigDecimal vatStandard;
    //endregion

    public Country(String codeOfCountry, String nameOfCountry,
                   BigDecimal vatStandard, BigDecimal vatReduced,
                   boolean hasVatSpecial) {
        this.codeOfCountry = codeOfCountry;
        this.nameOfCountry = nameOfCountry;
        this.vatStandard = vatStandard;
        this.vatReduced = vatReduced;
        this.hasVatSpecial = hasVatSpecial;
    }

    //region Getters and Setters
    public String getCodeOfCountry() {
        return codeOfCountry;
    }

    public void setCodeOfCountry(String codeOfCountry) {
        this.codeOfCountry = codeOfCountry;
    }

    public boolean hasVatSpecial() {
        return hasVatSpecial;
    }

    public void setVatSpecial(boolean hasVatSpecial) {
        this.hasVatSpecial = hasVatSpecial;
    }

    public String getNameOfCountry() {
        return nameOfCountry;
    }

    public void setNameOfCountry(String nameOfCountry) {
        this.nameOfCountry = nameOfCountry;
    }

    public BigDecimal getVatReduced() {
        return vatReduced;
    }

    public void setVatReduced(BigDecimal vatReduced) {
        this.vatReduced = vatReduced;
    }

    public BigDecimal getVatStandard() {
        return vatStandard;
    }

    public void setVatStandard(BigDecimal vatStandard) {
        this.vatStandard = vatStandard;
    }
    //endregion

    public String getDescription() {
        return getNameOfCountry() + " (" + getCodeOfCountry() + "): "
                + Settings.getNumberFormat().format(getVatStandard()) + " %";
    }

    public String getDescriptionVerbose() {
        return getNameOfCountry() + " (" + getCodeOfCountry() + "): "
                + Settings.getNumberFormat().format(getVatStandard()) + " % ("
                + Settings.getNumberFormat().format(getVatReduced()) + " %)";
    }
}
