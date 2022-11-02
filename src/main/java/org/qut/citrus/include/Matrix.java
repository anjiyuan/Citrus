/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.qut.citrus.include;


import htsjdk.tribble.util.LittleEndianInputStream;
import htsjdk.tribble.util.LittleEndianOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.Deflater;
import org.broad.igv.tdf.BufferedByteWriter;
import org.broad.igv.util.CompressionUtils;

/**
 *
 * @author an
 */
public class Matrix {
    protected LittleEndianOutputStream[] losArray = new LittleEndianOutputStream[1];    
    
    private final CompressionUtils compressionUtils = new CompressionUtils();
    private final Map<String, IndexEntry> masterIndex = new HashMap<>();
    private final String Hic_fn;
    List<Chromosome> chromosomes = new ArrayList<>();
    int version;
//    int[] blockBinCount;
//    int[] blockColumnCount;
    BufferedWriter bw;
    long hic_fileposition = 0;

    public Matrix(String Hic_fn, int version, Map materIndex, List<Chromosome> chromosomes) throws IOException {
        this.masterIndex.putAll(materIndex);
        this.Hic_fn = Hic_fn;
        this.version = version;
        this.chromosomes.addAll(chromosomes);
        bw = new BufferedWriter(new FileWriter("output_new"));
    }
    
    public void close() throws IOException {
        bw.close();
    }

    private byte[] seekAndFullyReadCompressedBytes(DataInputStream dis, IndexEntry idx) throws IOException {
        byte[] compressedBytes = new byte[idx.size];
//        SeekableStream stream = getValidStream();
//        stream.seek(idx.position);
//        stream.readFully(compressedBytes);
//        stream.close();
        dis.read(compressedBytes);
        return compressedBytes;
    } 
    
    public Map<Integer, numContact_X_Y_per_resolution_str> readMatrix(String key, int max_resolution) throws IOException {
        Map<Integer, numContact_X_Y_per_resolution_str> ret = new TreeMap<>();
//        key="11_24";
//key="1_1";
        IndexEntry idx = masterIndex.get(key);
        if (idx == null) {
            return null;
        }
        FileInputStream fin = new FileInputStream(Hic_fn);
        fin.getChannel().position(idx.position);
        DataInputStream dis = new DataInputStream(fin);

        hic_fileposition = idx.position;
        
        System.out.println("chr="+key+" idx:(position="+idx.position+" size="+idx.size+")");
        byte[] buffer = seekAndFullyReadCompressedBytes(dis, idx);
        hic_fileposition += idx.size;
//IndexEntry ie = new IndexEntry(1,2,390973);
//byte[] buffer = seekAndFullyReadCompressedBytes(dis, ie);
//
//byte[] buffer1;
//                 try {
//                    buffer1 = decompress(buffer);
//
//                } catch (Exception e) {
//                    throw new RuntimeException("Block read error: " + e.getMessage());
//                }

        
        LittleEndianInputStream buffer_dis = new LittleEndianInputStream(new ByteArrayInputStream(buffer));
        
        int chr1Idx = buffer_dis.readInt();
        System.out.println("chr1Idx:"+chr1Idx);
        int chr2Idx = buffer_dis.readInt();
        System.out.println("chr2Idx:"+chr2Idx);
        Chromosome chr1 = chromosomes.get(Collections.binarySearch(chromosomes, new Chromosome(chr1Idx), new cmp_Chromosome()));
        Chromosome chr2 = chromosomes.get(Collections.binarySearch(chromosomes, new Chromosome(chr1Idx), new cmp_Chromosome()));
        
        int nResolutions = buffer_dis.readInt();
        System.out.println("nResolutions:"+nResolutions);
        long currentFilePosition = idx.position + 12;
        System.out.println("\n+++++++key="+key+" nResolutions=" + nResolutions);
        bw.write("+++++++key="+key+" nResolutions=" + nResolutions+"\n");
//        blockBinCount = new int[Math.min(nResolutions, max_resolution)];
//        blockColumnCount = new int[Math.min(nResolutions, max_resolution)];
BufferedWriter bw = new BufferedWriter(new FileWriter("aaa"));
        for (int resolution_no = 0; (resolution_no < nResolutions) && (resolution_no < max_resolution); resolution_no++) {
            System.out.println("resolution=(resolution_no=" + resolution_no + ")");
            bw.write("resolution=(resolution_no=" + resolution_no + ")\n");
//            if (resolution_no != 2){
//                continue;
//            }
            String hicUnitStr = buffer_dis.readString();
            System.out.println("hicUnitStr:"+hicUnitStr);
            int resIdx = buffer_dis.readInt();                // Old "zoom" index -- not used

            // Stats.  Not used yet, but we need to read them anyway
            float sumCounts = buffer_dis.readFloat();
            float occupiedCellCount = buffer_dis.readFloat();
            float stdDev = buffer_dis.readFloat();
            float percent95 = buffer_dis.readFloat();

            int binSize = buffer_dis.readInt();
            System.out.println("binSize:"+binSize);
//        HiCZoom zoom = new HiCZoom(unit, binSize);
            // TODO: Default binSize value for "ALL" is 6197...
            //  (actually (genomeLength/1000)/500; depending on bug fix, could be 6191 for hg19);
            //  We need to make sure our maps hold a valid binSize value as default.

            int blockBinCount = buffer_dis.readInt();
//            blockBinCount[resolution_no] = blockBinCount1;
            System.out.println("blockBinCount:"+blockBinCount);
            int blockColumnCount = buffer_dis.readInt();
//            blockColumnCount[resolution_no] = blockColumnCount1;
            System.out.println("blockColumnCount:"+blockColumnCount);

//        MatrixZoomData zd = new MatrixZoomData(chr1, chr2, zoom, blockBinCount, blockColumnCount, chr1Sites, chr2Sites,
//                this);
            int nBlocks = buffer_dis.readInt();//blockCount
            System.out.println("blockCount:"+nBlocks+"\n");
            long currentFilePointer = currentFilePosition + (9 * 4) + hicUnitStr.getBytes().length + 1; // resolution_no think 1 byte for 0 terminated string?

//        if (binSize < 50 && HiCGlobals.allowDynamicBlockIndex) {
//            int maxPossibleBlockNumber = blockColumnCount * blockColumnCount - 1;
//            DynamicBlockIndex blockIndex = new DynamicBlockIndex(getValidStream(), nBlocks, maxPossibleBlockNumber, currentFilePointer);
//            blockIndexMap.put(zd.getKey(), blockIndex);
//        } else {
            BlockIndex blockIndex = new BlockIndex(nBlocks);
            blockIndex.populateBlocks(buffer_dis);
//            blockIndexMap.put(zd.getKey(), blockIndex);
//        }
            currentFilePointer += (nBlocks * 16L);

            long nBins1 = chr1.getLength() / binSize;
            long nBins2 = chr2.getLength() / binSize;
            double avgCount = (sumCounts / nBins1) / nBins2;   // <= trying to avoid overflows
//        zd.setAverageCount(avgCount);

//        stream.close();
//        return new Pair<>(zd, currentFilePointer);
            ret.put(resolution_no, readBlock(bw, dis, hicUnitStr, resIdx, sumCounts, occupiedCellCount, stdDev, percent95,
                    binSize, blockIndex, blockBinCount, blockColumnCount, nBlocks));

        }
        bw.close();
        return ret;
    }
   
    numContact_X_Y_per_resolution_str readBlock(BufferedWriter bw, DataInputStream dis, String hicUnitStr,
            int resIdx, float sumCounts, float occupiedCellCount, float stdDev, float percent95, int binSize,
            BlockIndex blockIndex, int blockBinCount, int blockColumnCount, int nBlocks) throws IOException {
//        Map<Integer, List<ContactRecord>> ret = new TreeMap<>();
//        System.out.println("blockIndex.blockIndex.size()="+blockIndex.blockIndex.size());
//        bw.write("blockIndex.blockIndex.size()="+blockIndex.blockIndex.size() + "\n");
        numContact_X_Y_per_resolution_str ret = new numContact_X_Y_per_resolution_str();
        float maxCount_contact_X_Y = -Float.MAX_VALUE;
        float minCount_contact_X_Y = Float.MAX_VALUE;
        int num = 0;
        for (int block_no : blockIndex.blockIndex.keySet()) {
//            if(block_no==24){
//                int debug = 0;
//            }
//            System.out.println("block_no="+block_no);
//            bw.write("block_no="+block_no + "\n");
            IndexEntry idx = blockIndex.getBlock(block_no);
            if (idx != null) {
                num += idx.size;
//                bw.write("block_no="+block_no+ " idx:pos="+idx.position+" size="+idx.size+"\n");
//                System.out.println("block_no="+block_no+ " idx:(pos="+idx.position+" size="+idx.size+")");
                byte[] compressedBytes = seekAndFullyReadCompressedBytes(dis, idx);
                hic_fileposition += idx.size;
                byte[] buffer;
                 try {
                    buffer = decompress(compressedBytes);

                } catch (Exception e) {
                    throw new RuntimeException("Block read error: " + e.getMessage());
                }

                LittleEndianInputStream buffer_dis = new LittleEndianInputStream(new ByteArrayInputStream(buffer));
                int nRecords = buffer_dis.readInt();
//                System.out.println("nRecords="+nRecords);//Jiyuan
//                bw.write("nRecords="+nRecords+"\n");
//                float[][] records = new float[blockBinCount][blockBinCount];
                List<ContactRecord> records = new ArrayList<>(nRecords);
 
                if (version < 7) {
                    for (int i = 0; i < nRecords; i++) {
                        int binX = buffer_dis.readInt();
                        int binY = buffer_dis.readInt();
                        float counts = buffer_dis.readFloat();
                        if (counts > maxCount_contact_X_Y) {
                            maxCount_contact_X_Y = counts;
                        }
                        if (counts < minCount_contact_X_Y) {
                            minCount_contact_X_Y = counts;
                        }
//                        records[binX][binY] = counts;
                        records.add(new ContactRecord(binX, binY, counts));
                    }
                } else {

                    int binXOffset = buffer_dis.readInt();
                    int binYOffset = buffer_dis.readInt();
//                    binXOffset = 0;//added by jiyuan An
//                    binYOffset = 0;//added by jiyuan An
                    byte useShortMark = buffer_dis.readByte();
                    boolean useShort = (useShortMark == 0);// buffer_dis.readByte() == 0;
                    boolean useShortBinX = true, useShortBinY = true;
                    if (version > 8) {
                        useShortBinX = buffer_dis.readByte() == 0;
                        useShortBinY = buffer_dis.readByte() == 0;
                    }

                    byte type = buffer_dis.readByte();

                    switch (type) {
                        case 1:
                            if (useShortBinX && useShortBinY) {
                                // List-of-rows representation
                                int rowCount = buffer_dis.readShort();
                                for (int i = 0; i < rowCount; i++) {
                                    int binY = binYOffset + buffer_dis.readShort();
                                    int colCount = buffer_dis.readShort();
                                    for (int j = 0; j < colCount; j++) {
                                        int binX = binXOffset + buffer_dis.readShort();
                                        float counts = useShort ? buffer_dis.readShort() : buffer_dis.readFloat();
                                        if(counts > maxCount_contact_X_Y){
                                            maxCount_contact_X_Y = counts;
                                        }
                                        if(counts < minCount_contact_X_Y){
                                            minCount_contact_X_Y = counts;
                                        }
//                                        records[binX][binY] = counts;
                                        records.add(new ContactRecord(binX, binY, counts));
                                    }
                                }
                            } else if (useShortBinX && !useShortBinY) {
                                // List-of-rows representation
                                int rowCount = buffer_dis.readInt();
                                for (int i = 0; i < rowCount; i++) {
                                    int binY = binYOffset + buffer_dis.readInt();
                                    int colCount = buffer_dis.readShort();
                                    for (int j = 0; j < colCount; j++) {
                                        int binX = binXOffset + buffer_dis.readShort();
                                        float counts = useShort ? buffer_dis.readShort() : buffer_dis.readFloat();
                                        if(counts > maxCount_contact_X_Y){
                                            maxCount_contact_X_Y = counts;
                                        }
                                        if(counts < minCount_contact_X_Y){
                                            minCount_contact_X_Y = counts;
                                        }
//                                        records[binX][binY] = counts;
                                        records.add(new ContactRecord(binX, binY, counts));
                                    }
                                }
                            } else if (!useShortBinX && useShortBinY) {
                                // List-of-rows representation
                                int rowCount = buffer_dis.readShort();
                                for (int i = 0; i < rowCount; i++) {
                                    int binY = binYOffset + buffer_dis.readShort();
                                    int colCount = buffer_dis.readInt();
                                    for (int j = 0; j < colCount; j++) {
                                        int binX = binXOffset + buffer_dis.readInt();
                                        float counts = useShort ? buffer_dis.readShort() : buffer_dis.readFloat();
                                        if(counts > maxCount_contact_X_Y){
                                            maxCount_contact_X_Y = counts;
                                        }
                                        if(counts < minCount_contact_X_Y){
                                            minCount_contact_X_Y = counts;
                                        }
//                                        records[binX][binY] = counts;
                                        records.add(new ContactRecord(binX, binY, counts));
                                    }
                                }
                            } else {
                                // List-of-rows representation
                                int rowCount = buffer_dis.readInt();
                                for (int i = 0; i < rowCount; i++) {
                                    int binY = binYOffset + buffer_dis.readInt();
                                    int colCount = buffer_dis.readInt();
                                    for (int j = 0; j < colCount; j++) {
                                        int binX = binXOffset + buffer_dis.readInt();
                                        float counts = useShort ? buffer_dis.readShort() : buffer_dis.readFloat();
                                        if(counts > maxCount_contact_X_Y){
                                            maxCount_contact_X_Y = counts;
                                        }
                                        if(counts < minCount_contact_X_Y){
                                            minCount_contact_X_Y = counts;
                                        }
//                                        records[binX][binY] = counts;
                                        records.add(new ContactRecord(binX, binY, counts));
                                    }
                                }
                            }
                            break;
                        case 2:
        
                            int nPts = buffer_dis.readInt();
                            int w = buffer_dis.readShort();
        
                            for (int i = 0; i < nPts; i++) {
                                //int idx = (p.y - binOffset2) * w + (p.x - binOffset1);
                                int row = i / w;
                                int col = i - row * w;
                                int bin1 = binXOffset + col;
                                int bin2 = binYOffset + row;

                                if (useShort) {
                                    short counts = buffer_dis.readShort();
                                    if (counts != Short.MIN_VALUE) {
//                                        records[bin1][bin2] = counts;
                                        records.add(new ContactRecord(bin1, bin2, counts));
                                        if (counts > maxCount_contact_X_Y) {
                                            maxCount_contact_X_Y = counts;
                                        }
                                        if (counts < minCount_contact_X_Y) {
                                            minCount_contact_X_Y = counts;
                                        }
                                    }
                                } else {
                                    float counts = buffer_dis.readFloat();
                                    if (!Float.isNaN(counts)) {
                                        if (counts > maxCount_contact_X_Y) {
                                            maxCount_contact_X_Y = counts;
                                        }
                                        if (counts < minCount_contact_X_Y) {
                                            minCount_contact_X_Y = counts;
                                        }
//                                        records[bin1][bin2] = counts;
                                        records.add(new ContactRecord(bin1, bin2, counts));
                                    }
                                }
                            }

                            break;
                        default:
                            throw new RuntimeException("Unknown block type: " + type);
                    }
                }
//                int r = 0;
//                for (ContactRecord cr : records) {//Jiyuan
//                    bw.write("cr.getBinX()=" + cr.getBinX() + " cr.getBinY()=" + cr.getBinY() + " cr.getCounts()=" + cr.getCounts() + "\n");
//                    if (r++ < 3) {
//                        System.out.println("cr.getBinX()=" + cr.getBinX() + " cr.getBinY()=" + cr.getBinY() + " cr.getCounts()=" + cr.getCounts());
//                    }
//                }  
//                Block b = new Block(block_no, records, "dummy");
                ret.contactRec_per_block.put(block_no, records);
            }
        }
        bw.write(num+"\n");
        ret.hicUnitStr = hicUnitStr;
        ret.resIdx = resIdx;
        ret.sumCounts = sumCounts;
        ret.occupiedCellCount = occupiedCellCount;
        ret.stdDev = stdDev;
        ret.percent95 = percent95;
        ret.binSize = binSize;
        ret.maxCount_contact_X_Y = maxCount_contact_X_Y;
        ret.minCount_contact_X_Y = minCount_contact_X_Y;
        ret.blockBinCount = blockBinCount;
        ret.blockColumnCount = blockColumnCount;
        ret.nBlocks = nBlocks;
        return ret;
    }
    

    private byte[] decompress(byte[] compressedBytes) {
        return compressionUtils.decompress(compressedBytes);
    }
    
}
