/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.qut.citrus.include;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 *
 * @author an
 */
public class MyBufferedImage extends BufferedImage {

    Hic_header HH;
    numContact_X_Y_per_resolution_str blocks_per_resolution;
    int block_no;
    int blockColumnCount;
    List<Chromosome> chromosomes;
    int[] bpBinSizes;
//    long whole_genome_size;
    int MARGIN = 20;
    int view_width_orig;
    int display_resolution = 0;
    double display_color_range = 0.5;
    String chr_chr = "1_1";// "1_10";
    Map<Point, Float> color_ratio = new TreeMap<>(new cmp_Point());
//    Color highValueColour = Color.BLACK;
//    Color lowValueColour = Color.WHITE;
//    int region_start, region_stop;
////    Color colourScale = SCALE_LINEAR;
//// How many RGB steps there are between the high and low colours.
//    private int colourValueDistance;

    public MyBufferedImage(Hic_header HH, numContact_X_Y_per_resolution_str blocks_per_resolution, int blockColumnCount, int block_no, int display_resolution, double display_color_range, int view_width_orig, String Hic_fn) throws IOException { // used in zoom in region
        super(blocks_per_resolution.blockBinCount, blocks_per_resolution.blockBinCount, BufferedImage.TYPE_INT_ARGB);
        this.blockColumnCount = blockColumnCount;
        this.block_no = block_no;
        this.HH = HH;
        this.blocks_per_resolution = blocks_per_resolution;
        this.display_resolution = display_resolution;
        this.display_color_range = display_color_range;
        this.view_width_orig = view_width_orig;
//        int view_width = view_width_orig * display_resolution;
//        this.setPreferredSize(new Dimension(view_width + 50, view_width + 50));
        chromosomes = HH.getChromosomes();
        bpBinSizes = HH.getBpBinSizes();
//        whole_genome_size = 0;
//        for(Chromosome chr : chromosomes){
//            whole_genome_size += chr.getLength();
//        }
        
        
        Graphics g = super.getGraphics();
        draw_image(g);
    }

    public void set_color(Double new_display_color_range) {
        for(Point p : color_ratio.keySet()){
            double s = color_ratio.get(p);
            if (s < new_display_color_range) {
                s = s / new_display_color_range;
            } else {
                s = 1.0;
            }
            int RGB = java.awt.Color.HSBtoRGB(0.0f, (float)s, 1.0f);
            this.setRGB(p.x, p.y, RGB);
        }
    }

    private void draw_image(Graphics graph) {//, double ratio) {
//        int chr1_no = Integer.parseInt(chr_chr.split("_")[0]);
//        int chr2_no = Integer.parseInt(chr_chr.split("_")[1]);
//        long max_chr_bp = Math.max(chromosomes.get(chr1_no).getLength(), chromosomes.get(chr2_no).getLength());
//        int num_scales = 5;
//        long scale_step = round_scale_step(max_chr_bp / num_scales);
//        num_scales = (int)(max_chr_bp / scale_step);
//        double bp_per_pixel = max_chr_bp / view_width_orig;        
        int numPix_per_record = 1;// bpBinSizes[display_resolution] / bp_per_pixel;
        Graphics2D g2D = (Graphics2D) graph;
//        int pos_axis = MARGIN - 1;
//        int view_width = view_width_orig * display_resolution;
//        g2D.drawLine(pos_axis, pos_axis, view_width + pos_axis, pos_axis);//X line
//        g2D.drawLine(pos_axis, pos_axis, pos_axis, view_width + pos_axis);//Y line
//        long scale = 0;
//        g2D.drawLine(pos_axis, pos_axis - 5, pos_axis, pos_axis);//orig X
//        g2D.drawLine(pos_axis - 5, pos_axis, pos_axis, pos_axis);//orig Y
        
//        for(int i = 0; i <= num_scales; i++){
//            String scale_str;
//            if(scale > 1000000){
//                scale_str = 2 * scale / 1000000 + "M";
//            }else if(scale > 1000){
//                scale_str = 2 * scale / 1000 + "K";
//            }else{
//                scale_str = Long.toString(2 * scale);
//            }
//            g2D.drawString(scale_str, pos_axis + (int)(scale / bp_per_pixel) - g2D.getFontMetrics().stringWidth(scale_str)/2, pos_axis - 5);
//            g2D.drawLine(pos_axis + (int)(scale / bp_per_pixel), pos_axis - 5, pos_axis + (int)(scale / bp_per_pixel), pos_axis);
////            drawRotate(g2D, pos_axis - 5, pos_axis + (int)(scale / bp_per_pixel) + g2D.getFontMetrics().stringWidth(scale_str)/2, -90, scale_str);
//            g2D.drawLine(pos_axis - 5, pos_axis + (int)(scale / bp_per_pixel), pos_axis, pos_axis + (int)(scale / bp_per_pixel));
//            scale += scale_step;            
//        }
//        g2D.drawLine(view_width + pos_axis, pos_axis - 5, view_width + pos_axis, pos_axis);//orig X
//        g2D.drawLine(pos_axis - 5, view_width + pos_axis, pos_axis, view_width + pos_axis);//orig Y
////        numContact_X_Y_per_resolution_str blocks = Hic_data_in_chr_chr.get(display_resolution);//resolution - 1);
        if (blocks_per_resolution != null) {
            double lowValue = blocks_per_resolution.minCount_contact_X_Y;//Double.MAX_VALUE;
            double highValue = blocks_per_resolution.maxCount_contact_X_Y;//Double.MIN_VALUE;
            int offset_x = block_no % blocks_per_resolution.blockColumnCount;
            int offset_y = (block_no - offset_x) / blocks_per_resolution.blockColumnCount;
//            int offset_x_pix_no = offset_x * blocks_per_resolution.blockBinCount;
//            int offset_y_pix_no = offset_y * blocks_per_resolution.blockBinCount;
            int offset_pix_x = offset_x * blocks_per_resolution.blockBinCount;
            int offset_pix_y = offset_y * blocks_per_resolution.blockBinCount;
            color_ratio.clear();
            for (ContactRecord rec : blocks_per_resolution.contactRec_per_block.get(block_no)) {
                int binX = rec.binX % blocks_per_resolution.blockBinCount;
                int binY = rec.binY % blocks_per_resolution.blockBinCount;
                double s = (((double) rec.counts - lowValue) / (highValue - lowValue));
                color_ratio.put(new Point(binX, binY), (float)s);
                if (s < display_color_range) {
                    s = s / display_color_range;
                } else {
                    s = 1.0;
                }
                Color color = Color.getHSBColor(0.0f, (float) (s), 1.0f);
                g2D.setColor(color);
                g2D.drawRect(binX, binY, 1, 1);
            }
//            for(int binY = 0; binY < blocks_per_resolution.blockBinCount; binY++) {
//                for (int binX = 0; binX < Math.min(blocks_per_resolution.blockBinCount, binY + (offset_pix_y - offset_pix_x)); binX++) {
//                    if (blocks_per_resolution.contactRec_per_block.get(block_no).[binX][binY] > 0.001) {
//                        double s = (((double) blocks_per_resolution.contactRec_per_block.get(block_no)[binX][binY] - lowValue) / (highValue - lowValue));
//                        if (s < display_color_range) {
//                            s = s / display_color_range;
//                        } else {
//                            s = 1.0;
//                        }
//                        Color color = Color.getHSBColor(0.0f, (float) (s), 1.0f);
//                        g2D.setColor(color);
//                        g2D.drawRect(binX, binY, 1, 1);
//                    }
//                }
//            }
        }
            
    }
}

class cmp_Point implements Comparator<Point>{

    @Override
    public int compare(Point o1, Point o2) {
        int ret = o1.y - o2.y;
        if(ret == 0){
            return o1.x - o2.x;
        }
        return ret;
    }
    
}