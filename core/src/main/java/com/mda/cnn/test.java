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


public class test {

    private static int height = 160;
    private static int width = 160;
    private static int channels = 3;
    
    private static long seed = 69;

    private static Logger log = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws IOException {
        File loc = new File("core/src/main/java/com/mda/cnn/cnn_trained.zip");
        MultiLayerNetwork network = ModelSerializer.restoreMultiLayerNetwork(loc);

        // Set the path to the directory containing the images you want to predict
        String imagesPath = "C:/Users/caspe/Desktop/imagesTEST/";

        // Iterate over the images in the directory
        File[] imageFiles = new File(imagesPath).listFiles();
        if (imageFiles != null) {
            for (File imageFile : imageFiles) {
                try {
                    // Load the image
                    NativeImageLoader loader = new NativeImageLoader(height, width, channels);
                    INDArray image = loader.asMatrix(imageFile);

                    // Preprocess the image
                    DataNormalization scaler = new ImagePreProcessingScaler(0, 1);
                    scaler.transform(image);

                    // Make predictions
                    INDArray output = network.output(image);

                    // Print the predictions
                    System.out.println("Predictions for image: " + imageFile.getName());
                    System.out.println(output);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }
}
