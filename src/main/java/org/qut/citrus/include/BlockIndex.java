/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.qut.citrus.include;
import htsjdk.tribble.util.LittleEndianInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import static org.qut.citrus.include.utility_access_binary_file.readInt;
import static org.qut.citrus.include.utility_access_binary_file.readLong;


/**
 *
 * @author an
 */
public class BlockIndex {

    protected final Map<Integer, IndexEntry> blockIndex;
    protected final int numBlocks;

    public BlockIndex(int nBlocks) {
        numBlocks = nBlocks;
        blockIndex = new TreeMap<>();
    }

    public void populateBlocks(LittleEndianInputStream dis) throws IOException {
        for (int b = 0; b < numBlocks; b++) {
            int blockNumber = dis.readInt();
            long filePosition = dis.readLong();
            int blockSizeInBytes = dis.readInt();
            blockIndex.put(blockNumber, new IndexEntry(filePosition, blockSizeInBytes));
        }
    }
//    public void populateBlocks(DataInputStream dis) throws IOException {
//        byte[] buffer = new byte[numBlocks * 16];//(4 + 8 + 4)];
//        for (int b = 0; b < numBlocks; b++) {
//            int blockNumber = byte2int(buffer, b * 16);// dis.readInt();
//            long filePosition = byte2long(buffer, b * 16 + 4);
//            int blockSizeInBytes = byte2int(buffer, b * 16 + 12);;
//            blockIndex.put(blockNumber, new IndexEntry(filePosition, blockSizeInBytes));
//        }
//    }
    
    public void populateBlocks(DataInputStream dis) throws IOException {
//        byte[] buffer = new byte[numBlocks * 16];//(4 + 8 + 4)];
        for (int b = 0; b < numBlocks; b++) {
            int blockNumber = readInt(dis);//byte2int(buffer, b * 16);// dis.readInt();
            long filePosition = readLong(dis);//byte2long(buffer, b * 16 + 4);
            int blockSizeInBytes = readInt(dis);//byte2int(buffer, b * 16 + 12);;
            blockIndex.put(blockNumber, new IndexEntry(filePosition, blockSizeInBytes));
        }
    }
    

    public List<Integer> getBlockNumbers() {
        return new ArrayList<>(blockIndex.keySet());
    }

    public IndexEntry getBlock(int blockNumber) {
        return blockIndex.get(blockNumber);
    }

}
