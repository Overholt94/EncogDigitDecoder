package com.company;

import org.encog.engine.network.activation.ActivationBiPolar;
import org.encog.engine.network.activation.ActivationFunction;
import org.encog.ml.data.MLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.Propagation;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.persist.EncogDirectoryPersistence;

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
        int epochsCount = 2000;

        // Get
        BasicNetwork network = new BasicNetwork();

        // input layer
        network.addLayer(new BasicLayer(null, true, inputSize));

        // (Many different activation functions)
        final ActivationFunction activationFunction = new ActivationBiPolar();

        // add hidden layer
        for(int i=0;i<hiddenLayerCount;i++) {
            network.addLayer(new BasicLayer(activationFunction, true, hiddenLayerNeuronsCount));
        }
        // output layer
        network.addLayer(new BasicLayer(activationFunction, false, outputSize));
        network.getStructure().finalizeStructure();
        network.reset();


        //(many different propagation functions)
        final Propagation train = new Backpropagation(network, trainingSet);

        for(int epoch = 1; epoch <= epochsCount; epoch++)
        {
            train.iteration();
            System.out.println("Epoch #" + epoch + " Error:" + train.getError());
        }
        train.finishTraining();

        this.bestNetwork = network;
    }
}
