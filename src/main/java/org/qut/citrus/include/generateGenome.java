/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.qut.citrus.include;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;
/**
 *
 * @author an
 */
public class generateGenome {

    String assembly_fn;
    String superfold_fn;
    int min_genome_block;
    int N_between_superscaffold;
    TreeMap<Integer, SupperScaffold> assembly = new TreeMap();
    TreeMap<String, StringBuilder> scaffold = new TreeMap();
    TreeMap<String, ArrayList<Integer>> chr_order_set = new TreeMap();

    public generateGenome(String assembly_fn, String superfold_fn, int min_genome_block, int N_between_superscaffold) throws IOException {
        this.assembly_fn = assembly_fn;
        this.superfold_fn = superfold_fn;
        this.min_genome_block = min_genome_block;
        this.N_between_superscaffold = N_between_superscaffold;
        BufferedReader br = new BufferedReader(new FileReader(assembly_fn));
        String line = br.readLine();
        String[] strarray = line.split(" ");
        int order = Integer.parseInt(strarray[1]);
        assembly.put(order, new SupperScaffold(line));
        String current_scaffold = assembly.get(0).scafold_fragment_subType_ID;
        int current_start = assembly.get(0).size_in_superscaffold;
        int chrom = 1;
        while ((line = br.readLine()) != null) {
            if (line.startsWith(">")) {
                if (line.startsWith(">")) {
                    strarray = line.split(" ");
                    order = Integer.parseInt(strarray[1]);
                    assembly.put(order, new SupperScaffold(line));
                    if (current_scaffold.equals(assembly.get(assembly.size() - 1).scafold_fragment_subType_ID)) {
                        assembly.get(assembly.size() - 1).start_in_superscaffold = current_start;
                    } else {
                        current_scaffold = assembly.get(assembly.size() - 1).scafold_fragment_subType_ID;
                        current_start = 0;
                    }
                    current_start += assembly.get(assembly.size() - 1).size_in_superscaffold;
                }
            } else {
                ArrayList<Integer> order_set = new ArrayList();
                strarray = line.split(" ");
                for (int i = 0; i < strarray.length; i += 2) {
                    order_set.add(Integer.parseInt(strarray[i]));
                }
                chr_order_set.put("chr" + String.format("%02d", chrom), order_set);
                chrom++;
            }
        }
        br.close();
        br = new BufferedReader(new FileReader(superfold_fn));
        String header = br.readLine().substring(1);
        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine()) != null) {
            if (!line.startsWith(">")) {
                sb.append(line);
            } else {
                scaffold.put(header, new StringBuilder(sb));
                header = line.substring(1);
                sb.setLength(0);
            }
        }
        scaffold.put(header, sb);
        br.close();
    }

    public void writeOut() throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(assembly_fn + ".genome.fasta"));
        BufferedWriter bw_debris = new BufferedWriter(new FileWriter(assembly_fn + ".debris.fasta"));
        long num_chrom = 0;
        long num_debris = 0;
        long num_debris_remove_N = 0;
        for (String chrom : chr_order_set.keySet()) {
            boolean is_debris = false;
            long num_bp = 0;
            for (int order : chr_order_set.get(chrom)) {
                SupperScaffold as = assembly.get(order);
                if (as.scafold_fragment_subType_ID.endsWith("debris")) {
                    is_debris = true;
                }
                num_bp += Math.abs(as.size_in_superscaffold);
            }
            if (is_debris || (num_bp < min_genome_block)) {
                bw_debris.write(">" + chrom + "\n");
                System.out.println("debris:" + chrom);
                StringBuilder sb = new StringBuilder();
                for (int order : chr_order_set.get(chrom)) {
                    SupperScaffold as = assembly.get(order);
                    if (scaffold.get(as.scafold_fragment_subType_ID) != null) {
                        if (order > 0) {
                            sb.append(scaffold.get(as.scafold_fragment_subType_ID).substring(as.start_in_superscaffold, as.start_in_superscaffold + as.size_in_superscaffold));
                        } else {
                            sb.append(toMinusStrand(scaffold.get(as.scafold_fragment_subType_ID).substring(as.start_in_superscaffold, as.start_in_superscaffold + as.size_in_superscaffold)));
                        }
                        num_debris += Math.abs(as.size_in_superscaffold);
                        num_debris_remove_N += Math.abs(scaffold.get(as.scafold_fragment_subType_ID).substring(as.start_in_superscaffold, as.start_in_superscaffold + as.size_in_superscaffold).replace("N", "").length());
                    } else {
                        System.err.println("scafold:" + as.scafold_fragment_subType_ID + " does not exist");
                        System.exit(-1);
                    }
                }
                int start_pos = 0;
                while (start_pos < sb.length() - 100) {
                    bw_debris.write(sb.substring(start_pos, start_pos + 100) + "\n");
                    start_pos += 100;
                }
                bw_debris.write(sb.substring(start_pos) + "\n");
            } else {
                bw.write(">" + chrom + "\n");
                boolean firstTime = true;
                System.out.println("genome:" + chrom);
                StringBuilder sb = new StringBuilder();
                for (int order : chr_order_set.get(chrom)) {
                    SupperScaffold as = assembly.get(order);
                    if (scaffold.get(as.scafold_fragment_subType_ID) != null) {
                        if (!firstTime) {
                            for (int i = 0; i < N_between_superscaffold; i++) {
                                sb.append("N");
                                firstTime = false;
                            }
                        }
                        if (order > 0) {
                            sb.append(scaffold.get(as.scafold_fragment_subType_ID).substring(as.start_in_superscaffold, as.start_in_superscaffold + as.size_in_superscaffold));
                        } else {
                            sb.append(toMinusStrand(scaffold.get(as.scafold_fragment_subType_ID).substring(as.start_in_superscaffold, as.start_in_superscaffold + as.size_in_superscaffold)));
                        }
                        num_chrom += Math.abs(as.size_in_superscaffold);
                    } else {
                        System.err.println("scafold:" + as.scafold_fragment_subType_ID + " does not exist");
                        System.exit(-1);
                    }
                }
                int start_pos = 0;
                while (start_pos < sb.length() - 100) {
                    bw.write(sb.substring(start_pos, start_pos + 100) + "\n");
                    start_pos += 100;
                }
                bw.write(sb.substring(start_pos) + "\n");
            }
        }
        bw.close();
        bw_debris.close();
        System.out.println("num_chrom=" + num_chrom);
        System.out.println("num_debris=" + num_debris);
        System.out.println("num_debris_remove_N=" + num_debris_remove_N);
    }

    private String toMinusStrand(String seq) {
        String reverse = new StringBuffer(seq).reverse().toString();
        return reverse.replace("c", "@").replace("g", "c").replace("@", "g").replace("a", "#").replace("t", "a").replace("#", "t")
                .replace("C", "@").replace("G", "C").replace("@", "G").replace("A", "#").replace("T", "A").replace("#", "T");
    }

//    SupperScaffold get_assembly(int order) {
//        for (SupperScaffold as : assembly) {
//            if (as.order == Math.abs(order)) {
//                return as;
//            }
//        }
//        return null;
//    }    
}
