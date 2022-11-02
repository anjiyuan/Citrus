/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.qut.citrus.include;

import java.util.Objects;

/**
 *
 * @author an
 */
public class ContactRecord  implements Comparable<ContactRecord> {
    
    /**
     * Bin number in x coordinate
     */
    public int binX;

    /**
     * Bin number in y coordinate
     */
    public int binY;

    /**
     * Total number of counts, or cumulative score
     */
    public float counts;

    public ContactRecord(int binX, int binY, float counts) {
        this.binX = binX;
        this.binY = binY;
        this.counts = counts;
    }

    public void incrementCount(float score) {
        counts += score;
    }


    public int getBinX() {
        return binX;
    }

    public int getBinY() {
        return binY;
    }

    public float getCounts() {
        return counts;
    }

    @Override
    public int compareTo(ContactRecord contactRecord) {
        if (this.binY != contactRecord.binY) {
            return binY - contactRecord.binY;
        } else if (this.binX != contactRecord.binX) {
            return binX - contactRecord.binX;
        } else return 0;
    }

    public String toString() {
        return "binX=" + binX + " binY=" + binY + " counts=" + counts;
    }

    @Override
    public int hashCode() {
        return Objects.hash(binX, binY, counts);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        return compareTo((ContactRecord) obj) == 0;
    }


    public String getKey(NormalizationType normalizationType) {
        return binX + "_" + binY + "_" + normalizationType;
    }
}
