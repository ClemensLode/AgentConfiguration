/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package agentconfiguration;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.io.FileInputStream;
import java.nio.MappedByteBuffer;

/**
 *
 * @author Clemens Lode, 1151459, University Karlsruhe (TH)
 */
public class joschka {
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/** Fast & simple file copy. */
public static void copy(File source, File dest) throws IOException {
     FileChannel in = null, out = null;
     try {          
          in = new FileInputStream(source).getChannel();
          out = new FileOutputStream(dest).getChannel();
 
          long size = in.size();
          MappedByteBuffer buf = in.map(FileChannel.MapMode.READ_ONLY, 0, size);
 
          out.write(buf);
     } finally {
          if (in != null) {
              in.close();
          }
          if (out != null) {
              out.close();
          }
     }
}
    
    /**
     * @param args the command line arguments
     */
    public static void run() {
        File dir = new File(".");
        
        FileFilter plotFileFilter = new FileFilter() {
            public boolean accept(File file) {
                if(file.getName().startsWith("plot-all-")) {
                    return true;
                } else {
                    return false;
                }
            }
        };        
        FileFilter configFileFilter = new FileFilter() {
            public boolean accept(File file) {
                if(file.getName().startsWith("config-")) {
                    return true;
                } else {
                    return false;
                }
            }
        };
        FileFilter outputDirectoriesFilter = new FileFilter() {
            public boolean accept(File file) {
                if(file.isDirectory() && file.getName().startsWith("output")) {
                    return true;
                } else {
                    return false;
                }
            }
        };        
        
        File[] config_files = dir.listFiles(configFileFilter);
        File[] output_directories = dir.listFiles(outputDirectoriesFilter);
        File[] all_plot_files = dir.listFiles(plotFileFilter);
        
        ArrayList<String> open_calculations = new ArrayList<String>();
        
        for(File g : config_files) {
            String name = g.getName();
            String date = name.substring(7, name.length() - 4);
            boolean directory_found = false;
            for(File f : output_directories) {
                if(f.getName().endsWith(date)) {
                    directory_found = true;
                    break;
                }
            }
            if(!directory_found) {
                open_calculations.add(name);
            }
        }

        SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yy--HH-mm-ss-SS");
        String new_date = fmt.format(new Date());
        // job file
        String joschka_file_name = "joschka-agent-" + new_date + ".txt";
        // directory to copy to ceres
        String outputDirectory = "agent-" + new_date;
        String batch_file_name = outputDirectory + "\\batch-agent-" + new_date + ".bat";
        
        File output_dir = new File(outputDirectory);
        if(!output_dir.exists()) {
            output_dir.mkdir();
        }
        // copy files
        for(String config_file : open_calculations) {
            File f = new File(config_file);
            if(!f.renameTo(new File(outputDirectory + "\\" + config_file))) {
                System.out.println("Could not move config file to work directory.");
                return;
            }
        }
        
        for(File plot_file : all_plot_files) {
            if(!plot_file.renameTo(new File(outputDirectory + "\\" + plot_file))) {
                System.out.println("Could not move plot file to work directory.");
                return;                
            }
        }
        
        
        
        File j = new File("dist\\agentsimulator.jar");
        File dj = new File(outputDirectory + "\\agentsimulator.jar");
        try {
            copy(j, dj);
        } catch(IOException e) {
            System.out.println("Error copying agentsimulator.jar file");
        }
        
        
        File batch_file = new File(batch_file_name);
        File joschka_file = new File(joschka_file_name);
        try {
            joschka_file.createNewFile();
            batch_file.createNewFile();
        } catch (Exception e) {
            System.out.println("Error opening file " + joschka_file.getAbsoluteFile());
            return;
        }
        FileOutputStream f_joschka;
        PrintStream p_joschka;
        FileOutputStream f_batch;
        PrintStream p_batch;
        try {
            f_joschka = new FileOutputStream(joschka_file.getAbsoluteFile());
            p_joschka = new PrintStream(f_joschka);
            f_batch = new FileOutputStream(batch_file.getAbsoluteFile());
            p_batch = new PrintStream(f_batch);
            String batch_entry = new String("java -jar agentsimulator.jar");            

            int index = 1;
            for(String config_file : open_calculations) {
                String entry = new String("");
                entry += "cllo_" + outputDirectory + "\t";
                entry += "J\t";
                entry += "java -jar agentsimulator.jar " + config_file + "\t";
                entry += "*\t";
                entry += "NO\t";
                entry += "agentsimulator.jar;" + config_file + "\t\t\t";
                
                // job name
                entry += outputDirectory + "-" + index;
                p_joschka.println(entry);
                
                batch_entry += " " + config_file;
               
                
                index++;
            }
            batch_entry += "\n";
            p_batch.print(batch_entry);
            p_joschka.close();
            p_batch.close();
        } catch(Exception e) {
            System.out.println("Error writing to file " + joschka_file.getAbsoluteFile());
        }     
        
    }

}
