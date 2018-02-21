package com.ing.ideal.connector;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Class that contains an issuer's information.
 */
public class Issuer {

    /**
     * Issuer ID.
     */
    private String issuerID;
    /**
     * The name of the Issuer (as this should be displayed to the Consumer on the
     * Merchant‟s Issuer list).
     */
    private String issuerName;

    /**
     * @param issuerID   Issuer ID.
     * @param issuerName The name of the Issuer (as this should be displayed to the Consumer on the Merchant‟s Issuer list).
     */
    Issuer(String issuerID, String issuerName) {
        this.issuerID = issuerID;
        this.issuerName = issuerName;
    }

    /**
     * @return issuer ID
     */
    public String getIssuerID() {
        return issuerID;
    }

    /**
     * @return issuer name
     */
    public String getIssuerName() {
        return issuerName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Issuer issuer = (Issuer) o;
        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(issuerID, issuer.issuerID)
                .append(issuerName, issuer.issuerName)
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(issuerID)
                .append(issuerName)
                .toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("issuerID", issuerID)
                .append("issuerName", issuerName)
                .build();
    }
}