/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.qut.citrus.include;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import static org.qut.citrus.include.utility_access_binary_file.readInt;
import static org.qut.citrus.include.utility_access_binary_file.readLong;
import static org.qut.citrus.include.utility_access_binary_file.readString;

/**
 *
 * @author an
 */
public class Hic_header {
    public int version;
    public long masterIndexPos;
    public String genomeId;
    public long nviHeaderPosition;
    public Map<String, String> attributes = new HashMap<>();
    public final Map<String, IndexEntry> masterIndex = Collections.synchronizedMap(new HashMap<>());
    public List<Chromosome> chromosomes;
    public int[] bpBinSizes;
    public int[] resBP;
    public int[] resFrag;
    public int[] site;
    private long normVectorFilePosition;

    
    public Hic_header(String Hic_fn) throws IOException {
        FileInputStream fin = new FileInputStream(Hic_fn);
        DataInputStream dis = new DataInputStream(fin);
        long position = 0L;
        String magicString = readString(dis);
        position += magicString.length() + 1;
        if (!magicString.equals("HIC")) {
            throw new IOException("Magic string is not HIC, this does not appear to be a hic file.");
        }

        version = readInt(dis);
        position += 4;

        System.out.println("HiC file version: " + version);
        masterIndexPos = readLong(dis);
        System.out.println("masterIndexPos: " + masterIndexPos);
        position += 8;

        // will set genomeId below
        genomeId = readString(dis);
        System.out.println("genomeId: " + genomeId);
        position += genomeId.length() + 1;

        if (version > 8) {
            // read NVI todo
            nviHeaderPosition = position;
            long nvi = readLong(dis);
            long nviSize = readLong(dis);
            System.err.println(nvi + " " + nviSize);
            position += 16;
        }

        // Attributes  (key-value pairs)
        if (version > 4) {
            int nAttributes = readInt(dis);
            System.out.println("nAttributes: " + nAttributes);
            position += 4;

            for (int i = 0; i < nAttributes; i++) {
                String key = readString(dis);
                position += key.length() + 1;

                String value = readString(dis);
                position += value.length() + 1;
                attributes.put(key, value);
            }
        }

        // Read chromosome dictionary
        int nChrs = readInt(dis);
        System.out.println("nChrs: " + nChrs);
        position += 4;

        chromosomes = new ArrayList<>(nChrs);
        for (int i = 0; i < nChrs; i++) {
            String name = readString(dis);
            position += name.length() + 1;

            long size;
            if (version > 8) {
                size = readLong(dis);
                position += 8;
            } else {
                size = readInt(dis);
                position += 4;
            }
            System.out.println("chrom name=" + name + " size=" + size);
            chromosomes.add(new Chromosome(i, name, size));
        }
        Collections.sort(chromosomes, new cmp_Chromosome());

        int nBpResolutions = readInt(dis);
        position += 4;

        bpBinSizes = new int[nBpResolutions];
        for (int i = 0; i < nBpResolutions; i++) {
            bpBinSizes[i] = readInt(dis);
            System.out.println("bpBinSizes["+i+"]=" + bpBinSizes[i]);
            position += 4;
        }

        int nFragResolutions = readInt(dis);
        position += 4;

        resFrag = new int[nFragResolutions];
        for (int i = 0; i < nFragResolutions; i++) {
            resFrag[i] = readInt(dis);
            System.out.println("resFrag["+i+"]=" + resFrag[i]);
            position += 4;
        }
        System.out.println("header size:"+position);
        System.out.println("header finished");
        
        fin.close();
        dis.close();
       
        fin = new FileInputStream(Hic_fn);
        fin.getChannel().position(masterIndexPos);
        dis = new DataInputStream(fin);
        long currentPosition = masterIndexPos;

        long nBytes;
        if (version > 8) {
            nBytes = readLong(dis);
            currentPosition += 8;
            normVectorFilePosition = masterIndexPos + nBytes + 8;  // 8 bytes for the buffer size
        } else {
            nBytes = readInt(dis);
            currentPosition += 4;
            normVectorFilePosition = masterIndexPos + nBytes + 4;  // 4 bytes for the buffer size
        }

        int nEntries = readInt(dis);
        currentPosition += 4;
        //System.err.println(nEntries);
        System.out.println("masterIndex (Body):   nEntries="+nEntries);
        System.out.println("key" + "\t" + "filePosition" + "\t" + "sizeInBytes");
        for (int i = 0; i < nEntries; i++) {
            String key = readString(dis);
            currentPosition += (key.length() + 1);
            long filePosition = readLong(dis);
            int sizeInBytes = readInt(dis);
            currentPosition += 12;
            masterIndex.put(key, new IndexEntry(filePosition, sizeInBytes));
            System.out.println(key + "\t" + filePosition + "\t" + sizeInBytes);
        }
        System.out.println("masterIndex.size()="+masterIndex.size());
        for (String key : masterIndex.keySet()) {
            System.out.println("key in masterIndex="+key);
//            readMatrix(key);
        }        
        
        fin.close();
        dis.close();
    }
    public int getVersion() {
        return version;
    }

    public String getGenomeId() {
        return genomeId;
    }

    public Map<String, IndexEntry> getMasterIndex() {
        return masterIndex;
    }

    public Chromosome getChr(String chr){
        for(Chromosome chrom : chromosomes){
            if(chrom.getName().equals(chr)){
                return chrom;
            }
        }
        return null;
    }

    public List<Chromosome> getChromosomes() {
        return chromosomes;
    }

    public int[] getBpBinSizes() {
        return bpBinSizes;
    }

    public int[] getResBP() {
        return resBP;
    }
        
    
    
}
