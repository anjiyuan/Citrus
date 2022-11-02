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
public class NormalizationType {
	//LOADED("Loaded");
	private final String label;
	private final String description;
	
	public NormalizationType(String label, String description) {

        this.label = label.toUpperCase();
        String description2 = description;
        if (label.endsWith("SCALE")) {
            description2 = description2.replaceAll("Fast scaling", "Balanced++");
            description2 = description2.replaceAll("fast scaling", "Balanced++");
        }

        this.description = description2;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return label;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof NormalizationType) {
            NormalizationType norm2 = (NormalizationType) obj;
            return label.equalsIgnoreCase(norm2.getLabel());
        }
        return false;
    }

    @Override
    public int hashCode() {
		return Objects.hash(label.hashCode(), description.hashCode());
    }
    
}
