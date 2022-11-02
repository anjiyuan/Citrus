/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.qut.citrus.include;

import java.util.Comparator;
import java.util.List;

/**
 *
 * @author an
 */
public class cmp_binX_idx_list implements Comparator<List<binX_idx>>{

    @Override
    public int compare(List<binX_idx> o1, List<binX_idx> o2) {
        int ret = o2.get(0).block_no - o1.get(0).block_no;
        if(ret == 0){
            return o2.get(0).idx - o1.get(0).idx;
        }
        return ret;
    }
    
}
