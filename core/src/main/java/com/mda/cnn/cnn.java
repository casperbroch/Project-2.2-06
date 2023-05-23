package com.mda.cnn;

import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.SplitTestAndTrain;

import java.io.File;
import java.io.IOException;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.common.io.ClassPathResource;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.evaluation.regression.RegressionEvaluation;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mda.App;

public class cnn {

    private static Logger log = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws IOException, InterruptedException {
        RecordReader rcReader = new CSVRecordReader(0,';');
        rcReader.initialize(new FileSplit(new File("core/src/main/java/com/mda/cnn/facedata.csv")));

        int labelIndex = 25600;
        int batchSize = 200;

        DataSetIterator iterator = new RecordReaderDataSetIterator(rcReader, batchSize, labelIndex, 2);
        DataSet allData = iterator.next();
        allData.shuffle();

        SplitTestAndTrain split = allData.splitTestAndTrain(0.66);
        DataSet trainData = split.getTrain();
        DataSet testData = split.getTest();

        DataNormalization normalizer = new NormalizerStandardize();
        normalizer.fit(trainData);
        normalizer.transform(trainData);
        normalizer.transform(testData);

        int outputNum = 2;
        int iterations = 100;

        MultiLayerConfiguration cfg = new NeuralNetConfiguration.Builder()
        .weightInit(WeightInit.UNIFORM)
        .list()
        .layer(0,new DenseLayer.Builder()
            .activation(Activation.SIGMOID)
            .nIn(25600)
            .nOut(20000)
            .build())
        .layer(1,new 
        OutputLayer.Builder(LossFunctions.LossFunction.MSE)
            .activation(Activation.SIGMOID)
            .nIn(20000)
            .nOut(outputNum)
            .build())
        .build();

        MultiLayerNetwork network = new MultiLayerNetwork(cfg);
        network.init();
        network.setLearningRate(0.1);
        
        for( int i=0; i < iterations; i++ ) {
            System.out.println(i);
            network.fit(trainData);
        }

        Evaluation eval = new Evaluation(1);
        INDArray output = network.output(testData.getFeatures());
        eval.eval(testData.getLabels(), output);

        System.out.println(eval.stats());
        log.info(eval.stats());

    }
}
