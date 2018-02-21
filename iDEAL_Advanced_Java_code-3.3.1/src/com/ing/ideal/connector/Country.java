package com.ing.ideal.connector;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

/**
 * Contains all CountryName and Issuer sub-elements.
 */
public class Country {

    /**
     * Contains the countryNames in the official languages of the country,
     * separated by a „/‟ symbol (e.g. „België/Belgique‟)
     */
    private String countryNames;
    /**
     * List of issuers.
     */
    private List<Issuer> issuerList;

    public Country() {
    }

    /**
     * @param countryNames country names
     * @param issuerList   list of issuers
     */
    public Country(String countryNames, List<Issuer> issuerList) {
        this.countryNames = countryNames;
        this.issuerList = issuerList;
    }

    /**
     * Returns the countryNames in the official languages of the country,
     * separated by a „/‟ symbol (e.g. „België/Belgique‟)
     *
     * @return country names
     */
    public String getCountryNames() {
        return countryNames;
    }

    /**
     * @return list of issuers for the country
     */
    public List<Issuer> getIssuers() {
        return issuerList;
    }

    /**
     * @param countryNames country names
     */
    public void setCountryNames(String countryNames) {
        this.countryNames = countryNames;
    }

    /**
     * @param issuerList list of issuers for the country
     */
    public void setIssuers(List<Issuer> issuerList) {
        this.issuerList = issuerList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Country country = (Country) o;
        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(countryNames, country.countryNames)
                .append(issuerList, country.issuerList)
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(countryNames)
                .append(issuerList)
                .toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("countryNames", countryNames)
                .append("issuerList", issuerList)
                .toString();
    }
}
