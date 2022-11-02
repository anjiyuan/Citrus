/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.qut.citrus.include;

import java.util.Comparator;
import java.util.Objects;

/**
 *
 * @author an
 */
public class Chromosome {

    private final String name;
    private int index;
    private long length = 0;

    public Chromosome(int index, String name, long length) {
        this.name = name;
        this.index = index;
        this.length = length;
    }
    public Chromosome(int index){
        this.name = "dummy";
        this.index = index;
    }
     @Override
    public String toString(){
        return "name="+name+" index="+index+" length="+length;
    }
    
    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    public long getLength() {
        return length;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public boolean equals(Object obj) {
        return obj instanceof Chromosome && ((Chromosome) obj).getIndex() == this.getIndex() && ((Chromosome) obj).getLength() == this.getLength();
    }

    public int hashCode() {
        return Objects.hash(this.index, this.length);
    }

    public org.broad.igv.feature.Chromosome toIGVChromosome() {
        return new org.broad.igv.feature.Chromosome(index, name, (int) length); // assumed for IGV
    } 
}

class cmp_Chromosome implements Comparator<Chromosome>{

    @Override
    public int compare(Chromosome o1, Chromosome o2) {
        return o1.getIndex() - o2.getIndex();
    }
    
}