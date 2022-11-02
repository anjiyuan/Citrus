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
public class binX_idx {
    public int block_no;
    public int idx;
    public binX_idx(int block_no, int idx){
        this.block_no = block_no;
        this.idx = idx;
    }
    public String toString() {
        return block_no + ":" + idx;
    }
}

