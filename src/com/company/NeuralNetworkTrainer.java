package com.company;

import org.encog.engine.network.activation.*;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.Propagation;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.neural.networks.training.propagation.manhattan.ManhattanPropagation;
import org.encog.neural.networks.training.propagation.quick.QuickPropagation;
import org.encog.persist.EncogDirectoryPersistence;

import javax.swing.*;
import java.io.File;

/**
 * Created by Skynet on 12.03.2016.
 */
public class NeuralNetworkTrainer {

    /**
     * The best network
     */
    BasicNetwork bestNetwork;

    /**
     * All available training data
     */
    MLDataSet trainingSet;
    /**
     * Validation set for training data
     */
    MLDataSet validationSet;

    public NeuralNetworkTrainer(){
    }

    /**
     * Gets best trained network
     * @return
     */
    public BasicNetwork getBestNetwork(){
        return bestNetwork;
    }

    /**
     * Adds training data to the neural network
     * @param trainingSet Training data
     */
    public void setTrainingSet(MLDataSet trainingSet){
        this.trainingSet = trainingSet;
    }

    /**
     * Adds a validation set to the neural network trainer
     * @param validationSet
     */
    public void setValidationSet(MLDataSet validationSet){
        this.validationSet = validationSet;
    }

    /**
     * Call to train Network based on given data
     */
    public void trainNetwork(){
        int inputSize = trainingSet.get(0).getInputArray().length;
        int outputSize = trainingSet.get(0).getIdealArray().length;

        int hiddenLayerNeuronsCount = 400; // 100-200
        int hiddenLayerCount = 2;
        int epochsCount = 200;

        // Get
        BasicNetwork network = new BasicNetwork();

        // input layer
        network.addLayer(new BasicLayer(null, true, inputSize));

        // (Many different activation functions)
        final ActivationFunction activationFunction = new ActivationSigmoid();

        // add hidden layer
        for(int i=0;i<hiddenLayerCount;i++) {
            network.addLayer(new BasicLayer(activationFunction, true, hiddenLayerNeuronsCount));
        }
        // output layer
        network.addLayer(new BasicLayer(activationFunction, false, outputSize));
        network.getStructure().finalizeStructure();
        network.reset();


        //(many different propagation functions)
        final Propagation train = new QuickPropagation(network, trainingSet);

        for(int epoch = 1; epoch <= epochsCount; epoch++)
        {
            train.iteration();
            //train2.iteration();
            //train3.iteration();
            System.out.println("Backpropagation: " + train.getError());
            //System.out.println("ManhattanPropagation: " + train2.getError());
            //System.out.println("QuickPropagation: " + train3.getError());
            System.out.println("------- Cycle " + epoch);
        }
        train.finishTraining();

        this.bestNetwork = network;
    }

    public void recognize(double[] data, double[] label){
        if(bestNetwork == null){
            return;
        }
        double[][]dataSet = new double[1][data.length];
        double[][]labelSet = new double[1][label.length];
        dataSet[0] = data;
        labelSet[0] = label;
        MLDataSet mldata = new BasicMLDataSet(dataSet,labelSet);
        for(MLDataPair pair : mldata){
            final MLData output = bestNetwork.compute(pair.getInput());
            JOptionPane.showMessageDialog(null, "Predicted:" + Analyzer.analyzeDigitEncodingWithUncertainty(output.getData()) + ", Ideal: " + Analyzer.analyzeDigitEncodingWithUncertainty(pair.getIdeal().getData()));
        }

    }
}
