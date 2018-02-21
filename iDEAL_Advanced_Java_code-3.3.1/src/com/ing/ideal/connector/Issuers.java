package com.ing.ideal.connector;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Directory request result.
 */
public class Issuers {
    /**
     * List of countries. Contains all CountryName and Issuer sub-elements.
     */
    private List<Country> countryList;
    /**
     * The date on which the list of Issuers was updated by the Acquirer.
     */
    private String dateTimeStamp;
    /**
     * Acquirer ID.
     */
    private String acquirerID;

    /**
     * Creates a new Issuers object.
     *
     * @param dateTimeStamp the date on which the list of Issuers was updated by the Acquirer
     */
    public Issuers(String dateTimeStamp) {
        this.dateTimeStamp = dateTimeStamp;
        setCountryList(new ArrayList<Country>());
    }

    /**
     * @return The date on which the list of Issuers was updated by the Acquirer.
     */
    public String getDateTimeStamp() {
        return dateTimeStamp;
    }

    /**
     * @return the countryList
     */
    public List<Country> getCountryList() {
        return countryList;
    }

    /**
     * @param countryList the countryList to set
     */
    public void setCountryList(List<Country> countryList) {
        this.countryList = countryList;
    }

    /**
     * @return the acquirerID
     */
    public String getAcquirerID() {
        return acquirerID;
    }

    /**
     * @param acquirerID the acquirerID to set
     */
    public void setAcquirerID(String acquirerID) {
        this.acquirerID = acquirerID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Issuers issuers = (Issuers) o;
        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(acquirerID, issuers.acquirerID)
                .append(countryList, issuers.countryList)
                .append(dateTimeStamp, issuers.dateTimeStamp)
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(acquirerID)
                .append(countryList)
                .append(dateTimeStamp)
                .toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("countryList", countryList)
                .append("dateTimeStamp", dateTimeStamp)
                .append("acquirerID", acquirerID)
                .toString();
    }
}