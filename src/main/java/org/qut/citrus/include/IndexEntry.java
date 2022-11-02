/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.qut.citrus.include;

/**
 *
 * @author an
 */
public class IndexEntry {
    public final long position;
    public final int size;
    int id;

    IndexEntry(int id, long position, int size) {
        this.id = id;
        this.position = position;
        this.size = size;
    }

    public IndexEntry(long position, int size) {
        this.position = position;
        this.size = size;
    } 
    public String toString(){
        return "id="+id+" position="+position+" size="+size;
    }
}
