/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.qut.citrus;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import org.qut.citrus.include.Chromosome;
import org.qut.citrus.include.Hic_header;
import org.qut.citrus.include.SupperScaffold;

/**
 *
 * @author an
 */
public class Canvas extends JPanel {

    Map<Integer, BufferedImage> buffered_images = new TreeMap();
    TreeMap<String, StringBuilder> fasta = new TreeMap();
    TreeMap<Integer, SupperScaffold> assembly_order_map = new TreeMap();
    TreeMap<Integer, List<Integer>> assembly_chr_order_list = new TreeMap();
    TreeMap<Integer, Integer> assembly_block_direction = new TreeMap<>();
//    long genome_size_assembly;
    int selected_order = Integer.MIN_VALUE;
    int selected_order_min_line = -1;
    int selected_order_max_line = -1;
    int insert_candidate_order_no = -1;
    boolean ctrl_down = false;
    boolean shift_down = false;
    int MARGIN = 0;
    int bpBinSize = 0;
    int view_width_orig;
    int display_resolution = 0;
    double display_color_range = 0.5;
    String chr_chr = "1_1";// "1_10";
    Color highValueColour = Color.BLACK;
    Color lowValueColour = Color.WHITE;
    int region_start, region_stop;
//    Color colourScale = SCALE_LINEAR;
// How many RGB steps there are between the high and low colours.
    private int colourValueDistance;
    Point cutting_XY = new Point(-1, -1);
    
    public void clear(){
        this.selected_order = Integer.MIN_VALUE;
        this.insert_candidate_order_no = -1;
        ctrl_down = false;
        shift_down = false;
    }

    public void set_display_resolution(int display_resolution){
        this.display_resolution = display_resolution;
    }

    public void set_display_scale(int bpBinSize) {
        this.bpBinSize = bpBinSize;
//        long total_genome_size = 0;
//        for (int superfold : assembly_order_map.keySet()) {
//            total_genome_size += assembly_order_map.get(superfold).size_in_superscaffold;
//        }
//        double ratio_assembly_Hic = (genome_size_assembly * 1000L / total_genome_size) / 1000.0;
//        this.bpBinSize = (int)(bpBinSize / ratio_assembly_Hic);
    }

    public void set_display_color_range(double display_color_range){
        this.display_color_range = display_color_range;
    }

    public void set_region(String region){
        this.region_start = Integer.parseInt(region.split("-")[0]);
        this.region_stop = Integer.parseInt(region.split("-")[1]);
    }
    
//    public Canvas(String Hic_fn, int view_width_orig, int display_resolution) throws IOException {
    public Canvas(int view_width_orig, int display_resolution) throws IOException {
        this.display_resolution = display_resolution;
        this.view_width_orig = view_width_orig;
//        int view_width = view_width_orig * display_resolution;
//        this.setPreferredSize(new Dimension(view_width + 50, view_width + 50));
    }
//    void set_genome_size_assembly(long genome_size_assembly){
//        this.genome_size_assembly = genome_size_assembly;
//    }

    private void updateColourDistance() {
        int r1 = lowValueColour.getRed();
        int g1 = lowValueColour.getGreen();
        int b1 = lowValueColour.getBlue();
        int r2 = highValueColour.getRed();
        int g2 = highValueColour.getGreen();
        int b2 = highValueColour.getBlue();

        colourValueDistance = Math.abs(r1 - r2);
        colourValueDistance += Math.abs(g1 - g2);
        colourValueDistance += Math.abs(b1 - b2);
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // call superclass's paintComponent
        Graphics2D g2D = (Graphics2D)g;
        g2D.setFont(new Font("MyriadPro", Font.BOLD, 12));
        double image_center_x = MARGIN, image_center_y = MARGIN;
        int row_no = 1, col_no = 1;
        for (int block_no : buffered_images.keySet()) {
//            if(block_no != 3) continue;
            g2D.drawImage(buffered_images.get(block_no), (int)(image_center_x + 0.5), (int)(image_center_y + 0.5), this);
            double half_block_len = buffered_images.get(block_no).getWidth() / 2.0;
            image_center_x += half_block_len;
            image_center_y += half_block_len;
            g2D.translate(image_center_x, image_center_y);
            g2D.rotate(-Math.PI / 2);
            double delta = image_center_y - image_center_x; //if block is not at diagonal, the position should be changed.
            g2D.drawImage(buffered_images.get(block_no),
                    (int)(-half_block_len + delta + 0.5), (int)(-half_block_len + delta + 0.5),
                    (int)(half_block_len + delta + 0.5), (int)(half_block_len + delta + 0.5),
                    (int)(2.0 * half_block_len + 0.5), 0,
                    0, (int)(2.0 * half_block_len + 0.5),
                    this);
            g2D.rotate(Math.PI / 2);
            g2D.translate(-image_center_x, -image_center_y);
            if (row_no == col_no) {
                row_no++;
                col_no = 1;
                image_center_y += half_block_len;
                image_center_x = MARGIN;
            } else {
                col_no++;
                image_center_x += half_block_len;
                image_center_y -= half_block_len;
            }
        }
        if (bpBinSize != 0) {
            draw_assembly(g2D);
        }
        if (selected_order > Integer.MIN_VALUE) {
            Stroke ss = g2D.getStroke();
            g2D.setStroke(new BasicStroke(2));
//            int mind = -1, max = -1;
            if (shift_down) {
                Point p = get_fragment_min_max(selected_order, bpBinSize);
                selected_order_min_line = p.x;
                selected_order_max_line = p.y;
//                SupperScaffold selectSuperscafforld = assembly_order_map.get((selected_order));
//                selected_order_min_line = MARGIN+(int) (selectSuperscafforld.genome_position / (scale * 2.0) + 0.5);
//                selected_order_max_line = MARGIN+(int) ((selectSuperscafforld.genome_position + selectSuperscafforld.size_in_superscaffold) / (scale * 2.0) + 0.5);
            } else if (ctrl_down) {
                Point p = get_chr_min_max(selected_order, bpBinSize);
                selected_order_min_line = p.x;
                selected_order_max_line = p.y;
//                for (int chr_no : assembly_chr_order_list.keySet()){
//                    if(assembly_chr_order_list.get(chr_no).contains(selected_order)){
//                        SupperScaffold first = assembly_order_map.get((assembly_chr_order_list.get(chr_no).get(0)));
//                        SupperScaffold last = assembly_order_map.get((assembly_chr_order_list.get(chr_no).get(assembly_chr_order_list.get(chr_no).size() - 1)));
//                        selected_order_min_line = MARGIN+(int) (first.genome_position / (scale * 2.0) + 0.5);
//                        selected_order_max_line = MARGIN+(int) ((last.genome_position + last.size_in_superscaffold) / (scale * 2.0) + 0.5);
//                        break;
//                    }
//                }
            }
            g2D.setColor(Color.ORANGE);
            g2D.drawLine(MARGIN+0, selected_order_min_line, this.getWidth(), selected_order_min_line);
            g2D.drawLine(MARGIN+0, selected_order_max_line, this.getWidth(), selected_order_max_line);
            g2D.drawLine(selected_order_min_line, MARGIN+0, selected_order_min_line, this.getWidth());
            g2D.drawLine(selected_order_max_line, MARGIN+0, selected_order_max_line, this.getWidth());
            g2D.setStroke(ss);
        } else {
            int jjj = 0;
        }
    }

    Point get_chr_min_max(int selected_order, int local_scale) {
        for (int chr_no : assembly_chr_order_list.keySet()) {
            if (assembly_chr_order_list.get(chr_no).contains(selected_order)) {
                SupperScaffold first = assembly_order_map.get((assembly_chr_order_list.get(chr_no).get(0)));
                SupperScaffold last = assembly_order_map.get((assembly_chr_order_list.get(chr_no).get(assembly_chr_order_list.get(chr_no).size() - 1)));
                int selected_order_min_line1 = MARGIN + (int) (first.genome_position / (local_scale * 1.0) + 0.5);
                int selected_order_max_line1 = MARGIN + (int) ((last.genome_position + last.size_in_superscaffold) / (local_scale * 1.0) + 0.5);
                return new Point(selected_order_min_line1, selected_order_max_line1);
            }
        }
        return null;
    }
    
    Point get_chr_min_max(Map<Integer, List<Integer>> fragments_moved, int local_scale) {
        int selected_order_min_line1 = Integer.MAX_VALUE;
        int selected_order_max_line1 = Integer.MIN_VALUE;
        for (int removed_chr_no : fragments_moved.keySet()) {
            for (int order : fragments_moved.get(removed_chr_no)) {
                SupperScaffold ss = assembly_order_map.get(order);
                int min = MARGIN + (int) (ss.genome_position / (local_scale * 1.0) + 0.5);
                if (min < selected_order_min_line1) {
                    selected_order_min_line1 = min;
                }
                int max = MARGIN + (int) ((ss.genome_position + ss.size_in_superscaffold) / (local_scale * 1.0) + 0.5);
                if (max > selected_order_max_line1) {
                    selected_order_max_line1 = max;
                }
            }
        }
        return new Point(selected_order_min_line1, selected_order_max_line1);
//        for (int chr_no : assembly_chr_order_list.keySet()) {
//            if (assembly_chr_order_list.get(chr_no).containsAll(fragments_moved)) {
//                for (int order : fragments_moved) {
//                    SupperScaffold ss = assembly_order_map.get((assembly_chr_order_list.get(chr_no).get(order)));
//                    int min = MARGIN + (int) (ss.genome_position / (scale * 2.0) + 0.5);
//                    if (min < selected_order_min_line1) {
//                        selected_order_min_line1 = min;
//                    }
//                    int max = MARGIN + (int) ((ss.genome_position + ss.size_in_superscaffold) / (scale * 2.0) + 0.5);
//                    if (max > selected_order_max_line1) {
//                        selected_order_max_line1 = max;
//                    }
//                }
//                return new Point(selected_order_min_line1, selected_order_max_line1);
//            }
//        }
//        return null;
    }    
    
    Point get_fragment_min_max(int selected_order, int local_scale) {
        SupperScaffold selectSuperscafforld = assembly_order_map.get((selected_order));
        int selected_order_min_line1 = MARGIN + (int) (selectSuperscafforld.genome_position / (local_scale * 1.0) + 0.5);
        int selected_order_max_line1 = MARGIN + (int) ((selectSuperscafforld.genome_position + selectSuperscafforld.size_in_superscaffold) / (local_scale * 1.0) + 0.5);
        return new Point(selected_order_min_line1, selected_order_max_line1);
    }

    void set_selected_order(Point p){
        long x = (p.x - MARGIN) * bpBinSize * 1L;
        long y = (p.y - MARGIN) * bpBinSize * 1L;        
        for(int order_no : assembly_order_map.keySet()){
//            int order_no_abs = Math.abs(order_no);
            SupperScaffold sc = assembly_order_map.get(order_no);
            if((x > sc.genome_position) && (x < sc.genome_position + sc.size_in_superscaffold)
                    && (y > sc.genome_position) && (y < sc.genome_position + sc.size_in_superscaffold)) {
                selected_order = order_no;// * assembly_block_direction.get(order_no_abs);
//                System.out.println("selectedFragment="+selectedFragment);
                this.repaint();
                return;
            }           
        }
        selected_order = Integer.MIN_VALUE;
        this.repaint();
    }
    
    
    void draw_assembly(Graphics2D g2D){
        long offset = 0;
        Stroke ss = g2D.getStroke();
        g2D.setStroke(new BasicStroke(2));
        
//                for (int superfold : canvas.assembly_order_map.keySet()) {
//                    canvas.assembly_order_map.get(superfold).display_size *= HH.getChr("assembly").getLength() / total_genome_size;
//                    canvas.assembly_order_map.get(superfold).display_start *= HH.getChr("assembly").getLength() / total_genome_size;
//                }
        
        
        
        for(int chr_no : assembly_chr_order_list.keySet()){
            List<Integer> order_per_chr = assembly_chr_order_list.get(chr_no);
            g2D.setColor(Color.green);
            long chr_size = 0;
            for(int order_no : order_per_chr){
                SupperScaffold sc = assembly_order_map.get(order_no);
                sc.genome_position = offset;
//                int pos = offset + sc.start / scale;
                sc.display_start = Math.round(offset / (float)bpBinSize);
                sc.display_size =  Math.round((offset + sc.size_in_superscaffold) / (float)bpBinSize) - sc.display_start;
//                System.out.println(order_no + " "+sc.display_start+" "+sc.display_size);
                g2D.drawRect(MARGIN+sc.display_start, MARGIN+sc.display_start, sc.display_size, sc.display_size);
                offset += sc.size_in_superscaffold;
                chr_size += sc.size_in_superscaffold;
                System.out.println("sc.display_start="+sc.display_start+" sc.display_size="+sc.display_size);
            }
            g2D.setColor(Color.blue);
            g2D.drawRect(MARGIN + Math.round((offset - chr_size) / (float)bpBinSize), MARGIN + Math.round((offset - chr_size) / (float)bpBinSize), 
                    Math.round(chr_size / (float)bpBinSize), Math.round(chr_size / (float)bpBinSize));
//            System.out.println(Integer.toString(Math.round((offset - chr_size) / (float)bpBinSize)) + " : " + Integer.toString(Math.round(chr_size / (float)bpBinSize)));
//            g2D.drawRect(MARGIN + (int) ((offset - chr_size) / scale / 2), MARGIN + (int) ((offset - chr_size) / scale / 2), chr_size / scale / 2, chr_size / scale / 2);
        }
        g2D.setStroke(ss);
    }
    
    
    

    private long round_scale_step(long scale_step){
        if(scale_step > 100000000){
            scale_step = (scale_step / 100000000) * 100000000;
        }else if(scale_step > 10000000){
            scale_step = (scale_step / 10000000) * 10000000;
        }else if(scale_step > 1000000){
            scale_step = (scale_step / 1000000) * 1000000;
        }else if(scale_step > 100000){
            scale_step = (scale_step / 100000) * 100000;
        }else if(scale_step > 10000){
            scale_step = (scale_step / 10000) * 10000;
        }else if(scale_step > 1000){
            scale_step = (scale_step / 1000) * 1000;
        }else if(scale_step > 100){
            scale_step = (scale_step / 100) * 100;
        }
        return scale_step;
    }

    /*
     * Returns how many colour shifts are required from the lowValueColour to
     * get to the correct colour position. The result will be different
     * depending on the colour scale used: LINEAR, LOGARITHMIC, EXPONENTIAL.
     */
    private int getColourPosition(double percentPosition) {
        return (int) Math.round(colourValueDistance * Math.pow(percentPosition, 1));
    }
    
    private int changeColourValue(int colourValue, int colourDistance) {
        if (colourDistance < 0) {
            return colourValue + 1;
        } else if (colourDistance > 0) {
            return colourValue - 1;
        } else {
            // This shouldn't actually happen here.
            return colourValue;
        }
    }


    
    public void drawRotate(Graphics2D g2d, int x, int y, int angle, BufferedImage image) {
        g2d.translate((float) x, (float) y);
        g2d.rotate(Math.toRadians(angle));
        g2d.drawImage(image, 300, 300, null);
        g2d.rotate(-Math.toRadians(angle));
        g2d.translate(-(float) x, -(float) y);
    }

    public void flip(BufferedImage image) {
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < i; j++) {
                int tmp = image.getRGB(i, j);
                image.setRGB(i, j, image.getRGB(j, i));
                image.setRGB(j, i, tmp);
            }
        }
    }

}

class y_scale extends JPanel {

    String chr_chr = "1_1";
    Hic_header HH;
    List<Chromosome> chromosomes;
    int length = 100;

//    int view_width_orig;
//    int display_resolution;

    public y_scale(int length) {//int view_width_orig, int display_resolution) {
        this.length = length;
//        this.view_width_orig = view_width_orig;
//        this.display_resolution = display_resolution;
    }
    
    void setLength(int length){
        this.length = length;
    }
    public void set_HH(Hic_header HH){
        this.HH = HH;
        chromosomes = HH.getChromosomes();
    }
    
//    public void set_display_resolution(int display_resolution){
//        this.display_resolution = display_resolution;
//    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g); // call superclass's paintComponent
//        int height = this.getHeight();//view_width_orig * display_resolution;//getWidth();
//        int width = this.getWidth();//getHeight();
//        g.setColor(Color.white);
//        g.fillRect(0, 0, width, height / 2);
//        g.setColor(Color.red);
//        for (int i = 0; i < height / 2; i += height / 20) {
//            g.drawString("F" + Integer.toString(i), 0, i);
//        }
//        g.setColor(Color.white);
//        g.fillRect(0, height / 2, width, height);
//        g.setColor(Color.blue);
//        for (int i = height / 2; i < height; i += height / 20) {
//            g.drawString("B" + Integer.toString(i), 0, i);
//        }
        
        if (HH != null) {
//            int view_height = this.getHeight();//view_width_orig * display_resolution;
            int chr1_no = Integer.parseInt(chr_chr.split("_")[0]);
            int chr2_no = Integer.parseInt(chr_chr.split("_")[1]);
            long max_chr_bp = Math.max(chromosomes.get(chr1_no).getLength(), chromosomes.get(chr2_no).getLength());
            double num_scales = length / 100.0;
            long scale_step = round_scale_step((int)(max_chr_bp / num_scales + 0.5));
            num_scales = (int) (max_chr_bp / scale_step);
            double bp_per_pixel = max_chr_bp / length;//view_width_orig;        
            Graphics2D g2D = (Graphics2D) g;
//            int pos_axis = 0;//MARGIN - 1;
            g2D.drawLine(19, 0, 19, length);//X line
//        g2D.drawLine(pos_axis, pos_axis, pos_axis, view_width + pos_axis);//Y line?
            long scale = 0;

//        g2D.drawLine(pos_axis, 15, pos_axis, 20);//orig X
//        g2D.drawLine(pos_axis - 5, pos_axis, pos_axis, pos_axis);//orig Y
            for (int i = 0; i <= num_scales; i++) {
                String scale_str;
                if (scale > 1000000) {
                    scale_str = (scale * 1) / 1000000 + "M";
                } else if (scale > 1000) {
                    scale_str = (scale * 1) / 1000 + "K";
                } else {
                    scale_str = Long.toString((scale * 1));
                }
//                g2D.drawString(scale_str, (int) (scale / bp_per_pixel) - g2D.getFontMetrics().stringWidth(scale_str) / 2, 12);
//                g2D.drawLine((int) (scale / bp_per_pixel), 15, (int) (scale / bp_per_pixel), 20);
                if (scale > 0) {
                    drawRotate(g2D, 15, (int) (scale / bp_per_pixel) + g2D.getFontMetrics().stringWidth(scale_str) / 2, -90, scale_str);
                }
                g2D.drawLine(15, (int) (scale / bp_per_pixel), 20, (int) (scale / bp_per_pixel));
                scale += scale_step;
            }
            g2D.drawLine(15, length, 20, length);//orig X
//        g2D.drawLine(pos_axis - 5, view_width + pos_axis, pos_axis, view_width + pos_axis);//orig Y

        }
        
        
        
    }
    
    private long round_scale_step(long scale_step){
        if(scale_step > 100000000){
            scale_step = (scale_step / 100000000) * 100000000;
        }else if(scale_step > 10000000){
            scale_step = (scale_step / 10000000) * 10000000;
        }else if(scale_step > 1000000){
            scale_step = (scale_step / 1000000) * 1000000;
        }else if(scale_step > 100000){
            scale_step = (scale_step / 100000) * 100000;
        }else if(scale_step > 10000){
            scale_step = (scale_step / 10000) * 10000;
        }else if(scale_step > 1000){
            scale_step = (scale_step / 1000) * 1000;
        }else if(scale_step > 100){
            scale_step = (scale_step / 100) * 100;
        }
        return scale_step;
    }    
    private void drawRotate(Graphics2D g2d, double x, double y, int angle, String text) {
        g2d.translate((float) x, (float) y);
        g2d.rotate(Math.toRadians(angle));
        g2d.drawString(text, 0, 0);
        g2d.rotate(-Math.toRadians(angle));
        g2d.translate(-(float) x, -(float) y);
    }
    
    
}

class x_scale extends JPanel{
    String chr_chr = "1_1";
    Hic_header HH;
    List<Chromosome> chromosomes;
    int length = 100;
//    int view_width_orig;
//    int display_resolution;
    public x_scale(int length){//int view_width_orig, int display_resolution) {
        this.length = length;
//        this.view_width_orig = view_width_orig;
//        this.display_resolution = display_resolution;
    }
    void setLength(int length){
        this.length = length;
    }
    public void set_HH(Hic_header HH){
        this.HH = HH;
        chromosomes = HH.getChromosomes();
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // call superclass's paintComponent
//        int width = this.getWidth();//view_width_orig * display_resolution;//getWidth();
//        int height = this.getHeight();//getHeight();
//        g.setColor(Color.white);
//        g.fillRect(0, 0, width/2, height);
//        g.setColor(Color.red);
//        for (int i = 0; i < width/2; i += width/20){
//            g.drawString("F"+Integer.toString(i), i, 10);
//        }
//        g.setColor(Color.white);
//        g.fillRect(width/2, 0, width/2, height);
//        g.setColor(Color.blue);
//        for (int i = width/2; i < width; i += width/20){
//            g.drawString("B"+Integer.toString(i), i, 10);
//        }
        if (HH != null) {
//            int view_width = this.getWidth();//view_width_orig * display_resolution;
            int chr1_no = Integer.parseInt(chr_chr.split("_")[0]);
            int chr2_no = Integer.parseInt(chr_chr.split("_")[1]);
            long max_chr_bp = Math.max(chromosomes.get(chr1_no).getLength(), chromosomes.get(chr2_no).getLength());
            double num_scales = length / 100.0;//10;
            long scale_step = round_scale_step((int)(max_chr_bp / num_scales + 0.5));
            num_scales = (int) (max_chr_bp / scale_step);
            double bp_per_pixel = max_chr_bp / length;//view_width_orig;        
            Graphics2D g2D = (Graphics2D) g;
//            int pos_axis = 0;//MARGIN - 1;
            g2D.drawLine(0, 19, length, 19);//X line
//        g2D.drawLine(pos_axis, pos_axis, pos_axis, view_width + pos_axis);//Y line?
            long scale = 0;

//        g2D.drawLine(pos_axis, 15, pos_axis, 20);//orig X
//        g2D.drawLine(pos_axis - 5, pos_axis, pos_axis, pos_axis);//orig Y
            for (int i = 0; i <= num_scales; i++) {
                String scale_str;
                if (scale > 1000000) {
                    scale_str = (scale * 1) / 1000000 + "M";
                } else if (scale > 1000) {
                    scale_str = (scale * 1) / 1000 + "K";
                } else {
                    scale_str = Long.toString((scale * 1));
                }
                if (scale > 0) {
                    g2D.drawString(scale_str, (int) (scale / bp_per_pixel) - g2D.getFontMetrics().stringWidth(scale_str) / 2, 12);
                }
                g2D.drawLine((int) (scale / bp_per_pixel), 15, (int) (scale / bp_per_pixel), 20);
//            drawRotate(g2D, pos_axis - 5, pos_axis + (int)(scale / bp_per_pixel) + g2D.getFontMetrics().stringWidth(scale_str)/2, -90, scale_str);
//            g2D.drawLine(pos_axis - 5, pos_axis + (int)(scale / bp_per_pixel), pos_axis, pos_axis + (int)(scale / bp_per_pixel));
                scale += scale_step;
            }
//            g2D.drawLine(view_width, 15, view_width, 15);//orig X
//        g2D.drawLine(pos_axis - 5, view_width + pos_axis, pos_axis, view_width + pos_axis);//orig Y

        }
        
        
//        g.fillRect(width/2, 0, width/2, height);
    }
    
    private long round_scale_step(long scale_step){
        if(scale_step > 100000000){
            scale_step = (scale_step / 100000000) * 100000000;
        }else if(scale_step > 10000000){
            scale_step = (scale_step / 10000000) * 10000000;
        }else if(scale_step > 1000000){
            scale_step = (scale_step / 1000000) * 1000000;
        }else if(scale_step > 100000){
            scale_step = (scale_step / 100000) * 100000;
        }else if(scale_step > 10000){
            scale_step = (scale_step / 10000) * 10000;
        }else if(scale_step > 1000){
            scale_step = (scale_step / 1000) * 1000;
        }else if(scale_step > 100){
            scale_step = (scale_step / 100) * 100;
        }
        return scale_step;
    }    
    private void drawRotate(Graphics2D g2d, double x, double y, int angle, String text) {
        g2d.translate((float) x, (float) y);
        g2d.rotate(Math.toRadians(angle));
        g2d.drawString(text, 0, 0);
        g2d.rotate(-Math.toRadians(angle));
        g2d.translate(-(float) x, -(float) y);
    }
}