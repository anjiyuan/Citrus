/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.qut.citrus.include;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author an
 */
public class Block {
    private final int number;
    private final String uniqueRegionID;
    private final List<ContactRecord> records;

    public Block(int number, String regionID) {
        this.number = number;
        records = new ArrayList<>();
        uniqueRegionID = regionID + "_" + number;
    }

    public Block(int number, List<ContactRecord> records, String regionID) {
        this.number = number;
        this.records = records;
        this.uniqueRegionID = regionID + "_" + number;
    }

    public int getNumber() {
        return number;
    }

    public String getUniqueRegionID() {
        return uniqueRegionID;
    }

    public List<ContactRecord> getContactRecords() {
        return records;
    }    
}
