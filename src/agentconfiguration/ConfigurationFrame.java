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
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import java.util.ArrayList;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.FileFilter;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

import agent.Configuration;
/**
 *
 * @author Clemens Lode, 1151459, University Karlsruhe (TH)
 */
public class ConfigurationFrame extends javax.swing.JFrame {

    private class SelectionListener implements ListSelectionListener {
        private JTable table;

        SelectionListener(JTable table) {
            this.table = table;
        }

        public void valueChanged(ListSelectionEvent e) {
         if (e.getValueIsAdjusting()) {
             return;
          }
          int row = sorter.modelIndex(table.getSelectedRow());
          Object[] my_row = results.getRow(row);
          String config_string = (String)(my_row[0]);
          loadSettings(config_string);
        }
    }

    private static BufferedWriter plot_out;
    static String timeString = new String("");
    static int conf_id = 0;
    static ArrayList<String> config_strings = new ArrayList<String>();

        
    /**
     * result database
     */
        ResultsTable results = new ResultsTable();
        TableSorter sorter = new TableSorter(results);     
        
    /** Creates new form ConfigurationFrame */
    public ConfigurationFrame() {
        initComponents();
        resetTimeString();
        loadSettings("default.txt");
        loadResultsIntoDatabase();

        resultsTable.setModel(sorter);
        sorter.setTableHeader(resultsTable.getTableHeader());
        resultsTable.getTableHeader().setToolTipText(
                "Click to specify sorting; Control-Click to specify secondary sorting");


        ListSelectionModel listSelectionModel = resultsTable.getSelectionModel();
        listSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listSelectionModel.addListSelectionListener(new SelectionListener(resultsTable));
        resultsTable.setSelectionModel(listSelectionModel);
    }



    /**
     * load data from a BufferedReader into the results database
     * @param p A BufferedReader device, e.g. a file
     * @throws java.lang.NumberFormatException Error parsing an integer field
     * @throws java.io.IOException Error reading from BufferedReader
     */
    private void loadResultsIntoDatabase() {
        results.datas.clear();
        
        /**
         * search for all Agent-*** Directories
         * gather there all config-* names 
         * read the appropriate goal_percentage file
         * 
         */
        File dir = new File(".");
        
        FileFilter agentDirectoriesFilter = new FileFilter() {
            public boolean accept(File file) {
                if(file.isDirectory() && file.getName().startsWith("agent-")) {
                    return true;
                } else {
                    return false;
                }
            }
        };        
        
        FileFilter outputDirectoriesFilter = new FileFilter() {
            public boolean accept(File file) {
                if(file.getName().startsWith("output_")) {
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

        File[] agent_directories = dir.listFiles(agentDirectoriesFilter);
        for(File agent_directory : agent_directories) {
            File[] output_directories = agent_directory.listFiles(outputDirectoriesFilter);
            for(File output_directory : output_directories) {
                String directory_string = agent_directory.getName() + "//" + output_directory.getName() + "//";
                String id_string = output_directory.getName().substring(7, output_directory.getName().length());
                String config_string = directory_string + "config-" + id_string + ".txt";
                File result_file = new File(directory_string + "results-" + id_string + ".dat");
                File config_file = new File(config_string);
                String spread_individual_total_points = new String("--");
                String spreadIndividualTotalPoints = new String("--");
                String averageIndividualTotalPoints = new String("--");
                String spreadAgentDistance = new String("--");
                String spreadGoalAgentDistance = new String("--");
                String averageAgentDistance = new String("--");
                String averageGoalAgentDistance = new String("--");
                String coveredAreaFactor = new String("--");
                String goalAgentObservedPercentage = new String("--");
                
                if(result_file.exists()) {
                    try {
                        BufferedReader p = new BufferedReader(new FileReader(result_file.getAbsoluteFile()));
                        spreadIndividualTotalPoints = p.readLine();
                        averageIndividualTotalPoints = p.readLine();
                        spreadAgentDistance = p.readLine();
                        spreadGoalAgentDistance = p.readLine();
                        averageAgentDistance = p.readLine();
                        averageGoalAgentDistance = p.readLine();
                        coveredAreaFactor = p.readLine();
                        goalAgentObservedPercentage = p.readLine();
                        
                        p.close();
                    } catch(Exception e) {
                        JOptionPane.showMessageDialog(this, "Error opening/reading file " + result_file.getAbsoluteFile() + "(" + e + ")", "Error opening/reading file", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                Object[] object_row = new Object[9];
                object_row[0] = new String(config_string);
                object_row[1] = new String(spreadIndividualTotalPoints);
                object_row[2] = new String(averageIndividualTotalPoints);
                object_row[3] = new String(spreadAgentDistance);
                object_row[4] = new String(averageAgentDistance);
                object_row[5] = new String(spreadGoalAgentDistance);
                object_row[6] = new String(averageGoalAgentDistance);
                object_row[7] = new String(coveredAreaFactor);
                object_row[8] = new String(goalAgentObservedPercentage);
                results.datas.add(object_row);
            }
        }
        results.fireTableDataChanged();
    }    
    
    private void resetTimeString() {
        SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yy--HH-mm-ss-SS");
        timeString = fmt.format(new Date());        
        conf_id = 0;
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

            p.println(Long.valueOf(randomSeedTextField.getText()));
            p.println(Integer.valueOf(numberOfExperimentsTextField.getText()));
            p.println(Integer.valueOf(numberOfProblemsTextField.getText()));
            p.println(Integer.valueOf(numberOfStepsTextField.getText()));

            p.println(Integer.valueOf(maxPopSizeTextField.getText()));

            p.println(isEventDrivenCheckBox.isSelected());

            p.println(Integer.valueOf(maxXTextField.getText()));
            p.println(Integer.valueOf(maxYTextField.getText()));

            p.println(isTorusCheckBox.isSelected());
            
            p.println(withObstaclesCheckBox.isSelected());
            p.println(obstaclesBlockSightCheckBox.isSelected());
            p.println(Double.valueOf(obstaclePercentageTextField.getText()));
            p.println(Double.valueOf(obstacleConnectionFactorTextField.getText()));
            
            p.println(Double.valueOf(rewardDistanceTextField.getText()));

            p.println(Double.valueOf(sightRangeTextField.getText()));
            p.println(Integer.valueOf(maxAgentsTextField.getText()));

            p.println(Integer.valueOf(maxStackSizeTextField.getText()));

            p.println(Double.valueOf(coveringWildcardProbabilityTextField.getText()));
            p.println(doEvolutionaryAlgorithmCheckBox.isSelected());

            p.println(Double.valueOf(thetaSubsumerTextField.getText()));
            p.println(Double.valueOf(epsilon0TextField.getText()));

            p.println(Double.valueOf(betaTextField.getText()));

            p.println(Double.valueOf(predictionInitializationTextField.getText()));
            p.println(Double.valueOf(predictionErrorInitializationTextField.getText()));
            p.println(Double.valueOf(fitnessInitializationTextField.getText()));

            p.println(Double.valueOf(deltaTextField.getText()));
            p.println(Double.valueOf(thetaDelTextField.getText()));

            p.println(doActionSetSubsumptionCheckBox.isSelected());

            p.println(Double.valueOf(alphaTextField.getText()));
            p.println(Double.valueOf(gammaTextField.getText()));
            p.println(Double.valueOf(nuTextField.getText()));
            p.println(Double.valueOf(thetaTextField.getText()));

            p.println(Double.valueOf(predictionErrorReductionTextField.getText()));
            p.println(Double.valueOf(fitnessReductionTextField.getText()));
            p.println(Double.valueOf(mutationProbabilityTextField.getText()));

            p.println(doGASubsumptionCheckBox.isSelected());
            
            if(noExplorationModeRadioButton.isSelected()) {
                p.println(Configuration.NO_EXPLORATION_MODE);
            } else 
            if(alwaysExplorationModeRadioButton.isSelected()) {
                p.println(Configuration.ALWAYS_EXPLORATION_MODE);
            } else
            if(switchExplorationAndExploitationModeRadioButton.isSelected()) {
                p.println(Configuration.SWITCH_EXPLORATION_MODE);
            } else
            if(exploreThenExploitModeRadioButton.isSelected()) {
                p.println(Configuration.EXPLORE_THEN_EXPLOIT_MODE);
            } else 
            if(linearExplorationReductionModeRadioButton.isSelected()) {
                p.println(Configuration.LINEAR_REDUCTION_EXPLORE_MODE);
            }
            
            if(totalRandomGoalAgentMovementRadioButton.isSelected()) {            
                p.println(Configuration.TOTAL_RANDOM_MOVEMENT);
            } else
            if(randomGoalAgentMovementRadioButton.isSelected()) {            
                p.println(Configuration.RANDOM_MOVEMENT);
            } else
            if(intelligentOpenGoalAgentMovementRadioButton.isSelected()) {
                p.println(Configuration.INTELLIGENT_MOVEMENT_OPEN);
            } else
            if(intelligentHideGoalAgentMovementRadioButton.isSelected()) {
                p.println(Configuration.INTELLIGENT_MOVEMENT_HIDE);
            } else
            if(maxOneDirectionChangeGoalAgentMovementRadioButton.isSelected()) {            
                p.println(Configuration.RANDOM_DIRECTION_CHANGE);
            } else
            if(alwaysInTheSameDirectionGoalAgentMovementRadioButton.isSelected()) {            
                p.println(Configuration.ALWAYS_SAME_DIRECTION);
            }                 
            
            p.println(Integer.valueOf(goalAgentMovementSpeedTextField.getText()));
            
            if(randomizedMovementRadioButton.isSelected()) {
                p.println(Configuration.RANDOMIZED_MOVEMENT_AGENT_TYPE);
            } else
            if(simpleAIAgentRadioButton.isSelected()) {
                p.println(Configuration.SIMPLE_AI_AGENT_TYPE);
            } else
            if(intelligentAIAgentRadioButton.isSelected()) {
                p.println(Configuration.INTELLIGENT_AI_AGENT_TYPE);
            } else
            if(singleStepLCSAgentRadioButton.isSelected()) {
                p.println(Configuration.SINGLE_STEP_LCS_AGENT_TYPE);
            } else
            if(multiStepLCSAgentRadioButton.isSelected()) {
                p.println(Configuration.MULTI_STEP_LCS_AGENT_TYPE);
            } else
            if(newLCSAgentRadioButton.isSelected()) {
                p.println(Configuration.NEW_LCS_AGENT_TYPE);
            }
            
            if(noExternalRewardRadioButton.isSelected()) {
                p.println(Configuration.NO_EXTERNAL_REWARD);
            } else 
            if(rewardAllEquallyRadioButton.isSelected()) {
                p.println(Configuration.REWARD_ALL_EQUALLY);
            } else
            if(rewardSimpleRadioButton.isSelected()) {
                p.println(Configuration.REWARD_SIMPLE);
            } else
            if(rewardComplexRadioButton.isSelected()) {
                p.println(Configuration.REWARD_COMPLEX);
            }

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

                randomSeedTextField.setText(Long.valueOf(p.readLine()).toString());
                numberOfExperimentsTextField.setText(Integer.valueOf(p.readLine()).toString());
                numberOfProblemsTextField.setText(Integer.valueOf(p.readLine()).toString());
                numberOfStepsTextField.setText(Integer.valueOf(p.readLine()).toString());

                maxPopSizeTextField.setText(Integer.valueOf(p.readLine()).toString());

                isEventDrivenCheckBox.setSelected(Boolean.valueOf(p.readLine()));

                maxXTextField.setText(Integer.valueOf(p.readLine()).toString());
                maxYTextField.setText(Integer.valueOf(p.readLine()).toString());

                isTorusCheckBox.setSelected(Boolean.valueOf(p.readLine()));
                

                withObstaclesCheckBox.setSelected(Boolean.valueOf(p.readLine()));
                if(withObstaclesCheckBox.isSelected()) {
                    obstaclePercentageLabel.setEnabled(true);
                    obstaclePercentageTextField.setEnabled(true);
                    obstacleConnectionFactorLabel.setEnabled(true);
                    obstacleConnectionFactorTextField.setEnabled(true);
                    obstaclesBlockSightCheckBox.setEnabled(true);
                }
                obstaclesBlockSightCheckBox.setSelected(Boolean.valueOf(p.readLine()));
                obstaclePercentageTextField.setText(Double.valueOf(p.readLine()).toString());
                obstacleConnectionFactorTextField.setText(Double.valueOf(p.readLine()).toString());
                
                rewardDistanceTextField.setText(Double.valueOf(p.readLine()).toString());
                sightRangeTextField.setText(Double.valueOf(p.readLine()).toString());

                maxAgentsTextField.setText(Integer.valueOf(p.readLine()).toString());

                // number of steps for multi step problem
                maxStackSizeTextField.setText(Integer.valueOf(p.readLine()).toString());

                coveringWildcardProbabilityTextField.setText(Double.valueOf(p.readLine()).toString());
                doEvolutionaryAlgorithmCheckBox.setSelected(Boolean.valueOf(p.readLine()));

                thetaSubsumerTextField.setText(Double.valueOf(p.readLine()).toString());
                epsilon0TextField.setText(Double.valueOf(p.readLine()).toString());

                betaTextField.setText(Double.valueOf(p.readLine()).toString());

                predictionInitializationTextField.setText(Double.valueOf(p.readLine()).toString());
                predictionErrorInitializationTextField.setText(Double.valueOf(p.readLine()).toString());
                fitnessInitializationTextField.setText(Double.valueOf(p.readLine()).toString());

                deltaTextField.setText(Double.valueOf(p.readLine()).toString());
                thetaDelTextField.setText(Double.valueOf(p.readLine()).toString());

                doActionSetSubsumptionCheckBox.setSelected(Boolean.valueOf(p.readLine()));

                alphaTextField.setText(Double.valueOf(p.readLine()).toString());
                gammaTextField.setText(Double.valueOf(p.readLine()).toString());
                nuTextField.setText(Double.valueOf(p.readLine()).toString());
                thetaTextField.setText(Double.valueOf(p.readLine()).toString());

                predictionErrorReductionTextField.setText(Double.valueOf(p.readLine()).toString());
                fitnessReductionTextField.setText(Double.valueOf(p.readLine()).toString());
                mutationProbabilityTextField.setText(Double.valueOf(p.readLine()).toString());

                doGASubsumptionCheckBox.setSelected(Boolean.valueOf(p.readLine()));
                
                int selected_exploration_mode = Integer.valueOf(p.readLine());
                
                switch(selected_exploration_mode) {
                    case Configuration.NO_EXPLORATION_MODE:explorationModeButtonGroup.setSelected(noExplorationModeRadioButton.getModel(), true);break;
                    case Configuration.ALWAYS_EXPLORATION_MODE:explorationModeButtonGroup.setSelected(alwaysExplorationModeRadioButton.getModel(), true);break;
                    case Configuration.SWITCH_EXPLORATION_MODE:explorationModeButtonGroup.setSelected(switchExplorationAndExploitationModeRadioButton.getModel(), true);break;
                    case Configuration.EXPLORE_THEN_EXPLOIT_MODE:explorationModeButtonGroup.setSelected(exploreThenExploitModeRadioButton.getModel(), true);break;
                    case Configuration.LINEAR_REDUCTION_EXPLORE_MODE:explorationModeButtonGroup.setSelected(linearExplorationReductionModeRadioButton.getModel(), true);break;
                    default:throw new Exception("Exploration mode type invalid");
                }

                int selected_goal_agent_movement = Integer.valueOf(p.readLine());
                
                switch(selected_goal_agent_movement) {
                    case Configuration.TOTAL_RANDOM_MOVEMENT:goalAgentMovementButtonGroup.setSelected(totalRandomGoalAgentMovementRadioButton.getModel(), true);break;
                    case Configuration.RANDOM_MOVEMENT:goalAgentMovementButtonGroup.setSelected(randomGoalAgentMovementRadioButton.getModel(), true);break;
                    case Configuration.INTELLIGENT_MOVEMENT_OPEN:goalAgentMovementButtonGroup.setSelected(intelligentOpenGoalAgentMovementRadioButton.getModel(), true);break;
                    case Configuration.INTELLIGENT_MOVEMENT_HIDE:goalAgentMovementButtonGroup.setSelected(intelligentHideGoalAgentMovementRadioButton.getModel(), true);break;
                    case Configuration.RANDOM_DIRECTION_CHANGE:goalAgentMovementButtonGroup.setSelected(maxOneDirectionChangeGoalAgentMovementRadioButton.getModel(), true);break;
                    case Configuration.ALWAYS_SAME_DIRECTION:goalAgentMovementButtonGroup.setSelected(alwaysInTheSameDirectionGoalAgentMovementRadioButton.getModel(), true);break;
                    default:throw new Exception("Goal movement type invalid");
                }
    
                
                goalAgentMovementSpeedTextField.setText(Integer.valueOf(p.readLine()).toString());
                
                int selected_agent_type = Integer.valueOf(p.readLine());
                
                switch(selected_agent_type) {
                    case Configuration.RANDOMIZED_MOVEMENT_AGENT_TYPE:agentTypeButtonGroup.setSelected(randomizedMovementRadioButton.getModel(), true);break;
                    case Configuration.SIMPLE_AI_AGENT_TYPE:agentTypeButtonGroup.setSelected(simpleAIAgentRadioButton.getModel(), true);break;
                    case Configuration.INTELLIGENT_AI_AGENT_TYPE:agentTypeButtonGroup.setSelected(intelligentAIAgentRadioButton.getModel(), true);break;
                    case Configuration.SINGLE_STEP_LCS_AGENT_TYPE:agentTypeButtonGroup.setSelected(singleStepLCSAgentRadioButton.getModel(), true);break;
                    case Configuration.MULTI_STEP_LCS_AGENT_TYPE:agentTypeButtonGroup.setSelected(multiStepLCSAgentRadioButton.getModel(), true);break;
                    case Configuration.NEW_LCS_AGENT_TYPE:agentTypeButtonGroup.setSelected(newLCSAgentRadioButton.getModel(), true);break;
                    default:throw new Exception("Agent type invalid");
                }    
                
                int selected_external_reward = Integer.valueOf(p.readLine());
                
                switch(selected_external_reward) {
                    case Configuration.NO_EXTERNAL_REWARD:externalRewardButtonGroup.setSelected(noExternalRewardRadioButton.getModel(), true);break;
                    case Configuration.REWARD_ALL_EQUALLY:externalRewardButtonGroup.setSelected(rewardAllEquallyRadioButton.getModel(), true);break;
                    case Configuration.REWARD_SIMPLE:externalRewardButtonGroup.setSelected(rewardSimpleRadioButton.getModel(), true);break;
                    case Configuration.REWARD_COMPLEX:externalRewardButtonGroup.setSelected(rewardComplexRadioButton.getModel(), true);break;
                    default:throw new Exception("External reward invalid");
                }
                
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
    
   
    public void createAllPlotFile() {
        String entry = new String("");
        int number_steps = Integer.valueOf(numberOfStepsTextField.getText());
        int number_problems = Integer.valueOf(numberOfProblemsTextField.getText());
        double sight_range = Double.valueOf(sightRangeTextField.getText());
        
        entry += "set key left box\n" + 
                  "set xrange [0:" + number_steps * number_problems + "]\n";
        String file_name = "plot-all-" + timeString + ".plt";
        
        String header = new String("");
        String do_plot1 = new String("");
        String do_plot2 = new String("");
        
        header += "set output \"plot_";
        do_plot1 += ".eps\"\n" +
                   "set terminal postscript eps\n" + 
                   "plot ";
        do_plot2 += ".png\"\n" + 
                   "set terminal png\n" +
                   "plot ";
                         
        String[] stats = {"points_spread", "points_average", "distance_spread", "goal_agent_distance_spread", "distance_average", "goal_agent_distance_average", "covered_area", "goal_percentage"};
        String[] yrange = {"0:" + number_steps*number_problems/10, "0:" + number_steps*number_problems, "0:" + 2.0*sight_range, "0:" + 2.0*sight_range, "0:" + 2*sight_range, "0:" + 2*sight_range, "0.0:1.0", "0.0:1.0"};
        entry = new String("");
  
        int n = 0;
        for(String s : stats) {
            entry += "set yrange [" + yrange[n] + "]\n";
            n++;
            String dat_files = new String("");
            int nn = config_strings.size();
            for(String c : config_strings) {
                dat_files += "\"output_" + c + "\\\\" + s + "-" + c + ".dat\" with lines";
                if(nn > 1) {
                    dat_files += ", ";
                }
                nn--;
            }
            dat_files += "\n";
            entry += header + s + "-" + timeString + do_plot1 + dat_files;
            entry += header + s + "-" + timeString + do_plot2 + dat_files;
        }
        
        try {
            plot_out = new BufferedWriter(new FileWriter(file_name, true));
            plot_out.write(entry);
            plot_out.flush();
            plot_out.close();            
        } catch(Exception e) {
            JOptionPane.showMessageDialog(this, "Unable to open plot file: " + e + " (" + file_name + ")", "Error opening file", JOptionPane.ERROR_MESSAGE);
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
        goalAgentMovementButtonGroup = new javax.swing.ButtonGroup();
        agentTypeButtonGroup = new javax.swing.ButtonGroup();
        explorationModeButtonGroup = new javax.swing.ButtonGroup();
        externalRewardButtonGroup = new javax.swing.ButtonGroup();
        loadSettingsButton = new javax.swing.JButton();
        saveSettingsButton = new javax.swing.JButton();
        saveNewButton = new javax.swing.JButton();
        problemDefinitionPanel = new javax.swing.JPanel();
        gridPanel = new javax.swing.JPanel();
        withObstaclesCheckBox = new javax.swing.JCheckBox();
        obstaclesBlockSightCheckBox = new javax.swing.JCheckBox();
        obstaclePercentageLabel = new javax.swing.JLabel();
        obstaclePercentageTextField = new javax.swing.JTextField();
        obstacleConnectionFactorTextField = new javax.swing.JTextField();
        isTorusCheckBox = new javax.swing.JCheckBox();
        maxXLabel = new javax.swing.JLabel();
        maxXTextField = new javax.swing.JTextField();
        maxYTextField = new javax.swing.JTextField();
        rewardDistanceLabel = new javax.swing.JLabel();
        sightRangeTextField = new javax.swing.JTextField();
        obstacleConnectionFactorLabel = new javax.swing.JLabel();
        testsPanel = new javax.swing.JPanel();
        numberOfExperimentsLabel = new javax.swing.JLabel();
        numberOfExperimentsTextField = new javax.swing.JTextField();
        numberOfProblemsTextField = new javax.swing.JTextField();
        numberOfStepsTextField = new javax.swing.JTextField();
        randomSeedLabel = new javax.swing.JLabel();
        randomSeedTextField = new javax.swing.JTextField();
        agentCountLabel = new javax.swing.JLabel();
        maxAgentsTextField = new javax.swing.JTextField();
        stepsLabel = new javax.swing.JLabel();
        problemsLabel = new javax.swing.JLabel();
        goalAgentMovementPanel = new javax.swing.JPanel();
        totalRandomGoalAgentMovementRadioButton = new javax.swing.JRadioButton();
        randomGoalAgentMovementRadioButton = new javax.swing.JRadioButton();
        maxOneDirectionChangeGoalAgentMovementRadioButton = new javax.swing.JRadioButton();
        alwaysInTheSameDirectionGoalAgentMovementRadioButton = new javax.swing.JRadioButton();
        goalAgentMovementSpeedLabel = new javax.swing.JLabel();
        goalAgentMovementSpeedTextField = new javax.swing.JTextField();
        intelligentHideGoalAgentMovementRadioButton = new javax.swing.JRadioButton();
        intelligentOpenGoalAgentMovementRadioButton = new javax.swing.JRadioButton();
        lcsParametersPanel = new javax.swing.JPanel();
        gaParametersPanel = new javax.swing.JPanel();
        thetaLabel = new javax.swing.JLabel();
        predictionErrorReductionLabel = new javax.swing.JLabel();
        fitnessReductionLabel = new javax.swing.JLabel();
        fitnessReductionTextField = new javax.swing.JTextField();
        predictionErrorReductionTextField = new javax.swing.JTextField();
        thetaTextField = new javax.swing.JTextField();
        mutationProbabilityLabel = new javax.swing.JLabel();
        mutationProbabilityTextField = new javax.swing.JTextField();
        doEvolutionaryAlgorithmCheckBox = new javax.swing.JCheckBox();
        fitnessAndPredictionPanel = new javax.swing.JPanel();
        predictionInitializationLabel = new javax.swing.JLabel();
        predictionErrorInitializationLabel = new javax.swing.JLabel();
        fitnessInitializationLabel = new javax.swing.JLabel();
        fitnessInitializationTextField = new javax.swing.JTextField();
        predictionErrorInitializationTextField = new javax.swing.JTextField();
        predictionInitializationTextField = new javax.swing.JTextField();
        epsilon0Label = new javax.swing.JLabel();
        epsilon0TextField = new javax.swing.JTextField();
        alphaLabel = new javax.swing.JLabel();
        betaLabel = new javax.swing.JLabel();
        nuLabel = new javax.swing.JLabel();
        betaTextField = new javax.swing.JTextField();
        alphaTextField = new javax.swing.JTextField();
        nuTextField = new javax.swing.JTextField();
        gammaLabel = new javax.swing.JLabel();
        gammaTextField = new javax.swing.JTextField();
        classifierSubsumptionAndDeletionPanel = new javax.swing.JPanel();
        thetaSubsumerLabel = new javax.swing.JLabel();
        thetaSubsumerTextField = new javax.swing.JTextField();
        deltaLabel = new javax.swing.JLabel();
        thetaDelLabel = new javax.swing.JLabel();
        deltaTextField = new javax.swing.JTextField();
        thetaDelTextField = new javax.swing.JTextField();
        maxPopSizeLabel = new javax.swing.JLabel();
        maxPopSizeTextField = new javax.swing.JTextField();
        doGASubsumptionCheckBox = new javax.swing.JCheckBox();
        doActionSetSubsumptionCheckBox = new javax.swing.JCheckBox();
        coveringWildcardProbabilityLabel = new javax.swing.JLabel();
        coveringWildcardProbabilityTextField = new javax.swing.JTextField();
        agentTypePanel = new javax.swing.JPanel();
        randomizedMovementRadioButton = new javax.swing.JRadioButton();
        simpleAIAgentRadioButton = new javax.swing.JRadioButton();
        intelligentAIAgentRadioButton = new javax.swing.JRadioButton();
        singleStepLCSAgentRadioButton = new javax.swing.JRadioButton();
        multiStepLCSAgentRadioButton = new javax.swing.JRadioButton();
        newLCSAgentRadioButton = new javax.swing.JRadioButton();
        explorationModePanel = new javax.swing.JPanel();
        noExplorationModeRadioButton = new javax.swing.JRadioButton();
        alwaysExplorationModeRadioButton = new javax.swing.JRadioButton();
        switchExplorationAndExploitationModeRadioButton = new javax.swing.JRadioButton();
        exploreThenExploitModeRadioButton = new javax.swing.JRadioButton();
        linearExplorationReductionModeRadioButton = new javax.swing.JRadioButton();
        packageButton = new javax.swing.JButton();
        rewardModelPanel = new javax.swing.JPanel();
        maxStackSizeLabel = new javax.swing.JLabel();
        maxStackSizeTextField = new javax.swing.JTextField();
        isEventDrivenCheckBox = new javax.swing.JCheckBox();
        noExternalRewardRadioButton = new javax.swing.JRadioButton();
        rewardAllEquallyRadioButton = new javax.swing.JRadioButton();
        rewardSimpleRadioButton = new javax.swing.JRadioButton();
        rewardComplexRadioButton = new javax.swing.JRadioButton();
        rewardDistanceTextField = new javax.swing.JTextField();
        rewardRangeLabel = new javax.swing.JLabel();
        clemensLodeLabel = new javax.swing.JLabel();
        clemensMailLabel = new javax.swing.JLabel();
        resultsScrollPane = new javax.swing.JScrollPane();
        resultsTable = new javax.swing.JTable();
        updateDatabaseButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Agent Configuration File Editor v1.00");
        setResizable(false);

        loadSettingsButton.setFont(new java.awt.Font("Arial", 0, 12));
        loadSettingsButton.setText("Load settings...");
        loadSettingsButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                loadSettingsButtonMouseClicked(evt);
            }
        });

        saveSettingsButton.setFont(new java.awt.Font("Arial", 0, 12));
        saveSettingsButton.setText("Save settings...");
        saveSettingsButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                saveSettingsButtonMouseClicked(evt);
            }
        });

        saveNewButton.setFont(new java.awt.Font("Tahoma", 1, 11));
        saveNewButton.setText("Save");
        saveNewButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                saveNewButtonMouseClicked(evt);
            }
        });

        problemDefinitionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Problem definition", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        gridPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Grid", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        withObstaclesCheckBox.setFont(new java.awt.Font("Arial", 0, 12));
        withObstaclesCheckBox.setText("with obstacles?");
        withObstaclesCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                withObstaclesCheckBoxActionPerformed(evt);
            }
        });

        obstaclesBlockSightCheckBox.setFont(new java.awt.Font("Arial", 0, 12));
        obstaclesBlockSightCheckBox.setText("block sight?");
        obstaclesBlockSightCheckBox.setEnabled(false);

        obstaclePercentageLabel.setFont(new java.awt.Font("Arial", 0, 12));
        obstaclePercentageLabel.setText("Grid percentage");
        obstaclePercentageLabel.setToolTipText("Percentage of the grid that is occupied by obstacles");
        obstaclePercentageLabel.setEnabled(false);

        obstaclePercentageTextField.setText("20");
        obstaclePercentageTextField.setEnabled(false);

        obstacleConnectionFactorTextField.setText("0.1");
        obstacleConnectionFactorTextField.setEnabled(false);

        isTorusCheckBox.setFont(new java.awt.Font("Arial", 0, 12));
        isTorusCheckBox.setSelected(true);
        isTorusCheckBox.setText("Is grid torus?");

        maxXLabel.setFont(new java.awt.Font("Arial", 0, 12));
        maxXLabel.setText("Max X / Max Y");

        maxXTextField.setText("32");

        maxYTextField.setText("32");

        rewardDistanceLabel.setFont(new java.awt.Font("Arial", 0, 12));
        rewardDistanceLabel.setText("Sight range");

        sightRangeTextField.setText("5");

        obstacleConnectionFactorLabel.setFont(new java.awt.Font("Arial", 0, 12));
        obstacleConnectionFactorLabel.setText("Connection factor");
        obstacleConnectionFactorLabel.setEnabled(false);

        javax.swing.GroupLayout gridPanelLayout = new javax.swing.GroupLayout(gridPanel);
        gridPanel.setLayout(gridPanelLayout);
        gridPanelLayout.setHorizontalGroup(
            gridPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(gridPanelLayout.createSequentialGroup()
                .addGroup(gridPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(isTorusCheckBox)
                    .addGroup(gridPanelLayout.createSequentialGroup()
                        .addComponent(maxXLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 19, Short.MAX_VALUE)
                        .addComponent(maxXTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(maxYTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(withObstaclesCheckBox)
                    .addComponent(obstaclesBlockSightCheckBox)
                    .addGroup(gridPanelLayout.createSequentialGroup()
                        .addComponent(rewardDistanceLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                        .addComponent(sightRangeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(25, 25, 25))
                    .addGroup(gridPanelLayout.createSequentialGroup()
                        .addGroup(gridPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(obstaclePercentageLabel)
                            .addComponent(obstacleConnectionFactorLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(gridPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(obstacleConnectionFactorTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(obstaclePercentageTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        gridPanelLayout.setVerticalGroup(
            gridPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(gridPanelLayout.createSequentialGroup()
                .addGroup(gridPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rewardDistanceLabel)
                    .addComponent(sightRangeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(gridPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(maxXLabel)
                    .addComponent(maxYTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(maxXTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(isTorusCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(withObstaclesCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(obstaclesBlockSightCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(gridPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(obstaclePercentageLabel)
                    .addComponent(obstaclePercentageTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(gridPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(obstacleConnectionFactorLabel)
                    .addComponent(obstacleConnectionFactorTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        testsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Tests", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        numberOfExperimentsLabel.setFont(new java.awt.Font("Arial", 0, 12));
        numberOfExperimentsLabel.setText("Experiments");

        numberOfExperimentsTextField.setText("10");

        numberOfProblemsTextField.setText("500");

        numberOfStepsTextField.setText("500");

        randomSeedLabel.setFont(new java.awt.Font("Arial", 0, 12));
        randomSeedLabel.setText("Random Seed");

        randomSeedTextField.setText("0");

        agentCountLabel.setFont(new java.awt.Font("Arial", 0, 12));
        agentCountLabel.setText("Number of agents");

        maxAgentsTextField.setText("5");

        stepsLabel.setFont(new java.awt.Font("Arial", 0, 12));
        stepsLabel.setText("Steps");

        problemsLabel.setFont(new java.awt.Font("Arial", 0, 12));
        problemsLabel.setText("Problems");

        javax.swing.GroupLayout testsPanelLayout = new javax.swing.GroupLayout(testsPanel);
        testsPanel.setLayout(testsPanelLayout);
        testsPanelLayout.setHorizontalGroup(
            testsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(testsPanelLayout.createSequentialGroup()
                .addGroup(testsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, testsPanelLayout.createSequentialGroup()
                        .addGroup(testsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(randomSeedLabel)
                            .addComponent(numberOfExperimentsLabel))
                        .addGap(20, 20, 20)
                        .addGroup(testsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(randomSeedTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(numberOfExperimentsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, testsPanelLayout.createSequentialGroup()
                        .addGroup(testsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(agentCountLabel)
                            .addComponent(stepsLabel)
                            .addComponent(problemsLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(testsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(testsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(numberOfStepsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(maxAgentsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(numberOfProblemsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(10, 10, 10))
        );

        testsPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {maxAgentsTextField, numberOfExperimentsTextField, numberOfProblemsTextField, numberOfStepsTextField, randomSeedTextField});

        testsPanelLayout.setVerticalGroup(
            testsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(testsPanelLayout.createSequentialGroup()
                .addGroup(testsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(randomSeedLabel)
                    .addComponent(randomSeedTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(testsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(numberOfExperimentsLabel)
                    .addComponent(numberOfExperimentsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addGroup(testsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(problemsLabel)
                    .addComponent(numberOfProblemsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(testsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(stepsLabel)
                    .addComponent(numberOfStepsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(testsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(agentCountLabel)
                    .addComponent(maxAgentsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        goalAgentMovementPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Goal Agent Movement", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        goalAgentMovementButtonGroup.add(totalRandomGoalAgentMovementRadioButton);
        totalRandomGoalAgentMovementRadioButton.setFont(new java.awt.Font("Arial", 0, 12));
        totalRandomGoalAgentMovementRadioButton.setText("Total random");

        goalAgentMovementButtonGroup.add(randomGoalAgentMovementRadioButton);
        randomGoalAgentMovementRadioButton.setFont(new java.awt.Font("Arial", 0, 12));
        randomGoalAgentMovementRadioButton.setSelected(true);
        randomGoalAgentMovementRadioButton.setLabel("Random neighbor");

        goalAgentMovementButtonGroup.add(maxOneDirectionChangeGoalAgentMovementRadioButton);
        maxOneDirectionChangeGoalAgentMovementRadioButton.setFont(new java.awt.Font("Arial", 0, 12));
        maxOneDirectionChangeGoalAgentMovementRadioButton.setText("One direction change");

        goalAgentMovementButtonGroup.add(alwaysInTheSameDirectionGoalAgentMovementRadioButton);
        alwaysInTheSameDirectionGoalAgentMovementRadioButton.setFont(new java.awt.Font("Arial", 0, 12));
        alwaysInTheSameDirectionGoalAgentMovementRadioButton.setText("Always same direction");

        goalAgentMovementSpeedLabel.setFont(new java.awt.Font("Arial", 0, 12));
        goalAgentMovementSpeedLabel.setText("Speed");

        goalAgentMovementSpeedTextField.setText("2");

        goalAgentMovementButtonGroup.add(intelligentHideGoalAgentMovementRadioButton);
        intelligentHideGoalAgentMovementRadioButton.setFont(new java.awt.Font("Arial", 0, 12));
        intelligentHideGoalAgentMovementRadioButton.setText("Intelligent (hide)");
        intelligentHideGoalAgentMovementRadioButton.setToolTipText("Move away from other agents, tend to move towards walls");

        goalAgentMovementButtonGroup.add(intelligentOpenGoalAgentMovementRadioButton);
        intelligentOpenGoalAgentMovementRadioButton.setFont(new java.awt.Font("Arial", 0, 12));
        intelligentOpenGoalAgentMovementRadioButton.setText("Intelligent (open)");
        intelligentOpenGoalAgentMovementRadioButton.setToolTipText("Move away from other agents, tend to move away from walls");

        javax.swing.GroupLayout goalAgentMovementPanelLayout = new javax.swing.GroupLayout(goalAgentMovementPanel);
        goalAgentMovementPanel.setLayout(goalAgentMovementPanelLayout);
        goalAgentMovementPanelLayout.setHorizontalGroup(
            goalAgentMovementPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(goalAgentMovementPanelLayout.createSequentialGroup()
                .addGroup(goalAgentMovementPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(intelligentHideGoalAgentMovementRadioButton)
                    .addComponent(totalRandomGoalAgentMovementRadioButton)
                    .addComponent(randomGoalAgentMovementRadioButton)
                    .addComponent(maxOneDirectionChangeGoalAgentMovementRadioButton)
                    .addComponent(alwaysInTheSameDirectionGoalAgentMovementRadioButton)
                    .addComponent(intelligentOpenGoalAgentMovementRadioButton))
                .addContainerGap(7, Short.MAX_VALUE))
            .addGroup(goalAgentMovementPanelLayout.createSequentialGroup()
                .addComponent(goalAgentMovementSpeedLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 47, Short.MAX_VALUE)
                .addComponent(goalAgentMovementSpeedTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30))
        );
        goalAgentMovementPanelLayout.setVerticalGroup(
            goalAgentMovementPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(goalAgentMovementPanelLayout.createSequentialGroup()
                .addComponent(totalRandomGoalAgentMovementRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(randomGoalAgentMovementRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(intelligentOpenGoalAgentMovementRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(intelligentHideGoalAgentMovementRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(maxOneDirectionChangeGoalAgentMovementRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(alwaysInTheSameDirectionGoalAgentMovementRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(goalAgentMovementPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(goalAgentMovementSpeedLabel)
                    .addComponent(goalAgentMovementSpeedTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout problemDefinitionPanelLayout = new javax.swing.GroupLayout(problemDefinitionPanel);
        problemDefinitionPanel.setLayout(problemDefinitionPanelLayout);
        problemDefinitionPanelLayout.setHorizontalGroup(
            problemDefinitionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(goalAgentMovementPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(testsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(gridPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        problemDefinitionPanelLayout.setVerticalGroup(
            problemDefinitionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(problemDefinitionPanelLayout.createSequentialGroup()
                .addComponent(testsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(gridPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(goalAgentMovementPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        lcsParametersPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "LCS parameters", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        gaParametersPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "GA parameters", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        thetaLabel.setFont(new java.awt.Font("Arial", 0, 12));
        thetaLabel.setText("<html>GA threshold <i>&theta<sub>GA</sub></i></html>");
        thetaLabel.setToolTipText("The threshold for the GA application in an action set (time between GA runs)");

        predictionErrorReductionLabel.setFont(new java.awt.Font("Arial", 0, 12));
        predictionErrorReductionLabel.setText("Prediction error reduction");
        predictionErrorReductionLabel.setToolTipText("The reduction of the prediction error when generating an offspring classifier");

        fitnessReductionLabel.setFont(new java.awt.Font("Arial", 0, 12));
        fitnessReductionLabel.setText("Fitness Reduction");
        fitnessReductionLabel.setToolTipText("The reduction of the fitness when generating an offspring classifier");

        fitnessReductionTextField.setText("0.1");
        fitnessReductionTextField.setToolTipText("The reduction of the fitness when generating an offspring classifier");

        predictionErrorReductionTextField.setText("0.25");
        predictionErrorReductionTextField.setToolTipText("The reduction of the prediction error when generating an offspring classifier");

        thetaTextField.setText("25");
        thetaTextField.setToolTipText("The threshold for the GA application in an action set (time between GA runs)");

        mutationProbabilityLabel.setFont(new java.awt.Font("Arial", 0, 12));
        mutationProbabilityLabel.setText("<html>Mutation probability <i>&mu;</i> </html>");
        mutationProbabilityLabel.setToolTipText("The probability of mutating one allele and the action in an offspring classifier");

        mutationProbabilityTextField.setText("0.04");
        mutationProbabilityTextField.setToolTipText("The probability of mutating one allele and the action in an offspring classifier");

        doEvolutionaryAlgorithmCheckBox.setFont(new java.awt.Font("Arial", 0, 12));
        doEvolutionaryAlgorithmCheckBox.setSelected(true);
        doEvolutionaryAlgorithmCheckBox.setText("Use evolutionary algorithm?");
        doEvolutionaryAlgorithmCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doEvolutionaryAlgorithmCheckBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout gaParametersPanelLayout = new javax.swing.GroupLayout(gaParametersPanel);
        gaParametersPanel.setLayout(gaParametersPanelLayout);
        gaParametersPanelLayout.setHorizontalGroup(
            gaParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(gaParametersPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(gaParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(gaParametersPanelLayout.createSequentialGroup()
                        .addGroup(gaParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(thetaLabel)
                            .addComponent(mutationProbabilityLabel)
                            .addComponent(predictionErrorReductionLabel)
                            .addComponent(fitnessReductionLabel))
                        .addGap(18, 18, 18)
                        .addGroup(gaParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(fitnessReductionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(predictionErrorReductionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(mutationProbabilityTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(thetaTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(doEvolutionaryAlgorithmCheckBox))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        gaParametersPanelLayout.setVerticalGroup(
            gaParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, gaParametersPanelLayout.createSequentialGroup()
                .addComponent(doEvolutionaryAlgorithmCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(gaParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(thetaLabel)
                    .addComponent(thetaTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(gaParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mutationProbabilityLabel)
                    .addComponent(mutationProbabilityTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(gaParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(predictionErrorReductionLabel)
                    .addComponent(predictionErrorReductionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(gaParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fitnessReductionLabel)
                    .addComponent(fitnessReductionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(21, 21, 21))
        );

        fitnessAndPredictionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Fitness and Prediction", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        predictionInitializationLabel.setFont(new java.awt.Font("Arial", 0, 12));
        predictionInitializationLabel.setText("<html>Prediction init <i>p<sub>I</sub></i></html>");

        predictionErrorInitializationLabel.setFont(new java.awt.Font("Arial", 0, 12));
        predictionErrorInitializationLabel.setText("<html>Prediction error init <i>&epsilon;<sub>I</sub></i></html>");

        fitnessInitializationLabel.setFont(new java.awt.Font("Arial", 0, 12));
        fitnessInitializationLabel.setText("<html>Fitness init <i>F<sub>I</sub></i></html>");

        fitnessInitializationTextField.setText("0.01");

        predictionErrorInitializationTextField.setText("0.0");

        predictionInitializationTextField.setText("10.0");

        epsilon0Label.setFont(new java.awt.Font("Arial", 0, 12));
        epsilon0Label.setText("<html>Accuracy equal below <i>&epsilon;<sub>0</sub></i></html>");
        epsilon0Label.setToolTipText("The error threshold (prediction error) under which the accuracy of a classifier is set to one.");

        epsilon0TextField.setText("10.0");
        epsilon0TextField.setToolTipText("The error threshold (prediction error) under which the accuracy of a classifier is set to one.");

        alphaLabel.setFont(new java.awt.Font("Arial", 0, 12));
        alphaLabel.setText("<html>Accuracy calculation <i>&alpha;</i></html>");
        alphaLabel.setToolTipText("The fall of rate in the fitness evaluation");

        betaLabel.setFont(new java.awt.Font("Arial", 0, 12));
        betaLabel.setText("<html>Learning rate <i>&beta;</i> </html>");
        betaLabel.setToolTipText("The learning rate for updating fitness, prediction, prediction error and action set size estimate in XCS's classifiers");

        nuLabel.setFont(new java.awt.Font("Arial", 0, 12));
        nuLabel.setText("<html>Accuracy power <i>&nu;</i></html>");
        nuLabel.setToolTipText("Specifies the exponent in the power function for the fitness evaluation");

        betaTextField.setText("0.2");
        betaTextField.setToolTipText("The learning rate for updating fitness, prediction, prediction error and action set size estimate in XCS's classifiers");

        alphaTextField.setText("0.1");
        alphaTextField.setToolTipText("The fall of rate in the fitness evaluation");

        nuTextField.setText("5.0");
        nuTextField.setToolTipText("Specifies the exponent in the power function for the fitness evaluation");

        gammaLabel.setFont(new java.awt.Font("Arial", 0, 12));
        gammaLabel.setText("<html>Prediction discount <i>&gamma;</i></html>");
        gammaLabel.setToolTipText("The discount rate in multi-step problems.");

        gammaTextField.setText("0.95");
        gammaTextField.setToolTipText("The discount rate in multi-step problems.");

        javax.swing.GroupLayout fitnessAndPredictionPanelLayout = new javax.swing.GroupLayout(fitnessAndPredictionPanel);
        fitnessAndPredictionPanel.setLayout(fitnessAndPredictionPanelLayout);
        fitnessAndPredictionPanelLayout.setHorizontalGroup(
            fitnessAndPredictionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fitnessAndPredictionPanelLayout.createSequentialGroup()
                .addGroup(fitnessAndPredictionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(epsilon0Label)
                    .addComponent(predictionErrorInitializationLabel)
                    .addComponent(predictionInitializationLabel)
                    .addComponent(fitnessInitializationLabel)
                    .addComponent(alphaLabel)
                    .addComponent(betaLabel)
                    .addComponent(gammaLabel)
                    .addComponent(nuLabel))
                .addGap(29, 29, 29)
                .addGroup(fitnessAndPredictionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(predictionInitializationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(predictionErrorInitializationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fitnessInitializationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(epsilon0TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(alphaTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nuTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(gammaTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(betaTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        fitnessAndPredictionPanelLayout.setVerticalGroup(
            fitnessAndPredictionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fitnessAndPredictionPanelLayout.createSequentialGroup()
                .addGroup(fitnessAndPredictionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(predictionInitializationLabel)
                    .addComponent(predictionInitializationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(fitnessAndPredictionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(predictionErrorInitializationLabel)
                    .addComponent(predictionErrorInitializationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(fitnessAndPredictionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fitnessInitializationLabel)
                    .addComponent(fitnessInitializationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(fitnessAndPredictionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(epsilon0Label)
                    .addComponent(epsilon0TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(fitnessAndPredictionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(alphaLabel)
                    .addComponent(alphaTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(fitnessAndPredictionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nuLabel)
                    .addComponent(nuTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(fitnessAndPredictionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(gammaLabel)
                    .addComponent(gammaTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(fitnessAndPredictionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(betaLabel)
                    .addComponent(betaTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26))
        );

        classifierSubsumptionAndDeletionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Classifier subsumption and deletion", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        thetaSubsumerLabel.setFont(new java.awt.Font("Arial", 0, 12));
        thetaSubsumerLabel.setText("<html>Subsumption threshold <i>&theta;<sub>sub</sub></i></html>");
        thetaSubsumerLabel.setToolTipText("The experience of a classifier required to be a subsumer");

        thetaSubsumerTextField.setText("20");
        thetaSubsumerTextField.setToolTipText("The experience of a classifier required to be a subsumer");

        deltaLabel.setFont(new java.awt.Font("Arial", 0, 12));
        deltaLabel.setText("<html>Fraction mean fitness <i>&delta;</i></html>");
        deltaLabel.setToolTipText("The fraction of the mean fitness of the population below which the fitness of a classifier may be considered in its vote for deletion");

        thetaDelLabel.setFont(new java.awt.Font("Arial", 0, 12));
        thetaDelLabel.setText("<html>Deletion threshold <i>&theta;<sub>del</sub></i></html>");
        thetaDelLabel.setToolTipText("Specified the threshold (experience!) over which the fitness of a classifier may be considered in its deletion probability");

        deltaTextField.setText("0.1");
        deltaTextField.setToolTipText("The fraction of the mean fitness of the population below which the fitness of a classifier may be considered in its vote for deletion");

        thetaDelTextField.setText("20");
        thetaDelTextField.setToolTipText("Specified the threshold (experience!) over which the fitness of a classifier may be considered in its deletion probability.");

        maxPopSizeLabel.setFont(new java.awt.Font("Arial", 0, 12));
        maxPopSizeLabel.setText("<html>Max population <i>N</i> </html>");

        maxPopSizeTextField.setText("800");

        doGASubsumptionCheckBox.setFont(new java.awt.Font("Arial", 0, 12));
        doGASubsumptionCheckBox.setSelected(true);
        doGASubsumptionCheckBox.setText("GA subsumption");
        doGASubsumptionCheckBox.setActionCommand("Do GA Subsumption");

        doActionSetSubsumptionCheckBox.setFont(new java.awt.Font("Arial", 0, 12));
        doActionSetSubsumptionCheckBox.setSelected(true);
        doActionSetSubsumptionCheckBox.setText("Action set subsumption");

        coveringWildcardProbabilityLabel.setFont(new java.awt.Font("Arial", 0, 12));
        coveringWildcardProbabilityLabel.setText("<html>Covering # probability <i>P<sub>#</sub></i></html>");
        coveringWildcardProbabilityLabel.setToolTipText("The probability of using a don't care symbol in an allele when covering");

        coveringWildcardProbabilityTextField.setText("0.5");
        coveringWildcardProbabilityTextField.setToolTipText("The probability of using a don't care symbol in an allele when covering");

        javax.swing.GroupLayout classifierSubsumptionAndDeletionPanelLayout = new javax.swing.GroupLayout(classifierSubsumptionAndDeletionPanel);
        classifierSubsumptionAndDeletionPanel.setLayout(classifierSubsumptionAndDeletionPanelLayout);
        classifierSubsumptionAndDeletionPanelLayout.setHorizontalGroup(
            classifierSubsumptionAndDeletionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(classifierSubsumptionAndDeletionPanelLayout.createSequentialGroup()
                .addGroup(classifierSubsumptionAndDeletionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(classifierSubsumptionAndDeletionPanelLayout.createSequentialGroup()
                        .addGroup(classifierSubsumptionAndDeletionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(thetaSubsumerLabel)
                            .addComponent(deltaLabel)
                            .addComponent(thetaDelLabel)
                            .addComponent(maxPopSizeLabel)
                            .addComponent(coveringWildcardProbabilityLabel))
                        .addGap(14, 14, 14)
                        .addGroup(classifierSubsumptionAndDeletionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(thetaSubsumerTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(coveringWildcardProbabilityTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(thetaDelTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(deltaTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(maxPopSizeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(doActionSetSubsumptionCheckBox)
                    .addComponent(doGASubsumptionCheckBox))
                .addContainerGap())
        );
        classifierSubsumptionAndDeletionPanelLayout.setVerticalGroup(
            classifierSubsumptionAndDeletionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(classifierSubsumptionAndDeletionPanelLayout.createSequentialGroup()
                .addGroup(classifierSubsumptionAndDeletionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(maxPopSizeLabel)
                    .addComponent(maxPopSizeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(classifierSubsumptionAndDeletionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(deltaLabel)
                    .addComponent(deltaTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(classifierSubsumptionAndDeletionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(thetaDelLabel)
                    .addComponent(thetaDelTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(classifierSubsumptionAndDeletionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(thetaSubsumerLabel)
                    .addComponent(thetaSubsumerTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(classifierSubsumptionAndDeletionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(coveringWildcardProbabilityLabel)
                    .addComponent(coveringWildcardProbabilityTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(doGASubsumptionCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(doActionSetSubsumptionCheckBox)
                .addGap(11, 11, 11))
        );

        javax.swing.GroupLayout lcsParametersPanelLayout = new javax.swing.GroupLayout(lcsParametersPanel);
        lcsParametersPanel.setLayout(lcsParametersPanelLayout);
        lcsParametersPanelLayout.setHorizontalGroup(
            lcsParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(gaParametersPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(classifierSubsumptionAndDeletionPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(fitnessAndPredictionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        lcsParametersPanelLayout.setVerticalGroup(
            lcsParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lcsParametersPanelLayout.createSequentialGroup()
                .addComponent(classifierSubsumptionAndDeletionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(gaParametersPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fitnessAndPredictionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        agentTypePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Agent type", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        agentTypeButtonGroup.add(randomizedMovementRadioButton);
        randomizedMovementRadioButton.setFont(new java.awt.Font("Arial", 0, 12));
        randomizedMovementRadioButton.setText("Randomized movement");
        randomizedMovementRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                randomizedMovementRadioButtonActionPerformed(evt);
            }
        });

        agentTypeButtonGroup.add(simpleAIAgentRadioButton);
        simpleAIAgentRadioButton.setFont(new java.awt.Font("Arial", 0, 12));
        simpleAIAgentRadioButton.setText("Simple AI agent");
        simpleAIAgentRadioButton.setToolTipText("Randomized movement, but move to goal agent when in sight");
        simpleAIAgentRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                simpleAIAgentRadioButtonActionPerformed(evt);
            }
        });

        agentTypeButtonGroup.add(intelligentAIAgentRadioButton);
        intelligentAIAgentRadioButton.setFont(new java.awt.Font("Arial", 0, 12));
        intelligentAIAgentRadioButton.setText("Intelligent AI agent");
        intelligentAIAgentRadioButton.setToolTipText("Randomized movement, keep distance to other agents, move to goal agent when in sight");
        intelligentAIAgentRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                intelligentAIAgentRadioButtonActionPerformed(evt);
            }
        });

        agentTypeButtonGroup.add(singleStepLCSAgentRadioButton);
        singleStepLCSAgentRadioButton.setFont(new java.awt.Font("Arial", 0, 12));
        singleStepLCSAgentRadioButton.setText("LCS agent (single step)");
        singleStepLCSAgentRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                singleStepLCSAgentRadioButtonActionPerformed(evt);
            }
        });

        agentTypeButtonGroup.add(multiStepLCSAgentRadioButton);
        multiStepLCSAgentRadioButton.setFont(new java.awt.Font("Arial", 0, 12));
        multiStepLCSAgentRadioButton.setText("LCS agent (multi step)");
        multiStepLCSAgentRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                multiStepLCSAgentRadioButtonActionPerformed(evt);
            }
        });

        agentTypeButtonGroup.add(newLCSAgentRadioButton);
        newLCSAgentRadioButton.setFont(new java.awt.Font("Arial", 0, 12));
        newLCSAgentRadioButton.setSelected(true);
        newLCSAgentRadioButton.setText("New LCS agent");
        newLCSAgentRadioButton.setToolTipText("LCS Agent with special reward function");
        newLCSAgentRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newLCSAgentRadioButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout agentTypePanelLayout = new javax.swing.GroupLayout(agentTypePanel);
        agentTypePanel.setLayout(agentTypePanelLayout);
        agentTypePanelLayout.setHorizontalGroup(
            agentTypePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(randomizedMovementRadioButton)
            .addComponent(intelligentAIAgentRadioButton)
            .addComponent(simpleAIAgentRadioButton)
            .addComponent(singleStepLCSAgentRadioButton)
            .addComponent(multiStepLCSAgentRadioButton)
            .addComponent(newLCSAgentRadioButton)
        );
        agentTypePanelLayout.setVerticalGroup(
            agentTypePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(agentTypePanelLayout.createSequentialGroup()
                .addComponent(randomizedMovementRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(simpleAIAgentRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(intelligentAIAgentRadioButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(singleStepLCSAgentRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(multiStepLCSAgentRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(newLCSAgentRadioButton))
        );

        explorationModePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Exploration Mode", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        explorationModeButtonGroup.add(noExplorationModeRadioButton);
        noExplorationModeRadioButton.setFont(new java.awt.Font("Arial", 0, 12));
        noExplorationModeRadioButton.setSelected(true);
        noExplorationModeRadioButton.setText("No exploration");

        explorationModeButtonGroup.add(alwaysExplorationModeRadioButton);
        alwaysExplorationModeRadioButton.setFont(new java.awt.Font("Arial", 0, 12));
        alwaysExplorationModeRadioButton.setText("Always exploration");

        explorationModeButtonGroup.add(switchExplorationAndExploitationModeRadioButton);
        switchExplorationAndExploitationModeRadioButton.setFont(new java.awt.Font("Arial", 0, 12));
        switchExplorationAndExploitationModeRadioButton.setText("Switch explore/exploit");

        explorationModeButtonGroup.add(exploreThenExploitModeRadioButton);
        exploreThenExploitModeRadioButton.setFont(new java.awt.Font("Arial", 0, 12));
        exploreThenExploitModeRadioButton.setText("Explore then exploit");

        explorationModeButtonGroup.add(linearExplorationReductionModeRadioButton);
        linearExplorationReductionModeRadioButton.setFont(new java.awt.Font("Arial", 0, 12));
        linearExplorationReductionModeRadioButton.setText("Exploration reduction");

        javax.swing.GroupLayout explorationModePanelLayout = new javax.swing.GroupLayout(explorationModePanel);
        explorationModePanel.setLayout(explorationModePanelLayout);
        explorationModePanelLayout.setHorizontalGroup(
            explorationModePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(explorationModePanelLayout.createSequentialGroup()
                .addGroup(explorationModePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(noExplorationModeRadioButton)
                    .addComponent(alwaysExplorationModeRadioButton)
                    .addComponent(switchExplorationAndExploitationModeRadioButton)
                    .addComponent(exploreThenExploitModeRadioButton)
                    .addComponent(linearExplorationReductionModeRadioButton))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        explorationModePanelLayout.setVerticalGroup(
            explorationModePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(explorationModePanelLayout.createSequentialGroup()
                .addComponent(noExplorationModeRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(alwaysExplorationModeRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(switchExplorationAndExploitationModeRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(exploreThenExploitModeRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(linearExplorationReductionModeRadioButton))
        );

        packageButton.setFont(new java.awt.Font("Tahoma", 1, 11));
        packageButton.setText("Package");
        packageButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                packageButtonMouseClicked(evt);
            }
        });

        rewardModelPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Reward model", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        maxStackSizeLabel.setFont(new java.awt.Font("Arial", 0, 12));
        maxStackSizeLabel.setText("Stack size");

        maxStackSizeTextField.setText("32");

        isEventDrivenCheckBox.setFont(new java.awt.Font("Arial", 0, 12));
        isEventDrivenCheckBox.setSelected(true);
        isEventDrivenCheckBox.setText("Is event driven?");

        externalRewardButtonGroup.add(noExternalRewardRadioButton);
        noExternalRewardRadioButton.setFont(new java.awt.Font("Arial", 0, 12));
        noExternalRewardRadioButton.setText("No external reward");

        externalRewardButtonGroup.add(rewardAllEquallyRadioButton);
        rewardAllEquallyRadioButton.setFont(new java.awt.Font("Arial", 0, 12));
        rewardAllEquallyRadioButton.setText("Reward all equally");
        rewardAllEquallyRadioButton.setActionCommand("all equally");

        externalRewardButtonGroup.add(rewardSimpleRadioButton);
        rewardSimpleRadioButton.setFont(new java.awt.Font("Arial", 0, 12));
        rewardSimpleRadioButton.setSelected(true);
        rewardSimpleRadioButton.setText("Simple relation");

        externalRewardButtonGroup.add(rewardComplexRadioButton);
        rewardComplexRadioButton.setFont(new java.awt.Font("Arial", 0, 12));
        rewardComplexRadioButton.setText("Complex relation");

        rewardDistanceTextField.setText("4");

        rewardRangeLabel.setFont(new java.awt.Font("Arial", 0, 12));
        rewardRangeLabel.setText("Reward range");

        javax.swing.GroupLayout rewardModelPanelLayout = new javax.swing.GroupLayout(rewardModelPanel);
        rewardModelPanel.setLayout(rewardModelPanelLayout);
        rewardModelPanelLayout.setHorizontalGroup(
            rewardModelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rewardModelPanelLayout.createSequentialGroup()
                .addGroup(rewardModelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(isEventDrivenCheckBox)
                    .addGroup(rewardModelPanelLayout.createSequentialGroup()
                        .addGroup(rewardModelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(rewardModelPanelLayout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addComponent(rewardRangeLabel))
                            .addGroup(rewardModelPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(maxStackSizeLabel)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(rewardModelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(maxStackSizeTextField)
                            .addComponent(rewardDistanceTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)))
                    .addComponent(noExternalRewardRadioButton)
                    .addComponent(rewardAllEquallyRadioButton)
                    .addComponent(rewardSimpleRadioButton)
                    .addComponent(rewardComplexRadioButton))
                .addContainerGap(28, Short.MAX_VALUE))
        );
        rewardModelPanelLayout.setVerticalGroup(
            rewardModelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rewardModelPanelLayout.createSequentialGroup()
                .addGroup(rewardModelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rewardDistanceTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rewardRangeLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(rewardModelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(maxStackSizeLabel)
                    .addComponent(maxStackSizeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(isEventDrivenCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(noExternalRewardRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rewardAllEquallyRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rewardSimpleRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rewardComplexRadioButton))
        );

        clemensLodeLabel.setFont(new java.awt.Font("Arial", 0, 12));
        clemensLodeLabel.setText("(c) Clemens Lode");

        clemensMailLabel.setFont(new java.awt.Font("Arial", 0, 12));
        clemensMailLabel.setText("clemens@lode.de");

        resultsTable.setModel(results);
        resultsScrollPane.setViewportView(resultsTable);

        updateDatabaseButton.setText("Update");
        updateDatabaseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateDatabaseButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(saveNewButton, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(packageButton, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(problemDefinitionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addComponent(lcsParametersPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(clemensLodeLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(clemensMailLabel, javax.swing.GroupLayout.Alignment.TRAILING))
                    .addComponent(rewardModelPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(agentTypePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(explorationModePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(saveSettingsButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(loadSettingsButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(resultsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 335, Short.MAX_VALUE)
                    .addComponent(updateDatabaseButton))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(problemDefinitionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(saveNewButton, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(packageButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(lcsParametersPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(resultsScrollPane, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(agentTypePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(rewardModelPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(explorationModePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(loadSettingsButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(saveSettingsButton)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(clemensLodeLabel)
                            .addComponent(updateDatabaseButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(clemensMailLabel)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {packageButton, saveNewButton});

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

private void saveNewButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_saveNewButtonMouseClicked
    this.saveSettings("default.txt");
    String id = new String(timeString + "-" + conf_id);
    this.saveSettings("config-" + id + ".txt");
    config_strings.add(id);
    conf_id++;
}//GEN-LAST:event_saveNewButtonMouseClicked

private void packageButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_packageButtonMouseClicked
    createAllPlotFile();
    config_strings.clear();
    resetTimeString();
    joschka.run();
}//GEN-LAST:event_packageButtonMouseClicked

private void withObstaclesCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_withObstaclesCheckBoxActionPerformed
    if(withObstaclesCheckBox.isSelected()) {
        obstaclePercentageLabel.setEnabled(true);
        obstaclePercentageTextField.setEnabled(true);
        obstacleConnectionFactorLabel.setEnabled(true);
        obstacleConnectionFactorTextField.setEnabled(true);
        obstaclesBlockSightCheckBox.setEnabled(true);
    } else {
        obstaclePercentageLabel.setEnabled(false);
        obstaclePercentageTextField.setEnabled(false);
        obstacleConnectionFactorLabel.setEnabled(false);
        obstacleConnectionFactorTextField.setEnabled(false);        
        obstaclesBlockSightCheckBox.setEnabled(false);
    }
}//GEN-LAST:event_withObstaclesCheckBoxActionPerformed

private void doEvolutionaryAlgorithmCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doEvolutionaryAlgorithmCheckBoxActionPerformed
    if(doEvolutionaryAlgorithmCheckBox.isSelected()) {
        thetaLabel.setEnabled(true);
        thetaTextField.setEnabled(true);
        mutationProbabilityLabel.setEnabled(true);
        mutationProbabilityTextField.setEnabled(true);
        predictionErrorReductionLabel.setEnabled(true);
        predictionErrorReductionTextField.setEnabled(true);
        fitnessReductionLabel.setEnabled(true);
        fitnessReductionTextField.setEnabled(true);
    } else {
        thetaLabel.setEnabled(false);
        thetaTextField.setEnabled(false);
        mutationProbabilityLabel.setEnabled(false);
        mutationProbabilityTextField.setEnabled(false);
        predictionErrorReductionLabel.setEnabled(false);
        predictionErrorReductionTextField.setEnabled(false);
        fitnessReductionLabel.setEnabled(false);
        fitnessReductionTextField.setEnabled(false);
    }
}//GEN-LAST:event_doEvolutionaryAlgorithmCheckBoxActionPerformed

private void updateDatabaseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateDatabaseButtonActionPerformed
    loadResultsIntoDatabase();
}//GEN-LAST:event_updateDatabaseButtonActionPerformed

private void randomizedMovementRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_randomizedMovementRadioButtonActionPerformed
    activateLCSControls(!randomizedMovementRadioButton.isSelected());
}//GEN-LAST:event_randomizedMovementRadioButtonActionPerformed

private void simpleAIAgentRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_simpleAIAgentRadioButtonActionPerformed
    activateLCSControls(!simpleAIAgentRadioButton.isSelected());
}//GEN-LAST:event_simpleAIAgentRadioButtonActionPerformed

private void intelligentAIAgentRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_intelligentAIAgentRadioButtonActionPerformed
    activateLCSControls(!intelligentAIAgentRadioButton.isSelected());
}//GEN-LAST:event_intelligentAIAgentRadioButtonActionPerformed

private void singleStepLCSAgentRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_singleStepLCSAgentRadioButtonActionPerformed
    activateLCSControls(singleStepLCSAgentRadioButton.isSelected());
}//GEN-LAST:event_singleStepLCSAgentRadioButtonActionPerformed

private void multiStepLCSAgentRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_multiStepLCSAgentRadioButtonActionPerformed
    activateLCSControls(multiStepLCSAgentRadioButton.isSelected());
}//GEN-LAST:event_multiStepLCSAgentRadioButtonActionPerformed

private void newLCSAgentRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newLCSAgentRadioButtonActionPerformed
    activateLCSControls(newLCSAgentRadioButton.isSelected());
}//GEN-LAST:event_newLCSAgentRadioButtonActionPerformed



private void activateLCSControls(boolean activate) {
    if(activate) {

    } else {
        // TODO
    }
}
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
    private javax.swing.ButtonGroup agentTypeButtonGroup;
    private javax.swing.JPanel agentTypePanel;
    private javax.swing.JLabel alphaLabel;
    private javax.swing.JTextField alphaTextField;
    private javax.swing.JRadioButton alwaysExplorationModeRadioButton;
    private javax.swing.JRadioButton alwaysInTheSameDirectionGoalAgentMovementRadioButton;
    private javax.swing.JLabel betaLabel;
    private javax.swing.JTextField betaTextField;
    private javax.swing.JPanel classifierSubsumptionAndDeletionPanel;
    private javax.swing.JLabel clemensLodeLabel;
    private javax.swing.JLabel clemensMailLabel;
    private javax.swing.JLabel coveringWildcardProbabilityLabel;
    private javax.swing.JTextField coveringWildcardProbabilityTextField;
    private javax.swing.JLabel deltaLabel;
    private javax.swing.JTextField deltaTextField;
    private javax.swing.JCheckBox doActionSetSubsumptionCheckBox;
    private javax.swing.JCheckBox doEvolutionaryAlgorithmCheckBox;
    private javax.swing.JCheckBox doGASubsumptionCheckBox;
    private javax.swing.JLabel epsilon0Label;
    private javax.swing.JTextField epsilon0TextField;
    private javax.swing.ButtonGroup explorationModeButtonGroup;
    private javax.swing.JPanel explorationModePanel;
    private javax.swing.JRadioButton exploreThenExploitModeRadioButton;
    private javax.swing.ButtonGroup externalRewardButtonGroup;
    private javax.swing.JFileChooser fileChooser;
    private javax.swing.JPanel fitnessAndPredictionPanel;
    private javax.swing.JLabel fitnessInitializationLabel;
    private javax.swing.JTextField fitnessInitializationTextField;
    private javax.swing.JLabel fitnessReductionLabel;
    private javax.swing.JTextField fitnessReductionTextField;
    private javax.swing.JPanel gaParametersPanel;
    private javax.swing.JLabel gammaLabel;
    private javax.swing.JTextField gammaTextField;
    private javax.swing.ButtonGroup goalAgentMovementButtonGroup;
    private javax.swing.JPanel goalAgentMovementPanel;
    private javax.swing.JLabel goalAgentMovementSpeedLabel;
    private javax.swing.JTextField goalAgentMovementSpeedTextField;
    private javax.swing.JPanel gridPanel;
    private javax.swing.JRadioButton intelligentAIAgentRadioButton;
    private javax.swing.JRadioButton intelligentHideGoalAgentMovementRadioButton;
    private javax.swing.JRadioButton intelligentOpenGoalAgentMovementRadioButton;
    private javax.swing.JCheckBox isEventDrivenCheckBox;
    private javax.swing.JCheckBox isTorusCheckBox;
    private javax.swing.JPanel lcsParametersPanel;
    private javax.swing.JRadioButton linearExplorationReductionModeRadioButton;
    private javax.swing.JButton loadSettingsButton;
    private javax.swing.JTextField maxAgentsTextField;
    private javax.swing.JRadioButton maxOneDirectionChangeGoalAgentMovementRadioButton;
    private javax.swing.JLabel maxPopSizeLabel;
    private javax.swing.JTextField maxPopSizeTextField;
    private javax.swing.JLabel maxStackSizeLabel;
    private javax.swing.JTextField maxStackSizeTextField;
    private javax.swing.JLabel maxXLabel;
    private javax.swing.JTextField maxXTextField;
    private javax.swing.JTextField maxYTextField;
    private javax.swing.JRadioButton multiStepLCSAgentRadioButton;
    private javax.swing.JLabel mutationProbabilityLabel;
    private javax.swing.JTextField mutationProbabilityTextField;
    private javax.swing.JRadioButton newLCSAgentRadioButton;
    private javax.swing.JRadioButton noExplorationModeRadioButton;
    private javax.swing.JRadioButton noExternalRewardRadioButton;
    private javax.swing.JLabel nuLabel;
    private javax.swing.JTextField nuTextField;
    private javax.swing.JLabel numberOfExperimentsLabel;
    private javax.swing.JTextField numberOfExperimentsTextField;
    private javax.swing.JTextField numberOfProblemsTextField;
    private javax.swing.JTextField numberOfStepsTextField;
    private javax.swing.JLabel obstacleConnectionFactorLabel;
    private javax.swing.JTextField obstacleConnectionFactorTextField;
    private javax.swing.JLabel obstaclePercentageLabel;
    private javax.swing.JTextField obstaclePercentageTextField;
    private javax.swing.JCheckBox obstaclesBlockSightCheckBox;
    private javax.swing.JButton packageButton;
    private javax.swing.JLabel predictionErrorInitializationLabel;
    private javax.swing.JTextField predictionErrorInitializationTextField;
    private javax.swing.JLabel predictionErrorReductionLabel;
    private javax.swing.JTextField predictionErrorReductionTextField;
    private javax.swing.JLabel predictionInitializationLabel;
    private javax.swing.JTextField predictionInitializationTextField;
    private javax.swing.JPanel problemDefinitionPanel;
    private javax.swing.JLabel problemsLabel;
    private javax.swing.JRadioButton randomGoalAgentMovementRadioButton;
    private javax.swing.JLabel randomSeedLabel;
    private javax.swing.JTextField randomSeedTextField;
    private javax.swing.JRadioButton randomizedMovementRadioButton;
    private javax.swing.JScrollPane resultsScrollPane;
    private javax.swing.JTable resultsTable;
    private javax.swing.JRadioButton rewardAllEquallyRadioButton;
    private javax.swing.JRadioButton rewardComplexRadioButton;
    private javax.swing.JLabel rewardDistanceLabel;
    private javax.swing.JTextField rewardDistanceTextField;
    private javax.swing.JPanel rewardModelPanel;
    private javax.swing.JLabel rewardRangeLabel;
    private javax.swing.JRadioButton rewardSimpleRadioButton;
    private javax.swing.JButton saveNewButton;
    private javax.swing.JButton saveSettingsButton;
    private javax.swing.JTextField sightRangeTextField;
    private javax.swing.JRadioButton simpleAIAgentRadioButton;
    private javax.swing.JRadioButton singleStepLCSAgentRadioButton;
    private javax.swing.JLabel stepsLabel;
    private javax.swing.JRadioButton switchExplorationAndExploitationModeRadioButton;
    private javax.swing.JPanel testsPanel;
    private javax.swing.JLabel thetaDelLabel;
    private javax.swing.JTextField thetaDelTextField;
    private javax.swing.JLabel thetaLabel;
    private javax.swing.JLabel thetaSubsumerLabel;
    private javax.swing.JTextField thetaSubsumerTextField;
    private javax.swing.JTextField thetaTextField;
    private javax.swing.JRadioButton totalRandomGoalAgentMovementRadioButton;
    private javax.swing.JButton updateDatabaseButton;
    private javax.swing.JCheckBox withObstaclesCheckBox;
    // End of variables declaration//GEN-END:variables
    
}
