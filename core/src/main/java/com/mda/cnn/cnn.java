package com.mda.cnn;

import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.SplitTestAndTrain;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.datavec.api.io.filters.BalancedPathFilter;
import org.datavec.api.io.labels.ParentPathLabelGenerator;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.split.InputSplit;
import org.datavec.image.loader.NativeImageLoader;
import org.datavec.image.recordreader.ImageRecordReader;
import org.datavec.image.transform.FlipImageTransform;
import org.datavec.image.transform.ImageTransform;
import org.datavec.image.transform.WarpImageTransform;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.common.io.ClassPathResource;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.evaluation.regression.RegressionEvaluation;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mda.App;

public class cnn {

    private static int height = 160;
    private static int width = 160;
    private static int channels = 3;
    
    private static long seed = 89;

    private static Logger log = LoggerFactory.getLogger(App.class);
    
    public static void main(String[] args) throws IOException, InterruptedException {
        // ! GENERATING DATA
        int numLabels = 2;
        ParentPathLabelGenerator labelGen = new ParentPathLabelGenerator();

        int batchSize = 20;
        int totalSize = 40;
        Random random = new Random();

        File file = new File("C:/Users/caspe/Desktop/imagesSMALL/");
        FileSplit split = new FileSplit(file, NativeImageLoader.ALLOWED_FORMATS, random);
        BalancedPathFilter pathFilter = new BalancedPathFilter(random, labelGen, totalSize, numLabels, batchSize);

        double splitNum = 0.8;
        InputSplit[] inputSplit = split.sample(pathFilter, splitNum, 1-splitNum);
        InputSplit trainData = inputSplit[0];
        InputSplit testData = inputSplit[1];

        ImageTransform flipTrans1 = new FlipImageTransform(random);
        ImageTransform flipTrans2 = new FlipImageTransform(new Random());
        //ImageTransform warpTrans = new WarpImageTransform(random, 42);
        List<ImageTransform> transforms = Arrays.asList(new ImageTransform[]{flipTrans1, flipTrans2});

        ImageRecordReader recordReaderTrain = new ImageRecordReader(height, width, channels, labelGen);
        ImageRecordReader recordReaderTest = new ImageRecordReader(height, width, channels, labelGen);

        DataNormalization scaler = new ImagePreProcessingScaler(0, 1);

        ArrayList<DataSetIterator> iterators = new ArrayList<DataSetIterator>(2); 
        iterators.add(null);
        iterators.add(null);

        for(ImageTransform transform : transforms) {
            recordReaderTrain.initialize(trainData, transform);
            iterators.set(0, new RecordReaderDataSetIterator(recordReaderTrain, batchSize, 1, numLabels));

            scaler.fit(iterators.get(0));
            iterators.get(0).setPreProcessor(scaler);
        }

        for(ImageTransform transform : transforms) {
            recordReaderTest.initialize(testData, transform);
            iterators.set(1, new RecordReaderDataSetIterator(recordReaderTest, batchSize, 1, numLabels));

            scaler.fit(iterators.get(1));
            iterators.get(1).setPreProcessor(scaler);
        }

        // ! CREATE MULTILAYER NETWORK
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
            .seed(seed)
            .l2(0.0005)
            .activation(Activation.RELU)
            .weightInit(WeightInit.XAVIER)
            .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
            .updater(new Nesterovs(0.9))
            .list()
            .layer(0, convInit("cnn1", channels, 50 ,  new int[]{5, 5}, new int[]{1, 1}, new int[]{0, 0}, 0))
            .layer(1, maxPool("maxpool1", new int[]{2,2}))
            .layer(2, conv5x5("cnn2", 100, new int[]{5, 5}, new int[]{1, 1}, 0))
            .layer(3, maxPool("maxool2", new int[]{2,2}))
            .layer(4, new DenseLayer.Builder().nOut(500).build())
            .layer(5, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                .nOut(numLabels)
                .activation(Activation.SOFTMAX)
                .build())
            .setInputType(InputType.convolutional(height, width, channels))
            .build();

        MultiLayerNetwork network = new MultiLayerNetwork(conf);

        // ! VARIABLES FOR SAVING
        File loc = new File("core/src/main/java/com/mda/cnn/cnn_trained.zip");
        File locmid = new File("core/src/main/java/com/mda/cnn/cnn_trained_mid.zip");

        boolean saveUpdater = true;

        // ! TRAIN CNN
        int epoch = 500;
        for(int i=1; i<=epoch; i++) {
            network.fit(iterators.get(0));
            if(i%1==0) {
                System.out.println("Iteration no. ..."+i);
            }
            if(i%5==0) {
                Evaluation eval = network.evaluate(iterators.get(1));
                System.out.println("HALF-TERM SYSTEM EVAL");
                System.out.println(eval.stats(true));
                ModelSerializer.writeModel(network, locmid, saveUpdater);

            }
        }

        // ! EVALUATE CNN
        Evaluation eval = network.evaluate(iterators.get(1));
        System.out.println(eval.stats(true));
        log.info(eval.stats(true));
        ModelSerializer.writeModel(network, loc, saveUpdater);

    }

    private static ConvolutionLayer convInit(String name, int in, int out, int[] kernel, int[] stride, int[] pad, double bias) {
        return new ConvolutionLayer.Builder(kernel, stride, pad).name(name).nIn(in).nOut(out).biasInit(bias).build();
    }
    
    private static ConvolutionLayer conv5x5(String name, int out, int[] stride, int[] pad, double bias) {
        return new ConvolutionLayer.Builder(new int[]{5,5}, stride, pad).name(name).nOut(out).biasInit(bias).build();
    }
    
    private static SubsamplingLayer maxPool(String name,  int[] kernel) {
        return new SubsamplingLayer.Builder(kernel, new int[]{2,2}).name(name).build();
    }
}
