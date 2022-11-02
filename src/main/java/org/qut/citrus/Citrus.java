/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.qut.citrus;

import com.google.common.base.Strings;
import com.mongodb.util.Hash;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.qut.citrus.include.ContactRecord;
import org.qut.citrus.include.Hic_header;
import org.qut.citrus.include.History;
import org.qut.citrus.include.Matrix;
import org.qut.citrus.include.MyBufferedImage;
import org.qut.citrus.include.SupperScaffold;
import org.qut.citrus.include.Write_HIC;
import org.qut.citrus.include.binX_idx;
import org.qut.citrus.include.binX_range;
import org.qut.citrus.include.cmp_ContactRecord;
import org.qut.citrus.include.cmp_binX_idx_list;
import org.qut.citrus.include.cmp_rev_binX_idx;
import org.qut.citrus.include.cmp_rev_binX_range;
import org.qut.citrus.include.contact_position;
import org.qut.citrus.include.numContact_X_Y_per_resolution_str;

/**
 *
 * @author an
 */
public class Citrus  extends JFrame {

    Hic_header HH;
    Map<String, Map<Integer, numContact_X_Y_per_resolution_str>> Hic_data = new TreeMap<>();
    int max_resolution = 6;//10;//0;//10;
    JPanel pop_jpanel;
    Canvas canvas;
    x_scale x_axis;
    y_scale y_axis;
    int view_size = 3600;
    static int jScrollPane_size = 800;
    int resolution = 0;
    double color_range_slide_value = 0.005;
    JTextField region, Hic_fn_jTextField;//, fasta_fn_jTextField;//, Assembly_fn_jTextField;
    String dir = "C:\\Jiyuan\\sourceCode\\juicer\\LAB320\\";
    String Hic_fn, Assembly_fn, fasta_fn;
    String chr_chr = "1_1";
    private JPopupMenu popup;
    final JDialog dialog_sequence = new JDialog();
    JComboBox resolu_combobox;
    JSlider colorJslider;
    JTextArea pop_jTextArea = new JTextArea();
    JButton jButton0 = new JButton("open HiC file");
//    JButton jButton01 = new JButton("open assembly file");
//    JButton jButton02 = new JButton("open fasta file");

//    private JPanel jpnlColumn = new JPanel();
//    private JPanel jpnlRow = new JPanel();
//    private JPanel jpnlContent = new JPanel();    
//    Dimension dim = new Dimension(win_size, win_size);

    Stack<History> history = new Stack<>(); 

    public Citrus() throws IOException {//, int corner_x, int corner_y) {
//        this.canvasSize= new Dimension(canvasSize);
//        this.win_size = win_size;
        componentinit();
    }
    
    public static void main(String[] args) throws IOException {
        JFrame frame = new Citrus();
        frame.setTitle("Citrus");
        frame.setSize(new Dimension(jScrollPane_size + 50, jScrollPane_size+150));
        frame.setVisible(true);
    }
    
    private void componentinit() throws IOException {
        Hic_fn = "C:\\Jiyuan\\sourceCode\\juicer\\dat\\inter.hic";
        Hic_fn = "C:\\\\Jiyuan\\\\sourceCode\\\\juicer\\\\data\\\\juicer\\\\juicer\\\\Mod_EXP_REFINEFINAL1_bppAdjust_cmap_NbF1AP_1stpass_fasta_NGScontigs_HYBRID_SCAFFOLD.final.hic";   
//        Hic_fn = "C:\\Jiyuan\\sourceCode\\juicer\\Qspa77-bionano-juicer\\EXP_REFINEFINAL1_bppAdjust_cmap_NbQLD-hybrid-3racon-1pilon_fasta_NGScontigs_HYBRID_SCAFFOLD.final.hic";
        fasta_fn = "C:\\Jiyuan\\sourceCode\\juicer\\LAB320\\CN3KP_BNO.rawchrom.fasta";
//        Hic_fn = "C:\\Jiyuan\\sourceCode\\juicer\\before_bionano\\CNbD.contigs.rawchrom.hic";
//        Hic_fn = "C:\\Jiyuan\\sourceCode\\juicer\\data\\fff.hic";
//        Hic_fn = "C:\\Jiyuan\\sourceCode\\juicer\\QLD082\\EXP_REFINEFINAL1_bppAdjust_cmap_NbQLD-hybrid-3racon-1pilon_fasta_NGScontigs_HYBRID_SCAFFOLD.final.hic";

//        Hic_fn = "C:\\Jiyuan\\sourceCode\\juicer\\LAB\\CNbD.contigs.final.hic";
//          Hic_fn = "C:\\Jiyuan\\sourceCode\\juicer\\LAB320\\CN3KP_BNO.uppercase_FINAL.hic";
//
//          Hic_fn = "C:\\Jiyuan\\sourceCode\\juicer\\LAB320\\LAB_bionano_part6\\LAB_bionano_part.final.hic";
          Hic_fn = "C:\\Jiyuan\\sourceCode\\juicer\\LAB320\\LAB_bionano_part2\\LAB_bionano_part.final.hic";
//          Hic_fn = "C:\\Jiyuan\\sourceCode\\juicer\\LAB320\\CN3KP_BNO.final.hic";
//        fasta_fn = "C:\\Jiyuan\\sourceCode\\juicer\\QLD082\\EXP_REFINEFINAL1_bppAdjust_cmap_NbQLD-hybrid-3racon-1pilon_fasta_NGScontigs_HYBRID_SCAFFOLD.final.fasta";
//        fasta_fn = "C:\\Jiyuan\\sourceCode\\juicer\\LAB\\CNbD.contigs.final.hic";
//        fasta_fn = "C:\\Jiyuan\\sourceCode\\juicer\\LAB\\CNbD.contigs.FINAL.fasta";
//        Assembly_fn = "C:\\Jiyuan\\sourceCode\\juicer\\LAB320\\CN3KP_BNO.final.assembly";
//        Assembly_fn = "C:\\Jiyuan\\sourceCode\\juicer\\before_bionano\\CNbD.contigs.rawchrom.assembly";
//        fasta_fn = "C:\\Jiyuan\\sourceCode\\juicer\\before_bionano\\CNbD.contigs.rawchrom.fasta";
        fasta_fn = "C:\\Jiyuan\\sourceCode\\juicer\\LAB320\\CN3KP_BNO.final.fasta";
//        fasta_fn = "C:\\Jiyuan\\sourceCode\\juicer\\LAB320\\LAB_bionano_part2\\LAB_bionano_part.final.fasta";
        fasta_fn = "C:\\Jiyuan\\sourceCode\\juicer\\LAB320\\LAB_both\\LAB_bionano_both.final.fasta";
        Assembly_fn = "C:\\Jiyuan\\sourceCode\\juicer\\LAB320\\LAB_both\\LAB_bionano_both.final.assembly";
        Hic_fn = "C:\\Jiyuan\\sourceCode\\juicer\\LAB320\\LAB_both\\LAB_bionano_both.final.hic";
        Hic_fn = System.getProperty("user.dir");
        
        pop_jpanel = new JPanel();
        Box vbox1 = Box.createVerticalBox();
        Box hbox01 = Box.createHorizontalBox();
        Box hbox0 = Box.createHorizontalBox();
        
        jButton0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton0.setEnabled(false);
                file_Hic_choice(evt);
//                file_choice_readIn(evt, Hic_fn_jTextField);
                repaint();
                jButton0.setEnabled(true);
            }
        });
        hbox0.add(jButton0);
        Hic_fn_jTextField = new JTextField(Hic_fn);
        hbox0.add(Box.createHorizontalStrut(5));
        hbox0.add(Hic_fn_jTextField);

//        jButton0_update.addActionListener(new java.awt.event.ActionListener() {
//            public void actionPerformed(java.awt.event.ActionEvent evt) {
//                jButton0_update.setEnabled(false);
//                String review_Hic_fn = file_choice_update(evt, Hic_fn_jTextField);
//                if (review_Hic_fn != null) {
//                    try {
//                        Write_HIC wh = new Write_HIC(review_Hic_fn, HH, Hic_data);
//                        wh.preprocess();
//                        String review_Assembly_fn = file_choice_update(evt, Assembly_fn_jTextField);
//                        if (review_Assembly_fn != null) {
//                            writeAssembly(review_Assembly_fn);
//                        }
//                    } catch (IOException ex) {
//                        Logger.getLogger(Citrus.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                }
//                jButton0_update.setEnabled(true);
//            }
//        });
//        hbox0.add(Box.createHorizontalStrut(5));
//        hbox0.add(jButton0_update);
        vbox1.add(hbox0);

//        jButton01.addActionListener(new java.awt.event.ActionListener() {
//            public void actionPerformed(java.awt.event.ActionEvent evt) {
////                file_assembly_choice(evt);
//                file_choice_readIn(evt, Assembly_fn_jTextField);
//            }
//        });
//        hbox01.add(jButton01);
//        Assembly_fn_jTextField = new JTextField(Assembly_fn);
//        hbox01.add(Box.createHorizontalStrut(5));
//        hbox01.add(Assembly_fn_jTextField);
//
        vbox1.add(Box.createVerticalStrut(3));
        vbox1.add(hbox01);
//        Box hbox02 = Box.createHorizontalBox();
//        jButton02.addActionListener(new java.awt.event.ActionListener() {
//            public void actionPerformed(java.awt.event.ActionEvent evt) {
////               file_fasta_choice(evt); 
//                file_choice_readIn(evt, fasta_fn_jTextField);
//            }
//        });
//        hbox02.add(jButton02);
//        fasta_fn_jTextField = new JTextField(fasta_fn);
//        hbox02.add(Box.createHorizontalStrut(5));
//        hbox02.add(fasta_fn_jTextField);
//        vbox1.add(Box.createVerticalStrut(3));
//        vbox1.add(hbox02);

        
        Box hbox1 = Box.createHorizontalBox();
        hbox1.add(new JLabel("Resolution(BP)"));
        hbox1.add(Box.createHorizontalStrut(5));
        ArrayList<String> resolu_Strings = new ArrayList<>();
        resolu_Strings.add("resolution");
//        for(int i = 0; i < max_resolution; i++){
//            resolu_Strings.add(Integer.toString(i));
//        }
        resolu_combobox = new JComboBox(resolu_Strings.toArray());
        resolu_combobox.setSelectedIndex(0);
        resolu_combobox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                resolution = resolu_combobox.getSelectedIndex();
            }
        });

//        resolu = new JTextField("0");
        hbox1.add(resolu_combobox);
//        hbox1.add(Box.createHorizontalStrut(15));
//        hbox1.add(new JLabel("genome region"));
//        hbox1.add(Box.createHorizontalStrut(5));
//        region = new JTextField("0-1000000");
//        hbox1.add(region);
        hbox1.add(Box.createHorizontalStrut(15));
        hbox1.add(new JLabel("color region"));
        hbox1.add(Box.createHorizontalStrut(5));
        
        colorJslider = new JSlider(JSlider.HORIZONTAL, 1, 10, 5);
        colorJslider.setPaintLabels(true);
        colorJslider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                color_range_slide_value = colorJslider.getValue() / 1000.0;
                changeColor();//repaint();
            }
        });
        
//        color_range = new JTextField("0.005");
//        hbox1.add(color_range);
        hbox1.add(colorJslider);
        JButton jButton1 = new JButton("refresh");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
//                readIn_Assembly();
                repaint();
            }
        });
        hbox1.add(Box.createHorizontalStrut(15));
        hbox1.add(jButton1);
        JButton jButton2 = new JButton("save modified Hic & Assembly");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                String review_Hic_fn = file_choice_update(evt, Hic_fn_jTextField);
                if (review_Hic_fn != null) {
                    try {
                        Write_HIC wh = new Write_HIC(review_Hic_fn, HH, Hic_data);
                        wh.preprocess();
                        String review_Assembly_fn = review_Hic_fn.substring(0, review_Hic_fn.lastIndexOf(".")) + ".assembly";// file_choice_update(evt, Assembly_fn_jTextField);
                        if (review_Assembly_fn != null) {
                            writeAssembly(review_Assembly_fn);
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(Citrus.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
//                for (int solu = 0; solu <= Integer.parseInt(resolu.getText()); solu++){
//                    Write_HIC wh = new Write_HIC("fff.hic", HH, Hic_data);
//                    try {
//                        wh.preprocess();
////                    write_Hic(solu);
//                    } catch (IOException ex) {
//                        Logger.getLogger(Citrus.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                }
            }
        });
        hbox1.add(Box.createHorizontalStrut(5));
        hbox1.add(jButton2);
        vbox1.add(Box.createVerticalStrut(3));
        vbox1.add(hbox1);
        
        int display_resolution = resolution;//Integer.parseInt(resolu.getText());
        canvas = new Canvas(view_size, display_resolution);//, corner_x, corner_y);
        x_axis = new x_scale(view_size);//view_size, display_resolution);
        x_axis.setPreferredSize(new Dimension(0,20));
        y_axis = new y_scale(view_size);//view_size, display_resolution);
        y_axis.setPreferredSize(new Dimension(20,0));
        canvas.setPreferredSize(new Dimension(view_size + 50, view_size + 50));
        JScrollPane jScrollPane = new JScrollPane();
        jScrollPane.setPreferredSize(new Dimension(jScrollPane_size, jScrollPane_size));
        jScrollPane.getViewport().add(canvas);
        JViewport colVP = new JViewport();
        colVP.setView(x_axis);//jpnlColumn);
        jScrollPane.setColumnHeader(colVP);
        JViewport rowVP = new JViewport();
        rowVP.setView(y_axis);//jpnlRow);
        jScrollPane.setRowHeader(rowVP);
        this.getContentPane().add(jScrollPane);
        canvas.setAutoscrolls(true);
        MouseAdapter ma = getMouseAdapter();
        canvas.addMouseListener(ma);
        canvas.addMouseMotionListener(ma);
        vbox1.add(Box.createVerticalStrut(10));
        vbox1.add(jScrollPane);
        pop_jpanel.add(vbox1);
        this.add(pop_jpanel);
//        readIn_Assembly();
//        readIn_Hic();
//        readIn_Fasta();
//        create_popup_menu();
//        repaint();
    }

    @Override
    public void repaint() {
//        this.display_resolution = display_resolution;
//        this.view_width_orig = view_width_orig;
//        int view_width = view_width_orig * display_resolution;
//        canvas.setPreferredSize(new Dimension(view_width + 50, view_width + 50));
        
//        int resolution = Integer.parseInt(resolu.getText());
        if(Hic_data.isEmpty()){
            JOptionPane.showMessageDialog(null,"click \"open Hic file\" to load Hic file");
            return;
        }
        double display_color_range = color_range_slide_value;// Double.parseDouble(color_range.getText());
        int canvas_width = Hic_data.get(chr_chr).get(resolution).blockBinCount *  Hic_data.get(chr_chr).get(resolution).blockColumnCount + 10;
        canvas.setSize(new Dimension(canvas_width + 0, canvas_width + 0));x_axis.getWidth();
        x_axis.setSize(new Dimension(canvas_width + 0, 20));
        y_axis.setSize(new Dimension(20, canvas_width + 0));
        
        canvas.setPreferredSize(new Dimension(canvas_width + 0, canvas_width + 0));x_axis.getWidth();
        x_axis.setPreferredSize(new Dimension(canvas_width + 0, 20));
        y_axis.setPreferredSize(new Dimension(20, canvas_width + 0));
        
        x_axis.setLength(canvas_width);
        y_axis.setLength(canvas_width);
        
        canvas.buffered_images.clear();
//int  fuck=Hic_data.get(chr_chr).get(1).contactRec_per_block.get(4).get(245071).binX;
//contactRec_per_block.get(block_no).get(idx_X).binX
        try {
            for (int block_no :  Hic_data.get(chr_chr).get(resolution).contactRec_per_block.keySet()){
                numContact_X_Y_per_resolution_str blocks_per_resolution = Hic_data.get(chr_chr).get(resolution);
                canvas.buffered_images.put(block_no, new MyBufferedImage(HH, blocks_per_resolution, Hic_data.get(chr_chr).get(resolution).blockColumnCount, block_no, resolution, display_color_range, view_size, Hic_fn));
            }
//            canvas.buffered_image = new MyBufferedImage(HH, Hic_data.get(chr_chr), resolution, display_color_range, view_size, Hic_fn);
        } catch (IOException ex) {
            Logger.getLogger(Citrus.class.getName()).log(Level.SEVERE, null, ex);
        }
        canvas.set_display_resolution(resolution);//Integer.parseInt(resolu.getText()));
        canvas.set_display_scale(HH.bpBinSizes[resolution]);//Integer.parseInt(resolu.getText())]);
        canvas.set_display_color_range(color_range_slide_value);//Double.parseDouble(color_range.getText()));
        canvas.repaint();
        x_axis.set_HH(HH);
        x_axis.repaint();
//        y_axis.set_display_resolution(Integer.parseInt(resolu.getText()));
        y_axis.repaint();
        y_axis.set_HH(HH);
        pop_jpanel.repaint();
    }

//    void chk_size_fasta_assembly() {
//        Map<String, Integer> super_size = new TreeMap<>();
//        for (int i : canvas.assembly_order_map.keySet()) {
//            SupperScaffold ss = canvas.assembly_order_map.get(i);
//            String ID = ss.scafold_fragment_subType_ID.split(":::")[0];
//            if (super_size.get(ID) == null) {
//                super_size.put(ID, ss.size_in_superscaffold);
//            } else {
//                super_size.put(ID, super_size.get(ID) + ss.size_in_superscaffold);
//            }
//        }
//        for (String s : super_size.keySet()) {
//            if (canvas.fasta.get(s).length() != super_size.get(s)) {
//                System.out.println("error");
//            }
//        }
//    }
    
//    private JLabel getLabel(String text) {
//        JLabel lbl = new JLabel(text);
//        lbl.setPreferredSize(dim);
//        lbl.setMaximumSize(dim);
//        lbl.setMinimumSize(dim);
//        lbl.setBorder(BorderFactory.createEtchedBorder());
//        return lbl;
//    }
//    private List<JLabel> getLabelList(int count) {
//        List<JLabel> list = new ArrayList<JLabel>();
//        for (int i = 0; i < count; i++) {
//            list.add(getLabel(" JLabel" + i));
//        }
//
//        return list;
//    }

    
    
    MouseAdapter getMouseAdapter(){
        return new MouseAdapter() {
            private Point origin;
            @Override
            public void mouseMoved(MouseEvent e) {
                if (canvas.selected_order > Integer.MIN_VALUE) {
                    if ((e.getY() > e.getX() - 2) && (e.getY() < e.getX() + 2)) {
                        if ((e.getX() > canvas.selected_order_min_line) && (e.getX() < canvas.selected_order_max_line)
                                && (e.getY() > canvas.selected_order_min_line) && (e.getY() < canvas.selected_order_max_line)) {
                            canvas.cutting_XY.x = e.getX();
                            canvas.cutting_XY.y = e.getY();
                            Cursor cursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
                            canvas.setCursor(cursor);
                            canvas.setVisible(true);
                        } else {
                            canvas.cutting_XY.x = -1;
                            canvas.cutting_XY.y = -1;
                            canvas.insert_candidate_order_no = -1;
                            for(int order_no : canvas.assembly_order_map.keySet()){
                                if((e.getX() - canvas.MARGIN > canvas.assembly_order_map.get(order_no).display_start - 3) &&
                                        (e.getX() - canvas.MARGIN < canvas.assembly_order_map.get(order_no).display_start + 3)){
                                    if(canvas.insert_candidate_order_no == -1){
                                        canvas.insert_candidate_order_no = order_no;
                                        System.out.println("exist " + order_no);
                                    }else{//if more than one box targeted, ignore
                                        JOptionPane.showMessageDialog(null, "multiple blocks in the area ("+canvas.insert_candidate_order_no+","+order_no+")");
                                        canvas.insert_candidate_order_no = -1;
                                        System.out.println("repeated " + order_no);
                                        break;
                                    }
                                }
                            }
                            if (canvas.insert_candidate_order_no > -1) {
                                Cursor cursor = new Cursor(Cursor.NW_RESIZE_CURSOR);
                                canvas.setCursor(cursor);
                                canvas.setVisible(true);
                            } else {
                                Cursor cursor = new Cursor(Cursor.DEFAULT_CURSOR);
                                canvas.setCursor(cursor);
                                canvas.setVisible(true);
                            }
                        }
                    } else {
                        Cursor cursor = new Cursor(Cursor.DEFAULT_CURSOR);
                        canvas.setCursor(cursor);
                        canvas.setVisible(true);
                        canvas.cutting_XY.x = -1;
                        canvas.cutting_XY.y = -1;
                    }
                } else {
                    Cursor cursor = new Cursor(Cursor.DEFAULT_CURSOR);
                    canvas.setCursor(cursor);
                    canvas.setVisible(true);
                    canvas.cutting_XY.x = -1;
                    canvas.cutting_XY.y = -1;
                }
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isShiftDown()) {
                    canvas.set_selected_order(e.getPoint());
                    canvas.shift_down = true;
                    canvas.ctrl_down = false;
                } else if (e.isControlDown()) {
                    canvas.set_selected_order(e.getPoint());
                    canvas.ctrl_down = true;
                    canvas.shift_down = false;
                } else {
                    if(canvas.cutting_XY.x > -1){
                        int split_order = updateAssembly();
                        canvas.clear();
                        repaint();
                        history.push(new History(3, split_order, -1));
                    }else if ((canvas.insert_candidate_order_no > -1) && (canvas.selected_order > Integer.MIN_VALUE)){
                        int selected_next_order = -1;
                        boolean found = false;
                        if (canvas.shift_down) {
                            for (int chrom_no : canvas.assembly_chr_order_list.keySet()) {
                                if (selected_next_order > -1) {
                                    break;
                                }
                                if(found){//last previous chrom
                                    selected_next_order = canvas.assembly_chr_order_list.get(chrom_no).get(0);
                                    break;
                                }
                                for (int order_no : canvas.assembly_chr_order_list.get(chrom_no)) {
                                    if (found) {
                                        selected_next_order = order_no;
                                        break;
                                    }
                                    if (order_no == canvas.selected_order) {
                                        found = true;
                                    }
                                }
                            }
                            if (found && (selected_next_order == -1)) {//select one is the very end
                                selected_next_order = Integer.MAX_VALUE;
                            }
                            history.push(new History(1, canvas.selected_order, selected_next_order));
                        }else if(canvas.ctrl_down){
                            for (int chrom_no : canvas.assembly_chr_order_list.keySet()) {
                                if(found){
                                    selected_next_order = canvas.assembly_chr_order_list.get(chrom_no).get(0);
                                    break;
                                }
                                if(canvas.assembly_chr_order_list.get(chrom_no).contains(canvas.selected_order)){
                                    found = true;
                                }
                            }
                            if (found && (selected_next_order == -1)) {//select one is the very end
                                selected_next_order = Integer.MAX_VALUE;
                            }
                            History h = new History(2, canvas.selected_order, selected_next_order);
                            for (int chr_no : canvas.assembly_chr_order_list.keySet()) {
                                if (canvas.assembly_chr_order_list.get(chr_no).contains(canvas.selected_order)) {
                                    h.add_fragments_moved(chr_no, canvas.assembly_chr_order_list.get(chr_no));
                                    break;
                                }
                            }
                            history.push(h);
                        }else{
                            System.err.println("shift or ctrl should have been pressed");
                        }
                        move_fragment(canvas.selected_order, canvas.insert_candidate_order_no, null);
                        canvas.clear();
                        repaint();
                    }
                    if (e.getModifiersEx() == MouseEvent.BUTTON3_DOWN_MASK && e.getClickCount() == 1) {
                        // whatever
                        if (e.isPopupTrigger()) {
                            popup.show(e.getComponent(), e.getX(), e.getY());
                        }
                    }
                }
                origin = new Point(e.getPoint());

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (origin != null) {
                    JViewport viewPort = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, canvas);
                    if (viewPort != null) {
                        int deltaX = origin.x - e.getX();
                        int deltaY = origin.y - e.getY();
                        Rectangle view = viewPort.getViewRect();
                        view.x += deltaX;
                        view.y += deltaY;
                        canvas.scrollRectToVisible(view);
                    }
                }
            }
        };
    }
    private void combinAssembly(int selected_order){
        SupperScaffold a1 = canvas.assembly_order_map.get(selected_order);
        SupperScaffold a2 = canvas.assembly_order_map.get(selected_order + 1);
        SupperScaffold combined_fragment = a1.combin_fragment(a2);
        
        TreeMap<Integer, SupperScaffold> tmp_assembly_map = new TreeMap<>();
        tmp_assembly_map.putAll(canvas.assembly_order_map);
        canvas.assembly_order_map.clear();

        TreeMap<Integer, Integer> tmp_assembly_block_direction = new TreeMap<>();
        tmp_assembly_block_direction.putAll(canvas.assembly_block_direction);
        canvas.assembly_block_direction.clear();
        
        for (int order : tmp_assembly_map.descendingKeySet()) {
            SupperScaffold SS = tmp_assembly_map.get(order);
            if (order < selected_order) {
                canvas.assembly_order_map.put(order, SS);
                canvas.assembly_block_direction.put(order, tmp_assembly_block_direction.get(order));
            }else if(order == selected_order) {
                canvas.assembly_order_map.put(order, combined_fragment);
                canvas.assembly_block_direction.put(order, tmp_assembly_block_direction.get(order));
            }else if(order == selected_order + 1) {
                //do nothing
            }else{
                canvas.assembly_order_map.put(order - 1, SS);
                canvas.assembly_block_direction.put(order - 1, tmp_assembly_block_direction.get(order));
            }
        }
        TreeMap<Integer, ArrayList<Integer>> tmp_assembly_chr_order_list = new TreeMap();
        for(int chrom : canvas.assembly_chr_order_list.keySet()){
            ArrayList<Integer> t = new ArrayList<>();
            t.addAll(canvas.assembly_chr_order_list.get(chrom));
            tmp_assembly_chr_order_list.put(chrom, t);
        }
        canvas.assembly_chr_order_list.clear();
        for (int chrom : tmp_assembly_chr_order_list.keySet()) {
            ArrayList<Integer> t = new ArrayList<>();
            for (int order_idx = 0; order_idx < tmp_assembly_chr_order_list.get(chrom).size(); order_idx++) {
                int order = tmp_assembly_chr_order_list.get(chrom).get(order_idx);
                if(order < selected_order){
                    t.add(order);
                }else if(order > selected_order){
                    t.add(order - 1);
                }
            }
            canvas.assembly_chr_order_list.put(chrom, t);
        }
    }
    
    private int updateAssembly() {
        int display_split_pos = (canvas.cutting_XY.x + canvas.cutting_XY.y) / 2;
        long genome_split_pos = pix2Genome(display_split_pos);
        TreeMap<Integer, SupperScaffold> tmp_assembly_map = new TreeMap<>();
        tmp_assembly_map.putAll(canvas.assembly_order_map);
        canvas.assembly_order_map.clear();
        int split_order = -1;
        String[] split_superScafold_ID = null;
        int split_fragment = -1;
        for (int order : tmp_assembly_map.keySet()) {
            SupperScaffold SS = tmp_assembly_map.get(order);
            if ((display_split_pos > SS.display_start) && (display_split_pos < SS.display_start + SS.display_size)) {
                split_order = order;
                split_superScafold_ID = SS.scafold_fragment_subType_ID.split("fragment_");
                split_fragment = get_fragment_no(SS.scafold_fragment_subType_ID);
            }
        }
        
        for (int order : tmp_assembly_map.descendingKeySet()) {
            SupperScaffold SS = tmp_assembly_map.get(order);
            if (order < split_order) {
                canvas.assembly_order_map.put(order, SS);
                canvas.assembly_block_direction.put(order, canvas.assembly_block_direction.get(order));
            }else if(order == split_order) {
                int strand = canvas.assembly_block_direction.get(order);
                SupperScaffold new_ss = SS.split(genome_split_pos, display_split_pos, strand);
                if (strand > 0) {
                    canvas.assembly_order_map.put(order, SS);
                    canvas.assembly_order_map.put(order + 1, new_ss);
                    canvas.assembly_block_direction.put(order, canvas.assembly_block_direction.get(order));
                    canvas.assembly_block_direction.put(order + 1, canvas.assembly_block_direction.get(order));
                } else {
                    SS.scafold_fragment_subType_ID = SS_ID_increase_1(SS.scafold_fragment_subType_ID);
                    canvas.assembly_order_map.put(order + 1, SS);
                    canvas.assembly_block_direction.put(order + 1, canvas.assembly_block_direction.get(order));
                    new_ss.scafold_fragment_subType_ID = SS_ID_decrease_1(new_ss.scafold_fragment_subType_ID);
                    canvas.assembly_order_map.put(order, new_ss);
                    canvas.assembly_block_direction.put(order, canvas.assembly_block_direction.get(order));
                }
                boolean found = false;
                for (int chrom : canvas.assembly_chr_order_list.keySet()) {
                    for (int chrom_order = 0; chrom_order < canvas.assembly_chr_order_list.get(chrom).size(); chrom_order++) {
                        if (canvas.assembly_chr_order_list.get(chrom).get(chrom_order) == order) {
                            if (strand > 0) {
                                canvas.assembly_chr_order_list.get(chrom).add(chrom_order + 1, order + 1);
                            } else {
                                canvas.assembly_chr_order_list.get(chrom).add(chrom_order, order + 1);
                            }
                            found = true;
                            break;
                        }
                    }
                    if (found) {
                        break;
                    }
                }
            }else{
                String[] tt = SS.scafold_fragment_subType_ID.split("fragment_");
                if(split_superScafold_ID[0].equals(tt[0])){
                    if(get_fragment_no(SS.scafold_fragment_subType_ID) > split_fragment){
                        SS.scafold_fragment_subType_ID = SS_ID_increase_1(SS.scafold_fragment_subType_ID);
                    }
//                    String[] ttt = tt[1].split(":::");
//                    if (Integer.parseInt(ttt[0]) > split_fragment) {
//                        SS.scafold_fragment_subType_ID = tt[0] + "fragment_" + Integer.toString(Integer.parseInt(ttt[0]) + 1);
//                        if (ttt.length > 1) {
//                            SS.scafold_fragment_subType_ID += ":::" + ttt[1];
//                        }
//                    }
                }
                canvas.assembly_order_map.put(order + 1, SS);
                canvas.assembly_block_direction.put(order + 1, canvas.assembly_block_direction.get(order));                
            }
        }
        for (int chrom : canvas.assembly_chr_order_list.keySet()) {
            for (int order_idx = 0; order_idx < canvas.assembly_chr_order_list.get(chrom).size(); order_idx++) {
                int order = canvas.assembly_chr_order_list.get(chrom).get(order_idx);
                if (order > split_order + 1) {
                    canvas.assembly_chr_order_list.get(chrom).set(order_idx,
                            canvas.assembly_chr_order_list.get(chrom).get(order_idx) + 1);
                }
            }
        }
        return split_order;
//        writeAssembly("assembly_fn");
    }
    
    public int get_fragment_no(String superScafold_ID){
        return Integer.parseInt(superScafold_ID.split("fragment_")[1].split(":::")[0]); 
    }
    
    public static String SS_ID_increase_1(String superScafold_ID) {
        String[] tt = superScafold_ID.split("fragment_");
        String[] ttt = tt[1].split(":::");
        if (ttt.length > 1) {
            return tt[0] + "fragment_" + Integer.toString(Integer.parseInt(ttt[0]) + 1)+":::" + ttt[1];
        }else{
            return tt[0] + "fragment_" + Integer.toString(Integer.parseInt(ttt[0]) + 1);
        }
    }
    private String SS_ID_decrease_1(String superScafold_ID) {
        String[] tt = superScafold_ID.split("fragment_");
        String[] ttt = tt[1].split(":::");
        if (ttt.length > 1) {
            return tt[0] + "fragment_" + Integer.toString(Integer.parseInt(ttt[0]) - 1)+":::" + ttt[1];
        }else{
            return tt[0] + "fragment_" + Integer.toString(Integer.parseInt(ttt[0]) - 1);
        }
    }
    
    private void writeAssembly(String assembly_fn){
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(assembly_fn));
            for (int order : canvas.assembly_order_map.keySet()) {
                SupperScaffold ss = canvas.assembly_order_map.get(order);
                bw.write(ss.scafold_fragment_subType_ID + " " + order + " " + ss.size_in_superscaffold + "\n");
            }
            for(int chrom : canvas.assembly_chr_order_list.keySet()){
                bw.write(Integer.toString(canvas.assembly_block_direction.get(canvas.assembly_chr_order_list.get(chrom).get(0)) *
                        canvas.assembly_chr_order_list.get(chrom).get(0)));
                for(int order_idx = 1; order_idx < canvas.assembly_chr_order_list.get(chrom).size(); order_idx++){
                    int order = canvas.assembly_chr_order_list.get(chrom).get(order_idx);
                    bw.write(" "+canvas.assembly_block_direction.get(order) * order);
                }
                bw.write("\n");
            }
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(Citrus.class.getName()).log(Level.SEVERE, null, ex);
        }
      
    }
    
    void create_popup_menu() {
        popup = new JPopupMenu();

        JMenuItem menuItem_undo = new JMenuItem("undo");
        menuItem_undo.setMnemonic(KeyEvent.VK_F);
        menuItem_undo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                undo_proc(e);
            }
        });
        popup.add(menuItem_undo);

        JMenuItem menuItem1 = new JMenuItem("transpose");
        menuItem1.setMnemonic(KeyEvent.VK_F);
        menuItem1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                transpose_proc(e);
            }
        });
        popup.add(menuItem1);
        
        popup.addSeparator();
        JMenuItem menuItem = new JMenuItem("give me sequence");
        menuItem.setMnemonic(KeyEvent.VK_F);
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               get_sequence_proc(e);
            }
        });
        dialog_sequence.setModal(true);
        JScrollPane scrollPane = new JScrollPane(pop_jTextArea);
        dialog_sequence.add(scrollPane);
        dialog_sequence.setSize(new Dimension(350, 350));
        popup.add(menuItem);
        
        JMenuItem meta_menu = new JMenuItem("give me metadata ");
        meta_menu.setMnemonic(KeyEvent.VK_F);
        meta_menu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                get_meta_data(e);

            }
        });
        popup.add(meta_menu);

    }
    
//    void assembly_fragment_transpose(int selected_order){
//        for (int chrom : canvas.assembly_chr_order_list.keySet()){
//            int idx = 0;
//            for (int order : canvas.assembly_chr_order_list.get(chrom)){
//                if (order == selected_order){
//                    canvas.assembly_chr_order_list.get(chrom).set(idx, -canvas.assembly_chr_order_list.get(chrom).get(idx));
//                    return;
//                }
//                idx++;
//            }
//        }
//    }
    
    void assembly_fragment_move(int selected_order, int insert_candidate_order_no, Map<Integer, List<Integer>> fragments_moved) {
        if (canvas.shift_down) {
            for (int chrom : canvas.assembly_chr_order_list.keySet()) {
                int idx = 0;
                boolean found = false;
                for (int order : canvas.assembly_chr_order_list.get(chrom)) {
                    if (order == selected_order) {
                        canvas.assembly_chr_order_list.get(chrom).remove(idx);
                        found = true;
                        break;
                    }
                    idx++;
                }
                if (found) {
                    break;
                }
            }
            if(insert_candidate_order_no < Integer.MAX_VALUE) {
                for (int chrom : canvas.assembly_chr_order_list.keySet()) {
                    int idx = 0;
                    boolean found = false;
                    for (int order : canvas.assembly_chr_order_list.get(chrom)) {
                        if (order == insert_candidate_order_no) {
//                if(Math.abs(order) == Math.abs(insert_candidate_order_no)){
                            canvas.assembly_chr_order_list.get(chrom).add(idx, selected_order);
                            break;
                        }
                        idx++;
                    }
                    if (found) {
                        break;
                    }
                }
            } else{//very last elemnt
                canvas.assembly_chr_order_list.get(canvas.assembly_chr_order_list.lastKey()).add(selected_order);
            }
        } else if (canvas.ctrl_down) {
            List<Integer> chr_orders = new ArrayList<>();
            for (int chrom : canvas.assembly_chr_order_list.keySet()) {
                if(canvas.assembly_chr_order_list.get(chrom).contains(selected_order)){
                    chr_orders.addAll(canvas.assembly_chr_order_list.get(chrom));
                    canvas.assembly_chr_order_list.remove(chrom);
                    break;
                }
            }
            if(insert_candidate_order_no < Integer.MAX_VALUE) {
                for (int chrom : canvas.assembly_chr_order_list.keySet()) {
                    int idx = 0;
                    boolean found = false;
                    for (int order : canvas.assembly_chr_order_list.get(chrom)) {
                        if (order == insert_candidate_order_no) {
                            for (int removed_order : chr_orders) {
                                canvas.assembly_chr_order_list.get(chrom).add(idx, removed_order);
                                idx++;
                            }
                            break;
                        }
                        idx++;
                    }
                    if (found) {
                        break;
                    }
                }
            } else {//very last elemnt
                for (int removed_order : chr_orders) {
                    canvas.assembly_chr_order_list.get(canvas.assembly_chr_order_list.lastKey()).add(removed_order);
                }
            }
        } else {//from undo for chromo move
            for (int removed_chr_no : fragments_moved.keySet()) {
                for (int chr_no : canvas.assembly_chr_order_list.keySet()) {
                    if (canvas.assembly_chr_order_list.get(chr_no).containsAll(fragments_moved.get(removed_chr_no))) {
                        for (int order : fragments_moved.get(removed_chr_no)) {
                            for (int idx = 0; idx < canvas.assembly_chr_order_list.get(chr_no).size(); idx++) {
                                if (canvas.assembly_chr_order_list.get(chr_no).get(idx) == order) {
                                    canvas.assembly_chr_order_list.get(chr_no).remove(idx);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            for (int chr_no : fragments_moved.keySet()) {
                List<Integer> tmp = new ArrayList<>();
                tmp.addAll(fragments_moved.get(chr_no));
                canvas.assembly_chr_order_list.put(chr_no, tmp);
            }
//            for (int chrom : canvas.assembly_chr_order_list.keySet()) {
//                int idx = 0;
//                for (int order : canvas.assembly_chr_order_list.get(chrom)) {
//                    if (order == insert_candidate_order_no) {
//                        for (int removed_order : fragments_moved) {
//                            canvas.assembly_chr_order_list.get(chrom).add(idx, removed_order);
//                            idx++;
//                        }
//                        break;
//                    }
//                    idx++;
//                }
//            }
        }
    }

    void write_Hic(int solu) {
        numContact_X_Y_per_resolution_str dat = Hic_data.get(chr_chr).get(solu);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("aab." + solu + ".txt"));
            for (int block_no : dat.contactRec_per_block.keySet()) {
                bw.write("block_no=" + block_no + "\n");
                for (ContactRecord rec : dat.contactRec_per_block.get(block_no)){
                     bw.write(rec.binX + "\t" + rec.binY + "\t" + rec.counts + "\n");
                }
//                for (int binY = block_no * dat.blockBinCount; binY < block_no * (dat.blockBinCount + 1); binY++) {
//                    for (int binX = block_no * dat.blockBinCount; binX < binY; binX++) {
//                        bw.write(binX + "\t" + binY + "\t" + dat.contactRec_per_block.get(block_no)[binX][binY] + "\n");
//                    }
//                }
            }
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(Citrus.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static String reverseComplement(final String seq) {
        final StringBuilder sb = new StringBuilder();
        for (int i = seq.length() - 1; i >= 0; i--) {
            final char c = seq.charAt(i);
            switch (c) {
            case 'a':
                sb.append('t');
                break;
            case 'A':
                sb.append('T');
                break;
            case 'c':
                sb.append('g');
                break;
            case 'C':
                sb.append('G');
                break;
            case 'g':
                sb.append('c');
                break;
            case 'G':
                sb.append('C');
                break;
            case 't':
                sb.append('a');
                break;
            case 'T':
                sb.append('A');
                break;
            case '-':
                break;
            default: // n
                sb.append(c);
                break;
            }
        }
        return sb.toString();
    }

    private void readIn_Fasta(){
        try {
            String hic_fn = Hic_fn_jTextField.getText();
            fasta_fn = hic_fn.substring(0, hic_fn.lastIndexOf(".")) + ".fasta";
            if (new File(fasta_fn).exists()) {
                BufferedReader br = new BufferedReader(new FileReader(fasta_fn));
                String line = br.readLine();
                String ID = line;
                StringBuilder sb = new StringBuilder();
                canvas.fasta.clear();
                while ((line = br.readLine()) != null) {
                    if (line.startsWith(">")) {
                        canvas.fasta.put(ID, new StringBuilder(sb));
                        System.out.println(ID + " len="+sb.length());
                        sb.setLength(0);
                        ID = line;
                    } else {
                        sb.append(line);
                    }
                }
//                        canvas.fasta.get(">Super-Scaffold_100001").toString().length();
                canvas.fasta.put(ID, sb);
                System.out.println(ID + " len="+sb.length());
                br.close();
            } else {
                JOptionPane.showMessageDialog(null, fasta_fn+" does not exist");
            }
        } catch (IOException ex) {
            Logger.getLogger(Citrus.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    private void readIn_Assembly(){
        try {
            String hic_fn = Hic_fn_jTextField.getText();
            Assembly_fn = hic_fn.substring(0, hic_fn.lastIndexOf(".")) + ".assembly";
            if (new File(Assembly_fn).exists()) {
                BufferedReader br = new BufferedReader(new FileReader(Assembly_fn));
                String line = br.readLine();
                SupperScaffold super_tmp = new SupperScaffold(line);
                canvas.assembly_order_map.clear();
                canvas.assembly_chr_order_list.clear();
                String[] strarray = line.split(" ");
                int order = Integer.parseInt(strarray[1]);
                canvas.assembly_order_map.put(order, super_tmp);
                String current_scaffold = super_tmp.scafold_fragment_subType_ID.split(":::")[0];
                int current_start = super_tmp.size_in_superscaffold;
                int chr_no = 0;
                int Hic_order = -1;//need to be removed
                while ((line = br.readLine()) != null) {
                    if (line.startsWith(">")) {
                        if (line.startsWith(">")) {
                            super_tmp = new SupperScaffold(line);
                            strarray = line.split(" ");
                            order = Integer.parseInt(strarray[1]);
                            if (super_tmp.scafold_fragment_subType_ID.toLowerCase().startsWith(">hic_gap")) {
                                Hic_order = order;
                            } else {
                                canvas.assembly_order_map.put(order, super_tmp);
                                if (current_scaffold.equals(super_tmp.scafold_fragment_subType_ID.split(":::")[0])) {
                                    super_tmp.start_in_superscaffold = current_start;
                                } else {
                                    current_scaffold = super_tmp.scafold_fragment_subType_ID.split(":::")[0];
                                    current_start = 0;
                                }
                                current_start += super_tmp.size_in_superscaffold;
                            }
                        }
                    } else {
                        strarray = line.split(" ");
                        ArrayList<Integer> tmp = new ArrayList<>();
                        for (String s : strarray) {
                            int block_no = Integer.parseInt(s);
                            if (block_no != Hic_order) {
                                tmp.add(Math.abs(block_no));
                                if (block_no > 0) {
                                    canvas.assembly_block_direction.put(Math.abs(block_no), 1);
                                } else {
                                    canvas.assembly_block_direction.put(Math.abs(block_no), -1);
                                }
                            }
                        }
                        canvas.assembly_chr_order_list.put(chr_no, tmp);
                        chr_no++;
                    }
                }
                br.close();
            } else {
                JOptionPane.showMessageDialog(null, Assembly_fn + " does not exist");
            }
        } catch (IOException ex) {
            Logger.getLogger(Citrus.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
//    private int genome2pix(long genome_position){
//        return canvas.MARGIN+(int) (genome_position / canvas.scale / 2);
//    }
    
    private long pix2Genome(int pis_pos){
        return (pis_pos - canvas.MARGIN) * canvas.bpBinSize * 1L;
    }
    
    void adjust_bpBinSizes() {
        long total_genome_size = 0;
        for (int superfold : canvas.assembly_order_map.keySet()) {
            total_genome_size += canvas.assembly_order_map.get(superfold).size_in_superscaffold;
        }
        double ratio_assembly_Hic = (HH.getChr("assembly").getLength() * 1000000L / total_genome_size) / 1000000.0;
        for (int i = 0; i < HH.bpBinSizes.length; i++) {
            HH.bpBinSizes[i] = (int) (HH.bpBinSizes[i] / ratio_assembly_Hic);
        }
    }
    
    private void readIn_Hic(){
        Hic_fn = Hic_fn_jTextField.getText();
        try {
            HH = new Hic_header(Hic_fn);
            adjust_bpBinSizes();
            
            Matrix m = new Matrix(Hic_fn, HH.getVersion(), HH.getMasterIndex(), HH.getChromosomes());
            Hic_data.clear();
            for (String key : HH.getMasterIndex().keySet()) {
                Map<Integer, numContact_X_Y_per_resolution_str> ret = m.readMatrix(key, max_resolution);
                Hic_data.put(key, ret);
            }
            m.close();
            DefaultComboBoxModel model = (DefaultComboBoxModel) resolu_combobox.getModel();
            // removing old data
            model.removeAllElements();
            int jcombobox_idx = 0;
            for (int i = 0; (i < HH.bpBinSizes.length) && (i < max_resolution); i++) {
                if (HH.getChr("assembly").getLength() * 1.0 / HH.bpBinSizes[i] < jScrollPane_size) {
                    jcombobox_idx = i;
                }
                String item = String.format("%,d", HH.bpBinSizes[i]) + " BP";
                model.addElement(item);
            }
            
            // setting model with new data
            resolu_combobox.setModel(model);
            resolu_combobox.setSelectedIndex(jcombobox_idx);
            resolution = resolu_combobox.getSelectedIndex();

//            write_Hic(0);
        } catch (IOException ex) {
            Logger.getLogger(Citrus.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void changeColor() {
//        Map<Integer, BufferedImage> buffered_images_1 = new TreeMap();
        for (int block_no : canvas.buffered_images.keySet()) {
            BufferedImage imag = canvas.buffered_images.get(block_no);
            ((MyBufferedImage)imag).set_color(color_range_slide_value);
//            for (int y = 0; y < imag.getHeight(); y++) {
//                int max_x = imag.getWidth();
//                if ((block_no == 0) || ((block_no + 1) % Hic_data.get(chr_chr).get(resolution).blockColumnCount == 0)) {//diagnose
//                    max_x = y;
//                }
//                for (int x = 0; x < max_x; x++) {
//                    int p = imag.getRGB(x, y);
//                    float[] hsbvals = new float[3];
//                    int r = (p >> 16) & 0xff;
//                    int g = (p >> 8) & 0xff;
//                    int b = p & 0xff;
//                    java.awt.Color.RGBtoHSB((p >> 16) & 0xff, (p >> 8) & 0xff, p & 0xff, hsbvals);
//
//                    hsbvals[1] *= 0.9;
//                    int p1 = java.awt.Color.HSBtoRGB(hsbvals[0], hsbvals[1], hsbvals[2]);
//                    imag.setRGB(x, y, p1);                    
//                }
//            }
        }
        canvas.repaint();
    }

    void undo_proc(ActionEvent e){
        canvas.shift_down = false;
        canvas.ctrl_down = false;//unclear for move_fragment()
        if (!history.empty()) {
            History last_one = history.pop();
            if (last_one.type == 0) {
                int selected_order = last_one.selected_order;
                transpose_fragment(selected_order, null);
                canvas.clear();
                repaint();
            } else if (last_one.type == 10) {
                int selected_order = last_one.selected_order;
                transpose_fragment(selected_order, last_one.fragments_moved);
                canvas.clear();
                repaint();
            } else if (last_one.type == 1) {
                int selected_order = last_one.selected_order;
                int selected_next_order = last_one.selected_next_order;
                canvas.shift_down = true;
                move_fragment(selected_order, selected_next_order, null);
                canvas.clear();
                repaint();
            } else if (last_one.type == 2) {
                int selected_order = last_one.selected_order;
                int selected_next_order = last_one.selected_next_order;
                move_fragment(selected_order, selected_next_order, last_one.fragments_moved);
                canvas.clear();
                repaint();
            }else if (last_one.type == 3){
                combinAssembly(last_one.selected_order);
                canvas.clear();
                repaint();
            }
        }
    }
    
    
    void transpose_proc(ActionEvent e) {
        if(canvas.shift_down){
            history.add(new History(0, canvas.selected_order));
            transpose_fragment(canvas.selected_order, null);
        } else if (canvas.ctrl_down) {
            History h = new History(10, canvas.selected_order);
            for (int chr_no : canvas.assembly_chr_order_list.keySet()) {
                if (canvas.assembly_chr_order_list.get(chr_no).contains(canvas.selected_order)) {
                    h.add_fragments_moved(chr_no, canvas.assembly_chr_order_list.get(chr_no));
                    break;
                }
            }
            history.push(h);
            transpose_fragment(canvas.selected_order, h.fragments_moved);
        } else {
            System.err.println("fragment or chrom must be selected");
            return;
        }
//        int selected_min_pix_no = canvas.assembly_order_map.get(Math.abs(canvas.selected_order)).display_start;
//        int selected_max_pix_no = selected_min_pix_no + canvas.assembly_order_map.get(Math.abs(canvas.selected_order)).display_size;
//        int insert_pix_no = canvas.assembly_order_map.get(Math.abs(canvas.insert_candidate_order_no)).display_start;
//        assembly_fragment_transpose(canvas.selected_order);
//        transpose_fragment(selected_min_pix_no, selected_max_pix_no);
        canvas.clear();
        repaint();
    }
    
    void get_sequence_proc(ActionEvent e) {
        BufferedWriter bw = null;
        try {
            JFileChooser jfc = new JFileChooser();
            jfc.setSelectedFile(new File("HiC_scaffold_1.fasta"));
            jfc.setFileFilter(new javax.swing.filechooser.FileFilter() {
                public boolean accept(File f) {
                    return f.getName().toLowerCase().endsWith(".fasta") || f.isDirectory();
                }
                public String getDescription() {
                    return "fasta Files";
                }
            });
            if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                File file = jfc.getSelectedFile();
                bw = new BufferedWriter(new FileWriter(file.getAbsolutePath()));
                String pure_fn = file.getName();
                String fasta_ID;
                if(pure_fn.contains(".")){
                    fasta_ID = ">"+pure_fn.substring(0, pure_fn.lastIndexOf("."));
                }else{
                    fasta_ID = ">"+pure_fn;
                }                
                bw.write(fasta_ID+"\n");
                ArrayList<Integer> selected_all_order_no = new ArrayList<>();
                if (canvas.shift_down) {
                    selected_all_order_no.add(canvas.selected_order);
                } else if (canvas.ctrl_down) {
                    for (int chr_no : canvas.assembly_chr_order_list.keySet()) {
                        if (canvas.assembly_chr_order_list.get(chr_no).contains(canvas.selected_order)) {
                            selected_all_order_no.addAll(canvas.assembly_chr_order_list.get(chr_no));
                        }
                    }
                }
                StringBuilder sb = new StringBuilder();
                StringBuilder remaining = new StringBuilder();
                boolean first_fragment = true;
                for (int order_no : selected_all_order_no) {
//                int abs_order = Math.abs(order_no);
                    if (canvas.assembly_order_map.get(order_no) == null) {
                        pop_jTextArea.setText(order_no + " does not exist");
//                    pop_jTextArea.setText(canvas.assembly_fragments.get(canvas.selectedFragment).toString().replace(" ", "\n"));
                        dialog_sequence.setPreferredSize(new Dimension(350, 350));
                        dialog_sequence.setVisible(true);
                        return;
                    }
                    String ID = canvas.assembly_order_map.get(order_no).scafold_fragment_subType_ID;
                    if (sb.length() < 1000) {
                        sb.append(ID);
                        sb.append(" ").append(order_no).append("\n");
                    }
//                System.out.println(ID);
                    int start_pos = canvas.assembly_order_map.get(order_no).start_in_superscaffold;
                    int stop_pos = start_pos + canvas.assembly_order_map.get(order_no).size_in_superscaffold;
                    StringBuilder superScaffold_seq = canvas.fasta.get(ID.split(":::")[0]);//superScaffold_seq.length();canvas.fasta.get(">Super-Scaffold_100002").toString().length()
                    if (!first_fragment) {
                        remaining = remaining.append(Strings.repeat("N", 500));
                    }else{
                        first_fragment = false;
                    }
                    if (canvas.assembly_block_direction.get(order_no) > 0) {
                        remaining = remaining.append(superScaffold_seq.substring(start_pos, stop_pos));
                    } else {
                        StringBuilder tmp = new StringBuilder(superScaffold_seq.substring(start_pos, stop_pos));
                        tmp = new StringBuilder(tmp.reverse().toString().replace('A', '@').replace('T', 'A').replace('@', 'T').replace('G', '$').replace('C', 'G').replace('$', 'C'));
                        remaining = remaining.append(tmp);
                    }
//                bw.write(ID + ":" + order_no +" strand:" + canvas.assembly_block_direction.get(order_no) + "\n");
                    int i = 0;
                    for (; i < remaining.length() - 80; i = i + 80) {
                        String str = remaining.substring(i, i + 80);
                        if (sb.length() < 1000) {
                            sb.append(str).append("\n");
                        }
                        bw.write(str + "\n");
                    }
                    remaining = new StringBuilder(remaining.substring(i));
                }
                bw.write(remaining + "\n");
                bw.close();
                pop_jTextArea.setText(sb.toString());
                //                    pop_jTextArea.setText(canvas.assembly_fragments.get(canvas.selectedFragment).toString().replace(" ", "\n"));
                dialog_sequence.setPreferredSize(new Dimension(350, 350));
                dialog_sequence.setVisible(true);

            }
        } catch (IOException ex) {
            Logger.getLogger(Citrus.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                bw.close();
            } catch (IOException ex) {
                Logger.getLogger(Citrus.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    void get_meta_data(ActionEvent e) {
        ArrayList<Integer> selected_all_order_no = new ArrayList<>();
        if (canvas.shift_down) {
            selected_all_order_no.add(canvas.selected_order);
        } else if (canvas.ctrl_down) {
            for (int chr_no : canvas.assembly_chr_order_list.keySet()) {
                if (canvas.assembly_chr_order_list.get(chr_no).contains(canvas.selected_order)) {
                    selected_all_order_no.addAll(canvas.assembly_chr_order_list.get(chr_no));
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        for (int order_no : selected_all_order_no) {
//            int abs_order = Math.abs(order_no);
            if (canvas.assembly_order_map.get(order_no) == null) {
                pop_jTextArea.setText(order_no + " does not exist");
//                    pop_jTextArea.setText(canvas.assembly_fragments.get(canvas.selectedFragment).toString().replace(" ", "\n"));
                dialog_sequence.setPreferredSize(new Dimension(350, 350));
                dialog_sequence.setVisible(true);
                return;
            }
            String ID = canvas.assembly_order_map.get(order_no).scafold_fragment_subType_ID;
            sb.append(ID);
            sb.append(" ").append(order_no).append("\n");
            sb.append(" strand=").append(canvas.assembly_block_direction.get(order_no)).append("\n");
            
            sb.append(canvas.assembly_order_map.get(order_no).toString().replace(" ", "\n"));
        }
        pop_jTextArea.setText(sb.toString());
        //                    pop_jTextArea.setText(canvas.assembly_fragments.get(canvas.selectedFragment).toString().replace(" ", "\n"));
        dialog_sequence.setPreferredSize(new Dimension(350, 350));
        dialog_sequence.setVisible(true);
    }

    contact_position pix2blockPix(numContact_X_Y_per_resolution_str dat, int x_pix_no, int y_pix_no) {
        int block_no = (y_pix_no / dat.blockBinCount) * dat.blockColumnCount + x_pix_no / dat.blockBinCount;
        int x = x_pix_no - (block_no % dat.blockColumnCount) * dat.blockBinCount;
        int y = y_pix_no - (block_no / dat.blockColumnCount) * dat.blockBinCount;
//        if ((x < 0) || (y < 0)){
//            int debug = 0;
//        }
        return new contact_position(block_no, x, y);
    }

    List<binX_range> get_binX_range_old(numContact_X_Y_per_resolution_str dat, int binY_pos, int binX_start, int binX_stop){
        List<binX_range> ret = new ArrayList<>();
        int block_no_start = (binY_pos / dat.blockBinCount) * dat.blockColumnCount + (binX_start / dat.blockBinCount);
        int pos_start = Collections.binarySearch(dat.contactRec_per_block.get(block_no_start), 
                new ContactRecord(binX_start % dat.blockBinCount, binY_pos % dat.blockBinCount, 0));
        int block_no_stop = (binY_pos / dat.blockBinCount) * dat.blockColumnCount + (binX_stop / dat.blockBinCount);
        int pos_stop = Collections.binarySearch(dat.contactRec_per_block.get(block_no_stop), 
                new ContactRecord(binX_stop % dat.blockBinCount, binY_pos % dat.blockBinCount, 0));
        if(block_no_start == block_no_stop){
            ret.add(new binX_range(block_no_start, (pos_start > -1)? pos_start : (-pos_start - 1), 
                    (pos_stop > -1)? pos_stop : (-pos_stop - 1)));
        } else {
            int block_end_pos = Collections.binarySearch(dat.contactRec_per_block.get(block_no_start),
                    new ContactRecord(Integer.MAX_VALUE, binY_pos % dat.blockBinCount, 0));
            ret.add(new binX_range(block_no_start, (pos_start > -1)? pos_start : (-pos_start - 1), (block_end_pos > -1) ? block_end_pos : (-block_end_pos - 1)));
            if((ret.get(ret.size()-1).start_idx < 0) || (ret.get(ret.size()-1).stop_idx < 0)){
                int debug = 0;
            }
            for (int block_no = block_no_start + 1; block_no < block_no_stop; block_no++) {
                pos_start = Collections.binarySearch(dat.contactRec_per_block.get(block_no),
                        new ContactRecord(0, binY_pos % dat.blockBinCount, 0));;
                block_end_pos = Collections.binarySearch(dat.contactRec_per_block.get(block_no),
                        new ContactRecord(Integer.MAX_VALUE, binY_pos % dat.blockBinCount, 0));
                ret.add(new binX_range(block_no, (pos_start > -1) ? pos_start : (-pos_start - 1), (block_end_pos > -1) ? block_end_pos : (-block_end_pos - 1)));
            }
            pos_start = Collections.binarySearch(dat.contactRec_per_block.get(block_no_stop),
                    new ContactRecord(0, binY_pos % dat.blockBinCount, 0));;
            ret.add(new binX_range(block_no_stop, (pos_start > -1) ? pos_start : (-pos_start - 1), (pos_stop > -1) ? pos_stop : (-pos_stop - 1)));
            
            if((ret.get(ret.size()-1).start_idx < 0) || (ret.get(ret.size()-1).stop_idx < 0)){
                int debug = 0;
            }
        }
//        int offset = binX_start % dat.blockBinCount;
//        int block_no = block_no_start;
//        for (; block_no < block_no_stop; block_no++){
//            ret.add(new binX_range(block_no, offset, dat.blockBinCount));
//            offset = 0;
//        }
//        ret.add(new binX_range(block_no, offset, binX_stop % dat.blockBinCount));
        return ret;
    }

    List<binX_range> get_binX_range(numContact_X_Y_per_resolution_str dat, int binY_pos, int binX_start, int binX_stop){
        List<binX_range> ret = new ArrayList<>();
        int block_no_start = (binY_pos / dat.blockBinCount) * dat.blockColumnCount + (binX_start / dat.blockBinCount);
        int pos_start = Collections.binarySearch(dat.contactRec_per_block.get(block_no_start), 
                new ContactRecord(binX_start, binY_pos, 0));
        int block_no_stop = (binY_pos / dat.blockBinCount) * dat.blockColumnCount + (binX_stop / dat.blockBinCount);
        int pos_stop = Collections.binarySearch(dat.contactRec_per_block.get(block_no_stop), 
                new ContactRecord(binX_stop, binY_pos, 0), new cmp_ContactRecord());
        if(block_no_start == block_no_stop){
            int start_idx = (pos_start > -1)? pos_start : (-pos_start - 1);
            int stop_idx = (pos_stop > -1)? pos_stop : (-pos_stop - 1);
            if(stop_idx > start_idx){
                ret.add(new binX_range(block_no_start, start_idx, stop_idx));  //0 start, last one should + 1
            }
            
        } else {
            int block_end_pos //dat.contactRec_per_block.get(block_no_start).size();// 
                    = Collections.binarySearch(dat.contactRec_per_block.get(block_no_start), new ContactRecord(Integer.MAX_VALUE, binY_pos, 0));
            int start_idx = (pos_start > -1)? pos_start : (-pos_start - 1);
            int stop_idx = -block_end_pos - 1;//block_end_pos;
            if(stop_idx > start_idx){
                ret.add(new binX_range(block_no_start, start_idx, stop_idx));  //0 start, last one should + 1
            }
            for (int block_no = block_no_start + 1; block_no < block_no_stop; block_no++) {
                pos_start = Collections.binarySearch(dat.contactRec_per_block.get(block_no),
                        new ContactRecord(0, binY_pos, 0));;
                //block_end_pos = dat.contactRec_per_block.get(block_no_start).size();// Collections.binarySearch(dat.contactRec_per_block.get(block_no), new ContactRecord(Integer.MAX_VALUE, binY_pos, 0));
                block_end_pos = Collections.binarySearch(dat.contactRec_per_block.get(block_no), new ContactRecord(Integer.MAX_VALUE, binY_pos, 0));
                start_idx = (pos_start > -1) ? pos_start : (-pos_start - 1);
                stop_idx = -block_end_pos - 1;//block_end_pos;
                if (stop_idx > start_idx) {
                    ret.add(new binX_range(block_no, start_idx, stop_idx));  //0 start, last one should + 1
                }
//                ret.add(new binX_range(block_no, (pos_start > -1) ? pos_start : (-pos_start - 1), block_end_pos));
            }
            pos_start = Collections.binarySearch(dat.contactRec_per_block.get(block_no_stop),
                    new ContactRecord(0, binY_pos, 0));
            start_idx = (pos_start > -1) ? pos_start : (-pos_start - 1);
            stop_idx = (pos_stop > -1) ? pos_stop : (-pos_stop - 1);
            if(stop_idx > start_idx) {
                ret.add(new binX_range(block_no_stop, start_idx, stop_idx));
            }
            
            
//            if((ret.get(ret.size()-1).start_idx < 0) || (ret.get(ret.size()-1).stop_idx < 0)){
//                int debug = 0;
//            }
        }
//        int offset = binX_start % dat.blockBinCount;
//        int block_no = block_no_start;
//        for (; block_no < block_no_stop; block_no++){
//            ret.add(new binX_range(block_no, offset, dat.blockBinCount));
//            offset = 0;
//        }
//        ret.add(new binX_range(block_no, offset, binX_stop % dat.blockBinCount));
        return ret;
    }
    
    private ContactRecord getContactRecord(numContact_X_Y_per_resolution_str dat, List<binX_range> X_range, int no){
        int rec_no = 0;
        for(int X_range_idx = 0; X_range_idx < X_range.size(); X_range_idx++) {
            for (int idx = X_range.get(X_range_idx).start_idx; idx < X_range.get(X_range_idx).stop_idx; idx++) {
                if (rec_no == no) {
                    return dat.contactRec_per_block.get(X_range.get(X_range_idx).block_no).get(rec_no);
                }
            }
        }
        return null;
    }
    
    private void transpose_fragment(int selected_order, Map<Integer, List<Integer>> fragments_moved) {
//        int solu = Integer.parseInt(resolu.getText());
        for (int solu = 0; solu < Hic_data.get(chr_chr).size(); solu++) {
            int scale = HH.bpBinSizes[solu];

            int selected_min_pix_no, selected_max_pix_no;
            if (canvas.shift_down) {
                Point p = canvas.get_fragment_min_max(selected_order, scale);
                selected_min_pix_no = p.x;
                selected_max_pix_no = p.y;
            } else if (canvas.ctrl_down) {
                Point p = canvas.get_chr_min_max(selected_order, scale);
                selected_min_pix_no = p.x;
                selected_max_pix_no = p.y;
            } else {//from undo
                Point p = canvas.get_chr_min_max(fragments_moved, scale);
                selected_min_pix_no = p.x;
                selected_max_pix_no = p.y;
            }
            transpose_fragment(selected_min_pix_no, selected_max_pix_no, solu);
        }
        if (canvas.shift_down) {
            canvas.assembly_block_direction.put(selected_order, -canvas.assembly_block_direction.get(selected_order));
        }else {
            for(int chrom : fragments_moved.keySet()){//only 
                for(int order : fragments_moved.get(chrom)){
                    canvas.assembly_block_direction.put(order, -canvas.assembly_block_direction.get(order));
                }
            }
        }
    }

    private void transpose_fragment(int selected_min_pix_no, int selected_max_pix_no, int solu) {
        Set<Integer> need_re_sort_block_no = new HashSet();
        ArrayList<List<binX_idx>> need_to_move_other_block = new ArrayList<>();
        List<binX_idx> block_to_move = new ArrayList<>();        
        
        int scale = HH.bpBinSizes[solu];
//        int selected_min_pix_no = canvas.MARGIN + (int)(canvas.assembly_order_map.get(selected_order).genome_position / (scale * 2));
//        int selected_max_pix_no = canvas.MARGIN + (int)(selected_min_pix_no + (canvas.assembly_order_map.get(selected_order).genome_position + 1) / (scale * 2));
//        int insert_pix_no = canvas.MARGIN + (int)(canvas.assembly_order_map.get((insert_candidate_order_no)).genome_position / (scale * 2));

//        int selected_min_pix_no = canvas.MARGIN + (int)(canvas.assembly_order_map.get(selected_order).genome_position / (scale * 2));
//        int selected_max_pix_no = selected_min_pix_no + (int)((canvas.assembly_order_map.get(selected_order).size_in_superscaffold + 1) / (scale * 2));
//        int insert_pix_no = canvas.assembly_order_map.get(Math.abs(canvas.insert_candidate_order_no)).display_start;
//        assembly_fragment_transpose(selected_order);
//        canvas.assembly_block_direction.put(selected_order, canvas.assembly_block_direction.get(selected_order));

        int select_size = selected_max_pix_no - selected_min_pix_no;
        
        numContact_X_Y_per_resolution_str dat = Hic_data.get(chr_chr).get(solu);
        int blockBinCount = Hic_data.get(chr_chr).get(solu).blockBinCount;
        int blockColumnCount = Hic_data.get(chr_chr).get(solu).blockColumnCount;
        int max_plus_min = selected_max_pix_no + selected_min_pix_no;
        List<binX_range>[] tmp1 = new List[selected_max_pix_no - selected_min_pix_no];
        for (int y_pix_no = selected_min_pix_no; y_pix_no < selected_max_pix_no; y_pix_no++) {//1
            List<binX_range> X_range = get_binX_range(dat, y_pix_no, 0, selected_min_pix_no);
            tmp1[y_pix_no - selected_min_pix_no] = X_range;
        }
        
        List<binX_range>[] tmp2 = new List[(selected_max_pix_no - selected_min_pix_no) + (selected_max_pix_no - max_plus_min / 2)];//2
        for (int y_pix_no = selected_min_pix_no; y_pix_no < selected_max_pix_no; y_pix_no++) {//2
            List<binX_range> X_range;
            if(y_pix_no < max_plus_min / 2){
                X_range = get_binX_range(dat, y_pix_no, selected_min_pix_no, y_pix_no);
            }else{
                X_range = get_binX_range(dat, y_pix_no, selected_min_pix_no, max_plus_min - y_pix_no);
            }
            tmp2[y_pix_no - selected_min_pix_no] = X_range;
        }
        for (int y_pix_no = max_plus_min / 2; y_pix_no < selected_max_pix_no; y_pix_no++) {//3
            List<binX_range> X_range;
            X_range = get_binX_range(dat, y_pix_no, max_plus_min - y_pix_no, y_pix_no);
            tmp2[(selected_max_pix_no - selected_min_pix_no) + (y_pix_no - max_plus_min / 2)] = X_range;
        }

//        System.out.println("tmp3");
        List<binX_range>[] tmp3 =  new List[blockBinCount * blockColumnCount -  selected_max_pix_no];//new ArrayList<>();//4
        for (int y_pix_no = selected_max_pix_no; y_pix_no < blockBinCount * blockColumnCount; y_pix_no++) {//1. left horizental rectuangel
            List<binX_range> X_range = get_binX_range(dat, y_pix_no, selected_min_pix_no, selected_max_pix_no);
            tmp3[y_pix_no - selected_max_pix_no] = X_range;
        }
        
        for (List<binX_range> X_range : tmp3) {
            for (binX_range bxr : X_range) {
                for (int idx_X = bxr.start_idx; idx_X < bxr.stop_idx; idx_X++) {
                    int block_no = bxr.block_no;
//                    System.out.print(dat.contactRec_per_block.get(block_no).get(idx_X) + "==>");
//                    need_re_sort_block_no.add(block_no);
                    dat.contactRec_per_block.get(block_no).get(idx_X).binX = max_plus_min
                            - dat.contactRec_per_block.get(block_no).get(idx_X).binX;
                    add_needs_move_other_block(blockBinCount, blockColumnCount, dat, block_no, idx_X, need_re_sort_block_no, need_to_move_other_block, block_to_move);
                }
            }
        }
        
        System.out.println("tmp2");
        for (List<binX_range> X_range : tmp2) {
            for (binX_range bxr : X_range) {
                for (int idx_X = bxr.start_idx; idx_X < bxr.stop_idx; idx_X++) {
                    int block_no = bxr.block_no;
//                    need_re_sort_block_no.add(block_no);
//                    System.out.print(dat.contactRec_per_block.get(block_no).get(idx_X) + "==>");
                    int ss = dat.contactRec_per_block.get(block_no).get(idx_X).binY;
                    dat.contactRec_per_block.get(block_no).get(idx_X).binY = max_plus_min - dat.contactRec_per_block.get(block_no).get(idx_X).binX;
                    dat.contactRec_per_block.get(block_no).get(idx_X).binX = max_plus_min - ss;
                    add_needs_move_other_block(blockBinCount, blockColumnCount, dat, block_no, idx_X, need_re_sort_block_no, need_to_move_other_block, block_to_move);
                }
            }
        }
        
        System.out.println("tmp1");
        for (List<binX_range> X_range : tmp1) {
            for (binX_range bxr : X_range) {
                for (int idx_X = bxr.start_idx; idx_X < bxr.stop_idx; idx_X++) {
                    int block_no = bxr.block_no;
//                    need_re_sort_block_no.add(block_no);
//System.out.print("block_no="+block_no+" idx_X="+idx_X+":"+dat.contactRec_per_block.get(block_no).get(idx_X) + "==>");
                    dat.contactRec_per_block.get(block_no).get(idx_X).binY = max_plus_min
                            - dat.contactRec_per_block.get(block_no).get(idx_X).binY;
                    add_needs_move_other_block(blockBinCount, blockColumnCount, dat, block_no, idx_X, need_re_sort_block_no, need_to_move_other_block, block_to_move);
                }
            }
        }
        if (!block_to_move.isEmpty()) {
            need_to_move_other_block.add(block_to_move);
        }
        move_block_and_reSort(blockBinCount, blockColumnCount, dat, need_re_sort_block_no, need_to_move_other_block);
    }

    

    private void move_block_and_reSort(int blockBinCount, int blockColumnCount, numContact_X_Y_per_resolution_str dat, Set<Integer> need_re_sort_block_no, List<List<binX_idx>> need_to_move_other_block){
        System.out.println("###### move_block_and_reSort ######");
        Collections.sort(need_to_move_other_block, new cmp_binX_idx_list());//sorted in descent order
        int need_to_be_removed_idx_start = -1;
        int need_to_be_removed_idx_stop = -1;
        int current_new_block_no = 0;
        int current_old_block_no = 0;
        List<binX_range> need_to_be_removed = new ArrayList<>();
        List<ContactRecord> rec_list = new ArrayList<>();
        for (List<binX_idx> bi : need_to_move_other_block){
            int block_no = bi.get(0).block_no;
            int idx = bi.get(0).idx;
            int new_block_no = dat.contactRec_per_block.get(block_no).get(idx).binY / blockBinCount * blockColumnCount + dat.contactRec_per_block.get(block_no).get(idx).binX / blockBinCount;
            ContactRecord rec = dat.contactRec_per_block.get(block_no).get(idx);
//System.out.println("old:block_no="+block_no+" new block no="+rec.toString());
            current_new_block_no = new_block_no;
            current_old_block_no = block_no;
            need_to_be_removed_idx_start = idx;
            need_to_be_removed_idx_stop = idx;
            rec_list.add(rec);
            for (int i = 1; i < bi.size(); i++) {
                block_no = bi.get(i).block_no;
                idx = bi.get(i).idx;
                new_block_no = dat.contactRec_per_block.get(block_no).get(idx).binY / blockBinCount * blockColumnCount
                        + dat.contactRec_per_block.get(block_no).get(idx).binX / blockBinCount;
                ContactRecord rec1 = dat.contactRec_per_block.get(block_no).get(idx);
//System.out.println("old:(block_no="+block_no+",idx="+idx+") ==> new:(block_no="+new_block_no+")="+rec1.toString());
                if(current_new_block_no != new_block_no) {
//if(dat.contactRec_per_block.get(current_new_block_no) == null){
//    int debug = 0;
//}                       
                    dat.contactRec_per_block.get(current_new_block_no).addAll(rec_list);
                    need_re_sort_block_no.add(current_new_block_no);
                    need_to_be_removed.add(new binX_range(current_old_block_no, need_to_be_removed_idx_start, need_to_be_removed_idx_stop + 1));
//System.out.println("tobe_removed:block no="+ current_old_block_no+" start="+need_to_be_removed_idx_start+" stop="+Integer.toString(need_to_be_removed_idx_stop+1));
                    current_new_block_no = new_block_no;
                    need_to_be_removed_idx_start = idx;
//System.out.println("current_new_block_no="+current_new_block_no+" "+ rec_list);                 
                    rec_list.clear();
                }
                rec_list.add(rec1);
                need_to_be_removed_idx_stop = idx;
                current_old_block_no = block_no;
            }
            
//if(dat.contactRec_per_block.get(current_new_block_no) == null){
//    int debug = 0;
//}            
            dat.contactRec_per_block.get(current_new_block_no).addAll(rec_list);
            need_re_sort_block_no.add(current_new_block_no);
            need_to_be_removed.add(new binX_range(current_old_block_no, need_to_be_removed_idx_start, need_to_be_removed_idx_stop + 1));
//            System.out.println("tobe_removed:block no=" + current_old_block_no + " start=" + need_to_be_removed_idx_start + " stop=" + Integer.toString(need_to_be_removed_idx_stop + 1));
            current_new_block_no = new_block_no;
            need_to_be_removed_idx_start = idx;
//            System.out.println("current_new_block_no=" + current_new_block_no + " " + rec_list);
            rec_list.clear();
        }
//System.out.println("tobe_removed:block no="+ current_old_block_no+" start="+need_to_be_removed_idx_start+" stop="+Integer.toString(need_to_be_removed_idx_stop+1));
//System.out.println("current_new_block_no="+current_new_block_no+" "+ rec_list);                 
        
//        dat.contactRec_per_block.get(current_new_block_no).addAll(rec_list);
//        need_re_sort_block_no.add(current_new_block_no);
//        need_to_be_removed.add(new binX_range(current_old_block_no, need_to_be_removed_idx_start, need_to_be_removed_idx_stop + 1));
        Collections.sort(need_to_be_removed, new cmp_rev_binX_range());
        for(binX_range br : need_to_be_removed){
            dat.contactRec_per_block.get(br.block_no).subList(br.start_idx, br.stop_idx).clear();//dat.contactRec_per_block.get(2)
//            write_Hic(0);
        }
        
//        {   
//            int new_block_no = rec.binY / blockBinCount * blockColumnCount + rec.binX / blockBinCount;
//            dat.contactRec_per_block.get(new_block_no).add(bi);
//            
//            dat.contactRec_per_block.get(new_block_no).add(new ContactRecord(rec.binX, rec.binY, rec.counts));
//            dat.contactRec_per_block.get(bi.block_no).remove(bi.idx);
//        }
        for(int block_no : need_re_sort_block_no){
            Collections.sort(dat.contactRec_per_block.get(block_no));
        }
//        write_Hic(1);
        int debug = 0;//dat.contactRec_per_block.get(4).get(245071);
//        for(int block_no : dat.contactRec_per_block.keySet()){
//            Collections.sort(dat.contactRec_per_block.get(block_no));            
//        }
     
    }

    private void transpose_fragment_old(int selected_order) {
        Set<Integer> need_re_sort_block_no = new HashSet();
        ArrayList<binX_idx> need_to_move_other_block = new ArrayList<>();
        
        int selected_min_pix_no = canvas.assembly_order_map.get(selected_order).display_start;
        int selected_max_pix_no = selected_min_pix_no + canvas.assembly_order_map.get(selected_order).display_size;
//        int insert_pix_no = canvas.assembly_order_map.get(Math.abs(canvas.insert_candidate_order_no)).display_start;
//        assembly_fragment_transpose(selected_order);
        canvas.assembly_block_direction.put(selected_order, canvas.assembly_block_direction.get(selected_order));

        int select_size = selected_max_pix_no - selected_min_pix_no;
        int solu = resolution;//Integer.parseInt(resolu.getText());
        numContact_X_Y_per_resolution_str dat = Hic_data.get(chr_chr).get(solu);
        int blockBinCount = Hic_data.get(chr_chr).get(solu).blockBinCount;
        int blockColumnCount = Hic_data.get(chr_chr).get(solu).blockColumnCount;
        int max_plus_min = selected_max_pix_no + selected_min_pix_no;
//        List<List> tmp1 = new ArrayList<>();
        List<binX_range>[] tmp1 = new List[selected_max_pix_no - selected_min_pix_no];
        for (int y_pix_no = selected_min_pix_no; y_pix_no < selected_max_pix_no; y_pix_no++) {//1
            List<binX_range> X_range = get_binX_range(dat, y_pix_no, 0, selected_min_pix_no);
            tmp1[y_pix_no - selected_min_pix_no] = X_range;
        }
        
        List<binX_range>[] tmp2 = new List[(selected_max_pix_no - selected_min_pix_no) + (selected_max_pix_no - max_plus_min / 2)];//2
        for (int y_pix_no = selected_min_pix_no; y_pix_no < selected_max_pix_no; y_pix_no++) {//2
            List<binX_range> X_range;
            if(y_pix_no < max_plus_min / 2){
                X_range = get_binX_range(dat, y_pix_no, selected_min_pix_no, y_pix_no);
            }else{
                X_range = get_binX_range(dat, y_pix_no, selected_min_pix_no, max_plus_min - y_pix_no);
            }
            tmp2[y_pix_no - selected_min_pix_no] = X_range;
        }
        for (int y_pix_no = max_plus_min / 2; y_pix_no < selected_max_pix_no; y_pix_no++) {//3
            List<binX_range> X_range;
            X_range = get_binX_range(dat, y_pix_no, max_plus_min - y_pix_no, y_pix_no);
            tmp2[(selected_max_pix_no - selected_min_pix_no) + (y_pix_no - max_plus_min / 2)] = X_range;
        }

//        System.out.println("tmp3");
        List<binX_range>[] tmp3 =  new List[blockBinCount * blockColumnCount -  selected_max_pix_no];//new ArrayList<>();//4
        for (int y_pix_no = selected_max_pix_no; y_pix_no < blockBinCount * blockColumnCount; y_pix_no++) {//1. left horizental rectuangel
            List<binX_range> X_range = get_binX_range(dat, y_pix_no, selected_min_pix_no, selected_max_pix_no);
            tmp3[y_pix_no - selected_max_pix_no] = X_range;
        }
        for (List<binX_range> X_range : tmp3) {
            for (binX_range bxr : X_range) {
                for (int idx_X = bxr.start_idx; idx_X < bxr.stop_idx; idx_X++) {
                    int block_no = bxr.block_no;
//                    System.out.print(dat.contactRec_per_block.get(block_no).get(idx_X) + "==>");
                    need_re_sort_block_no.add(block_no);
                    dat.contactRec_per_block.get(block_no).get(idx_X).binX = max_plus_min
                            - dat.contactRec_per_block.get(block_no).get(idx_X).binX;
//                    System.out.println(dat.contactRec_per_block.get(block_no).get(idx_X));                    
                    if ((dat.contactRec_per_block.get(block_no).get(idx_X).binX < blockBinCount * block_no)
                            || (dat.contactRec_per_block.get(block_no).get(idx_X).binX > blockBinCount * (block_no + 1))) {
                        need_to_move_other_block.add(new binX_idx(block_no, idx_X));
                    }
                }
            }
        }
        
//        System.out.println("tmp2");
        for(List<binX_range> X_range : tmp2){
            for(binX_range bxr : X_range){
                for (int idx_X = bxr.start_idx; idx_X < bxr.stop_idx; idx_X++) {
                    int block_no = bxr.block_no;
                    need_re_sort_block_no.add(block_no);
//                    System.out.print(dat.contactRec_per_block.get(block_no).get(idx_X) + "==>");
                    int ss = dat.contactRec_per_block.get(block_no).get(idx_X).binY;
                    dat.contactRec_per_block.get(block_no).get(idx_X).binY = max_plus_min
                            - dat.contactRec_per_block.get(block_no).get(idx_X).binX;
                    dat.contactRec_per_block.get(block_no).get(idx_X).binX = max_plus_min
                            - ss;
//                    System.out.println(dat.contactRec_per_block.get(block_no).get(idx_X));
                    if ((dat.contactRec_per_block.get(block_no).get(idx_X).binX < blockBinCount * block_no)
                            || (dat.contactRec_per_block.get(block_no).get(idx_X).binX > blockBinCount * (block_no + 1))
                            || (dat.contactRec_per_block.get(block_no).get(idx_X).binY < blockBinCount * block_no)
                            || (dat.contactRec_per_block.get(block_no).get(idx_X).binY > blockBinCount * (block_no + 1))){
                        need_to_move_other_block.add(new binX_idx(block_no, idx_X));
                    }
                }
            }
        }
        
            System.out.println("tmp1");
        for (List<binX_range> X_range : tmp1) {
            for (binX_range bxr : X_range) {
                for (int idx_X = bxr.start_idx; idx_X < bxr.stop_idx; idx_X++) {
                    int block_no = bxr.block_no;
                    need_re_sort_block_no.add(block_no);
//System.out.print("block_no="+block_no+" idx_X="+idx_X+":"+dat.contactRec_per_block.get(block_no).get(idx_X) + "==>");
                    dat.contactRec_per_block.get(block_no).get(idx_X).binY = max_plus_min
                            - dat.contactRec_per_block.get(block_no).get(idx_X).binY;
//System.out.println(dat.contactRec_per_block.get(block_no).get(idx_X));
                    if ((dat.contactRec_per_block.get(block_no).get(idx_X).binY < blockBinCount * block_no)
                            || (dat.contactRec_per_block.get(block_no).get(idx_X).binY > blockBinCount * (block_no + 1))){
                        need_to_move_other_block.add(new binX_idx(block_no, idx_X));
                    }
                }
            }            
        }
        Collections.sort(need_to_move_other_block, new cmp_rev_binX_idx());//sorted in descent order
        for(binX_idx bi : need_to_move_other_block){
            ContactRecord rec = dat.contactRec_per_block.get(bi.block_no).get(bi.idx);
            int new_block_no = rec.binY / blockBinCount * blockColumnCount + rec.binX / blockBinCount;
            dat.contactRec_per_block.get(new_block_no).add(new ContactRecord(rec.binX, rec.binY, rec.counts));
            dat.contactRec_per_block.get(bi.block_no).remove(bi.idx);
        }
        for(int block_no : need_re_sort_block_no){
            Collections.sort(dat.contactRec_per_block.get(block_no));
        }
    }

    private void move_block_and_reSort_old(int blockBinCount, int blockColumnCount, numContact_X_Y_per_resolution_str dat, Set<Integer> need_re_sort_block_no, List<binX_idx> need_to_move_other_block) {
        Collections.sort(need_to_move_other_block, new cmp_rev_binX_idx());//sorted in descent order
        for (binX_idx bi : need_to_move_other_block) {
            ContactRecord rec = dat.contactRec_per_block.get(bi.block_no).get(bi.idx);
            int new_block_no = rec.binY / blockBinCount * blockColumnCount + rec.binX / blockBinCount;
            if (dat.contactRec_per_block.get(new_block_no) == null) {
                int debug = 0;
            }
            dat.contactRec_per_block.get(new_block_no).add(new ContactRecord(rec.binX, rec.binY, rec.counts));
            dat.contactRec_per_block.get(bi.block_no).remove(bi.idx);
        }
        for (int block_no : need_re_sort_block_no) {
            Collections.sort(dat.contactRec_per_block.get(block_no));
        }

    }

    
    
    private void move_fragment(int selected_order, int insert_candidate_order_no, Map<Integer, List<Integer>> fragments_moved) {
//        int selected_min_pix_no = canvas.assembly_order_map.get(selected_order).display_start;
//        int selected_max_pix_no = selected_min_pix_no + canvas.assembly_order_map.get(selected_order).display_size;
//        int insert_pix_no = canvas.assembly_order_map.get(insert_candidate_order_no).display_start;

        for (int solu = 0; solu < Hic_data.get(chr_chr).size(); solu++) {
            int scale = HH.bpBinSizes[solu];
//            double selected_min_pix_no_double = canvas.assembly_order_map.get((selected_order)).genome_position / (scale * 2.0);
//            double selected_size_double = canvas.assembly_order_map.get(selected_order).size_in_superscaffold / (scale * 2.0);
            double insert_double;// = canvas.assembly_order_map.get((insert_candidate_order_no)).genome_position / (scale * 2.0);
            if (insert_candidate_order_no < Integer.MAX_VALUE) {
                insert_double = canvas.assembly_order_map.get(insert_candidate_order_no).genome_position / (scale * 1.0);
            } else {
                long pos = canvas.assembly_order_map.get(canvas.assembly_order_map.lastKey()).genome_position
                        + canvas.assembly_order_map.get(canvas.assembly_order_map.lastKey()).size_in_superscaffold;
                insert_double = pos / (scale * 1.0) + 1;
            }
//            int selected_min_pix_no = canvas.selected_order_min_line;// canvas.MARGIN + (int) (selected_min_pix_no_double + 0.5);
//            int selected_max_pix_no = canvas.selected_order_max_line;//selected_min_pix_no + (int) (selected_size_double + 0.5);
            int insert_pix_no = (int) (insert_double + 0.5);
            int selected_min_pix_no, selected_max_pix_no;
            if (canvas.shift_down) {
                Point p = canvas.get_fragment_min_max(selected_order, scale);
                selected_min_pix_no = p.x;
                selected_max_pix_no = p.y;
            } else if (canvas.ctrl_down) {
                Point p = canvas.get_chr_min_max(selected_order, scale);
                selected_min_pix_no = p.x;
                selected_max_pix_no = p.y;
            } else {//from undo
                Point p = canvas.get_chr_min_max(fragments_moved, scale);
                selected_min_pix_no = p.x;
                selected_max_pix_no = p.y;
            }
            if (selected_max_pix_no < insert_pix_no) {
                move_down_fragment(selected_min_pix_no, selected_max_pix_no, insert_pix_no, solu);//selected_order, insert_candidate_order_no, solu);
            } else {
                move_up_fragment(selected_min_pix_no, selected_max_pix_no, insert_pix_no, solu);//selected_order, insert_candidate_order_no, solu);
            }
        }
        assembly_fragment_move(selected_order, insert_candidate_order_no, fragments_moved);
    }

    private void move_up_fragment(int selected_min_pix_no, int selected_max_pix_no, int insert_pix_no, int solu){// int selected_order, int insert_candidate_order_no) {
        Set<Integer> need_re_sort_block_no = new HashSet();
        List<List<binX_idx>> need_to_move_other_block = new ArrayList<>();
        List<binX_idx> block_to_move = new ArrayList<>();        


//        int selected_min_pix_no1 = canvas.assembly_order_map.get((selected_order)).display_start;
//        int selected_max_pix_no1 = selected_min_pix_no1 + canvas.assembly_order_map.get((selected_order)).display_size + 1;
//        int insert_pix_no1 = canvas.assembly_order_map.get((insert_candidate_order_no)).display_start;

//        int scale = HH.bpBinSizes[solu];
//        
//        double selected_min_pix_no_double = canvas.assembly_order_map.get((selected_order)).genome_position / (scale * 2.0);
//        double selected_size_double = canvas.assembly_order_map.get(selected_order).size_in_superscaffold / (scale * 2.0);
//        double insert_double = canvas.assembly_order_map.get((insert_candidate_order_no)).genome_position / (scale * 2.0);
//        int selected_min_pix_no = canvas.MARGIN + (int)(selected_min_pix_no_double + 0.5);
//        int selected_max_pix_no = selected_min_pix_no + (int)(selected_size_double + 0.5);
//        int insert_pix_no = (int)(insert_double + 0.5);
//        
//        double selected_min_pix_no1 = canvas.MARGIN + (canvas.assembly_order_map.get(selected_order).genome_position / (scale * 2.0));
//        double selected_max_pix_no1 = selected_min_pix_no1 + ((canvas.assembly_order_map.get(selected_order).size_in_superscaffold + 0) / (scale * 2.0)) + 1;
//        double insert_pix_no1 = canvas.MARGIN + (canvas.assembly_order_map.get((insert_candidate_order_no)).genome_position / (scale * 2.0));
//                
//System.out.println("selected_min_pix_no1="+selected_min_pix_no1+" selected_max_pix_no1="+selected_max_pix_no1+" insert_pix_no1="+insert_pix_no1);
//System.out.println("selected_min_pix_no="+selected_min_pix_no+" selected_max_pix_no="+selected_max_pix_no+" insert_pix_no="+insert_pix_no);
//        int solu = Integer.parseInt(resolu.getText());
        int blockBinCount = Hic_data.get(chr_chr).get(solu).blockBinCount;
        int blockColumnCount = Hic_data.get(chr_chr).get(solu).blockColumnCount;
//        int selected_min_pix_no = selected_min / scale / 2;
//        int selected_max_pix_no = selected_max / scale / 2;
        int select_size = selected_max_pix_no - selected_min_pix_no;
//        int insert_pix_no = ins_pos / scale / 2;
        numContact_X_Y_per_resolution_str dat =  Hic_data.get(chr_chr).get(solu);
        List<ContactRecord> tmp = new ArrayList<>();
        int selected_size = selected_max_pix_no - selected_min_pix_no;
        float[] t = new float[selected_max_pix_no - selected_min_pix_no];
        
        List<List<binX_range>> tmp1 = new ArrayList<>();//1
        for (int y_pix_no = selected_min_pix_no; y_pix_no < selected_max_pix_no; y_pix_no++) {
            List<binX_range> X_range = get_binX_range(dat, y_pix_no, 0, insert_pix_no);
            tmp1.add(X_range);
        }
        List<List<binX_range>> tmp2 = new ArrayList<>();//2
        for (int y_pix_no = insert_pix_no; y_pix_no < selected_min_pix_no; y_pix_no++) {
            List<binX_range> X_range = get_binX_range(dat, y_pix_no, 0, insert_pix_no);
            tmp2.add(X_range);
        }
        List<List<binX_range>> tmp4 = new ArrayList<>();//4
        for (int y_pix_no = selected_min_pix_no; y_pix_no < selected_max_pix_no; y_pix_no++) {//4. middle part  (moving small trangle )
            List<binX_range> X_range = get_binX_range(dat, y_pix_no, selected_min_pix_no, y_pix_no);
            tmp4.add(X_range);
        }
        List<List<binX_range>> tmp5 = new ArrayList<>();//5
        for (int y_pix_no = selected_min_pix_no; y_pix_no < selected_max_pix_no; y_pix_no++) {//5
            List<binX_range> X_range = get_binX_range(dat, y_pix_no, insert_pix_no, selected_min_pix_no);
            tmp5.add(X_range);
        }
        List<List<binX_range>> tmp8 = new ArrayList<>();//8
        for (int y_pix_no = selected_max_pix_no; y_pix_no < blockBinCount * blockColumnCount; y_pix_no++) {//8
            List<binX_range> X_range = get_binX_range(dat, y_pix_no, selected_min_pix_no, selected_max_pix_no);
            tmp8.add(X_range);
        }
        List<List<binX_range>> tmp9 = new ArrayList<>();//9
        for (int y_pix_no = selected_max_pix_no; y_pix_no < blockBinCount * blockColumnCount; y_pix_no++) {//9
            List<binX_range> X_range = get_binX_range(dat, y_pix_no, insert_pix_no, selected_min_pix_no);
            tmp9.add(X_range);
        }
        List<List> tmp6 = new ArrayList<>();//6
        for (int y_pix_no = selected_min_pix_no - 1; y_pix_no >= insert_pix_no; y_pix_no--) {//6
            List<binX_range> X_range = get_binX_range(dat, y_pix_no, insert_pix_no, y_pix_no);
            for(int idx_X_range = 0; idx_X_range < X_range.size(); idx_X_range++){
                int block_no = X_range.get(idx_X_range).block_no;
                for(int idx_X = X_range.get(idx_X_range).start_idx; idx_X < X_range.get(idx_X_range).stop_idx; idx_X++){
                    dat.contactRec_per_block.get(block_no).get(idx_X).binX += select_size;
                    dat.contactRec_per_block.get(block_no).get(idx_X).binY += select_size;
add_needs_move_other_block(blockBinCount, blockColumnCount, dat, block_no, idx_X, need_re_sort_block_no, need_to_move_other_block, block_to_move);
                }
            }
        }
        for (int y_no = 0; y_no < tmp5.size(); y_no++) {//5
            for (int idx_X_range = 0; idx_X_range < tmp5.get(y_no).size(); idx_X_range++) {
                int block_no = tmp5.get(y_no).get(idx_X_range).block_no;
                for (int idx_X = tmp5.get(y_no).get(idx_X_range).start_idx; idx_X < tmp5.get(y_no).get(idx_X_range).stop_idx; idx_X++) {
                    int ss = dat.contactRec_per_block.get(block_no).get(idx_X).binX;
                    dat.contactRec_per_block.get(block_no).get(idx_X).binX = insert_pix_no - selected_min_pix_no +
                            dat.contactRec_per_block.get(block_no).get(idx_X).binY;
                    dat.contactRec_per_block.get(block_no).get(idx_X).binY = ss + select_size;
add_needs_move_other_block(blockBinCount, blockColumnCount, dat, block_no, idx_X, need_re_sort_block_no, need_to_move_other_block, block_to_move);
                }
            }
        }
        for (int y_no = 0; y_no < tmp1.size(); y_no++) {//1
            for (int idx_X_range = 0; idx_X_range < tmp1.get(y_no).size(); idx_X_range++) {
                int block_no = tmp1.get(y_no).get(idx_X_range).block_no;
                for (int idx_X = tmp1.get(y_no).get(idx_X_range).start_idx; idx_X < tmp1.get(y_no).get(idx_X_range).stop_idx; idx_X++) {
                    dat.contactRec_per_block.get(block_no).get(idx_X).binY -= selected_min_pix_no - insert_pix_no;
add_needs_move_other_block(blockBinCount, blockColumnCount, dat, block_no, idx_X, need_re_sort_block_no, need_to_move_other_block, block_to_move);
                }
            }
        }
        for (int y_no = 0; y_no < tmp2.size(); y_no++) {//2
            for (int idx_X_range = 0; idx_X_range < tmp2.get(y_no).size(); idx_X_range++) {
                int block_no = tmp2.get(y_no).get(idx_X_range).block_no;
                for (int idx_X = tmp2.get(y_no).get(idx_X_range).start_idx; idx_X < tmp2.get(y_no).get(idx_X_range).stop_idx; idx_X++) {
                    dat.contactRec_per_block.get(block_no).get(idx_X).binY += select_size;
add_needs_move_other_block(blockBinCount, blockColumnCount, dat, block_no, idx_X, need_re_sort_block_no, need_to_move_other_block, block_to_move);
                }
            }
        }
        for (int y_no = 0; y_no < tmp8.size(); y_no++) {//8
            for (int idx_X_range = 0; idx_X_range < tmp8.get(y_no).size(); idx_X_range++) {
                int block_no = tmp8.get(y_no).get(idx_X_range).block_no;
                for (int idx_X = tmp8.get(y_no).get(idx_X_range).start_idx; idx_X < tmp8.get(y_no).get(idx_X_range).stop_idx; idx_X++) {
                    dat.contactRec_per_block.get(block_no).get(idx_X).binX -= selected_min_pix_no - insert_pix_no;
add_needs_move_other_block(blockBinCount, blockColumnCount, dat, block_no, idx_X, need_re_sort_block_no, need_to_move_other_block, block_to_move);
                }
            }
        }
        for (int y_no = 0; y_no < tmp9.size(); y_no++) {//9
            for (int idx_X_range = 0; idx_X_range < tmp9.get(y_no).size(); idx_X_range++) {
                int block_no = tmp9.get(y_no).get(idx_X_range).block_no;
                for (int idx_X = tmp9.get(y_no).get(idx_X_range).start_idx; idx_X < tmp9.get(y_no).get(idx_X_range).stop_idx; idx_X++) {
                    dat.contactRec_per_block.get(block_no).get(idx_X).binX += select_size;
add_needs_move_other_block(blockBinCount, blockColumnCount, dat, block_no, idx_X, need_re_sort_block_no, need_to_move_other_block, block_to_move);
                }
            }
        }
        for (int y_no = 0; y_no < tmp4.size(); y_no++) {//4
            for (int idx_X_range = 0; idx_X_range < tmp4.get(y_no).size(); idx_X_range++) {
                int block_no = tmp4.get(y_no).get(idx_X_range).block_no;
                for (int idx_X = tmp4.get(y_no).get(idx_X_range).start_idx; idx_X < tmp4.get(y_no).get(idx_X_range).stop_idx; idx_X++) {
                    dat.contactRec_per_block.get(block_no).get(idx_X).binX -= selected_min_pix_no - insert_pix_no;
                    dat.contactRec_per_block.get(block_no).get(idx_X).binY -= selected_min_pix_no - insert_pix_no;
add_needs_move_other_block(blockBinCount, blockColumnCount, dat, block_no, idx_X, need_re_sort_block_no, need_to_move_other_block, block_to_move);
                }
            }
        }
        
        if (!block_to_move.isEmpty()) {
            need_to_move_other_block.add(block_to_move);
        }
        move_block_and_reSort(blockBinCount, blockColumnCount, dat, need_re_sort_block_no, need_to_move_other_block);
    }
    
    private void add_needs_move_other_block(int blockBinCount, int blockColumnCount, numContact_X_Y_per_resolution_str dat, int block_no, int idx_X, Set<Integer> need_re_sort_block_no, List<List<binX_idx>> need_to_move_other_block, List<binX_idx> block_to_move) {
        int new_block_no = dat.contactRec_per_block.get(block_no).get(idx_X).binY / blockBinCount * blockColumnCount + dat.contactRec_per_block.get(block_no).get(idx_X).binX / blockBinCount;
        if (new_block_no != block_no) {
            if (block_to_move.isEmpty() || ((block_to_move.get(block_to_move.size() - 1).block_no == block_no) && (block_to_move.get(block_to_move.size() - 1).idx == idx_X - 1))) {
                block_to_move.add(new binX_idx(block_no, idx_X));
            } else {
                need_to_move_other_block.add(new ArrayList<binX_idx>(block_to_move));
                block_to_move.clear();
                block_to_move.add(new binX_idx(block_no, idx_X));
            }
        }
        need_re_sort_block_no.add(block_no);
    }
    
//    private void move_down_fragment(int selected_order, int insert_candidate_order_no, int solu) {
    private void move_down_fragment(int selected_min_pix_no, int selected_max_pix_no, int insert_pix_no, int solu) {//int selected_order, int insert_candidate_order_no, 
        Set<Integer> need_re_sort_block_no = new HashSet();
//        List<binX_idx> need_to_move_other_block = new ArrayList<>();
        List<List<binX_idx>> need_to_move_other_block = new ArrayList<>();
        List<binX_idx> block_to_move = new ArrayList<>();        

//        int selected_min_pix_no1 = canvas.assembly_order_map.get((selected_order)).display_start;
//        int selected_max_pix_no1 = selected_min_pix_no1 + canvas.assembly_order_map.get((selected_order)).display_size + 1;
//        int insert_pix_no1 = canvas.assembly_order_map.get((insert_candidate_order_no)).display_start;
        
//        int scale = HH.bpBinSizes[solu];
        
//        double selected_min_pix_no_double = canvas.assembly_order_map.get((selected_order)).genome_position / (scale * 2.0);
//        double selected_size_double = canvas.assembly_order_map.get(selected_order).size_in_superscaffold / (scale * 2.0);
//        double insert_double = canvas.assembly_order_map.get((insert_candidate_order_no)).genome_position / (scale * 2.0);
//        int selected_min_pix_no = canvas.MARGIN + (int)(selected_min_pix_no_double + 0.5);
//        int selected_max_pix_no = selected_min_pix_no + (int)(selected_size_double + 0.5);
//        int insert_pix_no = (int)(insert_double + 0.5);
//        
//        double selected_min_pix_no1 = canvas.MARGIN + (canvas.assembly_order_map.get((selected_order)).genome_position / (scale * 2.0));
//        double selected_max_pix_no1 = selected_min_pix_no1 + ((canvas.assembly_order_map.get(selected_order).size_in_superscaffold + 0) / (scale * 2.0)) + 1;
//        double insert_pix_no1 = canvas.MARGIN + (canvas.assembly_order_map.get((insert_candidate_order_no)).genome_position / (scale * 2.0)) + 1;
                
//System.out.println("selected_min_pix_no1="+selected_min_pix_no1+" selected_max_pix_no1="+selected_max_pix_no1+" insert_pix_no1="+insert_pix_no1);
//System.out.println("selected_min_pix_no="+selected_min_pix_no+" selected_max_pix_no="+selected_max_pix_no+" insert_pix_no="+insert_pix_no);






//        int solu = Integer.parseInt(resolu.getText());
        int blockBinCount = Hic_data.get(chr_chr).get(solu).blockBinCount;
        int blockColumnCount = Hic_data.get(chr_chr).get(solu).blockColumnCount;
//        int selected_min_pix_no = selected_min / scale / 2;
//        int selected_max_pix_no = selected_max / scale / 2;
        int select_size = selected_max_pix_no - selected_min_pix_no;
//        int insert_pix_no = ins_pos / scale / 2;
        numContact_X_Y_per_resolution_str dat =  Hic_data.get(chr_chr).get(solu);
        List<ContactRecord> tmp = new ArrayList<>();
        int selected_size = selected_max_pix_no - selected_min_pix_no;
        float[] t = new float[selected_max_pix_no - selected_min_pix_no];
        
        List<List<binX_range>> tmp1 = new ArrayList<>();//1
        for (int y_pix_no = selected_min_pix_no; y_pix_no < selected_max_pix_no; y_pix_no++) {
            List<binX_range> X_range = get_binX_range(dat, y_pix_no, 0, selected_min_pix_no);
            tmp1.add(X_range);
        }
        List<List<binX_range>> tmp2 = new ArrayList<>();//2
        for (int y_pix_no = selected_max_pix_no; y_pix_no < insert_pix_no; y_pix_no++) {
            List<binX_range> X_range = get_binX_range(dat, y_pix_no, 0, selected_min_pix_no);
            tmp2.add(X_range);
        }
        List<List<binX_range>> tmp4 = new ArrayList<>();//4
        for (int y_pix_no = selected_min_pix_no; y_pix_no < selected_max_pix_no; y_pix_no++) {//4. middle part  (moving small trangle )
            List<binX_range> X_range = get_binX_range(dat, y_pix_no, selected_min_pix_no, y_pix_no);
            tmp4.add(X_range);
        }
        List<List<binX_range>> tmp5 = new ArrayList<>();//5
        for (int y_pix_no = selected_max_pix_no; y_pix_no < insert_pix_no; y_pix_no++) {//5
            List<binX_range> X_range = get_binX_range(dat, y_pix_no, selected_min_pix_no, selected_max_pix_no);
            tmp5.add(X_range);
        }
        List<List<binX_range>> tmp8 = new ArrayList<>();//8
        for (int y_pix_no = insert_pix_no; y_pix_no < blockBinCount * blockColumnCount; y_pix_no++) {//8
            List<binX_range> X_range = get_binX_range(dat, y_pix_no, selected_min_pix_no, selected_max_pix_no);
            tmp8.add(X_range);
        }
        List<List<binX_range>> tmp9 = new ArrayList<>();//9
        for (int y_pix_no = insert_pix_no; y_pix_no < blockBinCount * blockColumnCount; y_pix_no++) {//9
            List<binX_range> X_range = get_binX_range(dat, y_pix_no, selected_max_pix_no, insert_pix_no);
            tmp9.add(X_range);
        }
        for (int y_pix_no = selected_max_pix_no; y_pix_no < insert_pix_no; y_pix_no++) {//6
            List<binX_range> X_range = get_binX_range(dat, y_pix_no, selected_max_pix_no, y_pix_no);
            for(int idx_X_range = 0; idx_X_range < X_range.size(); idx_X_range++){
                int block_no = X_range.get(idx_X_range).block_no;
                for(int idx_X = X_range.get(idx_X_range).start_idx; idx_X < X_range.get(idx_X_range).stop_idx; idx_X++){
                    dat.contactRec_per_block.get(block_no).get(idx_X).binX -= select_size;
                    dat.contactRec_per_block.get(block_no).get(idx_X).binY -= select_size;
add_needs_move_other_block(blockBinCount, blockColumnCount, dat, block_no, idx_X, need_re_sort_block_no, need_to_move_other_block, block_to_move);
//                    if ((dat.contactRec_per_block.get(block_no).get(idx_X).binY < blockBinCount * block_no)
//                            || (dat.contactRec_per_block.get(block_no).get(idx_X).binY > blockBinCount * (block_no + 1))) {
//                        need_to_move_other_block.add(new binX_idx(block_no, idx_X));
//                    }
//                    need_re_sort_block_no.add(block_no);
                }
            }
        }
        for (int y_no = 0; y_no < tmp4.size(); y_no++) {//4
            for (int idx_X_range = 0; idx_X_range < tmp4.get(y_no).size(); idx_X_range++) {
                int block_no = tmp4.get(y_no).get(idx_X_range).block_no;
                for (int idx_X = tmp4.get(y_no).get(idx_X_range).start_idx; idx_X < tmp4.get(y_no).get(idx_X_range).stop_idx; idx_X++) {
                    dat.contactRec_per_block.get(block_no).get(idx_X).binX += insert_pix_no - selected_max_pix_no;
                    dat.contactRec_per_block.get(block_no).get(idx_X).binY += insert_pix_no - selected_max_pix_no;
add_needs_move_other_block(blockBinCount, blockColumnCount, dat, block_no, idx_X, need_re_sort_block_no, need_to_move_other_block, block_to_move);
//                    if ((dat.contactRec_per_block.get(block_no).get(idx_X).binY < blockBinCount * block_no)
//                            || (dat.contactRec_per_block.get(block_no).get(idx_X).binY > blockBinCount * (block_no + 1))) {
//                        need_to_move_other_block.add(new binX_idx(block_no, idx_X));
//                    }
//                    need_re_sort_block_no.add(block_no);
                }
            }
        }
        for (int y_no = 0; y_no < tmp5.size(); y_no++) {//5
            for (int idx_X_range = 0; idx_X_range < tmp5.get(y_no).size(); idx_X_range++) {
                int block_no = tmp5.get(y_no).get(idx_X_range).block_no;
                for (int idx_X = tmp5.get(y_no).get(idx_X_range).start_idx; idx_X < tmp5.get(y_no).get(idx_X_range).stop_idx; idx_X++) {
                    int ss = dat.contactRec_per_block.get(block_no).get(idx_X).binX;
                    dat.contactRec_per_block.get(block_no).get(idx_X).binX =
                            dat.contactRec_per_block.get(block_no).get(idx_X).binY - select_size;
                    dat.contactRec_per_block.get(block_no).get(idx_X).binY = insert_pix_no - (selected_max_pix_no - ss);
add_needs_move_other_block(blockBinCount, blockColumnCount, dat, block_no, idx_X, need_re_sort_block_no, need_to_move_other_block, block_to_move);
//                    if ((dat.contactRec_per_block.get(block_no).get(idx_X).binY < blockBinCount * block_no)
//                            || (dat.contactRec_per_block.get(block_no).get(idx_X).binY > blockBinCount * (block_no + 1))) {
//                        need_to_move_other_block.add(new binX_idx(block_no, idx_X));
//                    }
//                    need_re_sort_block_no.add(block_no);
                }
            }
        }
        for (int y_no = 0; y_no < tmp1.size(); y_no++) {//1
            for (int idx_X_range = 0; idx_X_range < tmp1.get(y_no).size(); idx_X_range++) {
                int block_no = tmp1.get(y_no).get(idx_X_range).block_no;
                for (int idx_X = tmp1.get(y_no).get(idx_X_range).start_idx; idx_X < tmp1.get(y_no).get(idx_X_range).stop_idx; idx_X++) {
                    dat.contactRec_per_block.get(block_no).get(idx_X).binY += insert_pix_no - selected_max_pix_no;
add_needs_move_other_block(blockBinCount, blockColumnCount, dat, block_no, idx_X, need_re_sort_block_no, need_to_move_other_block, block_to_move);
//                    if ((dat.contactRec_per_block.get(block_no).get(idx_X).binY < blockBinCount * block_no)
//                            || (dat.contactRec_per_block.get(block_no).get(idx_X).binY > blockBinCount * (block_no + 1))) {
//                        need_to_move_other_block.add(new binX_idx(block_no, idx_X));
//                    }
//                    need_re_sort_block_no.add(block_no);
                }
            }
        }
        for (int y_no = 0; y_no < tmp2.size(); y_no++) {//2
            for (int idx_X_range = 0; idx_X_range < tmp2.get(y_no).size(); idx_X_range++) {
                int block_no = tmp2.get(y_no).get(idx_X_range).block_no;
                for (int idx_X = tmp2.get(y_no).get(idx_X_range).start_idx; idx_X < tmp2.get(y_no).get(idx_X_range).stop_idx; idx_X++) {
                    dat.contactRec_per_block.get(block_no).get(idx_X).binY -= select_size;
add_needs_move_other_block(blockBinCount, blockColumnCount, dat, block_no, idx_X, need_re_sort_block_no, need_to_move_other_block, block_to_move);
//                    if ((dat.contactRec_per_block.get(block_no).get(idx_X).binY < blockBinCount * block_no)
//                            || (dat.contactRec_per_block.get(block_no).get(idx_X).binY > blockBinCount * (block_no + 1))) {
//                        need_to_move_other_block.add(new binX_idx(block_no, idx_X));
//                    }
//                    need_re_sort_block_no.add(block_no);
                }
            }
        }
        for (int y_no = 0; y_no < tmp8.size(); y_no++) {//8
            for (int idx_X_range = 0; idx_X_range < tmp8.get(y_no).size(); idx_X_range++) {
                int block_no = tmp8.get(y_no).get(idx_X_range).block_no;
                for (int idx_X = tmp8.get(y_no).get(idx_X_range).start_idx; idx_X < tmp8.get(y_no).get(idx_X_range).stop_idx; idx_X++) {
                    dat.contactRec_per_block.get(block_no).get(idx_X).binX += insert_pix_no - selected_max_pix_no;
add_needs_move_other_block(blockBinCount, blockColumnCount, dat, block_no, idx_X, need_re_sort_block_no, need_to_move_other_block, block_to_move);
//                    if ((dat.contactRec_per_block.get(block_no).get(idx_X).binY < blockBinCount * block_no)
//                            || (dat.contactRec_per_block.get(block_no).get(idx_X).binY > blockBinCount * (block_no + 1))) {
//                        need_to_move_other_block.add(new binX_idx(block_no, idx_X));
//                    }
//                    need_re_sort_block_no.add(block_no);
                }
            }
        }
        for (int y_no = 0; y_no < tmp9.size(); y_no++) {//9
            for (int idx_X_range = 0; idx_X_range < tmp9.get(y_no).size(); idx_X_range++) {
                int block_no = tmp9.get(y_no).get(idx_X_range).block_no;
                for (int idx_X = tmp9.get(y_no).get(idx_X_range).start_idx; idx_X < tmp9.get(y_no).get(idx_X_range).stop_idx; idx_X++) {
                    dat.contactRec_per_block.get(block_no).get(idx_X).binX -= select_size;
add_needs_move_other_block(blockBinCount, blockColumnCount, dat, block_no, idx_X, need_re_sort_block_no, need_to_move_other_block, block_to_move);
//                    if ((dat.contactRec_per_block.get(block_no).get(idx_X).binY < blockBinCount * block_no)
//                            || (dat.contactRec_per_block.get(block_no).get(idx_X).binY > blockBinCount * (block_no + 1))) {
//                        need_to_move_other_block.add(new binX_idx(block_no, idx_X));
//                    }
//                    need_re_sort_block_no.add(block_no);
                }
            }
        }
        if (!block_to_move.isEmpty()) {
            need_to_move_other_block.add(block_to_move);
        }
        move_block_and_reSort(blockBinCount, blockColumnCount, dat, need_re_sort_block_no, need_to_move_other_block);

//        Collections.sort(need_to_move_other_block, new cmp_rev_binX_idx());//sorted in descent order
//        for(binX_idx bi : need_to_move_other_block){
//            ContactRecord rec = dat.contactRec_per_block.get(bi.block_no).get(bi.idx);
//            int new_block_no = rec.binY  / blockBinCount * blockColumnCount + rec.binX / blockBinCount;
//            if(dat.contactRec_per_block.get(new_block_no) == null){
//                int debug = 0;
//            }
//            dat.contactRec_per_block.get(new_block_no).add(new ContactRecord(rec.binX, rec.binY, rec.counts));
//            dat.contactRec_per_block.get(bi.block_no).remove(bi.idx);
//        }
//        for(int block_no : need_re_sort_block_no){
//            Collections.sort(dat.contactRec_per_block.get(block_no));
//        }
    }
    private void titleAlign(JFrame frame) {

        Font font = frame.getFont();

        String currentTitle = frame.getTitle().trim();
        FontMetrics fm = frame.getFontMetrics(font);
        int frameWidth = frame.getWidth();
        int titleWidth = fm.stringWidth(currentTitle);
        int spaceWidth = fm.stringWidth(" ");
        int centerPos = (frameWidth / 2) - (titleWidth / 2);
        int spaceCount = centerPos / spaceWidth;
        String pad = "";
        pad = String.format("%" + (spaceCount - 14) + "s", pad);
        frame.setTitle(pad + currentTitle);
    }
    

    void file_choice_readIn(ActionEvent e, JTextField fn_jTextField) {
        JFileChooser jfc = new JFileChooser();
        dir = fn_jTextField.getText();
        jfc.setSelectedFile(new File(dir));
        jfc.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                return f.getName().toLowerCase().endsWith(".hic") || f.isDirectory();
            }

            public String getDescription() {
                return "Hic Files";
            }
        });
        if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            //code to handle choosed file here.
            jButton0.setEnabled(false);
            File file = jfc.getSelectedFile();
            dir = file.getParent();
            Hic_fn = file.getAbsolutePath();
            fn_jTextField.setText(file.getAbsolutePath());
            jButton0.setEnabled(false);
            readIn_Hic();
            jButton0.setEnabled(true);
        }
    }
    
    String file_choice_update(ActionEvent e, JTextField fn_jTextField) {
        JFileChooser jfc = new JFileChooser();
        dir = fn_jTextField.getText();
        String new_Hic_fn = dir;
        new_Hic_fn = new_Hic_fn.substring(0, new_Hic_fn.lastIndexOf('.')) + ".review" + new_Hic_fn.substring(new_Hic_fn.lastIndexOf('.'));
        jfc.setSelectedFile(new File(new_Hic_fn));
        jfc.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                return f.getName().toLowerCase().endsWith(".hic") || f.isDirectory();
            }

            public String getDescription() {
                return "Hic Files";
            }
        });
        if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            jfc.setSelectedFile(new File(dir));
            //code to handle choosed file here.
//            File file = jfc.getSelectedFile();
//            String new_dir = file.getParent();
            return new_Hic_fn;
        }
        jfc.setSelectedFile(new File(dir));
        return null;
    }

    void file_Hic_choice(ActionEvent e) {
        JFileChooser jfc = new JFileChooser();
        jfc.setCurrentDirectory(new File(dir));
        jfc.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                return f.getName().toLowerCase().endsWith(".hic") || f.isDirectory();
            }

            public String getDescription() {
                return "Hic Files";
            }
        });
        if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            //code to handle choosed file here.
            jButton0.setEnabled(false);
            File file = jfc.getSelectedFile();
            dir = file.getParent();
            Hic_fn = file.getAbsolutePath();
            Hic_fn_jTextField.setText(file.getAbsolutePath());
            jButton0.setEnabled(false);
            readIn_Assembly();
            readIn_Hic();
            readIn_Fasta();
            jButton0.setEnabled(true);
        }
    }

//    void file_assembly_choice(ActionEvent e) {
//        JFileChooser jfc = new JFileChooser();
//        jfc.setCurrentDirectory(new File(dir));
//        jfc.setFileFilter(new javax.swing.filechooser.FileFilter() {
//            public boolean accept(File f) {
//                return f.getName().toLowerCase().endsWith(".assembly") || f.isDirectory();
//            }
//
//            public String getDescription() {
//                return "Assembly Files";
//            }
//        });
//        if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
//            //code to handle choosed file here.
//            jButton01.setEnabled(false);
//            File file = jfc.getSelectedFile();
//            dir = file.getParent();
//            Assembly_fn = file.getAbsolutePath();
//            Assembly_fn_jTextField.setText(file.getAbsolutePath());
//            jButton01.setEnabled(false);
//            readIn_Assembly();
//            jButton01.setEnabled(true);
//        }
//
//    }
//    void file_fasta_choice(ActionEvent e) {
//        JFileChooser jfc = new JFileChooser();
//        jfc.setCurrentDirectory(new File(dir));
//        jfc.setFileFilter(new javax.swing.filechooser.FileFilter() {
//            public boolean accept(File f) {
//                return f.getName().toLowerCase().endsWith(".fasta") || f.isDirectory();
//            }
//
//            public String getDescription() {
//                return "Assembly Files";
//            }
//        });
//        if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
//            //code to handle choosed file here.
//            jButton02.setEnabled(false);
//            File file = jfc.getSelectedFile();
//            dir = file.getParent();
//            fasta_fn = file.getAbsolutePath();
//            fasta_fn_jTextField.setText(file.getAbsolutePath());
//            jButton02.setEnabled(false);
//            readIn_Fasta();
//            jButton02.setEnabled(true);
//        }

//    }

}
