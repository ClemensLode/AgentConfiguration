/*
 * ConfigurationFrame.java
 *
 * Created on October 13, 2008, 7:58 AM
 */

package agentconfiguration;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.IOException;

import javax.swing.*;

/**
 *
 * @author Clemens Lode, 1151459, University Karlsruhe (TH)
 */


public class ConfigurationFrame extends javax.swing.JFrame {
    
    /** Creates new form ConfigurationFrame */
    public ConfigurationFrame() {
        initComponents();
        loadSettings("default_configuration.txt");
    }
    
    private void saveSettings(String file_name) {
        File my_file = new File(file_name);
        try {
            my_file.createNewFile();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error opening file " + my_file.getAbsoluteFile(), "Error opening file", JOptionPane.ERROR_MESSAGE);
            return;
        }
        FileOutputStream f;
        PrintStream p;
        try {
            f = new FileOutputStream(my_file.getAbsoluteFile());
            p = new PrintStream(f);

            p.println(Integer.valueOf(numberOfExperimentsTextField.getText()));
            p.println(Integer.valueOf(numberOfGARunsTextField.getText()));
            p.println(Integer.valueOf(gaRunLengthTextField.getText()));
            
            p.println(isEventDrivenCheckBox.isSelected());
            
            p.println(Integer.valueOf(maxXTextField.getText()));
            p.println(Integer.valueOf(maxYTextField.getText()));
            
            p.println(isTorusCheckBox.isSelected());
            p.println(Double.valueOf(rewardDistanceTextField.getText()));
            
            p.println(Double.valueOf(sightRangeTextField.getText()));
            p.println(Integer.valueOf(maxAgentsTextField.getText()));
            
            p.println(Integer.valueOf(maxStackSizeTextField.getText()));
            
            p.println(Double.valueOf(coveringWildcardProbabilityTextField.getText()));
            p.println(Double.valueOf(crossoverProbabilityTextField.getText()));
            p.println(Double.valueOf(crossoverMutationProbabilityTextField.getText()));
            
            p.println(Double.valueOf(elitistSelectionSizeTextField.getText()));
            p.println(Double.valueOf(evolutionaryMutationProbabilityTextField.getText()));
            p.println(Double.valueOf(rewardUpdateFactorTextField.getText()));
            p.println(doEvolutionaryAlgorithmCheckBox.isSelected());
            
            p.println(Double.valueOf(thetaSubsumerTextField.getText()));
            p.println(Double.valueOf(epsilon0TextField.getText()));
            
            p.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error writing to file " + my_file.getAbsoluteFile() + " : " + e, "Error writing file", JOptionPane.ERROR_MESSAGE);
        }

    }

    public void loadSettings(String file_name) {
        // load old settings if file exists
        File my_file = new File(file_name);
        if (my_file.exists()) {

            try {
                BufferedReader p = new BufferedReader(new FileReader(my_file.getAbsoluteFile()));

                numberOfExperimentsTextField.setText(Integer.valueOf(p.readLine()).toString());
                numberOfGARunsTextField.setText(Integer.valueOf(p.readLine()).toString());
                gaRunLengthTextField.setText(Integer.valueOf(p.readLine()).toString());
                
                isEventDrivenCheckBox.setSelected(Boolean.valueOf(p.readLine()));
                
                maxXTextField.setText(Integer.valueOf(p.readLine()).toString());
                maxYTextField.setText(Integer.valueOf(p.readLine()).toString());
                
                isTorusCheckBox.setSelected(Boolean.valueOf(p.readLine()));
                rewardDistanceTextField.setText(Double.valueOf(p.readLine()).toString());
                sightRangeTextField.setText(Double.valueOf(p.readLine()).toString());
    
                maxAgentsTextField.setText(Integer.valueOf(p.readLine()).toString());
    
    // number of steps for multi step problem
                maxStackSizeTextField.setText(Integer.valueOf(p.readLine()).toString());
    
                coveringWildcardProbabilityTextField.setText(Double.valueOf(p.readLine()).toString());
                crossoverProbabilityTextField.setText(Double.valueOf(p.readLine()).toString());
                crossoverMutationProbabilityTextField.setText(Double.valueOf(p.readLine()).toString());
                elitistSelectionSizeTextField.setText(Double.valueOf(p.readLine()).toString());
                evolutionaryMutationProbabilityTextField.setText(Double.valueOf(p.readLine()).toString());
                rewardUpdateFactorTextField.setText(Double.valueOf(p.readLine()).toString());
                doEvolutionaryAlgorithmCheckBox.setSelected(Boolean.valueOf(p.readLine()));

                thetaSubsumerTextField.setText(Double.valueOf(p.readLine()).toString());
                epsilon0TextField.setText(Double.valueOf(p.readLine()).toString());
                
                p.close();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "IO Exception: Error + " + e + " reading from file " + my_file.getAbsoluteFile(), "Error reading file", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "NumberFormatException: Error + " + e + " reading from file " + my_file.getAbsoluteFile(), "Error reading file", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Exception: Error + " + e + " reading from file " + my_file.getAbsoluteFile(), "Error reading file", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fileChooser = new javax.swing.JFileChooser();
        maxXLabel = new javax.swing.JLabel();
        maxYLabel = new javax.swing.JLabel();
        agentCountLabel = new javax.swing.JLabel();
        rewardDistanceLabel = new javax.swing.JLabel();
        maxStackSizeLabel = new javax.swing.JLabel();
        coveringWildcardProbabilityLabel = new javax.swing.JLabel();
        numberOfExperimentsLabel = new javax.swing.JLabel();
        lengthOfEachGArunLabel = new javax.swing.JLabel();
        elitistSelectionSizeLabel = new javax.swing.JLabel();
        mutationProbabilityEvoAlgLabel = new javax.swing.JLabel();
        crossoverProbabilityLabel = new javax.swing.JLabel();
        rewardUpdateFactorLabel = new javax.swing.JLabel();
        sightDistanceLabel = new javax.swing.JLabel();
        maxXTextField = new javax.swing.JTextField();
        maxYTextField = new javax.swing.JTextField();
        maxAgentsTextField = new javax.swing.JTextField();
        rewardDistanceTextField = new javax.swing.JTextField();
        maxStackSizeTextField = new javax.swing.JTextField();
        coveringWildcardProbabilityTextField = new javax.swing.JTextField();
        numberOfExperimentsTextField = new javax.swing.JTextField();
        gaRunLengthTextField = new javax.swing.JTextField();
        elitistSelectionSizeTextField = new javax.swing.JTextField();
        evolutionaryMutationProbabilityTextField = new javax.swing.JTextField();
        crossoverProbabilityTextField = new javax.swing.JTextField();
        rewardUpdateFactorTextField = new javax.swing.JTextField();
        sightRangeTextField = new javax.swing.JTextField();
        isTorusCheckBox = new javax.swing.JCheckBox();
        crossoverMutationProbabilityLabel = new javax.swing.JLabel();
        crossoverMutationProbabilityTextField = new javax.swing.JTextField();
        doEvolutionaryAlgorithmCheckBox = new javax.swing.JCheckBox();
        loadSettingsButton = new javax.swing.JButton();
        saveSettingsButton = new javax.swing.JButton();
        thetaSubsumerLabel = new javax.swing.JLabel();
        epsilon0Label = new javax.swing.JLabel();
        thetaSubsumerTextField = new javax.swing.JTextField();
        epsilon0TextField = new javax.swing.JTextField();
        numberOfGARunsTextField = new javax.swing.JTextField();
        numberOfGARunsLabel = new javax.swing.JLabel();
        isEventDrivenCheckBox = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Configuration File Editor");

        maxXLabel.setText("Max X");

        maxYLabel.setText("Max Y");

        agentCountLabel.setText("Number of Agents");

        rewardDistanceLabel.setText("Reward Distance");

        maxStackSizeLabel.setText("Stack Size (reward, multistep TODO)");

        coveringWildcardProbabilityLabel.setText("Covering Wildcard probability");

        numberOfExperimentsLabel.setText("Number of experiments");

        lengthOfEachGArunLabel.setText("Length of each GA run");

        elitistSelectionSizeLabel.setText("Elite selection size EvoAlg");

        mutationProbabilityEvoAlgLabel.setText("Mutation probability EvoAlg");

        crossoverProbabilityLabel.setText("crossover probability");

        rewardUpdateFactorLabel.setText("reward update factor");

        sightDistanceLabel.setText("Sight range");

        maxXTextField.setText("50");

        maxYTextField.setText("50");

        maxAgentsTextField.setText("5");

        rewardDistanceTextField.setText("4.0");

        maxStackSizeTextField.setText("32");

        coveringWildcardProbabilityTextField.setText("0.1");

        numberOfExperimentsTextField.setText("10");

        gaRunLengthTextField.setText("20");

        elitistSelectionSizeTextField.setText("0.2");

        evolutionaryMutationProbabilityTextField.setText("0.1");

        crossoverProbabilityTextField.setText("0.1");

        rewardUpdateFactorTextField.setText("0.1");

        sightRangeTextField.setText("5.0");

        isTorusCheckBox.setText("is grid Torus?");

        crossoverMutationProbabilityLabel.setText("crossover mutation probability");

        crossoverMutationProbabilityTextField.setText("0.1");

        doEvolutionaryAlgorithmCheckBox.setText("use evolutionary algorithm?");

        loadSettingsButton.setText("load settings...");
        loadSettingsButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                loadSettingsButtonMouseClicked(evt);
            }
        });

        saveSettingsButton.setText("save settings...");
        saveSettingsButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                saveSettingsButtonMouseClicked(evt);
            }
        });

        thetaSubsumerLabel.setText("Theta Subsumer");

        epsilon0Label.setText("Epsilon 0");

        thetaSubsumerTextField.setText("0.1");

        epsilon0TextField.setText("0.05");

        numberOfGARunsTextField.setText("500");

        numberOfGARunsLabel.setText("Number of GA runs");

        isEventDrivenCheckBox.setText("is event driven?");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(loadSettingsButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(saveSettingsButton))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(maxXLabel)
                            .addComponent(maxYLabel)
                            .addComponent(agentCountLabel)
                            .addComponent(rewardDistanceLabel)
                            .addComponent(maxStackSizeLabel)
                            .addComponent(coveringWildcardProbabilityLabel)
                            .addComponent(numberOfExperimentsLabel)
                            .addComponent(lengthOfEachGArunLabel)
                            .addComponent(elitistSelectionSizeLabel)
                            .addComponent(mutationProbabilityEvoAlgLabel)
                            .addComponent(crossoverProbabilityLabel)
                            .addComponent(crossoverMutationProbabilityLabel)
                            .addComponent(rewardUpdateFactorLabel)
                            .addComponent(isTorusCheckBox)
                            .addComponent(sightDistanceLabel)
                            .addComponent(epsilon0Label)
                            .addComponent(thetaSubsumerLabel)
                            .addComponent(numberOfGARunsLabel)
                            .addComponent(isEventDrivenCheckBox))
                        .addGap(32, 32, 32)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(doEvolutionaryAlgorithmCheckBox)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(maxAgentsTextField, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(maxYTextField, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(maxXTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(epsilon0TextField, javax.swing.GroupLayout.Alignment.LEADING, 0, 0, Short.MAX_VALUE)
                                .addComponent(thetaSubsumerTextField, javax.swing.GroupLayout.Alignment.LEADING, 0, 0, Short.MAX_VALUE)
                                .addComponent(rewardUpdateFactorTextField, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(crossoverMutationProbabilityTextField, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(crossoverProbabilityTextField, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(evolutionaryMutationProbabilityTextField, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(elitistSelectionSizeTextField, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(gaRunLengthTextField, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(numberOfExperimentsTextField, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(coveringWildcardProbabilityTextField, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(sightRangeTextField, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(maxStackSizeTextField, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(rewardDistanceTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
                                .addComponent(numberOfGARunsTextField, javax.swing.GroupLayout.Alignment.LEADING)))))
                .addContainerGap(274, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(maxXLabel)
                    .addComponent(maxXTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(maxYLabel)
                    .addComponent(maxYTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(agentCountLabel)
                    .addComponent(maxAgentsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(isTorusCheckBox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(isEventDrivenCheckBox))
                    .addComponent(doEvolutionaryAlgorithmCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rewardDistanceLabel)
                    .addComponent(rewardDistanceTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(maxStackSizeLabel)
                    .addComponent(maxStackSizeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sightDistanceLabel)
                    .addComponent(sightRangeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(coveringWildcardProbabilityLabel)
                    .addComponent(coveringWildcardProbabilityTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(numberOfExperimentsLabel)
                    .addComponent(numberOfExperimentsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(numberOfGARunsLabel)
                    .addComponent(numberOfGARunsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lengthOfEachGArunLabel)
                    .addComponent(gaRunLengthTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(elitistSelectionSizeLabel)
                    .addComponent(elitistSelectionSizeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mutationProbabilityEvoAlgLabel)
                    .addComponent(evolutionaryMutationProbabilityTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(crossoverProbabilityLabel)
                    .addComponent(crossoverProbabilityTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(crossoverMutationProbabilityLabel)
                    .addComponent(crossoverMutationProbabilityTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rewardUpdateFactorLabel)
                    .addComponent(rewardUpdateFactorTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(epsilon0Label))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(thetaSubsumerTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(thetaSubsumerLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(epsilon0TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(loadSettingsButton)
                    .addComponent(saveSettingsButton))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {rewardUpdateFactorTextField, thetaSubsumerTextField});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void saveSettingsButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_saveSettingsButtonMouseClicked
        if (fileChooser.showSaveDialog(this) == fileChooser.APPROVE_OPTION) {
            File my_file = fileChooser.getSelectedFile();
            try {
                my_file.createNewFile();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error opening file " + my_file.getAbsoluteFile(), "Error opening file", JOptionPane.ERROR_MESSAGE);
                return;
            }
            saveSettings(my_file.getAbsoluteFile().toString());
        }
    }//GEN-LAST:event_saveSettingsButtonMouseClicked

    private void loadSettingsButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_loadSettingsButtonMouseClicked
        if (fileChooser.showOpenDialog(this) == fileChooser.APPROVE_OPTION) {
            File my_file = fileChooser.getSelectedFile();
            if (!my_file.exists()) {
                JOptionPane.showMessageDialog(this, "Error opening file " + my_file.getAbsoluteFile(), "File not found", JOptionPane.ERROR_MESSAGE);
                return;
            }
            loadSettings(my_file.getAbsoluteFile().toString());
        }
    }//GEN-LAST:event_loadSettingsButtonMouseClicked
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ConfigurationFrame().setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel agentCountLabel;
    private javax.swing.JLabel coveringWildcardProbabilityLabel;
    private javax.swing.JTextField coveringWildcardProbabilityTextField;
    private javax.swing.JLabel crossoverMutationProbabilityLabel;
    private javax.swing.JTextField crossoverMutationProbabilityTextField;
    private javax.swing.JLabel crossoverProbabilityLabel;
    private javax.swing.JTextField crossoverProbabilityTextField;
    private javax.swing.JCheckBox doEvolutionaryAlgorithmCheckBox;
    private javax.swing.JLabel elitistSelectionSizeLabel;
    private javax.swing.JTextField elitistSelectionSizeTextField;
    private javax.swing.JLabel epsilon0Label;
    private javax.swing.JTextField epsilon0TextField;
    private javax.swing.JTextField evolutionaryMutationProbabilityTextField;
    private javax.swing.JFileChooser fileChooser;
    private javax.swing.JTextField gaRunLengthTextField;
    private javax.swing.JCheckBox isEventDrivenCheckBox;
    private javax.swing.JCheckBox isTorusCheckBox;
    private javax.swing.JLabel lengthOfEachGArunLabel;
    private javax.swing.JButton loadSettingsButton;
    private javax.swing.JTextField maxAgentsTextField;
    private javax.swing.JLabel maxStackSizeLabel;
    private javax.swing.JTextField maxStackSizeTextField;
    private javax.swing.JLabel maxXLabel;
    private javax.swing.JTextField maxXTextField;
    private javax.swing.JLabel maxYLabel;
    private javax.swing.JTextField maxYTextField;
    private javax.swing.JLabel mutationProbabilityEvoAlgLabel;
    private javax.swing.JLabel numberOfExperimentsLabel;
    private javax.swing.JTextField numberOfExperimentsTextField;
    private javax.swing.JLabel numberOfGARunsLabel;
    private javax.swing.JTextField numberOfGARunsTextField;
    private javax.swing.JLabel rewardDistanceLabel;
    private javax.swing.JTextField rewardDistanceTextField;
    private javax.swing.JLabel rewardUpdateFactorLabel;
    private javax.swing.JTextField rewardUpdateFactorTextField;
    private javax.swing.JButton saveSettingsButton;
    private javax.swing.JLabel sightDistanceLabel;
    private javax.swing.JTextField sightRangeTextField;
    private javax.swing.JLabel thetaSubsumerLabel;
    private javax.swing.JTextField thetaSubsumerTextField;
    // End of variables declaration//GEN-END:variables
    
}
