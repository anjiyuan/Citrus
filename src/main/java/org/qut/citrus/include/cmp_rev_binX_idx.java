/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.qut.citrus.include;

import java.util.Comparator;

/**
 *
 * @author an
 */
public class cmp_rev_binX_idx implements Comparator<binX_idx>{

    @Override
    public int compare(binX_idx o1, binX_idx o2) {
        int ret = o2.block_no - o1.block_no;
        if(ret == 0){
            return o2.idx - o1.idx;
        }
        return ret;
    }
    
}

