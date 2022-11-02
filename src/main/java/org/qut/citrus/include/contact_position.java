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
public class contact_position {
    public int block_no;
    public int x;
    public int y;
    public contact_position(int block_no, int x, int y){
        this.block_no = block_no;
        this.x = x;
        this.y = y;
    }
}
