/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.qut.citrus.include;

import htsjdk.tribble.util.LittleEndianOutputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
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
public class Write_HIC {

    String HIC_review_fn;
    Hic_header HH;
    Map<String, Map<Integer, numContact_X_Y_per_resolution_str>> Hic_data = new TreeMap<>();
    LittleEndianOutputStream los;
    public static final int bufferSize = 2097152;
    long masterIndexPositionPosition;
    long normVectorIndexPosition;
    long normVectorLengthPosition;
    int countThreshold = 0;
    protected final Deflater compressor;
     
    private final CompressionUtils compressionUtils = new CompressionUtils();
     

    public Write_HIC(String HIC_review_fn, Hic_header HH, Map<String, Map<Integer, numContact_X_Y_per_resolution_str>> Hic_data) {
        this.HIC_review_fn = HIC_review_fn;
        this.HH = HH;
        this.Hic_data = Hic_data;
        compressor = getDefaultCompressor();
        try {
            los = new LittleEndianOutputStream(new BufferedOutputStream(new FileOutputStream(HIC_review_fn), this.bufferSize));
        } catch (Exception e) {
            System.err.println("Unable to write to " + HIC_review_fn);
            System.exit(70);
        }
    }
    
    public void preprocess() throws IOException {
        writeHeader();
        List<IndexEntry> metaChrom = writeMatrix();
        long masterIndexPosition =writeFooter(metaChrom);
        los.close();
        
        
        
        updateMasterIndex(masterIndexPosition);
    }

//    void test() {
//        
//        byte[] compressedBytes = new byte[idx.size];
//        dis.read(compressedBytes);
//        byte[] compressedBytes = seekAndFullyReadCompressedBytes(dis, idx);
//        hic_fileposition += idx.size;
//        byte[] buffer;
//        try {
//            buffer = decompress(compressedBytes);
//
//        } catch (Exception e) {
//            throw new RuntimeException("Block read error: " + e.getMessage());
//        }
//    }
//
//    private byte[] decompress(byte[] compressedBytes) {
//        return compressionUtils.decompress(compressedBytes);
//    }
//    

    protected void updateMasterIndex(long masterIndexPosition) throws IOException {
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(HIC_review_fn, "rw");

            // Master index
            raf.getChannel().position(masterIndexPositionPosition);
            BufferedByteWriter buffer = new BufferedByteWriter();
            buffer.putLong(masterIndexPosition);
            raf.write(buffer.getBytes());
            System.out.println("masterIndexPosition: " + masterIndexPosition);

        } finally {
            if (raf != null) raf.close();
        }
    }

    
    
    private Deflater getDefaultCompressor() {
        Deflater compressor = new Deflater();
        compressor.setLevel(Deflater.DEFAULT_COMPRESSION);
        return compressor;
    }    
    
    protected void writeHeader() throws IOException {
        // Magic number
        byte[] magicBytes = "HIC".getBytes();
        los.write(magicBytes[0]);
        los.write(magicBytes[1]);
        los.write(magicBytes[2]);
        los.write(0);

        // VERSION
        los.writeInt(HH.version);
    
        // Placeholder for master index position, replaced with actual position after all contents are written
        masterIndexPositionPosition = los.getWrittenCount();
        los.writeLong(0L);
    
    
        // Genome ID
        los.writeString(HH.genomeId);
    
        if (HH.version > 8) {
            // Add NVI info
            //los.writeString(NVI_INDEX);
            normVectorIndexPosition = los.getWrittenCount();
            los.writeLong(0L);

            //los.writeString(NVI_LENGTH);
            normVectorLengthPosition = los.getWrittenCount();
            los.writeLong(0L);
        }
        los.writeInt(HH.attributes.size());
    
        // Sequence dictionary
        int nChrs = HH.chromosomes.size();
        los.writeInt(nChrs);
        for (Chromosome chromosome : HH.chromosomes) {
            los.writeString(chromosome.getName());
            if(HH.version > 8){
                los.writeLong(chromosome.getLength());
            }else{
                los.writeInt((int)chromosome.getLength());
            }
        }

        //BP resolution levels
        int nBpRes = HH.bpBinSizes.length;
        los.writeInt(nBpRes);
        for (int bpBinSize : HH.bpBinSizes) {
            los.writeInt(bpBinSize);
        }

        //fragment resolutions
        int nFragRes = HH.resFrag.length;// fragmentCalculation == null ? 0 : fragBinSizes.length;
        los.writeInt(nFragRes);
        for (int i = 0; i < nFragRes; i++) {
            los.writeInt(HH.resFrag[i]);
        }

        int numResolutions = nBpRes + nFragRes;

        // fragment sites
        if (nFragRes > 0) {
            System.err.println("nFragRes should be zero");
//            for (Chromosome chromosome : chromosomeHandler.getChromosomeArray()) {
//                int[] sites = fragmentCalculation.getSites(chromosome.getName());
//                int nSites = sites == null ? 0 : sites.length;
//                los.writeInt(nSites);
//                for (int i = 0; i < nSites; i++) {
//                    los.writeInt(sites[i]);
//                }
//            }
        }
    }
    
  
    protected long writeFooter(List<IndexEntry> metaChrom) throws IOException {

        long masterIndexPosition = los.getWrittenCount();
        List<BufferedByteWriter> bufferList = new ArrayList<>();
        bufferList.add(new BufferedByteWriter());
        bufferList.get(bufferList.size()-1).putInt(metaChrom.size());
        for (IndexEntry ie : metaChrom) {
            if (Integer.MAX_VALUE - bufferList.get(bufferList.size()-1).bytesWritten() < 1000) {
                bufferList.add(new BufferedByteWriter());
            }
            bufferList.get(bufferList.size()-1).putNullTerminatedString(ie.id+"_"+ie.id);
            bufferList.get(bufferList.size()-1).putLong(ie.position);
            bufferList.get(bufferList.size()-1).putInt(ie.size);
        }
        bufferList.get(bufferList.size()-1).putInt(0);//nExpectedValueVectors
        bufferList.get(bufferList.size()-1).putInt(0);//nNormExpectedValuevectors
        
        long nBytesV5 = 0;
        for (int i = 0; i<bufferList.size(); i++) {
            nBytesV5 += bufferList.get(i).getBytes().length;
        }
        System.out.println("nBytesV5: " + nBytesV5);

        los.writeInt((int)nBytesV5);
        for (int i = 0; i<bufferList.size(); i++) {
            los.write(bufferList.get(i).getBytes());
        }
        return masterIndexPosition;
        
//        long masterIndexPosition = los.getWrittenCount();
//        BufferedByteWriter buffer = new BufferedByteWriter(4 + 16 * metaChrom.size());
//        long position = los.getWrittenCount();
//        los.writeInt(0);
//        los.writeInt(metaChrom.size());
//        for (IndexEntry ie : metaChrom) {
//            los.writeString(ie.id+"_"+ie.id);
//            los.writeLong(ie.position);
//            los.writeInt(ie.size);
//        }
//        return masterIndexPosition;
//        
//        
//
//        los.write(buffer.getBytes());
//        // Index
//        List<BufferedByteWriter> bufferList = new ArrayList<>();
//        bufferList.add(new BufferedByteWriter());
// 
//        
//        bufferList.get(bufferList.size()-1).putInt(HH.masterIndex.size());
//        for (Map.Entry<String, IndexEntry> entry : HH.masterIndex.entrySet()) {
//            if (Integer.MAX_VALUE - bufferList.get(bufferList.size()-1).bytesWritten() < 1000) {
//                bufferList.add(new BufferedByteWriter());
//            }
//            bufferList.get(bufferList.size()-1).putNullTerminatedString(entry.getKey());
//            bufferList.get(bufferList.size()-1).putLong(entry.getValue().position);
//            bufferList.get(bufferList.size()-1).putInt(entry.getValue().size);
//        }
//        long nBytesV5 = 0;
//        for (int i = 0; i<bufferList.size(); i++) {
//            nBytesV5 += bufferList.get(i).getBytes().length;
//        }
//        System.out.println("nBytesV5: " + nBytesV5);
//
//        los.writeLong(nBytesV5);
//        for (int i = 0; i<bufferList.size(); i++) {
//            los.write(bufferList.get(i).getBytes());
//        }
    }
  
    

    protected List<IndexEntry> writeMatrix() throws IOException {
        List<IndexEntry> metaChrom = new ArrayList<>();
        int chrom_no = 0;
        for (String chrom : Hic_data.keySet()) {
            Map<Integer, numContact_X_Y_per_resolution_str> Hic_chrom = Hic_data.get(chrom);
            long position = los.getWrittenCount();
            los.writeInt(HH.chromosomes.get(0).getIndex());
            los.writeInt(HH.chromosomes.get(0).getIndex());
            los.writeInt(Hic_chrom.size());
            for(int resolu : Hic_chrom.keySet()){
                numContact_X_Y_per_resolution_str numContact_X_Y_per_resolution = Hic_chrom.get(resolu);
                writeZoomHeader(numContact_X_Y_per_resolution);
            }
            metaChrom.add(new IndexEntry(chrom_no++, position, (int)(los.getWrittenCount() - position)));
            System.out.println("meta"+metaChrom.get(metaChrom.size() -1));
            for(int solu : Hic_chrom.keySet()){
                Map<Long, List<IndexEntry>> localBlockIndexs = new TreeMap<>();
                List<IndexEntry> indexEntries = new ArrayList<>();
                for (int block_no : Hic_chrom.get(solu).contactRec_per_block.keySet()) {
                    long local_position = los.getWrittenCount();
                    writeBlock(Hic_chrom.get(solu).contactRec_per_block.get(block_no));
                    los.flush();
                    long size = los.getWrittenCount() - local_position;
                    indexEntries.add(new IndexEntry(block_no, local_position, (int) size));
                    System.out.println(indexEntries.get(indexEntries.size() - 1));
                }
                localBlockIndexs.put(Hic_chrom.get(solu).blockIndexPosition, indexEntries);
                updateIndexPositions(Hic_chrom.get(solu).blockIndexPosition, indexEntries);
            }//        los.flush();
        }
        return metaChrom;
    }
    protected void updateIndexPositions(long blockIndexPosition, List<IndexEntry> indexEntries) throws IOException{
        long losPos = los.getWrittenCount();
        los.close();
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(HIC_review_fn, "rw");
            // Write as little endian
            BufferedByteWriter buffer = new BufferedByteWriter();
            raf.getChannel().position(blockIndexPosition);
            for (IndexEntry entry : indexEntries) {
                buffer.putInt(entry.id);
                buffer.putLong(entry.position);
                buffer.putInt(entry.size);
            }
            raf.write(buffer.getBytes());
        } finally {
            if (raf != null) {
                raf.close();
            }
        }
        FileOutputStream fos = new FileOutputStream(HIC_review_fn, true);
        fos.getChannel().position(losPos);
        los = new LittleEndianOutputStream(new BufferedOutputStream(fos, bufferSize));
        los.setWrittenCount(losPos);
    }
    
//    protected void updateIndexPositions(List<IndexEntry> blockIndex, LittleEndianOutputStream[] losArray, boolean doRestore,
//            File outputFile, long currentPosition, long blockIndexPosition) throws IOException {
//
//        // Temporarily close output stream.  Remember position
//        long losPos = 0;
//        if (doRestore) {
//            losPos = losArray[0].getWrittenCount();
//            losArray[0].close();
//        }
//
//        RandomAccessFile raf = null;
//        try {
//            raf = new RandomAccessFile(outputFile, "rw");
//
//            // Block indices
//            long pos = blockIndexPosition;
//            raf.getChannel().position(pos);
//
//            // Write as little endian
//            BufferedByteWriter buffer = new BufferedByteWriter();
//            for (IndexEntry aBlockIndex : blockIndex) {
//                buffer.putInt(aBlockIndex.id);
//                buffer.putLong(aBlockIndex.position + currentPosition);
//                buffer.putInt(aBlockIndex.size);
//            }
//            raf.write(buffer.getBytes());
//
//        } finally {
//            if (raf != null) raf.close();
//        }
//        if (doRestore) {
//            FileOutputStream fos = new FileOutputStream(outputFile, true);
//            fos.getChannel().position(losPos);
//            losArray[0] = new LittleEndianOutputStream(new BufferedOutputStream(fos, bufferSize));
//            losArray[0].setWrittenCount(losPos);
//        }
//    }

    
    protected void writeBlock(List<ContactRecord> contactRec_in_one_block) throws IOException {

        int nRecords = contactRec_in_one_block.size();
        BufferedByteWriter buffer = new BufferedByteWriter(nRecords * 12);
        buffer.putInt(nRecords);
        // Find extents of occupied cells
        int binXOffset = Integer.MAX_VALUE;
        int binYOffset = Integer.MAX_VALUE;
        int binXMax = 0;
        int binYMax = 0;
        for(ContactRecord record : contactRec_in_one_block){
//        for (Map.Entry<Point, ContactCount> entry : records.entrySet()) {
//            Point point = entry.getKey();
            binXOffset = Math.min(binXOffset, record.binX);//point.x);
            binYOffset = Math.min(binYOffset, record.binY);//point.y);
            binXMax = Math.max(binXMax, record.binX);//point.x);
            binYMax = Math.max(binYMax, record.binY);//point.y);
        }

        buffer.putInt(binXOffset);
        buffer.putInt(binYOffset);
        final short w = (short) (binXMax - binXOffset + 1);
        final int w1 = binXMax - binXOffset + 1;
        final int w2 = binYMax - binYOffset + 1;

        boolean isInteger = true;
        float maxCounts = 0;

        LinkedHashMap<Integer, List<ContactRecord>> rows = new LinkedHashMap<>();
        for (ContactRecord record : contactRec_in_one_block){//Point point : keys) {
//            final ContactCount contactCount = records.get(point);
            float counts = record.counts;//contactCount.getCounts();
            if (counts >= countThreshold) {
                isInteger = isInteger && (Math.floor(counts) == counts);
                maxCounts = Math.max(counts, maxCounts);
                final int px = record.binX - binXOffset;
                final int py = record.binY - binYOffset;
                List<ContactRecord> row = rows.get(py);
                if (row == null) {
                    row = new ArrayList<>(10);
                    rows.put(py, row);
                }
                row.add(new ContactRecord(px, py, counts));
            }
        }

        // Compute size for each representation and choose smallest
        boolean useShort = isInteger && (maxCounts < Short.MAX_VALUE);
        boolean useShortBinX = w1 < Short.MAX_VALUE;
        boolean useShortBinY = w2 < Short.MAX_VALUE;
        int valueSize = useShort ? 2 : 4;

        int lorSize = 0;
//        int nDensePts = (lastPoint.y - binYOffset) * w + (lastPoint.x - binXOffset) + 1;
        int nDensePts = (contactRec_in_one_block.get(contactRec_in_one_block.size() - 1).binY - binYOffset) * w + 
                (contactRec_in_one_block.get(contactRec_in_one_block.size() - 1).binX - binXOffset) + 1;

        int denseSize = nDensePts * valueSize;
        for (List<ContactRecord> row : rows.values()) {
            lorSize += 4 + row.size() * valueSize;
        }
        buffer.put((byte) (useShort ? 0 : 1));
        if (HH.version > 8) {
            buffer.put((byte) (useShortBinX ? 0 : 1));
            buffer.put((byte) (useShortBinY ? 0 : 1));
        }
        //dense calculation is incorrect for v9
        denseSize = Integer.MAX_VALUE;
        if (lorSize < denseSize) {
            buffer.put((byte) 1);  // List of rows representation
            if (useShortBinY) {
                buffer.putShort((short) rows.size()); // # of rows
            } else {
                buffer.putInt(rows.size());  // # of rows
            }
            for (Map.Entry<Integer, List<ContactRecord>> entry : rows.entrySet()) {
                int py = entry.getKey();
                List<ContactRecord> row = entry.getValue();
                if (useShortBinY) {
                    buffer.putShort((short) py);  // Row number
                } else {
                    buffer.putInt(py); // Row number
                }
                if (useShortBinX) {
                    buffer.putShort((short) row.size());  // size of row
                } else {
                    buffer.putInt(row.size()); // size of row
                }
                for (ContactRecord contactRecord : row) {
                    if (useShortBinX) {
                        buffer.putShort((short) (contactRecord.getBinX()));
                    } else {
                        buffer.putInt(contactRecord.getBinX());
                    }

                    final float counts = contactRecord.getCounts();
                    if (useShort) {
                        buffer.putShort((short) counts);
                    } else {
                        buffer.putFloat(counts);
                    }
                }
            }
        } else {
            buffer.put((byte) 2);  // Dense matrix
            buffer.putInt(nDensePts);
            buffer.putShort(w);
            int lastIdx = 0;
            for (ContactRecord record : contactRec_in_one_block) {
                int idx = (record.binY - binYOffset) * w + (record.binX - binXOffset);
                for (int i = lastIdx; i < idx; i++) {
                    // Filler value
                    if (useShort) {
                        buffer.putShort(Short.MIN_VALUE);
                    } else {
                        buffer.putFloat(Float.NaN);
                    }
                }
                float counts = record.counts;// records.get(p).getCounts();
                if (useShort) {
                    buffer.putShort((short) counts);
                } else {
                    buffer.putFloat(counts);
                }
                lastIdx = idx + 1;
//                synchronized(sampledData) {
//                    sampledData.add(counts);
//                }
//                synchronized(zd) {
//                    zd.sum += counts;
//                }
            }
        }


        byte[] bytes = buffer.getBytes();
        byte[] compressedBytes = compress(bytes, compressor);//los.getWrittenCount()
        los.write(compressedBytes);
    }

    protected byte[] compress(byte[] data, Deflater compressor) {

        // Give the compressor the data to compress
        compressor.reset();
        compressor.setInput(data);
        compressor.finish();

        // Create an expandable byte array to hold the compressed data.
        // You cannot use an array that's the same size as the orginal because
        // there is no guarantee that the compressed data will be smaller than
        // the uncompressed data.
        ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);

        // Compress the data
        byte[] buf = new byte[1024];
        while (!compressor.finished()) {
            int count = compressor.deflate(buf);
            bos.write(buf, 0, count);
        }
        try {
            bos.close();
        } catch (IOException e) {
            System.err.println("Error clossing ByteArrayOutputStream");
            e.printStackTrace();
        }

        return bos.toByteArray();
    }

    

    private void writeZoomHeader(numContact_X_Y_per_resolution_str numContact_X_Y_per_resolution) throws IOException {


        los.writeString(numContact_X_Y_per_resolution.hicUnitStr);  // Unit
        los.writeInt(numContact_X_Y_per_resolution.resIdx);     // zoom index,  lowest res is zero
        los.writeFloat(numContact_X_Y_per_resolution.sumCounts);      // sum
        los.writeFloat(numContact_X_Y_per_resolution.occupiedCellCount);
        los.writeFloat(numContact_X_Y_per_resolution.stdDev);
        los.writeFloat(numContact_X_Y_per_resolution.percent95);        
        los.writeInt(numContact_X_Y_per_resolution.binSize);
        
        los.writeInt(numContact_X_Y_per_resolution.blockBinCount);
        los.writeInt(numContact_X_Y_per_resolution.blockColumnCount);
        los.writeInt(numContact_X_Y_per_resolution.nBlocks);

        numContact_X_Y_per_resolution.blockIndexPosition = los.getWrittenCount();

        // Placeholder for block index
        for (int i = 0; i < numContact_X_Y_per_resolution.nBlocks; i++) {
            los.writeInt(0);
            los.writeLong(0L);
            los.writeInt(0);
        }

    }

//    protected void writeBody(String inputFile, Map<Integer, List<long[]>> mndIndex) throws IOException {
//
//        MatrixPP wholeGenomeMatrix = computeWholeGenomeMatrix(inputFile);
//        writeMatrix(wholeGenomeMatrix, losArray, compressor, matrixPositions, -1, false);
//
//        PairIterator iter = (inputFile.endsWith(".bin"))
//                ? new BinPairIterator(inputFile)
//                : new AsciiPairIterator(inputFile, chromosomeIndexes, chromosomeHandler);
//
//        Set<String> writtenMatrices = Collections.synchronizedSet(new HashSet<>());
//
//        int currentChr1 = -1;
//        int currentChr2 = -1;
//        MatrixPP currentMatrix = null;
//        String currentMatrixKey = null;
//
//        while (iter.hasNext()) {
//            AlignmentPair pair = iter.next();
//            // skip pairs that mapped to contigs
//            if (!pair.isContigPair()) {
//                if (shouldSkipContact(pair)) {
//                    continue;
//                }
//                // Flip pair if needed so chr1 < chr2
//                int chr1, chr2, bp1, bp2, frag1, frag2;
//                if (pair.getChr1() < pair.getChr2()) {
//                    bp1 = pair.getPos1();
//                    bp2 = pair.getPos2();
//                    frag1 = pair.getFrag1();
//                    frag2 = pair.getFrag2();
//                    chr1 = pair.getChr1();
//                    chr2 = pair.getChr2();
//                    if (bp1 < 0) {
//                        bp1 = 0;
//                    } else if (bp1 > chromosomeHandler.getChromosomeFromIndex(chr1).getLength()) {
//                        bp1 = (int) chromosomeHandler.getChromosomeFromIndex(chr1).getLength();
//                    }
//                    if (bp2 < 0) {
//                        bp2 = 0;
//                    } else if (bp2 > chromosomeHandler.getChromosomeFromIndex(chr2).getLength()) {
//                        bp2 = (int) chromosomeHandler.getChromosomeFromIndex(chr2).getLength();
//                    }
//                } else {
//                    bp1 = pair.getPos2();
//                    bp2 = pair.getPos1();
//                    frag1 = pair.getFrag2();
//                    frag2 = pair.getFrag1();
//                    chr1 = pair.getChr2();
//                    chr2 = pair.getChr1();
//                    if (bp1 < 0) {
//                        bp1 = 0;
//                    } else if (bp1 > chromosomeHandler.getChromosomeFromIndex(chr2).getLength()) {
//                        bp1 = (int) chromosomeHandler.getChromosomeFromIndex(chr2).getLength();
//                    }
//                    if (bp2 < 0) {
//                        bp2 = 0;
//                    } else if (bp2 > chromosomeHandler.getChromosomeFromIndex(chr1).getLength()) {
//                        bp2 = (int) chromosomeHandler.getChromosomeFromIndex(chr1).getLength();
//                    }
//                }
//
//                // Randomize position within fragment site
//                if (allowPositionsRandomization && fragmentCalculation != null) {
//                    Pair<Integer, Integer> newBPos12 = getRandomizedPositions(chr1, chr2, frag1, frag2, bp1, bp2);
//                    bp1 = newBPos12.getFirst();
//                    bp2 = newBPos12.getSecond();
//                }
//                // only increment if not intraFragment and passes the mapq threshold
//                if (!(currentChr1 == chr1 && currentChr2 == chr2)) {
//                    // Starting a new matrix
//                    if (currentMatrix != null) {
//                        currentMatrix.parsingComplete();
//                        writeMatrix(currentMatrix, losArray, compressor, matrixPositions, -1, false);
//                        writtenMatrices.add(currentMatrixKey);
//                        currentMatrix = null;
//                        System.gc();
//                        //System.out.println("Available memory: " + RuntimeUtils.getAvailableMemory());
//                    }
//
//                    // Start the next matrix
//                    currentChr1 = chr1;
//                    currentChr2 = chr2;
//                    currentMatrixKey = currentChr1 + "_" + currentChr2;
//
//                    if (writtenMatrices.contains(currentMatrixKey)) {
//                        System.err.println("Error: the chromosome combination " + currentMatrixKey + " appears in multiple blocks");
//                        if (outputFile != null) {
//                            outputFile.deleteOnExit();
//                        }
//                        System.exit(58);
//                    }
//                    currentMatrix = new MatrixPP(currentChr1, currentChr2, chromosomeHandler, bpBinSizes,
//                            fragmentCalculation, fragBinSizes, countThreshold, v9DepthBase, BLOCK_CAPACITY);
//                }
//                currentMatrix.incrementCount(bp1, bp2, frag1, frag2, pair.getScore(), expectedValueCalculations, tmpDir);
//
//            }
//        }
//
//        /*
//        if (fragmentCalculation != null && allowPositionsRandomization) {
//            System.out.println(String.format("Randomization errors encountered: %d no map found, " +
//                    "%d two different maps found", noMapFoundCount, mapDifferentCount));
//        }
//         */
//        if (currentMatrix != null) {
//            currentMatrix.parsingComplete();
//            writeMatrix(currentMatrix, losArray, compressor, matrixPositions, -1, false);
//        }
//
//        if (iter != null) {
//            iter.close();
//        }
//
//        masterIndexPosition = losArray[0].getWrittenCount();
//    }

}
