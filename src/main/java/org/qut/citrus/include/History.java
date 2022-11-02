/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.qut.citrus.include;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author an
 */
public class History {
    public int type; //0:transpose (fragment) 10:transpose (chrom) 1:move (fragment)  2: move (chrom) 3:split block
    public int selected_order;
    public int selected_next_order = -1;//used for insert_order
    public Map<Integer, List<Integer>> fragments_moved = new TreeMap<>();
    public History(int type, int selected_order){
        this.type = type;
        this.selected_order = selected_order;
    }
    public History(int type, int selected_order, int selected_next_order){
        this.type = type;
        this.selected_order = selected_order;
        this.selected_next_order =  selected_next_order;
    }
    public void add_fragments_moved(int chr_no, List<Integer> orders){
        List<Integer> tmp = new ArrayList<>();
        tmp.addAll(orders);
        this.fragments_moved.put(chr_no, orders);
    }
}
