/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.qut.citrus.include;

/**
 *
 * @author an
 */
public class SupperScaffold {
    
    public String scafold_fragment_subType_ID;
//    int order;
    public int start_in_superscaffold;
    public int size_in_superscaffold;
    public long genome_position;
    public int display_start;
    public int display_size;

    public SupperScaffold(String scafold_fragment_subType_ID, int start_in_superscaffold, int size_in_superscaffold,
            long genome_position, int display_start, int display_size){
        this.scafold_fragment_subType_ID = scafold_fragment_subType_ID;
        this.start_in_superscaffold = start_in_superscaffold;
        this.size_in_superscaffold = size_in_superscaffold;
        this.genome_position = genome_position;
        this.display_start = display_start;
        this.display_size = display_size;
    }
    public SupperScaffold(SupperScaffold SS){
        this.scafold_fragment_subType_ID = SS.scafold_fragment_subType_ID;
        this.start_in_superscaffold = SS.start_in_superscaffold;
        this.size_in_superscaffold = SS.size_in_superscaffold;
        this.genome_position = SS.genome_position;
        this.display_start = SS.display_start;
        this.display_size = SS.display_size;
    }
            
    public SupperScaffold(String line) {
        String[] strarray = line.split(" ");
        scafold_fragment_subType_ID = strarray[0];
//        order = Integer.parseInt(strarray[1]);
        size_in_superscaffold = Integer.parseInt(strarray[2]);
    } 
    public String toString(){
        return scafold_fragment_subType_ID + " start_in_superscaffold="+start_in_superscaffold + " size_in_superscaffold="+size_in_superscaffold+
                " genome_position="+genome_position+ " display_start="+display_start+" display_size="+display_size;
    }
    public SupperScaffold combin_fragment(SupperScaffold a1){
        String ID1 = scafold_fragment_subType_ID;
        String ID2 = a1.scafold_fragment_subType_ID;
        if(get_fragment_no(ID2) - get_fragment_no(ID1) != 1){
            System.err.println("two fragment must be neighor");
            return null;
        }
        String new_scafold_fragment_subType_ID = ID1;
        int new_display_start = Math.min(display_start, a1.display_start);
        int new_display_size = display_size + a1.display_size;
        long new_genome_position = Math.min(genome_position, a1.genome_position);
        int new_size_in_superscaffold = size_in_superscaffold + a1.size_in_superscaffold;
        int new_start_in_superscaffold = Math.min(start_in_superscaffold, a1.start_in_superscaffold);
        return new SupperScaffold(new_scafold_fragment_subType_ID, new_start_in_superscaffold, new_size_in_superscaffold,
                new_genome_position, new_display_start, new_display_size);
    }
    
    public int get_fragment_no(String superScafold_ID){
        return Integer.parseInt(superScafold_ID.split("fragment_")[1].split(":::")[0]); 
    }    
    
    public SupperScaffold split(long genome_split_pos, int display_split_pos, int strand) {
        SupperScaffold ret;
        int combined_superscaffold_size = size_in_superscaffold;
        int combined_dsiplay_size = display_size;
        display_size = display_split_pos - display_start;
        size_in_superscaffold = (int)((double)combined_superscaffold_size / combined_dsiplay_size * display_size);
        
        String new_scafold_fragment_subType_ID = org.qut.citrus.Citrus.SS_ID_increase_1(scafold_fragment_subType_ID);
        int new_display_start = display_split_pos;
        int new_display_size = combined_dsiplay_size - display_split_pos;
        long new_genome_position = genome_split_pos;
        int new_size_in_superscaffold = combined_superscaffold_size - size_in_superscaffold;
        int new_start_in_superscaffold;
        if (strand > 0){
            new_start_in_superscaffold = start_in_superscaffold + size_in_superscaffold;
        }else{
            new_start_in_superscaffold = start_in_superscaffold;
            start_in_superscaffold = new_start_in_superscaffold + new_size_in_superscaffold;
        }
        return new SupperScaffold(new_scafold_fragment_subType_ID, new_start_in_superscaffold, new_size_in_superscaffold,
            new_genome_position, new_display_start, new_display_size);
        
//        int combined_superscaffold_size = size_in_superscaffold;
//        size_in_superscaffold = (int) (genome_split_pos - genome_position);
//        int combined_dsiplay_size = display_size;
//        String line;
//        String[] tmp = scafold_fragment_subType_ID.split("_");
//        tmp[tmp.length - 1] = Integer.toString(Integer.parseInt(tmp[tmp.length - 1]) + 1);
//        line = tmp[0];
//        for (int i = 1; i < tmp.length; i++) {
//            line += "_" + tmp[i];
//        }
//        line += " dummy";// + Integer.toString(order + 1);
//        line += " " + Integer.toString(combined_superscaffold_size - size_in_superscaffold);
//        ret = new SupperScaffold(line);
//        ret.start_in_superscaffold = start_in_superscaffold + size_in_superscaffold;
//        ret.genome_position = genome_split_pos;
//        ret.display_start = display_split_pos;
//        this.display_size = display_split_pos - this.display_start;
//        ret.display_size = combined_dsiplay_size - this.display_size;
//        if(strand < 0){//genome, scaff start size need to be changed, but display size start do not need to change
//            
//            
//            long t = ret.genome_position;
//            ret.genome_position = this.genome_position;
//            this.genome_position = t;
//            
//            int tt = this.size_in_superscaffold;
//            this.size_in_superscaffold = ret.size_in_superscaffold;
//            ret.size_in_superscaffold = tt;
//            
//            ret.start_in_superscaffold = this.start_in_superscaffold;
//            ret.size_in_superscaffold = this.start_in_superscaffold;
//            this.start_in_superscaffold = ret.start_in_superscaffold + ret.size_in_superscaffold;
//            this.size_in_superscaffold = combined_superscaffold_size - ret.size_in_superscaffold;
//            
//        }
////        if (strand > 0) {
////            String[] tmp = scafold_fragment_subType_ID.split("_");
////            tmp[tmp.length - 1] = Integer.toString(Integer.parseInt(tmp[tmp.length - 1]) + 1);
////            line = tmp[0];
////            for (int i = 1; i < tmp.length; i++) {
////                line += "_" + tmp[i];
////            }
////            line += " dummy";// + Integer.toString(order + 1);
////            line += " " + Integer.toString(combined_superscaffold_size - size_in_superscaffold);
////            ret = new SupperScaffold(line);
////            ret.start_in_superscaffold = start_in_superscaffold + size_in_superscaffold;
////            ret.genome_position = genome_split_pos;
////            ret.display_start = display_split_pos;
////            this.display_size = display_split_pos - this.display_start;
////            ret.display_size = combined_dsiplay_size - this.display_size;
////        } else {
////            String[] tmp = scafold_fragment_subType_ID.split("_");
////            line = scafold_fragment_subType_ID;
////            tmp[tmp.length - 1] = Integer.toString(Integer.parseInt(tmp[tmp.length - 1]) + 1);
////            scafold_fragment_subType_ID = tmp[0];
////            for (int i = 1; i < tmp.length; i++) {
////                scafold_fragment_subType_ID += "_" + tmp[i];
////            }
////            line += " dummy";// + Integer.toString(order);
////            line += " " + Integer.toString(size_in_superscaffold);
////            size_in_superscaffold = combined_superscaffold_size - size_in_superscaffold;
////            ret = new SupperScaffold(line);
////            ret.start_in_superscaffold = start_in_superscaffold + size_in_superscaffold;
////            ret.genome_position = this.genome_position;
////            this.genome_position = genome_split_pos;            
////            ret.display_start = this.display_start;
////            ret.display_start = display_split_pos;
////            ret.display_size = display_split_pos - this.display_start;
////            this.display_size = combined_dsiplay_size - ret.display_size;
////        }
//        return ret;
    }
}
 