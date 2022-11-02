/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.qut.citrus.include;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 * @author an
 */
public class writerHic {
    String hic_fn;
    DataOutputStream dis_bit;
    public writerHic(String hic_fn) throws FileNotFoundException {
        this.hic_fn = hic_fn;
        dis_bit = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(hic_fn)));
    }

    public void write_HH(Hic_header HH) throws IOException {
        long position = 0L;
        dis_bit.write(string2byte("HIC"));
        dis_bit.write(int2byte(HH.version));
//        String magicString = readString(dis);
//        position += magicString.length() + 1;
//        if (!magicString.equals("HIC")) {
//            throw new IOException("Magic string is not HIC, this does not appear to be a hic file.");
//        }
//
//        version = readInt(dis);
//        position += 4;
//
//        System.out.println("HiC file version: " + version);
        dis_bit.write(long2byte(HH.masterIndexPos));
//        masterIndexPos = readLong(dis);
//        System.out.println("masterIndexPos: " + masterIndexPos);
//        position += 8;

        // will set genomeId below
        dis_bit.write(string2byte(HH.genomeId));
//        genomeId = readString(dis);
//        System.out.println("genomeId: " + genomeId);
//        position += genomeId.length() + 1;

        if (HH.version > 8) {
            System.err.println("not yet for version > 8");
            System.exit(0);
//            // read NVI todo
//            nviHeaderPosition = position;
//            long nvi = readLong(dis);
//            long nviSize = readLong(dis);
//            System.err.println(nvi + " " + nviSize);
//            position += 16;
        }

        // Attributes  (key-value pairs)
        if (HH.version > 4) {
            dis_bit.write(int2byte(HH.attributes.size()));
//            int nAttributes = readInt(dis);
//            System.out.println("nAttributes: " + nAttributes);
//            position += 4;
            for(String key : HH.attributes.keySet()){
                 dis_bit.write(string2byte(key));
                 dis_bit.write(string2byte(HH.attributes.get(key)));
            }
//            for (int i = 0; i < nAttributes; i++) {
//                String key = readString(dis);
//                position += key.length() + 1;
//
//                String value = readString(dis);
//                position += value.length() + 1;
//                attributes.put(key, value);
//            }
        }

        // Read chromosome dictionary
        dis_bit.write(int2byte(HH.chromosomes.size()));
        for(Chromosome chrom : HH.chromosomes){
            dis_bit.write(string2byte(chrom.getName()));
            if(HH.version > 8){
                dis_bit.write(long2byte(chrom.getLength()));
            }else{
                dis_bit.write(int2byte((int)chrom.getLength()));
            }
        }
//        int nChrs = readInt(dis);
//        System.out.println("nChrs: " + nChrs);
//        position += 4;

//        chromosomes = new ArrayList<>(nChrs);
//        for (int i = 0; i < nChrs; i++) {
//            String name = readString(dis);
//            position += name.length() + 1;
//
//            long size;
//            if (version > 8) {
//                size = readLong(dis);
//                position += 8;
//            } else {
//                size = readInt(dis);
//                position += 4;
//            }
//            System.out.println("chrom name=" + name + " size=" + size);
//            chromosomes.add(new Chromosome(i, name, size));
//        }
//        Collections.sort(chromosomes, new cmp_Chromosome());

        dis_bit.write(int2byte(HH.bpBinSizes.length));
//        int nBpResolutions = readInt(dis);
//        position += 4;
        for(int i = 0; i < HH.bpBinSizes.length; i++){
            dis_bit.write(int2byte(HH.bpBinSizes[i]));
        }
//        bpBinSizes = new int[nBpResolutions];
//        for (int i = 0; i < nBpResolutions; i++) {
//            bpBinSizes[i] = readInt(dis);
//            System.out.println("bpBinSizes["+i+"]=" + bpBinSizes[i]);
//            position += 4;
//        }
        dis_bit.write(int2byte(HH.resFrag.length));
//        int nFragResolutions = readInt(dis);
//        position += 4;
        for(int resu : HH.resFrag){
            dis_bit.write(int2byte(resu));
        }
//        int[] fragBinSizes = new int[nFragResolutions];
//        for (int i = 0; i < nFragResolutions; i++) {
//            fragBinSizes[i] = readInt(dis);
//            System.out.println("fragBinSizes["+i+"]=" + fragBinSizes[i]);
//            position += 4;
//        }
        
        
//        fin.close();
//        dis.close();
//       
//        fin = new FileInputStream(Hic_fn);
//        fin.getChannel().position(masterIndexPos);
//        dis = new DataInputStream(fin);
//        long currentPosition = masterIndexPos;
//
//        long nBytes;
//        if (version > 8) {
//            nBytes = readLong(dis);
//            currentPosition += 8;
//            normVectorFilePosition = masterIndexPos + nBytes + 8;  // 8 bytes for the buffer size
//        } else {
//            nBytes = readInt(dis);
//            currentPosition += 4;
//            normVectorFilePosition = masterIndexPos + nBytes + 4;  // 4 bytes for the buffer size
//        }
//
//        int nEntries = readInt(dis);
//        currentPosition += 4;
//        //System.err.println(nEntries);
//        System.out.println("masterIndex (Body):   nEntries="+nEntries);
//        System.out.println("key" + "\t" + "filePosition" + "\t" + "sizeInBytes");
//        for (int i = 0; i < nEntries; i++) {
//            String key = readString(dis);
//            currentPosition += (key.length() + 1);
//            long filePosition = readLong(dis);
//            int sizeInBytes = readInt(dis);
//            currentPosition += 12;
//            masterIndex.put(key, new IndexEntry(filePosition, sizeInBytes));
//            System.out.println(key + "\t" + filePosition + "\t" + sizeInBytes);
//        }
//        System.out.println("masterIndex.size()="+masterIndex.size());
//        for (String key : masterIndex.keySet()) {
//            System.out.println("key in masterIndex="+key);
////            readMatrix(key);
//        }        
//        
//        fin.close();
//        dis.close();

    }
    private byte[] int2byte(int value) {
        return new byte[]{
            (byte) value,
            (byte) (value >>> 8),
            (byte) (value >>> 16),
            (byte) (value >>> 24)
        };
    }

    private byte[] string2byte(String str) {
        return str.getBytes();
    }

    private byte[] long2byte(final long i) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        dos.writeLong(i);
        dos.flush();
        return bos.toByteArray();
    }

    
    
}
