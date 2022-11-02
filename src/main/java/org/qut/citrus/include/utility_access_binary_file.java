/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.qut.citrus.include;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author an
 */
public class utility_access_binary_file {

    static public int readInt(DataInputStream dis_bit) throws IOException {
        byte[] b = new byte[4];
        if (dis_bit.read(b) != 4) {
            System.err.println("EOF in readInt");
        }
        return (b[3] << 24) & 0xff000000
                | (b[2] << 16) & 0x00ff0000
                | (b[1] << 8) & 0x0000ff00
                | (b[0]) & 0x000000ff;
    }

    static public float readFloat(DataInputStream dis_bit) throws IOException {
        byte[] b = new byte[4];
        if (dis_bit.read(b) != 4) {
            System.err.println("EOF in readInt");
        }
        int asInt = (b[3] << 24) & 0xff000000
                | (b[2] << 16) & 0x00ff0000
                | (b[1] << 8) & 0x0000ff00
                | (b[0]) & 0x000000ff;
        return Float.intBitsToFloat(asInt);
    }
    
    static public int byte2int(byte[] b, int pos){
        int ret = 0;
        for (int i = 0; i < 4; i++){
            ret |= ((b[pos + i] & 0xff) << (8 * i));
        }
        return ret;
    }

    static public long byte2long(byte[] b, int pos){
        int ret = 0;
        for (int i = 4; i < 8; i++){
            ret |= ((b[pos + i] & 0xff) << (8 * i));
        }
        for (int i = 0; i < 4; i++){
            ret |= ((b[pos + i] & 0xff) << (8 * i));
        }
        return ret;
    }
    
    
    static public long readLong(DataInputStream dis_bit) throws IOException {
        byte[] b = new byte[8];
        if (dis_bit.read(b) != 8) {
            System.err.println("EOF in readInt");
        }
        long first = (b[3] << 24) & 0xff000000
                | (b[2] << 16) & 0x00ff0000
                | (b[1] << 8) & 0x0000ff00
                | (b[0]) & 0x000000ff;
        long second = (b[7] << 24) & 0xff000000
                | (b[6] << 16) & 0x00ff0000
                | (b[5] << 8) & 0x0000ff00
                | (b[4]) & 0x000000ff;
        return (second << 32) + first;
    }

    static public int readShort(DataInputStream dis_bit) throws IOException {
        byte[] b = new byte[2];
        if (dis_bit.read(b) != 2) {
            System.err.println("EOF in readShort");
        }
        return (b[1] << 8) & 0x0000ff00
                | (b[0]) & 0x000000ff;
    }


    static public int readByte(DataInputStream dis_bit) throws IOException {
        byte[] b = new byte[1];
        dis_bit.read(b);
        return (b[0]) & 0x000000ff;
    }

    static public String readString(DataInputStream dis_bit) throws IOException {
        ArrayList<Byte> ret = new ArrayList<>();
        byte[] b = new byte[1];
        while(dis_bit.read(b) == 1){
            if(b[0] == 0){
                byte[] tmp = new byte[ret.size()];
                for(int i = 0; i < tmp.length; i++){
                    tmp[i] = ret.get(i);                    
                }
                return new String(tmp);
            }else{
                ret.add(b[0]);
            }
        }
        return "";
    }


    }
