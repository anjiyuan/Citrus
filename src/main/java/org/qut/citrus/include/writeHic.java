/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.qut.citrus.include;

import htsjdk.tribble.util.LittleEndianOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.zip.Deflater;
import org.broad.igv.tdf.BufferedByteWriter;

/**
 *
 * @author an
 */
public class writeHic {
    protected static final int VERSION = 9;
    protected static final int BLOCK_SIZE = 1000;
    public static final String V9_DEPTH_BASE = "v9-depth-base";
    protected int v9DepthBase = 2;
    public static final String HIC_FILE_SCALING = "hicFileScalingFactor";
    public static final String STATISTICS = "statistics";
    public static final String GRAPHS = "graphs";
    public static final String SOFTWARE = "software";
    protected static final String NVI_INDEX = "nviIndex";
    protected static final String NVI_LENGTH = "nviLength";

//    protected final ChromosomeHandler chromosomeHandler;
    protected Map<String, Integer> chromosomeIndexes;
    protected final File outputFile;
    protected final Map<String, IndexEntry> matrixPositions;
    protected String genomeId;
    protected final Deflater compressor;
    protected LittleEndianOutputStream[] losArray = new LittleEndianOutputStream[1];
    protected long masterIndexPosition;
    protected int countThreshold = 0;
    protected int mapqThreshold = 0;
    protected boolean diagonalsOnly = false;
    protected String fragmentFileName = null;
    protected String statsFileName = null;
    protected String graphFileName = null;
    protected String expectedVectorFile = null;
    protected Set<String> randomizeFragMapFiles = null;
//    protected FragmentCalculation fragmentCalculation = null;
    protected Set<String> includedChromosomes;
//    protected ArrayList<FragmentCalculation> fragmentCalculationsForRandomization = null;
//    protected Alignment alignmentFilter;
    protected static final Random random = new Random(5);
    protected static boolean allowPositionsRandomization = false;
    protected static boolean throwOutIntraFrag = false;
    public static int BLOCK_CAPACITY = 1000;
    
    // Base-pair resolutions
    protected int[] bpBinSizes = {2500000, 1000000, 500000, 250000, 100000, 50000, 25000, 10000, 5000, 1000};
    
    // Fragment resolutions
    protected int[] fragBinSizes = {500, 200, 100, 50, 20, 5, 2, 1};

    // number of resolutions
    protected int numResolutions = bpBinSizes.length + fragBinSizes.length;

    // hic scaling factor value
    protected double hicFileScalingFactor = 1;
    
    protected Long normVectorIndex = 0L, normVectorLength = 0L;
    
    /**
     * The position of the field containing the masterIndex position
     */
    protected long masterIndexPositionPosition;
    protected long normVectorIndexPosition;
    protected long normVectorLengthPosition;
//    protected Map<String, ExpectedValueCalculation> expectedValueCalculations;
    protected File tmpDir;

    public writeHic(File outputFile){//, String genomeId, ChromosomeHandler chromosomeHandler, double hicFileScalingFactor) {
//        this.genomeId = genomeId;
        this.outputFile = outputFile;
        this.matrixPositions = new LinkedHashMap<>();

//        this.chromosomeHandler = chromosomeHandler;
//        chromosomeIndexes = new Hashtable<>();
//        for (int i = 0; i < chromosomeHandler.size(); i++) {
//            chromosomeIndexes.put(chromosomeHandler.getChromosomeFromIndex(i).getName(), i);
//        }

        compressor = null;//getDefaultCompressor();

//        this.tmpDir = null;  // TODO -- specify this
//
//        if (hicFileScalingFactor > 0) {
//            this.hicFileScalingFactor = hicFileScalingFactor;
//        }
    }
    
    
    public void preprocess(final String inputFile, final String headerFile, final String footerFile, Map<Integer, List<long[]>> mndIndex) throws IOException {
        File file = new File(inputFile);

        if (!file.exists() || file.length() == 0) {
            System.err.println(inputFile + " does not exist or does not contain any reads.");
            System.exit(57);
        }

//        try {
//            StringBuilder stats = null;
//            StringBuilder graphs = null;
//            StringBuilder hicFileScaling = new StringBuilder().append(hicFileScalingFactor);
//            if (fragmentFileName != null) {
//                try {
//                    fragmentCalculation = FragmentCalculation.readFragments(fragmentFileName, chromosomeHandler);
//                } catch (Exception e) {
//                    System.err.println("Warning: Unable to process fragment file. Pre will continue without fragment file.");
//                    fragmentCalculation = null;
//                }
//            } else {
//                System.out.println("Not including fragment map");
//            }
//
//            if (allowPositionsRandomization) {
//                if (randomizeFragMapFiles != null) {
//                    fragmentCalculationsForRandomization = new ArrayList<>();
//                    for (String fragmentFileName : randomizeFragMapFiles) {
//                        try {
//                            FragmentCalculation fragmentCalculation = FragmentCalculation.readFragments(fragmentFileName, chromosomeHandler);
//                            fragmentCalculationsForRandomization.add(fragmentCalculation);
//                            System.out.println(String.format("added %s", fragmentFileName));
//                        } catch (Exception e) {
//                            System.err.println(String.format("Warning: Unable to process fragment file %s. Randomization will continue without fragment file %s.", fragmentFileName, fragmentFileName));
//                        }
//                    }
//                } else {
//                    System.out.println("Using default fragment map for randomization");
//                }
//
//            } else if (randomizeFragMapFiles != null) {
//                System.err.println("Position randomizer seed not set, disregarding map options");
//            }
//
//            if (statsFileName != null) {
//                FileInputStream is = null;
//                try {
//                    is = new FileInputStream(statsFileName);
//                    BufferedReader reader = new BufferedReader(new InputStreamReader(is), HiCGlobals.bufferSize);
//                    stats = new StringBuilder();
//                    String nextLine;
//                    while ((nextLine = reader.readLine()) != null) {
//                        stats.append(nextLine).append("\n");
//                    }
//                } catch (IOException e) {
//                    System.err.println("Error while reading stats file: " + e);
//                    stats = null;
//                } finally {
//                    if (is != null) {
//                        is.close();
//                    }
//                }
//
//            }
//            if (graphFileName != null) {
//                FileInputStream is = null;
//                try {
//                    is = new FileInputStream(graphFileName);
//                    BufferedReader reader = new BufferedReader(new InputStreamReader(is), HiCGlobals.bufferSize);
//                    graphs = new StringBuilder();
//                    String nextLine;
//                    while ((nextLine = reader.readLine()) != null) {
//                        graphs.append(nextLine).append("\n");
//                    }
//                } catch (IOException e) {
//                    System.err.println("Error while reading graphs file: " + e);
//                    graphs = null;
//                } finally {
//                    if (is != null) {
//                        is.close();
//                    }
//                }
//            }
//
//            if (expectedVectorFile == null) {
//                expectedValueCalculations = Collections.synchronizedMap(new LinkedHashMap<>());
//                for (int bBinSize : bpBinSizes) {
//                    ExpectedValueCalculation calc = new ExpectedValueCalculation(chromosomeHandler, bBinSize, null, NormalizationHandler.NONE);
//                    String key = "BP_" + bBinSize;
//                    expectedValueCalculations.put(key, calc);
//                }
//            }
//            if (fragmentCalculation != null) {
//
//                // Create map of chr name -> # of fragments
//                Map<String, int[]> sitesMap = fragmentCalculation.getSitesMap();
//                Map<String, Integer> fragmentCountMap = new HashMap<>();
//                for (Map.Entry<String, int[]> entry : sitesMap.entrySet()) {
//                    int fragCount = entry.getValue().length + 1;
//                    String chr = entry.getKey();
//                    fragmentCountMap.put(chr, fragCount);
//                }
//
//                if (expectedVectorFile == null) {
//                    for (int fBinSize : fragBinSizes) {
//                        ExpectedValueCalculation calc = new ExpectedValueCalculation(chromosomeHandler, fBinSize, fragmentCountMap, NormalizationHandler.NONE);
//                        String key = "FRAG_" + fBinSize;
//                        expectedValueCalculations.put(key, calc);
//                    }
//                }
//            }
//
//            LittleEndianOutputStream[] losFooter = new LittleEndianOutputStream[1];
//            try {
//                losArray[0] = new LittleEndianOutputStream(new BufferedOutputStream(new FileOutputStream(headerFile), HiCGlobals.bufferSize));
//                if (footerFile.equalsIgnoreCase(headerFile)) {
//                    losFooter = losArray;
//                } else {
//                    losFooter[0] = new LittleEndianOutputStream(new BufferedOutputStream(new FileOutputStream(footerFile), HiCGlobals.bufferSize));
//                }
//            } catch (Exception e) {
//                System.err.println("Unable to write to " + outputFile);
//                System.exit(70);
//            }
//
//            System.out.println("Start preprocess");
//
//            System.out.println("Writing header");
//
//            writeHeader(stats, graphs, hicFileScaling);
//
//            System.out.println("Writing body");
//            writeBody(inputFile, mndIndex);
//
//            System.out.println();
//            System.out.println("Writing footer");
//            writeFooter(losFooter);
//
//            if (losFooter != null && losFooter[0] != null) {
//                losFooter[0].close();
//            }
//
//        } finally {
//            if (losArray != null && losArray[0] != null) {
//                losArray[0].close();
//            }
//        }

        updateMasterIndex(headerFile);
        System.out.println("\nFinished preprocess");
    }


    protected void updateMasterIndex(String headerFile) throws IOException {
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(headerFile, "rw");

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
    

//    protected void writeHeader(StringBuilder stats, StringBuilder graphs, StringBuilder hicFileScaling) throws IOException {
//        // Magic number
//        byte[] magicBytes = "HIC".getBytes();
//        LittleEndianOutputStream los = losArray[0];
//        los.write(magicBytes[0]);
//        los.write(magicBytes[1]);
//        los.write(magicBytes[2]);
//        los.write(0);
//
//        // VERSION
//        los.writeInt(VERSION);
//    
//        // Placeholder for master index position, replaced with actual position after all contents are written
//        masterIndexPositionPosition = los.getWrittenCount();
//        los.writeLong(0L);
//    
//    
//        // Genome ID
//        los.writeString(genomeId);
//    
//        // Add NVI info
//        //los.writeString(NVI_INDEX);
//        normVectorIndexPosition = los.getWrittenCount();
//        los.writeLong(0L);
//    
//        //los.writeString(NVI_LENGTH);
//        normVectorLengthPosition = los.getWrittenCount();
//        los.writeLong(0L);
//    
//    
//        // Attribute dictionary
//        int nAttributes = 1;
//        if (stats != null) nAttributes += 1;
//        if (graphs != null) nAttributes += 1;
//        if (hicFileScaling != null) nAttributes += 1;
//        if (v9DepthBase != 2) nAttributes += 1;
//    
//        los.writeInt(nAttributes);
//        los.writeString(SOFTWARE);
//        los.writeString("Juicer Tools Version " + HiCGlobals.versionNum);
//        if (stats != null) {
//            los.writeString(STATISTICS);
//            los.writeString(stats.toString());
//        }
//        if (graphs != null) {
//            los.writeString(GRAPHS);
//            los.writeString(graphs.toString());
//        }
//        if (hicFileScaling != null) {
//            los.writeString(HIC_FILE_SCALING);
//            los.writeString(hicFileScaling.toString());
//        }
//        if (v9DepthBase != 2) {
//            los.writeString(V9_DEPTH_BASE);
//            los.writeString("" + v9DepthBase);
//        }
//
//
//        // Sequence dictionary
//        int nChrs = chromosomeHandler.size();
//        los.writeInt(nChrs);
//        for (Chromosome chromosome : chromosomeHandler.getChromosomeArray()) {
//            los.writeString(chromosome.getName());
//            los.writeLong(chromosome.getLength());
//        }
//
//        //BP resolution levels
//        int nBpRes = bpBinSizes.length;
//        los.writeInt(nBpRes);
//        for (int bpBinSize : bpBinSizes) {
//            los.writeInt(bpBinSize);
//        }
//
//        //fragment resolutions
//        int nFragRes = fragmentCalculation == null ? 0 : fragBinSizes.length;
//        los.writeInt(nFragRes);
//        for (int i = 0; i < nFragRes; i++) {
//            los.writeInt(fragBinSizes[i]);
//        }
//
//        numResolutions = nBpRes + nFragRes;
//
//        // fragment sites
//        if (nFragRes > 0) {
//            for (Chromosome chromosome : chromosomeHandler.getChromosomeArray()) {
//                int[] sites = fragmentCalculation.getSites(chromosome.getName());
//                int nSites = sites == null ? 0 : sites.length;
//                los.writeInt(nSites);
//                for (int i = 0; i < nSites; i++) {
//                    los.writeInt(sites[i]);
//                }
//            }
//        }
//    }
//
//
//    protected void writeFooter(LittleEndianOutputStream[] los) throws IOException {
//
//        // Index
//        List<BufferedByteWriter> bufferList = new ArrayList<>();
//        bufferList.add(new BufferedByteWriter());
//        bufferList.get(bufferList.size()-1).putInt(matrixPositions.size());
//        for (Map.Entry<String, IndexEntry> entry : matrixPositions.entrySet()) {
//            if (Integer.MAX_VALUE - bufferList.get(bufferList.size()-1).bytesWritten() < 1000) {
//                bufferList.add(new BufferedByteWriter());
//            }
//            bufferList.get(bufferList.size()-1).putNullTerminatedString(entry.getKey());
//            bufferList.get(bufferList.size()-1).putLong(entry.getValue().position);
//            bufferList.get(bufferList.size()-1).putInt(entry.getValue().size);
//        }
//
//        // Vectors  (Expected values,  other).
//        /***  NEVA ***/
//        if (expectedVectorFile == null) {
//            if (Integer.MAX_VALUE - bufferList.get(bufferList.size()-1).bytesWritten() < 1000) {
//                bufferList.add(new BufferedByteWriter());
//            }
//            bufferList.get(bufferList.size()-1).putInt(expectedValueCalculations.size());
//            for (Map.Entry<String, ExpectedValueCalculation> entry : expectedValueCalculations.entrySet()) {
//                ExpectedValueCalculation ev = entry.getValue();
//    
//                ev.computeDensity();
//    
//                int binSize = ev.getGridSize();
//                HiC.Unit unit = ev.isFrag ? HiC.Unit.FRAG : HiC.Unit.BP;
//
//                bufferList.get(bufferList.size()-1).putNullTerminatedString(unit.toString());
//                bufferList.get(bufferList.size()-1).putInt(binSize);
//    
//                // The density values
//                ListOfDoubleArrays expectedValues = ev.getDensityAvg();
//                // todo @Suhas to handle buffer overflow
//                bufferList.get(bufferList.size()-1).putLong(expectedValues.getLength());
//                for (double[] expectedArray : expectedValues.getValues()) {
//                    bufferList.add(new BufferedByteWriter());
//                    for (double value : expectedArray) {
//                        if (Integer.MAX_VALUE - bufferList.get(bufferList.size()-1).bytesWritten() < 1000000) {
//                            bufferList.add(new BufferedByteWriter());
//                        }
//                        bufferList.get(bufferList.size()-1).putFloat( (float) value);
//                    }
//                }
//    
//                // Map of chromosome index -> normalization factor
//                Map<Integer, Double> normalizationFactors = ev.getChrScaleFactors();
//                if (Integer.MAX_VALUE - bufferList.get(bufferList.size()-1).bytesWritten() < 1000000) {
//                    bufferList.add(new BufferedByteWriter());
//                }
//                bufferList.get(bufferList.size()-1).putInt(normalizationFactors.size());
//                for (Map.Entry<Integer, Double> normFactor : normalizationFactors.entrySet()) {
//                    bufferList.get(bufferList.size()-1).putInt(normFactor.getKey());
//                    bufferList.get(bufferList.size()-1).putFloat(normFactor.getValue().floatValue());
//                    //System.out.println(normFactor.getKey() + "  " + normFactor.getValue());
//                }
//            }
//        }
//        else {
//            // read in expected vector file. to get # of resolutions, might have to read twice.
//
//            int count=0;
//            try (Reader reader = new FileReader(expectedVectorFile);
//                 BufferedReader bufferedReader = new BufferedReader(reader)) {
//
//                String line;
//                while ((line = bufferedReader.readLine()) != null) {
//                    if (line.startsWith("fixedStep"))
//                        count++;
//                    if (line.startsWith("variableStep")) {
//                        System.err.println("Expected vector file must be in wiggle fixedStep format");
//                        System.exit(19);
//                    }
//                }
//            }
//            bufferList.get(bufferList.size()-1).putInt(count);
//            try (Reader reader = new FileReader(expectedVectorFile);
//                 BufferedReader bufferedReader = new BufferedReader(reader)) {
//
//                String line;
//                while ((line = bufferedReader.readLine()) != null) {
//                    if (line.startsWith("fixedStep")) {
//                        String[] words = line.split("\\s+");
//                        for (String str:words){
//                            if (str.contains("chrom")){
//                                String[] chrs = str.split("=");
//
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        long nBytesV5 = 0;
//        for (int i = 0; i<bufferList.size(); i++) {
//            nBytesV5 += bufferList.get(i).getBytes().length;
//        }
//        System.out.println("nBytesV5: " + nBytesV5);
//
//        los[0].writeLong(nBytesV5);
//        for (int i = 0; i<bufferList.size(); i++) {
//            los[0].write(bufferList.get(i).getBytes());
//        }
//    }
//
//
//    protected Deflater getDefaultCompressor() {
//        Deflater compressor = new Deflater();
//        compressor.setLevel(Deflater.DEFAULT_COMPRESSION);
//        return compressor;
//    }    
//    
//    void writeBlock(DataOutputStream dis, numContact_X_Y_per_resolution_str Hic_dat, LittleEndianOutputStream los, Deflater compressor) throws IOException {
//        for (int block_no : Hic_dat.contactRec_per_block.keySet()) {
//            int nRecords = Hic_dat.contactRec_per_block.get(block_no).size();
//            BufferedByteWriter buffer = new BufferedByteWriter(nRecords * 12);
//            buffer.putInt(nRecords);
//            // Find extents of occupied cells
//            int binXOffset = Integer.MAX_VALUE;
//            int binYOffset = Integer.MAX_VALUE;
//            int binXMax = 0;
//            int binYMax = 0;
//            float maxCounts = 0;
//            ContactRecord lastRecord = Hic_dat.contactRec_per_block.get(block_no).get(
//                    Hic_dat.contactRec_per_block.get(block_no).size() - 1);
//            LinkedHashMap<Integer, List<ContactRecord>> rows = new LinkedHashMap<>();
//            for (ContactRecord record : Hic_dat.contactRec_per_block.get(block_no)) {
//                binXOffset = Math.min(binXOffset, record.binX);
//                binYOffset = Math.min(binYOffset, record.binY);
//                binXMax = Math.max(binXMax, record.binX);
//                binYMax = Math.max(binYMax, record.binY);
//                if (maxCounts < record.counts) {
//                    maxCounts = record.counts;
//                }
//                List<ContactRecord> row = rows.get(record.binY);
//                if (row == null) {
//                    row = new ArrayList<>(10);
//                    rows.put(record.binY, row);
//                }
//                row.add(new ContactRecord(record.binX, record.binY, record.counts));
//            }
//            buffer.putInt(binXOffset);
//            buffer.putInt(binYOffset);
//            final short w = (short) (binXMax - binXOffset + 1);
//            final int w1 = binXMax - binXOffset + 1;
//            final int w2 = binYMax - binYOffset + 1;
//            boolean isInteger = false;
//
//            // Compute size for each representation and choose smallest
//            boolean useShort = isInteger && (maxCounts < Short.MAX_VALUE);
//            boolean useShortBinX = w1 < Short.MAX_VALUE;
//            boolean useShortBinY = w2 < Short.MAX_VALUE;
//            int valueSize = useShort ? 2 : 4;
//
//            int lorSize = 0;
//            int nDensePts = (lastRecord.binY - binYOffset) * w + (lastRecord.binX - binXOffset) + 1;
//
//            int denseSize = nDensePts * valueSize;
//            for (List<ContactRecord> row : rows.values()) {
//                lorSize += 4 + row.size() * valueSize;
//            }
//
//            buffer.put((byte) (useShort ? 0 : 1));
//            buffer.put((byte) (useShortBinX ? 0 : 1));
//            buffer.put((byte) (useShortBinY ? 0 : 1));
//
//            //dense calculation is incorrect for v9
//            denseSize = Integer.MAX_VALUE;
//            denseSize = Integer.MAX_VALUE;
//            if (lorSize < denseSize) {
//                buffer.put((byte) 1);  // List of rows representation
//                if (useShortBinY) {
//                    buffer.putShort((short) rows.size()); // # of rows
//                } else {
//                    buffer.putInt(rows.size());  // # of rows
//                }
//                for (Map.Entry<Integer, List<ContactRecord>> entry : rows.entrySet()) {
//                    int py = entry.getKey();
//                    List<ContactRecord> row = entry.getValue();
//                    if (useShortBinY) {
//                        buffer.putShort((short) py);  // Row number
//                    } else {
//                        buffer.putInt(py); // Row number
//                    }
//                    if (useShortBinX) {
//                        buffer.putShort((short) row.size());  // size of row
//                    } else {
//                        buffer.putInt(row.size()); // size of row
//                    }
//                    for (ContactRecord contactRecord : row) {
//                        if (useShortBinX) {
//                            buffer.putShort((short) (contactRecord.getBinX()));
//                        } else {
//                            buffer.putInt(contactRecord.getBinX());
//                        }
//                        final float counts = contactRecord.getCounts();
//                        if (useShort) {
//                            buffer.putShort((short) counts);
//                        } else {
//                            buffer.putFloat(counts);
//                        }
////                    synchronized(sampledData) {
////                        sampledData.add(counts);
////                    }
////                    synchronized(zd) {
////                        zd.sum += counts;
////                    }
//                    }
//                }
//            } else {
////                buffer.put((byte) 2);  // Dense matrix
////                buffer.putInt(nDensePts);
////                buffer.putShort(w);
////                int lastIdx = 0;
////                for (Point p : keys) {
////                    int idx = (p.y - binYOffset) * w + (p.x - binXOffset);
////                    for (int i = lastIdx; i < idx; i++) {
////                        // Filler value
////                        if (useShort) {
////                            buffer.putShort(Short.MIN_VALUE);
////                        } else {
////                            buffer.putFloat(Float.NaN);
////                        }
////                    }
////                    float counts = records.get(p).getCounts();
////                    if (useShort) {
////                        buffer.putShort((short) counts);
////                    } else {
////                        buffer.putFloat(counts);
////                    }
////                    lastIdx = idx + 1;
////                    synchronized (sampledData) {
////                        sampledData.add(counts);
////                    }
////                    synchronized (zd) {
////                        zd.sum += counts;
////                    }
////                }
//            }
//            byte[] bytes = buffer.getBytes();
//            byte[] compressedBytes = compress(bytes, compressor);
//            los.write(compressedBytes);
//        }
//    }
//
//    protected Pair<Map<Long, List<IndexEntry>>, Long> writeMatrix(MatrixPP matrix, LittleEndianOutputStream[] losArray,
//            Deflater compressor, Map<String, IndexEntry> matrixPositions, int chromosomePairIndex, boolean doMultiThreadedBehavior) throws IOException {
//
//        LittleEndianOutputStream los = losArray[0];
//        long position = los.getWrittenCount();
//
//        los.writeInt(matrix.getChr1Idx());
//        los.writeInt(matrix.getChr2Idx());
//        int numResolutions = 0;
//
//        for (MatrixZoomDataPP zd : matrix.getZoomData()) {
//            if (zd != null) {
//                numResolutions++;
//            }
//        }
//        los.writeInt(numResolutions);
//
//        //fos.writeInt(matrix.getZoomData().length);
//        for (int i = 0; i < matrix.getZoomData().length; i++) {
//            MatrixZoomDataPP zd = matrix.getZoomData()[i];
//            if (zd != null) {
//                writeZoomHeader(zd, los);
//            }
//        }
//
//        long size = los.getWrittenCount() - position;
//        if (chromosomePairIndex > -1) {
//            matrixPositions.put("" + chromosomePairIndex, new IndexEntry(position, (int) size));
//        } else {
//            matrixPositions.put(matrix.getKey(), new IndexEntry(position, (int) size));
//        }
//
//        final Map<Long, List<IndexEntry>> localBlockIndexes = new ConcurrentHashMap<>();
//
//        for (int i = 0; i < matrix.getZoomData().length; i++) {
//            MatrixZoomDataPP zd = matrix.getZoomData()[i];
//            if (zd != null) {
//                List<IndexEntry> blockIndex = null;
//                if (doMultiThreadedBehavior) {
//                    if (losArray.length > 1) {
//                        blockIndex = zd.mergeAndWriteBlocks(losArray, compressor, i, matrix.getZoomData().length);
//                    } else {
//                        blockIndex = zd.mergeAndWriteBlocks(losArray[0], compressor);
//                    }
//                    localBlockIndexes.put(zd.blockIndexPosition, blockIndex);
//                } else {
//                    blockIndex = zd.mergeAndWriteBlocks(losArray[0], compressor);
//                    updateIndexPositions(blockIndex, losArray, true, outputFile, 0, zd.blockIndexPosition);
//                }
//            }
//        }
//
//        System.out.print(".");
//        return new Pair<>(localBlockIndexes, position);
//    }
//
//    private void writeZoomHeader(MatrixZoomDataPP zd, LittleEndianOutputStream los) throws IOException {
//
//        int numberOfBlocks = zd.blockNumbers.size();
//        los.writeString(zd.getUnit().toString());  // Unit
//        los.writeInt(zd.getZoom());     // zoom index,  lowest res is zero
//        los.writeFloat((float) zd.getSum());      // sum
//        los.writeFloat((float) zd.getOccupiedCellCount());
//        los.writeFloat((float) zd.getPercent5());
//        los.writeFloat((float) zd.getPercent95());
//        los.writeInt(zd.getBinSize());
//        los.writeInt(zd.getBlockBinCount());
//        los.writeInt(zd.getBlockColumnCount());
//        los.writeInt(numberOfBlocks);
//
//        zd.blockIndexPosition = los.getWrittenCount();
//
//        // Placeholder for block index
//        for (int i = 0; i < numberOfBlocks; i++) {
//            los.writeInt(0);
//            los.writeLong(0L);
//            los.writeInt(0);
//        }
//
//    }
//
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
//
//    /**
//     * todo should this be synchronized?
//     *
//     * @param data
//     * @param compressor
//     * @return
//     */
//    protected byte[] compress(byte[] data, Deflater compressor) {
//
//        // Give the compressor the data to compress
//        compressor.reset();
//        compressor.setInput(data);
//        compressor.finish();
//
//        // Create an expandable byte array to hold the compressed data.
//        // You cannot use an array that's the same size as the orginal because
//        // there is no guarantee that the compressed data will be smaller than
//        // the uncompressed data.
//        ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);
//
//        // Compress the data
//        byte[] buf = new byte[1024];
//        while (!compressor.finished()) {
//            int count = compressor.deflate(buf);
//            bos.write(buf, 0, count);
//        }
//        try {
//            bos.close();
//        } catch (IOException e) {
//            System.err.println("Error clossing ByteArrayOutputStream");
//            e.printStackTrace();
//        }
//
//        return bos.toByteArray();
//    }

}
