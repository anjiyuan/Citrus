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
public class binX_range {
    public int block_no;
    public int start_idx;
    public int stop_idx;
    public binX_range(int block_no, int start_idx, int stop_idx){
        this.block_no = block_no;
        this.start_idx = start_idx;
        this.stop_idx = stop_idx;
//        this.stop_idx = stop_idx;
    }
    @Override
    public String toString() {
        return block_no + ":" + start_idx + "-" + stop_idx;
    }
}





