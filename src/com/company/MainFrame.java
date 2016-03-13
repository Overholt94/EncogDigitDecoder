package com.company;

import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.InputMismatchException;

public class MainFrame extends JFrame implements ActionListener{
    public static void main(String[] args) {
    new MainFrame();
    }
    /**
     * Width and height of screen(Layout space)
     */
    public final static int SCREEN_WIDTH = 500,
                            SCREEN_HEIGHT = 500;
    /**
     * Import/Export Data
     */
    LoadSave loadSave;
    /**
     * Draw view for data
     */
    DrawPanel myDrawing;
    /**
     * All Buttons
     */
    JButton submit,
            recognize,
            deleteEntry,
            getData,
            clearScreen,
            trainNetwork;

    /**
     * Menu items
     */
    JMenuItem deleteItem,
              saveItem,
              getSizeItem;
    /**
     * Automatic Save on Submit
     */
    JRadioButtonMenuItem autoSave;

    /**
     * Neural Network
     */
    NeuralNetworkTrainer neuralNetworkTrainer;

    public MainFrame(){
        setResizable(false);
        setTitle("Digit Recognition with Encog (By Nils Lukas)");

        loadSave = new LoadSave();
        loadSave.load();

        JMenuBar menuBar = new JMenuBar();

        JMenu menu = new JMenu("File");

        saveItem = new JMenuItem("Save");
        menu.add(saveItem);

        getSizeItem = new JMenuItem("Get Size");
        menu.add(getSizeItem);

        deleteItem = new JMenuItem("Delete Savefile");
        menu.add(deleteItem);

        menuBar.add(menu);

        autoSave = new JRadioButtonMenuItem("Automatic Save on Submit");
        autoSave.setSelected(true);
        menuBar.add(autoSave);

        this.setJMenuBar(menuBar);

        myDrawing = new DrawPanel(this);
        neuralNetworkTrainer = new NeuralNetworkTrainer();

        submit = new JButton("Submit");
        recognize = new JButton("Recognize");
        deleteEntry = new JButton("Delete recent");
        getData = new JButton("Get");
        clearScreen = new JButton("Clear Screen");
        trainNetwork = new JButton("Train");

        JPanel jp = new JPanel();
        GridLayout fl = new GridLayout(2,4);
        jp.setLayout(fl);

        jp.add(submit);
        jp.add(deleteEntry);
        jp.add(recognize);
        jp.add(getData);
        jp.add(clearScreen);
        jp.add(trainNetwork);

        add(jp, BorderLayout.PAGE_END);
        add(myDrawing, BorderLayout.CENTER);

        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));

        // Panel
        myDrawing.addMouseMotionListener(new DrawViewMouseMotionListener(myDrawing));
        // Buttons
        submit.addActionListener(this);
        deleteEntry.addActionListener(this);
        getData.addActionListener(this);
        clearScreen.addActionListener(this);
        deleteItem.addActionListener(this);
        saveItem.addActionListener(this);
        trainNetwork.addActionListener(this);
        getSizeItem.addActionListener(this);
        recognize.addActionListener(this);

        pack();
        setVisible(true);
    }

    /**
     * Callback for submitting data
     */
    public void submit(){
        double[] hitArray = Analyzer.analyzeScreen(myDrawing);

        if(hitArray == null){
            JOptionPane.showMessageDialog(null, "Draw something before submitting!");
        }else {
            // Ask user for label that he drew
            String input = JOptionPane.showInputDialog("Please input the digit you just drew! (Only 0-9)");
            try {
                int i = Integer.parseInt(input);
                if(i<0 || i>9){
                    throw new InputMismatchException("Invalid input!");
                }
                System.out.println("Read: " + i);
                // Save testcase
                loadSave.append(hitArray,Analyzer.createDigitEncoding(i));
                if(autoSave.isSelected()){
                    save(false);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null,  ex.getMessage() + " Discarding!");
            }
        }
        myDrawing.clearScreen();
    }

    /**
     * Callback for training the network
     */
    public void trainNetwork(double takeAsTestSet){
        int ret = JOptionPane.showConfirmDialog(null, "This will take a long time.. continue?");
        final int YES = 0,NO = 1, CANCEL = 2;
        if(ret != YES){
            return;
        }
        System.out.println("Estimating size..");
        // Split set into Training and Validation data
        int totalSize = loadSave.getSize();
        int validSize = (int) (totalSize*takeAsTestSet);
        int entrySize = loadSave.getDataAt(0).length;
        System.out.println("Creating data.." + totalSize + "," + validSize +"," + entrySize);
        // --- Test data
        double[][] trainData = new double[totalSize-validSize][entrySize];
        double[][] trainLabel = new double[totalSize-validSize][10];
        // --- Vaildation Data
        double[][] valiData = new double[validSize][entrySize];
        double[][] valiLabel = new double[validSize][10];
        for(int i=0;i<totalSize;i++){
            if(i<totalSize-validSize){
                trainData[i] = loadSave.getDataAt(i);
                trainLabel[i] = loadSave.getLabelAt(i);
            }else{
                valiData[i-(totalSize-validSize)] = loadSave.getDataAt(i);
                valiLabel[i-(totalSize-validSize)] = loadSave.getLabelAt(i);
            }
        }
        MLDataSet trainDataSet = new BasicMLDataSet(trainData, trainLabel);
        MLDataSet validDataSet = new BasicMLDataSet(valiData, valiLabel);
        System.out.println("Adding data to trainer..");
        neuralNetworkTrainer.setTrainingSet(trainDataSet);
        neuralNetworkTrainer.setValidationSet(validDataSet);
        System.out.println("Training network..");
        neuralNetworkTrainer.trainNetwork();
        System.out.println("Done training!");
    }

    /**
     * Callback for starting to recognize digits
     */
    public void recognize(){
        neuralNetworkTrainer.recognize();
    }

    /**
     * callback for saving drawing
     * @param: output true iff output should be generated
     */
    public void save(boolean output){
        if(loadSave.save()){
            if(output){
                JOptionPane.showMessageDialog(null, "Successfully saved data! New size: " + loadSave.getSize());
            }
        }else{
            if(output) {
                JOptionPane.showMessageDialog(null, "Error: Could not save data!");
            }
        }
    }

    /**
     * Callback for iterating through the dataset
     */
    private int index = 0;
    public void get(){
        myDrawing.clearScreen();
        index = index % loadSave.getSize();
        double[] rawArr = loadSave.getDataAt(index);
        double[] rawLabels = loadSave.getLabelAt(index);
        index++;
        myDrawing.setData(rawArr);
        int label = Analyzer.analyzeDigitEncoding(rawLabels);
        if(label < 0){
            myDrawing.clearScreen();
            JOptionPane.showMessageDialog(null, "Invalid label for that entry (Only 0-9)! Deleting entry!");
            loadSave.removeDataAt(index);
        }else {
            myDrawing.setLabel("This is a " + label);
        }
    }

    /**
     * Callback for clearing the screen
     */
    public void clearScreen(){
        myDrawing.clearScreen();
    }

    /**
     * Callback to delete savefile
     */
    public void delete(){
        if(loadSave.reset()){
            myDrawing.clearScreen();
        }
    }

    public void deleteLastEntry(){
        loadSave.removeLastData();
        myDrawing.clearScreen();
        JOptionPane.showMessageDialog(null, "Deleted entry. New size: " + loadSave.getSize() + ".");
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if(src == submit){
            submit();
        }else if(src == recognize) {
            recognize();
        } else if(src == saveItem){
            save(true);
        }else if(src == getData){
            get();
        }else if(src == clearScreen){
            clearScreen();
        }else if(src == deleteItem){
            delete();
        }else if(src == deleteEntry){
            deleteLastEntry();
        }else if(src == trainNetwork){
            trainNetwork(0.1);
        }else if(src == getSizeItem){
            JOptionPane.showMessageDialog(null, "Current #Elements in Savefile: " + loadSave.getSize());
        }else if(src == recognize){
            recognize();
        }
    }

}
