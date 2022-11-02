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
public class cmp_ContactRecord implements Comparator<ContactRecord>{

    @Override
    public int compare(ContactRecord o1, ContactRecord o2) {
        int ret = o1.binY - o2.binY;
        if(ret == 0){
            return o1.binX - o2.binX;
        }
        return ret;
    }
    
}