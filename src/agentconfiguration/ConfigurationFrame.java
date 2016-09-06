/**
 *
 * @author Clemens Lode, 1151459, University Karlsruhe (TH)
 */
package agentconfiguration;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.ListSelectionModel;
import javax.swing.JTable;
import javax.swing.JOptionPane;
import javax.swing.ButtonModel;
import java.util.ArrayList;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.FileFilter;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

import agent.Configuration;

import java.awt.Component;
import java.awt.Color;
import javax.swing.table.TableColumnModel;

import javax.swing.AbstractButton;
import java.util.Enumeration;


/**
 *
 * @author Clemens Lode, 1151459, University Karlsruhe (TH)
 */
public class ConfigurationFrame extends javax.swing.JFrame {


    private static BufferedWriter plot_out;
    static String timeString = new String("");
    static int conf_id = 0;
    static ArrayList<String> config_strings = new ArrayList<String>();
    static String last_batch_file = new String();
    static String last_directory = new String();
    /**
     * result database
     */
    ResultsTable results = new ResultsTable();
    TableSorter sorter = new TableSorter(results);

    private static int last_change = 0;

    /**
     * Steps = 1
     * Stack Size = 2
     * Max population = 3
     *
     */



    /** Creates new form ConfigurationFrame */
    public ConfigurationFrame() {
        initComponents();
        resetTimeString();
        loadSettings("default.txt");

        resultsTable.setModel(sorter);
        sorter.setTableHeader(resultsTable.getTableHeader());
        resultsTable.getTableHeader().setToolTipText(
                "Click to specify sorting; Control-Click to specify secondary sorting");


        ListSelectionModel listSelectionModel = resultsTable.getSelectionModel();
        listSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listSelectionModel.addListSelectionListener(new SelectionListener(resultsTable));
        resultsTable.setSelectionModel(listSelectionModel);

        loadResultsIntoDatabase();
    }

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
            String config_string = (String) (my_row[0]);
            loadSettings(config_string);
        }
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
                if (file.isDirectory() && file.getName().startsWith("agent-")) {
                    return true;
                } else {
                    return false;
                }
            }
        };

        FileFilter outputDirectoriesFilter = new FileFilter() {

            public boolean accept(File file) {
                if (file.getName().startsWith("output_")) {
                    return true;
                } else {
                    return false;
                }
            }
        };
        FileFilter configFileFilter = new FileFilter() {

            public boolean accept(File file) {
                if (file.getName().startsWith("config-")) {
                    return true;
                } else {
                    return false;
                }
            }
        };

        File[] agent_directories = dir.listFiles(agentDirectoriesFilter);
        for (File agent_directory : agent_directories) {
            File[] output_directories = agent_directory.listFiles(outputDirectoriesFilter);
            for (File output_directory : output_directories) {
                String directory_string = agent_directory.getName() + "//" + output_directory.getName() + "//";
                String id_string = output_directory.getName().substring(7, output_directory.getName().length());
                String config_string = directory_string + "config-" + id_string + ".txt";
                File result_file = new File(directory_string + "results-" + id_string + ".dat");
                File half_result_file = new File(directory_string + "half_results-" + id_string + ".dat");

                try {
                    Configuration.initialize(config_string);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Error opening/reading file " + config_string + "(" + e + ")", "Error opening/reading config file", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String config_id = Configuration.getProblemID().toString();
                String spreadIndividualTotalPoints = new String("--");
                String averageIndividualTotalPoints = new String("--");
                String spreadAgentDistance = new String("--");
                String spreadGoalAgentDistance = new String("--");
                String averageAgentDistance = new String("--");
                String averageGoalAgentDistance = new String("--");
                String averagePredictionError = new String("--");
                String coveredAreaFactor = new String("--");
                String wastedCoverage = new String("--");
                String goalAgentObservedPercentage = new String("--");
                String halfSpreadIndividualTotalPoints = new String("--");
                String halfAverageIndividualTotalPoints = new String("--");
                String halfSpreadAgentDistance = new String("--");
                String halfSpreadGoalAgentDistance = new String("--");
                String halfAverageAgentDistance = new String("--");
                String halfAverageGoalAgentDistance = new String("--");
                String halfAveragePredictionError = new String("--");
                String halfCoveredAreaFactor = new String("--");
                String halfWastedCoverage = new String("--");
                String halfGoalAgentObservedPercentage = new String("--");

                if (result_file.exists()) {
                    try {
                        BufferedReader p = new BufferedReader(new FileReader(result_file.getAbsoluteFile()));
                        spreadIndividualTotalPoints = p.readLine();
                        averageIndividualTotalPoints = p.readLine();
                        spreadAgentDistance = p.readLine();
                        spreadGoalAgentDistance = p.readLine();
                        averageAgentDistance = p.readLine();
                        averageGoalAgentDistance = p.readLine();
                        averagePredictionError = p.readLine();
                        coveredAreaFactor = p.readLine();
                        wastedCoverage = p.readLine();
                        goalAgentObservedPercentage = p.readLine();

                        p.close();
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(this, "Error opening/reading file " + result_file.getAbsoluteFile() + "(" + e + ")", "Error opening/reading file", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } 
                if (half_result_file.exists()) {
                    try {
                        BufferedReader p = new BufferedReader(new FileReader(half_result_file.getAbsoluteFile()));
                        halfSpreadIndividualTotalPoints = p.readLine();
                        halfAverageIndividualTotalPoints = p.readLine();
                        halfSpreadAgentDistance = p.readLine();
                        halfSpreadGoalAgentDistance = p.readLine();
                        halfAverageAgentDistance = p.readLine();
                        halfAverageGoalAgentDistance = p.readLine();
                        halfAveragePredictionError = p.readLine();
                        halfCoveredAreaFactor = p.readLine();
                        halfWastedCoverage = p.readLine();
                        halfGoalAgentObservedPercentage = p.readLine();

                        p.close();
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(this, "Error opening/reading file " + half_result_file.getAbsoluteFile() + "(" + e + ")", "Error opening/reading file", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                Object[] object_row = new Object[ResultsTable.COLUMN_COUNT];
                object_row[0] = new String(config_string);
                object_row[1] = new String(config_id);
                object_row[2] = new String(spreadIndividualTotalPoints);
                object_row[3] = new String(averageIndividualTotalPoints);
                object_row[4] = new String(spreadAgentDistance);
                object_row[5] = new String(averageAgentDistance);
                object_row[6] = new String(spreadGoalAgentDistance);
                object_row[7] = new String(averageGoalAgentDistance);
                object_row[8] = new String(averagePredictionError);
                object_row[9] = new String(coveredAreaFactor);
                object_row[10] = new String(wastedCoverage);
                object_row[11] = new String(halfGoalAgentObservedPercentage);
                object_row[12] = new String(goalAgentObservedPercentage);
                results.datas.add(object_row);
            }
        }
        int vColIndex = 0;
        TableColumnModel tcm = resultsTable.getColumnModel();
        for(int i = 0; i < ResultsTable.COLUMN_COUNT; i++) {
            tcm.getColumn(i).setCellRenderer(new MyTableCellRenderer());
        }
        results.fireTableDataChanged();
    }

    private class MyTableCellRenderer
        extends javax.swing.table.DefaultTableCellRenderer
    {
        @Override
        public Component getTableCellRendererComponent(
            JTable table,
            Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column
        ) {
// component will actually be this.
            Component component = super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column
            );

            int actual_row = sorter.modelIndex(row);
            Object[] my_row = results.getRow(actual_row);

            String config_string = (String)(my_row[0]);

                try {
                    Configuration.initialize(config_string);
                } catch (Exception e) {
                    //JOptionPane.showMessageDialog(this, "Error opening/reading file " + config_string + "(" + e + ")", "Error opening/reading config file", JOptionPane.ERROR_MESSAGE);
                    return null;
                }
            Color c = Color.white;
            switch(Configuration.getAgentType()) {
                case Configuration.RANDOMIZED_MOVEMENT_AGENT_TYPE:c = Color.white;break;
                case Configuration.SIMPLE_AI_AGENT_TYPE:c = Color.LIGHT_GRAY;break;
                case Configuration.INTELLIGENT_AI_AGENT_TYPE:c = Color.GRAY;break;
                case Configuration.OLD_LCS_AGENT_TYPE:c = Color.cyan;break;
                case Configuration.NEW_LCS_AGENT_TYPE:c = Color.green;break;
                case Configuration.MULTISTEP_LCS_AGENT_TYPE:c = Color.orange;break;
                case Configuration.SINGLE_LCS_AGENT_TYPE:c = Color.blue;break;

            }

            component.setBackground(c);
            return component;
        }
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
            p.println(Boolean.valueOf(createAnimatedGIFCheckBox.isSelected()));

            p.println(Integer.valueOf(maxPopSizeTextField.getText()));

            p.println(isEventDrivenCheckBox.isSelected());
            p.println(useMaxPredictionCheckBox.isSelected());
            p.println(useQuadraticRewardCheckBox.isSelected());
            p.println(exchangeClassifiersCheckBox.isSelected());

            p.println(Integer.valueOf(maxXTextField.getText()));
            p.println(Integer.valueOf(maxYTextField.getText()));

            if(randomScenarioRadioButton.isSelected()) {
                p.println(Configuration.RANDOM_SCENARIO);
            } else if(mazeScenarioRadioButton.isSelected()) {
                p.println(Configuration.MAZE_SCENARIO);
            } else if(nonTorusScenarioRadioButton.isSelected()) {
                p.println(Configuration.NON_TORUS_SCENARIO);
            } else if(pillarScenarioRadioButton.isSelected()) {
                p.println(Configuration.PILLAR_SCENARIO);
            } else if(crossScenarioRadioButton.isSelected()) {
                p.println(Configuration.CROSS_SCENARIO);
            } else if(roomScenarioRadioButton.isSelected()) {
                p.println(Configuration.ROOM_SCENARIO);
            } else if(difficultScenarioRadioButton.isSelected()) {
                p.println(Configuration.DIFFICULT_SCENARIO);
            }


            p.println(Double.valueOf(obstaclePercentageTextField.getText()));
            p.println(Double.valueOf(obstacleConnectionFactorTextField.getText()));

            p.println(Double.valueOf(rewardDistanceTextField.getText()));

            p.println(Double.valueOf(sightRangeTextField.getText()));
            p.println(Integer.valueOf(maxAgentsTextField.getText()));

            p.println(Integer.valueOf(maxStackSizeTextField.getText()));

            p.println(Double.valueOf(coveringWildcardProbabilityTextField.getText()));
            p.println(randomStartCheckBox.isSelected());
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
            p.println(allowRotationCheckBox.isSelected());

            if (noExplorationModeRadioButton.isSelected()) {
                p.println(Configuration.NO_EXPLORATION_MODE);
            } else if (alwaysExplorationModeRadioButton.isSelected()) {
                p.println(Configuration.ALWAYS_EXPLORATION_MODE);
            } else if (switchExplorationAndExploitationModeRadioButton.isSelected()) {
                p.println(Configuration.SWITCH_EXPLORATION_MODE);
            } else if(randomExplorationAndExploitationModeRadioButton.isSelected()) {
                p.println(Configuration.RANDOM_EXPLORATION_MODE);
            } else if (exploreThenExploitModeRadioButton.isSelected()) {
                p.println(Configuration.EXPLORE_THEN_EXPLOIT_MODE);
            } else if (linearExplorationReductionModeRadioButton.isSelected()) {
                p.println(Configuration.LINEAR_REDUCTION_EXPLORE_MODE);
            }

            if (totalRandomGoalAgentMovementRadioButton.isSelected()) {
                p.println(Configuration.TOTAL_RANDOM_MOVEMENT);
            } else if (randomGoalAgentMovementRadioButton.isSelected()) {
                p.println(Configuration.RANDOM_MOVEMENT);
            } else if (intelligentOpenGoalAgentMovementRadioButton.isSelected()) {
                p.println(Configuration.INTELLIGENT_MOVEMENT_OPEN);
            } else if (intelligentHideGoalAgentMovementRadioButton.isSelected()) {
                p.println(Configuration.INTELLIGENT_MOVEMENT_HIDE);
            } else if (maxOneDirectionChangeGoalAgentMovementRadioButton.isSelected()) {
                p.println(Configuration.RANDOM_DIRECTION_CHANGE);
            } else if (alwaysInTheSameDirectionGoalAgentMovementRadioButton.isSelected()) {
                p.println(Configuration.ALWAYS_SAME_DIRECTION);
            } else if(randomHideGoalAgentMovementRadioButton.isSelected()) {
                p.println(Configuration.RANDOM_HIDE);
            } else if(LCSGoalAgentMovementRadioButton.isSelected()) {
                p.println(Configuration.LCS_MOVEMENT);
            }

            p.println(Integer.valueOf(goalAgentMovementSpeedTextField.getText()));

            if (randomizedMovementRadioButton.isSelected()) {
                p.println(Configuration.RANDOMIZED_MOVEMENT_AGENT_TYPE);
            } else if (simpleAIAgentRadioButton.isSelected()) {
                p.println(Configuration.SIMPLE_AI_AGENT_TYPE);
            } else if (intelligentAIAgentRadioButton.isSelected()) {
                p.println(Configuration.INTELLIGENT_AI_AGENT_TYPE);
            } else if (newLCSAgentRadioButton.isSelected()) {
                p.println(Configuration.NEW_LCS_AGENT_TYPE);
            } else if (oldLCSAgentRadioButton.isSelected()) {
                p.println(Configuration.OLD_LCS_AGENT_TYPE);
            } else if (multistepLCSAgentRadioButton.isSelected()) {
                p.println(Configuration.MULTISTEP_LCS_AGENT_TYPE);
            } else if (singleLCSAgentRadioButton.isSelected()) {
                p.println(Configuration.SINGLE_LCS_AGENT_TYPE);
            }

            if (noExternalRewardRadioButton.isSelected()) {
                p.println(Configuration.NO_EXTERNAL_REWARD);
            } else if (rewardAllEquallyRadioButton.isSelected()) {
                p.println(Configuration.REWARD_ALL_EQUALLY);
            } else if (rewardSimpleRadioButton.isSelected()) {
                p.println(Configuration.REWARD_SIMPLE);
            } else if (rewardComplexRadioButton.isSelected()) {
                p.println(Configuration.REWARD_COMPLEX);
            } else if(rewardNewRadioButton.isSelected()) {
                p.println(Configuration.REWARD_NEW);
            } else if(rewardEgoisticRadioButton.isSelected()) {
                p.println(Configuration.REWARD_EGOISM);
            }

            p.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error writing to file " + my_file.getAbsoluteFile() + " : " + e, "Error writing file", JOptionPane.ERROR_MESSAGE);
        }

    }

    private void loadSettings(String file_name) {
        try {
            Configuration.initialize(file_name);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error opening file " + file_name + ": " + e, "Error opening file", JOptionPane.ERROR_MESSAGE);
            return;
        }
        randomSeedTextField.setText(String.valueOf(Configuration.getRandomSeed()));
        numberOfExperimentsTextField.setText(String.valueOf(Configuration.getNumberOfExperiments()));
        numberOfProblemsTextField.setText(String.valueOf(Configuration.getNumberOfProblems()));
        numberOfStepsTextField.setText(String.valueOf(Configuration.getNumberOfSteps()));
        createAnimatedGIFCheckBox.setSelected(Configuration.isGifOutput());

        maxPopSizeTextField.setText(String.valueOf(Configuration.getMaxPopSize()));

        isEventDrivenCheckBox.setSelected(Configuration.isEventDriven());
        useMaxPredictionCheckBox.setSelected(Configuration.isUseMaxPrediction());
        useQuadraticRewardCheckBox.setSelected(Configuration.isUseQuadraticReward());
        exchangeClassifiersCheckBox.setSelected(Configuration.isExchangeClassifiers());

        maxXTextField.setText(String.valueOf(Configuration.getMaxX()));
        maxYTextField.setText(String.valueOf(Configuration.getMaxY()));

        switch(Configuration.getScenarioType()) {
            case Configuration.RANDOM_SCENARIO:
                scenarioTypeButtonGroup.setSelected(randomScenarioRadioButton.getModel(), true);
                break;
            case Configuration.MAZE_SCENARIO:
                scenarioTypeButtonGroup.setSelected(mazeScenarioRadioButton.getModel(), true);
                break;
            case Configuration.NON_TORUS_SCENARIO:
                scenarioTypeButtonGroup.setSelected(nonTorusScenarioRadioButton.getModel(), true);
                break;
            case Configuration.PILLAR_SCENARIO:
                scenarioTypeButtonGroup.setSelected(pillarScenarioRadioButton.getModel(), true);
            break;
            case Configuration.CROSS_SCENARIO:
                scenarioTypeButtonGroup.setSelected(crossScenarioRadioButton.getModel(), true);
            break;
            case Configuration.ROOM_SCENARIO:
                scenarioTypeButtonGroup.setSelected(roomScenarioRadioButton.getModel(), true);
            break;
            case Configuration.DIFFICULT_SCENARIO:
                scenarioTypeButtonGroup.setSelected(difficultScenarioRadioButton.getModel(), true);
                break;
        }

        obstaclePercentageTextField.setText(String.valueOf(Configuration.getObstaclePercentage()));
        obstacleConnectionFactorTextField.setText(String.valueOf(Configuration.getObstacleConnectionFactor()));

        rewardDistanceTextField.setText(String.valueOf(Configuration.getRewardDistance()));
        sightRangeTextField.setText(String.valueOf(Configuration.getSightRange()));

        maxAgentsTextField.setText(String.valueOf(Configuration.getMaxAgents()));

        // number of steps for multi step problem
        maxStackSizeTextField.setText(String.valueOf(Configuration.getMaxStackSize()));

        coveringWildcardProbabilityTextField.setText(String.valueOf(Configuration.getCoveringWildcardProbability()));
        randomStartCheckBox.setSelected(Configuration.isRandomStart());
        doEvolutionaryAlgorithmCheckBox.setSelected(Configuration.isDoEvolutionaryAlgorithm());
        doEvolutionaryAlgorithmCheckBoxActionPerformed(null);

        thetaSubsumerTextField.setText(String.valueOf(Configuration.getThetaSubsumer()));
        epsilon0TextField.setText(String.valueOf(Configuration.getEpsilon0()));

        betaTextField.setText(String.valueOf(Configuration.getBeta()));

        predictionInitializationTextField.setText(String.valueOf(Configuration.getPredictionInitialization()));
        predictionErrorInitializationTextField.setText(String.valueOf(Configuration.getPredictionErrorInitialization()));
        fitnessInitializationTextField.setText(String.valueOf(Configuration.getFitnessInitialization()));

        deltaTextField.setText(String.valueOf(Configuration.getDelta()));
        thetaDelTextField.setText(String.valueOf(Configuration.getThetaDel()));

        doActionSetSubsumptionCheckBox.setSelected(Configuration.isDoActionSetSubsumption());

        alphaTextField.setText(String.valueOf(Configuration.getAlpha()));
        gammaTextField.setText(String.valueOf(Configuration.getGamma()));
        nuTextField.setText(String.valueOf(Configuration.getNu()));
        thetaTextField.setText(String.valueOf(Configuration.getThetaGA()));

        predictionErrorReductionTextField.setText(String.valueOf(Configuration.getPredictionErrorReduction()));
        fitnessReductionTextField.setText(String.valueOf(Configuration.getFitnessReduction()));
        mutationProbabilityTextField.setText(String.valueOf(Configuration.getMutationProbability()));

        doGASubsumptionCheckBox.setSelected(Configuration.isDoGASubsumption());
        allowRotationCheckBox.setSelected(Configuration.isDoAllowRotation());

        switch (Configuration.getExplorationMode()) {
            case Configuration.NO_EXPLORATION_MODE:
                explorationModeButtonGroup.setSelected(noExplorationModeRadioButton.getModel(), true);
                break;
            case Configuration.ALWAYS_EXPLORATION_MODE:
                explorationModeButtonGroup.setSelected(alwaysExplorationModeRadioButton.getModel(), true);
                break;
            case Configuration.SWITCH_EXPLORATION_MODE:
                explorationModeButtonGroup.setSelected(switchExplorationAndExploitationModeRadioButton.getModel(), true);
                break;
            case Configuration.RANDOM_EXPLORATION_MODE:
                explorationModeButtonGroup.setSelected(randomExplorationAndExploitationModeRadioButton.getModel(), true);
                break;
            case Configuration.EXPLORE_THEN_EXPLOIT_MODE:
                explorationModeButtonGroup.setSelected(exploreThenExploitModeRadioButton.getModel(), true);
                break;
            case Configuration.LINEAR_REDUCTION_EXPLORE_MODE:
                explorationModeButtonGroup.setSelected(linearExplorationReductionModeRadioButton.getModel(), true);
                break;
        }

        switch (Configuration.getGoalAgentMovementType()) {
            case Configuration.TOTAL_RANDOM_MOVEMENT:
                goalAgentMovementButtonGroup.setSelected(totalRandomGoalAgentMovementRadioButton.getModel(), true);
                break;
            case Configuration.RANDOM_MOVEMENT:
                goalAgentMovementButtonGroup.setSelected(randomGoalAgentMovementRadioButton.getModel(), true);
                break;
            case Configuration.INTELLIGENT_MOVEMENT_OPEN:
                goalAgentMovementButtonGroup.setSelected(intelligentOpenGoalAgentMovementRadioButton.getModel(), true);
                break;
            case Configuration.INTELLIGENT_MOVEMENT_HIDE:
                goalAgentMovementButtonGroup.setSelected(intelligentHideGoalAgentMovementRadioButton.getModel(), true);
                break;
            case Configuration.RANDOM_DIRECTION_CHANGE:
                goalAgentMovementButtonGroup.setSelected(maxOneDirectionChangeGoalAgentMovementRadioButton.getModel(), true);
                break;
            case Configuration.ALWAYS_SAME_DIRECTION:
                goalAgentMovementButtonGroup.setSelected(alwaysInTheSameDirectionGoalAgentMovementRadioButton.getModel(), true);
                break;
            case Configuration.RANDOM_HIDE:
                goalAgentMovementButtonGroup.setSelected(randomHideGoalAgentMovementRadioButton.getModel(), true);
                break;
            case Configuration.LCS_MOVEMENT:
                goalAgentMovementButtonGroup.setSelected(LCSGoalAgentMovementRadioButton.getModel(), true);
                break;
        }


        goalAgentMovementSpeedTextField.setText(String.valueOf(Configuration.getGoalAgentMovementSpeed()));

        switch (Configuration.getAgentType()) {
            case Configuration.RANDOMIZED_MOVEMENT_AGENT_TYPE:
                agentTypeButtonGroup.setSelected(randomizedMovementRadioButton.getModel(), true);
                activateLCSControls(false);
                break;
            case Configuration.SIMPLE_AI_AGENT_TYPE:
                agentTypeButtonGroup.setSelected(simpleAIAgentRadioButton.getModel(), true);
                activateLCSControls(false);
                break;
            case Configuration.INTELLIGENT_AI_AGENT_TYPE:
                agentTypeButtonGroup.setSelected(intelligentAIAgentRadioButton.getModel(), true);
                activateLCSControls(false);
                break;
            case Configuration.NEW_LCS_AGENT_TYPE:
                agentTypeButtonGroup.setSelected(newLCSAgentRadioButton.getModel(), true);
                activateLCSControls(true);
                break;
            case Configuration.OLD_LCS_AGENT_TYPE:
                agentTypeButtonGroup.setSelected(oldLCSAgentRadioButton.getModel(), true);
                activateLCSControls(true);
                break;
            case Configuration.MULTISTEP_LCS_AGENT_TYPE:
                agentTypeButtonGroup.setSelected(multistepLCSAgentRadioButton.getModel(), true);
                activateLCSControls(true);
                break;
            case Configuration.SINGLE_LCS_AGENT_TYPE:
                agentTypeButtonGroup.setSelected(singleLCSAgentRadioButton.getModel(), true);
                activateLCSControls(true);
                break;
        }

        switch (Configuration.getExternalRewardMode()) {
            case Configuration.NO_EXTERNAL_REWARD:
                externalRewardButtonGroup.setSelected(noExternalRewardRadioButton.getModel(), true);
                break;
            case Configuration.REWARD_ALL_EQUALLY:
                externalRewardButtonGroup.setSelected(rewardAllEquallyRadioButton.getModel(), true);
                break;
            case Configuration.REWARD_SIMPLE:
                externalRewardButtonGroup.setSelected(rewardSimpleRadioButton.getModel(), true);
                break;
            case Configuration.REWARD_COMPLEX:
                externalRewardButtonGroup.setSelected(rewardComplexRadioButton.getModel(), true);
                break;
            case Configuration.REWARD_NEW:
                externalRewardButtonGroup.setSelected(rewardNewRadioButton.getModel(), true);
                break;
            case Configuration.REWARD_EGOISM:
                externalRewardButtonGroup.setSelected(rewardEgoisticRadioButton.getModel(), true);
                break;
        }
    }


    // TODO: Dateien die von einem früheren Start übernommen wurden, werden nicht in die plot file integriert
    public void createAllPlotFile() {
        String entry = new String("");
        int number_steps = Integer.valueOf(numberOfStepsTextField.getText());
        int number_problems = Integer.valueOf(numberOfProblemsTextField.getText());
        double sight_range = Double.valueOf(sightRangeTextField.getText());

        entry +=
                "set key left box\n" +
                "set xrange [0:" + (number_steps * number_problems) + "]\n";
        String file_name = "plot-all-" + timeString + ".plt";

        String header = new String("");
        String do_plot1 = new String("");
        String do_plot2 = new String("");
        String do_plot3 = new String("");

        header +=
                "set output \"plot_";
        do_plot1 +=
                ".eps\"\n" +
                "set terminal postscript eps\n" +
                "plot ";
        do_plot2 +=
                ".png\"\n" +
                "set terminal png\n" +
                "plot ";

        String[] stats = {"points_spread", "points_average", "distance_spread", "goal_agent_distance_spread", "distance_average", "goal_agent_distance_average", "covered_area", "goal_percentage"};
        String[] yrange = {"0:" + number_steps * number_problems / 10, "0:" + number_steps * number_problems, "0:" + 2.0 * sight_range, "0:" + 2.0 * sight_range, "0:" + 2 * sight_range, "0:" + 2 * sight_range, "0.0:1.0", "0.0:1.0"};
        entry =
                new String("");

        int n = 0;
        /*for (String s : stats) {
            entry += "set yrange [" + yrange[n] + "]\n";
            n++;

            String dat_files = new String("");
            int nn = config_strings.size();
            for (String c : config_strings) {
                dat_files += "\"output_" + c + "\\\\" + s + "-" + c + ".dat\" with lines";
                if (nn > 1) {
                    dat_files += ", ";
                }
                nn--;
            }

            dat_files += "\n";
            entry +=
                    header + s + "-" + timeString + do_plot1 + dat_files;
            entry +=
                    header + s + "-" + timeString + do_plot2 + dat_files;
        }*/

            String dat_files = new String("");
            int nn = config_strings.size();
            for (String c : config_strings) {
                dat_files += "\"output_" + c + "\\\\" + "100_goal_agent_observed" + "-" + c + ".dat\" with lines";
                if (nn > 1) {
                    dat_files += ", ";
                }
                nn--;
            }

            dat_files += "\n";
            entry +=
                    header + "100_goal_agent_observed" + "-" + timeString + do_plot1 + dat_files;
            entry +=
                    header + "100_goal_agent_observed" + "-" + timeString + do_plot2 + dat_files;

        try {
            plot_out = new BufferedWriter(new FileWriter(file_name, true));
            plot_out.write(entry);
            plot_out.flush();
            plot_out.close();
        } catch (Exception e) {
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
        scenarioTypeButtonGroup = new javax.swing.ButtonGroup();
        saveNewButton = new javax.swing.JButton();
        problemDefinitionPanel = new javax.swing.JPanel();
        gridPanel = new javax.swing.JPanel();
        obstaclePercentageLabel = new javax.swing.JLabel();
        obstaclePercentageTextField = new javax.swing.JTextField();
        obstacleConnectionFactorTextField = new javax.swing.JTextField();
        maxXLabel = new javax.swing.JLabel();
        maxXTextField = new javax.swing.JTextField();
        maxYTextField = new javax.swing.JTextField();
        obstacleConnectionFactorLabel = new javax.swing.JLabel();
        rewardRangeLabel = new javax.swing.JLabel();
        rewardDistanceTextField = new javax.swing.JTextField();
        randomScenarioRadioButton = new javax.swing.JRadioButton();
        pillarScenarioRadioButton = new javax.swing.JRadioButton();
        crossScenarioRadioButton = new javax.swing.JRadioButton();
        roomScenarioRadioButton = new javax.swing.JRadioButton();
        difficultScenarioRadioButton = new javax.swing.JRadioButton();
        nonTorusScenarioRadioButton = new javax.swing.JRadioButton();
        mazeScenarioRadioButton = new javax.swing.JRadioButton();
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
        createAnimatedGIFCheckBox = new javax.swing.JCheckBox();
        goalAgentMovementPanel = new javax.swing.JPanel();
        totalRandomGoalAgentMovementRadioButton = new javax.swing.JRadioButton();
        randomGoalAgentMovementRadioButton = new javax.swing.JRadioButton();
        maxOneDirectionChangeGoalAgentMovementRadioButton = new javax.swing.JRadioButton();
        alwaysInTheSameDirectionGoalAgentMovementRadioButton = new javax.swing.JRadioButton();
        goalAgentMovementSpeedLabel = new javax.swing.JLabel();
        goalAgentMovementSpeedTextField = new javax.swing.JTextField();
        intelligentHideGoalAgentMovementRadioButton = new javax.swing.JRadioButton();
        intelligentOpenGoalAgentMovementRadioButton = new javax.swing.JRadioButton();
        LCSGoalAgentMovementRadioButton = new javax.swing.JRadioButton();
        randomHideGoalAgentMovementRadioButton = new javax.swing.JRadioButton();
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
        allowRotationCheckBox = new javax.swing.JCheckBox();
        randomStartCheckBox = new javax.swing.JCheckBox();
        agentTypePanel = new javax.swing.JPanel();
        randomizedMovementRadioButton = new javax.swing.JRadioButton();
        simpleAIAgentRadioButton = new javax.swing.JRadioButton();
        intelligentAIAgentRadioButton = new javax.swing.JRadioButton();
        newLCSAgentRadioButton = new javax.swing.JRadioButton();
        oldLCSAgentRadioButton = new javax.swing.JRadioButton();
        rewardDistanceLabel = new javax.swing.JLabel();
        sightRangeTextField = new javax.swing.JTextField();
        multistepLCSAgentRadioButton = new javax.swing.JRadioButton();
        singleLCSAgentRadioButton = new javax.swing.JRadioButton();
        explorationModePanel = new javax.swing.JPanel();
        noExplorationModeRadioButton = new javax.swing.JRadioButton();
        alwaysExplorationModeRadioButton = new javax.swing.JRadioButton();
        switchExplorationAndExploitationModeRadioButton = new javax.swing.JRadioButton();
        exploreThenExploitModeRadioButton = new javax.swing.JRadioButton();
        linearExplorationReductionModeRadioButton = new javax.swing.JRadioButton();
        randomExplorationAndExploitationModeRadioButton = new javax.swing.JRadioButton();
        packageButton = new javax.swing.JButton();
        rewardModelPanel = new javax.swing.JPanel();
        maxStackSizeLabel = new javax.swing.JLabel();
        maxStackSizeTextField = new javax.swing.JTextField();
        isEventDrivenCheckBox = new javax.swing.JCheckBox();
        useMaxPredictionCheckBox = new javax.swing.JCheckBox();
        useQuadraticRewardCheckBox = new javax.swing.JCheckBox();
        resultsScrollPane = new javax.swing.JScrollPane();
        resultsTable = new javax.swing.JTable();
        updateDatabaseButton = new javax.swing.JButton();
        runLastBatchButton = new javax.swing.JButton();
        communicationPanel = new javax.swing.JPanel();
        noExternalRewardRadioButton = new javax.swing.JRadioButton();
        rewardAllEquallyRadioButton = new javax.swing.JRadioButton();
        rewardSimpleRadioButton = new javax.swing.JRadioButton();
        rewardComplexRadioButton = new javax.swing.JRadioButton();
        rewardNewRadioButton = new javax.swing.JRadioButton();
        exchangeClassifiersCheckBox = new javax.swing.JCheckBox();
        rewardEgoisticRadioButton = new javax.swing.JRadioButton();
        deleteButton = new javax.swing.JButton();
        saveAllRandomButton = new javax.swing.JButton();
        saveAllStackButton = new javax.swing.JButton();
        saveAllExplorationButton = new javax.swing.JButton();
        saveAllPopulationButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Agent Configuration File Editor v1.00");
        setResizable(false);

        saveNewButton.setFont(new java.awt.Font("Tahoma", 1, 11));
        saveNewButton.setText("Save");
        saveNewButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveNewButtonActionPerformed(evt);
            }
        });

        problemDefinitionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Problem definition", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        gridPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Grid", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        obstaclePercentageLabel.setFont(new java.awt.Font("Arial", 0, 10));
        obstaclePercentageLabel.setText("Grid percentage");
        obstaclePercentageLabel.setToolTipText("Percentage of the grid that is occupied by obstacles");

        obstaclePercentageTextField.setText("20");

        obstacleConnectionFactorTextField.setText("0.1");

        maxXLabel.setFont(new java.awt.Font("Arial", 0, 12));
        maxXLabel.setText("Max X / Max Y");

        maxXTextField.setText("16");

        maxYTextField.setText("16");

        obstacleConnectionFactorLabel.setFont(new java.awt.Font("Arial", 0, 10));
        obstacleConnectionFactorLabel.setText("Connection factor");

        rewardRangeLabel.setFont(new java.awt.Font("Arial", 0, 12));
        rewardRangeLabel.setText("Reward range");

        rewardDistanceTextField.setText("4");

        scenarioTypeButtonGroup.add(randomScenarioRadioButton);
        randomScenarioRadioButton.setText("Random scenario");
        randomScenarioRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                randomScenarioRadioButtonActionPerformed(evt);
            }
        });

        scenarioTypeButtonGroup.add(pillarScenarioRadioButton);
        pillarScenarioRadioButton.setText("Pillar scenario");
        pillarScenarioRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pillarScenarioRadioButtonActionPerformed(evt);
            }
        });

        scenarioTypeButtonGroup.add(crossScenarioRadioButton);
        crossScenarioRadioButton.setText("Cross scenario");
        crossScenarioRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                crossScenarioRadioButtonActionPerformed(evt);
            }
        });

        scenarioTypeButtonGroup.add(roomScenarioRadioButton);
        roomScenarioRadioButton.setText("Room scenario");
        roomScenarioRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                roomScenarioRadioButtonActionPerformed(evt);
            }
        });

        scenarioTypeButtonGroup.add(difficultScenarioRadioButton);
        difficultScenarioRadioButton.setText("Difficult scenario");

        scenarioTypeButtonGroup.add(nonTorusScenarioRadioButton);
        nonTorusScenarioRadioButton.setText("Non-Torus");

        scenarioTypeButtonGroup.add(mazeScenarioRadioButton);
        mazeScenarioRadioButton.setSelected(true);
        mazeScenarioRadioButton.setText("Maze scenario");
        mazeScenarioRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mazeScenarioRadioButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout gridPanelLayout = new javax.swing.GroupLayout(gridPanel);
        gridPanel.setLayout(gridPanelLayout);
        gridPanelLayout.setHorizontalGroup(
            gridPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(gridPanelLayout.createSequentialGroup()
                .addGroup(gridPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(crossScenarioRadioButton)
                    .addComponent(roomScenarioRadioButton)
                    .addComponent(difficultScenarioRadioButton)
                    .addComponent(randomScenarioRadioButton)
                    .addComponent(mazeScenarioRadioButton)
                    .addComponent(nonTorusScenarioRadioButton)
                    .addComponent(pillarScenarioRadioButton)
                    .addGroup(gridPanelLayout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(obstaclePercentageLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(obstaclePercentageTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, gridPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(obstacleConnectionFactorLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(obstacleConnectionFactorTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(gridPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, gridPanelLayout.createSequentialGroup()
                            .addComponent(maxXLabel)
                            .addGap(18, 18, 18)
                            .addComponent(maxXTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(maxYTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(gridPanelLayout.createSequentialGroup()
                            .addComponent(rewardRangeLabel)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(rewardDistanceTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        gridPanelLayout.setVerticalGroup(
            gridPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(gridPanelLayout.createSequentialGroup()
                .addGroup(gridPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rewardDistanceTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rewardRangeLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(gridPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(maxXLabel)
                    .addComponent(maxYTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(maxXTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(randomScenarioRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(gridPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(obstaclePercentageTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(obstaclePercentageLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(gridPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(obstacleConnectionFactorTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(obstacleConnectionFactorLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mazeScenarioRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nonTorusScenarioRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pillarScenarioRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(crossScenarioRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(roomScenarioRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(difficultScenarioRadioButton)
                .addGap(89, 89, 89))
        );

        testsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Tests", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        numberOfExperimentsLabel.setFont(new java.awt.Font("Arial", 0, 12));
        numberOfExperimentsLabel.setText("Experiments");

        numberOfExperimentsTextField.setText("10");

        numberOfProblemsTextField.setText("10");

        numberOfStepsTextField.setText("500");

        randomSeedLabel.setFont(new java.awt.Font("Arial", 0, 12));
        randomSeedLabel.setText("Random Seed");

        randomSeedTextField.setText("0");

        agentCountLabel.setFont(new java.awt.Font("Arial", 0, 12));
        agentCountLabel.setText("Number of agents");

        maxAgentsTextField.setText("8");

        stepsLabel.setFont(new java.awt.Font("Arial", 0, 12));
        stepsLabel.setText("Steps");

        problemsLabel.setFont(new java.awt.Font("Arial", 0, 12));
        problemsLabel.setText("Problems");

        createAnimatedGIFCheckBox.setText("Create animated GIF");

        javax.swing.GroupLayout testsPanelLayout = new javax.swing.GroupLayout(testsPanel);
        testsPanel.setLayout(testsPanelLayout);
        testsPanelLayout.setHorizontalGroup(
            testsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(testsPanelLayout.createSequentialGroup()
                .addGroup(testsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(createAnimatedGIFCheckBox)
                    .addGroup(testsPanelLayout.createSequentialGroup()
                        .addComponent(agentCountLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(maxAgentsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(testsPanelLayout.createSequentialGroup()
                        .addGroup(testsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(randomSeedLabel)
                            .addComponent(numberOfExperimentsLabel)
                            .addComponent(problemsLabel)
                            .addComponent(stepsLabel))
                        .addGap(20, 20, 20)
                        .addGroup(testsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(numberOfStepsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(numberOfProblemsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(testsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(randomSeedTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(numberOfExperimentsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
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
                    .addComponent(numberOfExperimentsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(numberOfExperimentsLabel))
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
                    .addComponent(maxAgentsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(createAnimatedGIFCheckBox)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        goalAgentMovementButtonGroup.add(LCSGoalAgentMovementRadioButton);
        LCSGoalAgentMovementRadioButton.setFont(new java.awt.Font("Arial", 0, 12));
        LCSGoalAgentMovementRadioButton.setText("LCS");
        LCSGoalAgentMovementRadioButton.setToolTipText("Reward by agents out of sight");

        goalAgentMovementButtonGroup.add(randomHideGoalAgentMovementRadioButton);
        randomHideGoalAgentMovementRadioButton.setFont(new java.awt.Font("Arial", 0, 12));
        randomHideGoalAgentMovementRadioButton.setText("Random (Hide)");

        javax.swing.GroupLayout goalAgentMovementPanelLayout = new javax.swing.GroupLayout(goalAgentMovementPanel);
        goalAgentMovementPanel.setLayout(goalAgentMovementPanelLayout);
        goalAgentMovementPanelLayout.setHorizontalGroup(
            goalAgentMovementPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(intelligentHideGoalAgentMovementRadioButton)
            .addComponent(totalRandomGoalAgentMovementRadioButton)
            .addComponent(randomGoalAgentMovementRadioButton)
            .addComponent(maxOneDirectionChangeGoalAgentMovementRadioButton)
            .addComponent(alwaysInTheSameDirectionGoalAgentMovementRadioButton)
            .addComponent(intelligentOpenGoalAgentMovementRadioButton)
            .addGroup(goalAgentMovementPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(goalAgentMovementSpeedLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 40, Short.MAX_VALUE)
                .addComponent(goalAgentMovementSpeedTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
            .addComponent(LCSGoalAgentMovementRadioButton)
            .addComponent(randomHideGoalAgentMovementRadioButton)
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
                .addComponent(randomHideGoalAgentMovementRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LCSGoalAgentMovementRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(goalAgentMovementPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(goalAgentMovementSpeedLabel)
                    .addComponent(goalAgentMovementSpeedTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout problemDefinitionPanelLayout = new javax.swing.GroupLayout(problemDefinitionPanel);
        problemDefinitionPanel.setLayout(problemDefinitionPanelLayout);
        problemDefinitionPanelLayout.setHorizontalGroup(
            problemDefinitionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(problemDefinitionPanelLayout.createSequentialGroup()
                .addGroup(problemDefinitionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(gridPanel, 0, 161, Short.MAX_VALUE)
                    .addComponent(testsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(goalAgentMovementPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        problemDefinitionPanelLayout.setVerticalGroup(
            problemDefinitionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(problemDefinitionPanelLayout.createSequentialGroup()
                .addComponent(testsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(gridPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(goalAgentMovementPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12))
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
                .addContainerGap(19, Short.MAX_VALUE))
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

        maxPopSizeTextField.setText("128");

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

        allowRotationCheckBox.setFont(new java.awt.Font("Arial", 0, 12));
        allowRotationCheckBox.setText("Allow rotation");
        allowRotationCheckBox.setToolTipText("Rotated classifiers are equal");
        allowRotationCheckBox.setActionCommand("Do GA Subsumption");

        randomStartCheckBox.setFont(new java.awt.Font("Arial", 0, 12));
        randomStartCheckBox.setSelected(true);
        randomStartCheckBox.setText("Random start");
        randomStartCheckBox.setActionCommand("Do GA Subsumption");

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
                    .addComponent(doGASubsumptionCheckBox)
                    .addComponent(allowRotationCheckBox)
                    .addComponent(randomStartCheckBox))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(randomStartCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(allowRotationCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(doActionSetSubsumptionCheckBox)
                .addGap(34, 34, 34))
        );

        javax.swing.GroupLayout lcsParametersPanelLayout = new javax.swing.GroupLayout(lcsParametersPanel);
        lcsParametersPanel.setLayout(lcsParametersPanelLayout);
        lcsParametersPanelLayout.setHorizontalGroup(
            lcsParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lcsParametersPanelLayout.createSequentialGroup()
                .addGroup(lcsParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(classifierSubsumptionAndDeletionPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, lcsParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(gaParametersPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(fitnessAndPredictionPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        lcsParametersPanelLayout.setVerticalGroup(
            lcsParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lcsParametersPanelLayout.createSequentialGroup()
                .addComponent(classifierSubsumptionAndDeletionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(gaParametersPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fitnessAndPredictionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );

        agentTypePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Agent type", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        agentTypeButtonGroup.add(randomizedMovementRadioButton);
        randomizedMovementRadioButton.setFont(new java.awt.Font("Arial", 0, 12));
        randomizedMovementRadioButton.setLabel("Randomized");
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

        agentTypeButtonGroup.add(newLCSAgentRadioButton);
        newLCSAgentRadioButton.setFont(new java.awt.Font("Arial", 0, 12));
        newLCSAgentRadioButton.setText("New LCS agent");
        newLCSAgentRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newLCSAgentRadioButtonActionPerformed(evt);
            }
        });

        agentTypeButtonGroup.add(oldLCSAgentRadioButton);
        oldLCSAgentRadioButton.setFont(new java.awt.Font("Arial", 0, 12));
        oldLCSAgentRadioButton.setSelected(true);
        oldLCSAgentRadioButton.setText("Old LCS agent");
        oldLCSAgentRadioButton.setToolTipText("LCS Agent with special reward function");
        oldLCSAgentRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                oldLCSAgentRadioButtonActionPerformed(evt);
            }
        });

        rewardDistanceLabel.setFont(new java.awt.Font("Arial", 0, 12));
        rewardDistanceLabel.setText("Sight range");

        sightRangeTextField.setText("5");

        agentTypeButtonGroup.add(multistepLCSAgentRadioButton);
        multistepLCSAgentRadioButton.setFont(new java.awt.Font("Arial", 0, 12));
        multistepLCSAgentRadioButton.setText("Multistep LCS agent");
        multistepLCSAgentRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                multistepLCSAgentRadioButtonActionPerformed(evt);
            }
        });

        agentTypeButtonGroup.add(singleLCSAgentRadioButton);
        singleLCSAgentRadioButton.setFont(new java.awt.Font("Arial", 0, 12));
        singleLCSAgentRadioButton.setText("Single LCS agent");
        singleLCSAgentRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                singleLCSAgentRadioButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout agentTypePanelLayout = new javax.swing.GroupLayout(agentTypePanel);
        agentTypePanel.setLayout(agentTypePanelLayout);
        agentTypePanelLayout.setHorizontalGroup(
            agentTypePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(agentTypePanelLayout.createSequentialGroup()
                .addGroup(agentTypePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(randomizedMovementRadioButton)
                    .addComponent(intelligentAIAgentRadioButton)
                    .addComponent(simpleAIAgentRadioButton)
                    .addComponent(oldLCSAgentRadioButton)
                    .addComponent(newLCSAgentRadioButton)
                    .addGroup(agentTypePanelLayout.createSequentialGroup()
                        .addComponent(rewardDistanceLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sightRangeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(multistepLCSAgentRadioButton)
                    .addComponent(singleLCSAgentRadioButton))
                .addContainerGap(16, Short.MAX_VALUE))
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
                .addComponent(oldLCSAgentRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(newLCSAgentRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(multistepLCSAgentRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(singleLCSAgentRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(agentTypePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rewardDistanceLabel)
                    .addComponent(sightRangeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)))
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

        explorationModeButtonGroup.add(randomExplorationAndExploitationModeRadioButton);
        randomExplorationAndExploitationModeRadioButton.setFont(new java.awt.Font("Arial", 0, 12));
        randomExplorationAndExploitationModeRadioButton.setText("Random explore/exploit");

        javax.swing.GroupLayout explorationModePanelLayout = new javax.swing.GroupLayout(explorationModePanel);
        explorationModePanel.setLayout(explorationModePanelLayout);
        explorationModePanelLayout.setHorizontalGroup(
            explorationModePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(noExplorationModeRadioButton)
            .addComponent(alwaysExplorationModeRadioButton)
            .addComponent(switchExplorationAndExploitationModeRadioButton)
            .addComponent(exploreThenExploitModeRadioButton)
            .addComponent(linearExplorationReductionModeRadioButton)
            .addComponent(randomExplorationAndExploitationModeRadioButton)
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
                .addComponent(randomExplorationAndExploitationModeRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(exploreThenExploitModeRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(linearExplorationReductionModeRadioButton))
        );

        packageButton.setFont(new java.awt.Font("Tahoma", 1, 11));
        packageButton.setText("Package");
        packageButton.setEnabled(false);
        packageButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                packageButtonActionPerformed(evt);
            }
        });

        rewardModelPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Reward model", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        maxStackSizeLabel.setFont(new java.awt.Font("Arial", 0, 12));
        maxStackSizeLabel.setText("Stack size");

        maxStackSizeTextField.setText("128");

        isEventDrivenCheckBox.setFont(new java.awt.Font("Arial", 0, 12));
        isEventDrivenCheckBox.setSelected(true);
        isEventDrivenCheckBox.setText("Is event driven?");

        useMaxPredictionCheckBox.setFont(new java.awt.Font("Arial", 0, 12));
        useMaxPredictionCheckBox.setSelected(true);
        useMaxPredictionCheckBox.setText("Use max prediction?");

        useQuadraticRewardCheckBox.setFont(new java.awt.Font("Arial", 0, 12));
        useQuadraticRewardCheckBox.setSelected(true);
        useQuadraticRewardCheckBox.setText("Use quadratic reward");

        javax.swing.GroupLayout rewardModelPanelLayout = new javax.swing.GroupLayout(rewardModelPanel);
        rewardModelPanel.setLayout(rewardModelPanelLayout);
        rewardModelPanelLayout.setHorizontalGroup(
            rewardModelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rewardModelPanelLayout.createSequentialGroup()
                .addGroup(rewardModelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(useMaxPredictionCheckBox)
                    .addComponent(isEventDrivenCheckBox)
                    .addGroup(rewardModelPanelLayout.createSequentialGroup()
                        .addComponent(maxStackSizeLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(maxStackSizeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(useQuadraticRewardCheckBox))
                .addContainerGap(12, Short.MAX_VALUE))
        );
        rewardModelPanelLayout.setVerticalGroup(
            rewardModelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rewardModelPanelLayout.createSequentialGroup()
                .addGroup(rewardModelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(maxStackSizeLabel)
                    .addComponent(maxStackSizeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(isEventDrivenCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(useMaxPredictionCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(useQuadraticRewardCheckBox))
        );

        resultsTable.setModel(results);
        resultsScrollPane.setViewportView(resultsTable);

        updateDatabaseButton.setText("Update");
        updateDatabaseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateDatabaseButtonActionPerformed(evt);
            }
        });

        runLastBatchButton.setText("Run last batch");
        runLastBatchButton.setEnabled(false);
        runLastBatchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runLastBatchButtonActionPerformed(evt);
            }
        });

        communicationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Communication", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

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

        externalRewardButtonGroup.add(rewardNewRadioButton);
        rewardNewRadioButton.setFont(new java.awt.Font("Arial", 0, 12));
        rewardNewRadioButton.setText("Simple relation 2");

        exchangeClassifiersCheckBox.setFont(new java.awt.Font("Arial", 0, 12));
        exchangeClassifiersCheckBox.setText("Exchange classifiers");

        externalRewardButtonGroup.add(rewardEgoisticRadioButton);
        rewardEgoisticRadioButton.setFont(new java.awt.Font("Arial", 0, 12));
        rewardEgoisticRadioButton.setText("Egoistic relation");

        javax.swing.GroupLayout communicationPanelLayout = new javax.swing.GroupLayout(communicationPanel);
        communicationPanel.setLayout(communicationPanelLayout);
        communicationPanelLayout.setHorizontalGroup(
            communicationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(communicationPanelLayout.createSequentialGroup()
                .addGroup(communicationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(noExternalRewardRadioButton)
                    .addComponent(rewardAllEquallyRadioButton)
                    .addComponent(rewardSimpleRadioButton)
                    .addComponent(rewardComplexRadioButton)
                    .addComponent(rewardNewRadioButton)
                    .addComponent(exchangeClassifiersCheckBox)
                    .addComponent(rewardEgoisticRadioButton))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        communicationPanelLayout.setVerticalGroup(
            communicationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(communicationPanelLayout.createSequentialGroup()
                .addComponent(exchangeClassifiersCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(noExternalRewardRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rewardAllEquallyRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rewardSimpleRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rewardComplexRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rewardNewRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rewardEgoisticRadioButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        deleteButton.setText("Delete");
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        saveAllRandomButton.setText("Save All random Scenario");
        saveAllRandomButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAllRandomButtonActionPerformed(evt);
            }
        });

        saveAllStackButton.setText("Save all stack sizes");
        saveAllStackButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAllStackButtonActionPerformed(evt);
            }
        });

        saveAllExplorationButton.setText("Save all exploration");
        saveAllExplorationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAllExplorationButtonActionPerformed(evt);
            }
        });

        saveAllPopulationButton.setText("Save all population sizes");
        saveAllPopulationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAllPopulationButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(problemDefinitionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(communicationPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(rewardModelPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(agentTypePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(saveNewButton, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(packageButton))
                            .addComponent(explorationModePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(saveAllRandomButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(saveAllExplorationButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(saveAllStackButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(saveAllPopulationButton))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lcsParametersPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(updateDatabaseButton, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(2, 2, 2)
                                .addComponent(runLastBatchButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(deleteButton))
                            .addComponent(resultsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 559, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(problemDefinitionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 753, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(agentTypePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1)
                        .addComponent(rewardModelPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(communicationPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(explorationModePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(resultsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 634, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(updateDatabaseButton)
                            .addComponent(runLastBatchButton)
                            .addComponent(deleteButton))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(packageButton)
                    .addComponent(saveNewButton)
                    .addComponent(saveAllRandomButton)
                    .addComponent(saveAllExplorationButton)
                    .addComponent(saveAllStackButton)
                    .addComponent(saveAllPopulationButton)))
            .addComponent(lcsParametersPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 684, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void doEvolutionaryAlgorithmCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doEvolutionaryAlgorithmCheckBoxActionPerformed
    boolean do_activate = doEvolutionaryAlgorithmCheckBox.isSelected();
    thetaLabel.setEnabled(do_activate);
    thetaTextField.setEnabled(do_activate);
        
    mutationProbabilityLabel.setEnabled(do_activate);
    mutationProbabilityTextField.setEnabled(do_activate);

    predictionErrorReductionLabel.setEnabled(do_activate);
    predictionErrorReductionTextField.setEnabled(do_activate);

    fitnessReductionLabel.setEnabled(do_activate);
    fitnessReductionTextField.setEnabled(do_activate);
}//GEN-LAST:event_doEvolutionaryAlgorithmCheckBoxActionPerformed

private void activateRandomGridParameters(boolean activate) {
    obstaclePercentageLabel.setEnabled(activate);
    obstaclePercentageTextField.setEnabled(activate);
    obstacleConnectionFactorLabel.setEnabled(activate);
    obstacleConnectionFactorTextField.setEnabled(activate);
}

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

private void newLCSAgentRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newLCSAgentRadioButtonActionPerformed
    activateLCSControls(newLCSAgentRadioButton.isSelected());
}//GEN-LAST:event_newLCSAgentRadioButtonActionPerformed

private void oldLCSAgentRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_oldLCSAgentRadioButtonActionPerformed
    activateLCSControls(oldLCSAgentRadioButton.isSelected());
}//GEN-LAST:event_oldLCSAgentRadioButtonActionPerformed

private void runLastBatchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runLastBatchButtonActionPerformed
    runLastBatchButton.setEnabled(false);
    try {
        Runtime rt = Runtime.getRuntime();
        String cur_dir = System.getProperty("user.dir");
        File work_dir = new File(cur_dir + "\\" + last_directory);        
        Process pr = rt.exec("cmd.exe /c " + last_batch_file, null, work_dir);
        BufferedReader error = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        String line=null;
        while((line=error.readLine()) != null) {
            System.out.println(line);
        }
        int exitVal = pr.waitFor();
        System.out.println("Exited with error code "+exitVal);
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error calling " + last_batch_file + " (" + e + ")", "Error calling batch file", JOptionPane.ERROR_MESSAGE);
    }
}//GEN-LAST:event_runLastBatchButtonActionPerformed

private void packageButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_packageButtonActionPerformed
    createAllPlotFile();
    try {
        String last_date = joschka.run();
        last_directory = "agent-" + last_date;
        last_batch_file = "batch-agent-" + last_date + ".bat";
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error packaging files: " + e, "Error packaging files", JOptionPane.ERROR_MESSAGE);
        return;
    }


    config_strings.clear();
    resetTimeString();

    packageButton.setEnabled(false);
    runLastBatchButton.setEnabled(true);
}//GEN-LAST:event_packageButtonActionPerformed

private void saveNewButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveNewButtonActionPerformed
    this.saveSettings("default.txt");
    String id = new String(timeString + "-" + conf_id);
    this.saveSettings("config-" + id + ".txt");
    config_strings.add(id);
    conf_id++;

    packageButton.setEnabled(true);
}//GEN-LAST:event_saveNewButtonActionPerformed


  private boolean deleteDirectory(File path) {
    if( path.exists() && path.canWrite()) {
      File[] files = path.listFiles();
      for(int i=0; i<files.length; i++) {
         if(files[i].isDirectory()) {
           if(!deleteDirectory(files[i])) {
               return false;
           }
         }
         else {
           if(!files[i].delete()) {
               return false;
           }
         }
      }
      return( path.delete() );
    } else {
        return false;
    }
  }

private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
    int row = sorter.modelIndex(resultsTable.getSelectedRow());
    Object[] my_row = results.getRow(row);
    String config_string = (String) (my_row[0]);
    String[] strings = config_string.split("//");
    File dir = new File(strings[0]);
    if(!deleteDirectory(dir)) {
        JOptionPane.showMessageDialog(this, "Could not delete directory " + strings[0] + ".", "Error deleting entry", JOptionPane.ERROR_MESSAGE);
        return;
    }
    loadResultsIntoDatabase();
}//GEN-LAST:event_deleteButtonActionPerformed

private void multistepLCSAgentRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_multistepLCSAgentRadioButtonActionPerformed
    activateLCSControls(multistepLCSAgentRadioButton.isSelected());
}//GEN-LAST:event_multistepLCSAgentRadioButtonActionPerformed

private void randomScenarioRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_randomScenarioRadioButtonActionPerformed
    activateRandomGridParameters(randomScenarioRadioButton.isSelected());
}//GEN-LAST:event_randomScenarioRadioButtonActionPerformed

private void pillarScenarioRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pillarScenarioRadioButtonActionPerformed
    activateRandomGridParameters(!pillarScenarioRadioButton.isSelected());
}//GEN-LAST:event_pillarScenarioRadioButtonActionPerformed

private void crossScenarioRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_crossScenarioRadioButtonActionPerformed
    activateRandomGridParameters(!crossScenarioRadioButton.isSelected());
}//GEN-LAST:event_crossScenarioRadioButtonActionPerformed

private void roomScenarioRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_roomScenarioRadioButtonActionPerformed
    activateRandomGridParameters(!roomScenarioRadioButton.isSelected());
}//GEN-LAST:event_roomScenarioRadioButtonActionPerformed

private void singleLCSAgentRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_singleLCSAgentRadioButtonActionPerformed
    activateCommunicationControls(singleLCSAgentRadioButton.isSelected());
}//GEN-LAST:event_singleLCSAgentRadioButtonActionPerformed

private void saveScenario() {
    String id = new String(timeString + "-" + conf_id);
    this.saveSettings("config-" + id + ".txt");
    config_strings.add(id);
    conf_id++;
}

private void saveAllRandomButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAllRandomButtonActionPerformed
    this.saveSettings("default.txt");
    String orig_obstacle_percentage = obstaclePercentageTextField.getText();
    String orig_obstacle_con = obstacleConnectionFactorTextField.getText();
    for(int k = 0; k < 7; k++) {
        switch(k) {
            case 0:{
                scenarioTypeButtonGroup.setSelected(randomScenarioRadioButton.getModel(), true);
                for(int i = 0; i < 5; i++) {
                    switch(i) {
                        case 0:obstaclePercentageTextField.setText("0.0");break;
                        case 1:obstaclePercentageTextField.setText("0.05");break;
                        case 2:obstaclePercentageTextField.setText("0.1");break;
                        case 3:obstaclePercentageTextField.setText("0.2");break;
                        case 4:obstaclePercentageTextField.setText("0.4");break;
                    }
                    for(int j = 0; j < 3; j++) {
                        switch(j) {
                            case 0:obstacleConnectionFactorTextField.setText("0.01");break;
                            case 1:obstacleConnectionFactorTextField.setText("0.5");break;
                            case 2:obstacleConnectionFactorTextField.setText("0.99");break;
                        }
                        saveScenario();
                        if(i == 0) {
                            break;
                        }
                    }
                }
            }break;
            case 1:{
                scenarioTypeButtonGroup.setSelected(mazeScenarioRadioButton.getModel(), true);
                
                saveScenario();
            }break;
            case 2:{
                scenarioTypeButtonGroup.setSelected(nonTorusScenarioRadioButton.getModel(), true);
                saveScenario();
            }break;
            case 3:{
                scenarioTypeButtonGroup.setSelected(pillarScenarioRadioButton.getModel(), true);
                saveScenario();
            }break;
            case 4:{
                scenarioTypeButtonGroup.setSelected(crossScenarioRadioButton.getModel(), true);
                saveScenario();
            }break;
            case 5:{
                scenarioTypeButtonGroup.setSelected(roomScenarioRadioButton.getModel(), true);
                saveScenario();
            }break;
            case 6:{
                scenarioTypeButtonGroup.setSelected(difficultScenarioRadioButton.getModel(), true);
                saveScenario();
            }break;
        }
    }
    obstaclePercentageTextField.setText(orig_obstacle_percentage);
    obstacleConnectionFactorTextField.setText(orig_obstacle_con);
    packageButton.setEnabled(true);
}//GEN-LAST:event_saveAllRandomButtonActionPerformed

private void saveAllStackButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAllStackButtonActionPerformed
    this.saveSettings("default.txt");
    String orig_stack = maxStackSizeTextField.getText();
    for(int i = 0; i < 10; i++) {
        switch(i) {
            case 0:maxStackSizeTextField.setText("2");break;
            case 1:maxStackSizeTextField.setText("4");break;
            case 2:maxStackSizeTextField.setText("8");break;
            case 3:maxStackSizeTextField.setText("16");break;
            case 4:maxStackSizeTextField.setText("32");break;
            case 5:maxStackSizeTextField.setText("64");break;
            case 6:maxStackSizeTextField.setText("128");break;
            case 7:maxStackSizeTextField.setText("256");break;
            case 8:maxStackSizeTextField.setText("512");break;
            case 9:maxStackSizeTextField.setText("1024");break;
        }
        String id = new String(timeString + "-" + conf_id);
        this.saveSettings("config-" + id + ".txt");
        config_strings.add(id);
        conf_id++;
    }
    maxStackSizeTextField.setText(orig_stack);
    packageButton.setEnabled(true);
}//GEN-LAST:event_saveAllStackButtonActionPerformed

private void saveAllExplorationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAllExplorationButtonActionPerformed
    this.saveSettings("default.txt");
    ButtonModel old_model = explorationModeButtonGroup.getSelection();

    for(int i = 0; i < 6; i++) {
        switch(i) {
            case 0:explorationModeButtonGroup.setSelected(noExplorationModeRadioButton.getModel(), true);break;
            case 1:explorationModeButtonGroup.setSelected(alwaysExplorationModeRadioButton.getModel(), true);break;
            case 2:explorationModeButtonGroup.setSelected(switchExplorationAndExploitationModeRadioButton.getModel(), true);break;
            case 3:explorationModeButtonGroup.setSelected(exploreThenExploitModeRadioButton.getModel(), true);break;
            case 4:explorationModeButtonGroup.setSelected(linearExplorationReductionModeRadioButton.getModel(), true);break;
            case 5:explorationModeButtonGroup.setSelected(randomExplorationAndExploitationModeRadioButton.getModel(), true);break;
        }
        String id = new String(timeString + "-" + conf_id);
        this.saveSettings("config-" + id + ".txt");
        config_strings.add(id);
        conf_id++;
    }
    explorationModeButtonGroup.setSelected(old_model, true);
    packageButton.setEnabled(true);
}//GEN-LAST:event_saveAllExplorationButtonActionPerformed

private void mazeScenarioRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mazeScenarioRadioButtonActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_mazeScenarioRadioButtonActionPerformed

private void saveAllPopulationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAllPopulationButtonActionPerformed
    this.saveSettings("default.txt");
    String old_pop_size = maxPopSizeTextField.getText();

    int popSize = 8;
    
    for(int i = 0; i < 8; i++) {
        maxPopSizeTextField.setText(new String("" + popSize));
        popSize*=2;

        String id = new String(timeString + "-" + conf_id);
        this.saveSettings("config-" + id + ".txt");
        config_strings.add(id);
        conf_id++;
    }
    maxPopSizeTextField.setText(old_pop_size);
    packageButton.setEnabled(true);
}//GEN-LAST:event_saveAllPopulationButtonActionPerformed

private void activateCommunicationControls(boolean activate) {
    exchangeClassifiersCheckBox.setEnabled(activate);
    for (Enumeration<AbstractButton> e = externalRewardButtonGroup.getElements() ; e.hasMoreElements() ;) {
        e.nextElement().setEnabled(activate);
    }
}
    private void activateLCSControls(boolean activate) {
            rewardModelPanel.setEnabled(activate);
            explorationModePanel.setEnabled(activate);
            communicationPanel.setEnabled(activate);
            maxStackSizeLabel.setEnabled(activate);
            maxStackSizeTextField.setEnabled(activate);
            isEventDrivenCheckBox.setEnabled(activate);
            useMaxPredictionCheckBox.setEnabled(activate);
            useQuadraticRewardCheckBox.setEnabled(activate);
            for (Enumeration<AbstractButton> e = explorationModeButtonGroup.getElements() ; e.hasMoreElements() ;) {
                e.nextElement().setEnabled(activate);
            }
            activateCommunicationControls(activate);
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
    private javax.swing.JRadioButton LCSGoalAgentMovementRadioButton;
    private javax.swing.JLabel agentCountLabel;
    private javax.swing.ButtonGroup agentTypeButtonGroup;
    private javax.swing.JPanel agentTypePanel;
    private javax.swing.JCheckBox allowRotationCheckBox;
    private javax.swing.JLabel alphaLabel;
    private javax.swing.JTextField alphaTextField;
    private javax.swing.JRadioButton alwaysExplorationModeRadioButton;
    private javax.swing.JRadioButton alwaysInTheSameDirectionGoalAgentMovementRadioButton;
    private javax.swing.JLabel betaLabel;
    private javax.swing.JTextField betaTextField;
    private javax.swing.JPanel classifierSubsumptionAndDeletionPanel;
    private javax.swing.JPanel communicationPanel;
    private javax.swing.JLabel coveringWildcardProbabilityLabel;
    private javax.swing.JTextField coveringWildcardProbabilityTextField;
    private javax.swing.JCheckBox createAnimatedGIFCheckBox;
    private javax.swing.JRadioButton crossScenarioRadioButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JLabel deltaLabel;
    private javax.swing.JTextField deltaTextField;
    private javax.swing.JRadioButton difficultScenarioRadioButton;
    private javax.swing.JCheckBox doActionSetSubsumptionCheckBox;
    private javax.swing.JCheckBox doEvolutionaryAlgorithmCheckBox;
    private javax.swing.JCheckBox doGASubsumptionCheckBox;
    private javax.swing.JLabel epsilon0Label;
    private javax.swing.JTextField epsilon0TextField;
    private javax.swing.JCheckBox exchangeClassifiersCheckBox;
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
    private javax.swing.JPanel lcsParametersPanel;
    private javax.swing.JRadioButton linearExplorationReductionModeRadioButton;
    private javax.swing.JTextField maxAgentsTextField;
    private javax.swing.JRadioButton maxOneDirectionChangeGoalAgentMovementRadioButton;
    private javax.swing.JLabel maxPopSizeLabel;
    private javax.swing.JTextField maxPopSizeTextField;
    private javax.swing.JLabel maxStackSizeLabel;
    private javax.swing.JTextField maxStackSizeTextField;
    private javax.swing.JLabel maxXLabel;
    private javax.swing.JTextField maxXTextField;
    private javax.swing.JTextField maxYTextField;
    private javax.swing.JRadioButton mazeScenarioRadioButton;
    private javax.swing.JRadioButton multistepLCSAgentRadioButton;
    private javax.swing.JLabel mutationProbabilityLabel;
    private javax.swing.JTextField mutationProbabilityTextField;
    private javax.swing.JRadioButton newLCSAgentRadioButton;
    private javax.swing.JRadioButton noExplorationModeRadioButton;
    private javax.swing.JRadioButton noExternalRewardRadioButton;
    private javax.swing.JRadioButton nonTorusScenarioRadioButton;
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
    private javax.swing.JRadioButton oldLCSAgentRadioButton;
    private javax.swing.JButton packageButton;
    private javax.swing.JRadioButton pillarScenarioRadioButton;
    private javax.swing.JLabel predictionErrorInitializationLabel;
    private javax.swing.JTextField predictionErrorInitializationTextField;
    private javax.swing.JLabel predictionErrorReductionLabel;
    private javax.swing.JTextField predictionErrorReductionTextField;
    private javax.swing.JLabel predictionInitializationLabel;
    private javax.swing.JTextField predictionInitializationTextField;
    private javax.swing.JPanel problemDefinitionPanel;
    private javax.swing.JLabel problemsLabel;
    private javax.swing.JRadioButton randomExplorationAndExploitationModeRadioButton;
    private javax.swing.JRadioButton randomGoalAgentMovementRadioButton;
    private javax.swing.JRadioButton randomHideGoalAgentMovementRadioButton;
    private javax.swing.JRadioButton randomScenarioRadioButton;
    private javax.swing.JLabel randomSeedLabel;
    private javax.swing.JTextField randomSeedTextField;
    private javax.swing.JCheckBox randomStartCheckBox;
    private javax.swing.JRadioButton randomizedMovementRadioButton;
    private javax.swing.JScrollPane resultsScrollPane;
    private javax.swing.JTable resultsTable;
    private javax.swing.JRadioButton rewardAllEquallyRadioButton;
    private javax.swing.JRadioButton rewardComplexRadioButton;
    private javax.swing.JLabel rewardDistanceLabel;
    private javax.swing.JTextField rewardDistanceTextField;
    private javax.swing.JRadioButton rewardEgoisticRadioButton;
    private javax.swing.JPanel rewardModelPanel;
    private javax.swing.JRadioButton rewardNewRadioButton;
    private javax.swing.JLabel rewardRangeLabel;
    private javax.swing.JRadioButton rewardSimpleRadioButton;
    private javax.swing.JRadioButton roomScenarioRadioButton;
    private javax.swing.JButton runLastBatchButton;
    private javax.swing.JButton saveAllExplorationButton;
    private javax.swing.JButton saveAllPopulationButton;
    private javax.swing.JButton saveAllRandomButton;
    private javax.swing.JButton saveAllStackButton;
    private javax.swing.JButton saveNewButton;
    private javax.swing.ButtonGroup scenarioTypeButtonGroup;
    private javax.swing.JTextField sightRangeTextField;
    private javax.swing.JRadioButton simpleAIAgentRadioButton;
    private javax.swing.JRadioButton singleLCSAgentRadioButton;
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
    private javax.swing.JCheckBox useMaxPredictionCheckBox;
    private javax.swing.JCheckBox useQuadraticRewardCheckBox;
    // End of variables declaration//GEN-END:variables
}
