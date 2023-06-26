# This code is fetched from the NLP course at Maastricht University 2022/2023.

from trax.supervised import training
from termcolor import colored
import random
import numpy as np
import pandas as pd
import trax
from trax import layers as tl
from trax.fastmath import numpy as fastnp
from trax.supervised import training
from utils import get_params, get_vocab
import random as rnd


# Must run this to obtain the paths.
##from google.colab import drive
#drive.mount('/content/drive')
#import sys
#sys.path.append('/content/drive/My Drive/Colab Notebooks/deepNER/')

data = pd.read_csv("/content/drive/My Drive/Colab Notebooks/deepNER/ner_dataset.csv", encoding = "ISO-8859-1")
train_sents = open('/content/drive/My Drive/Colab Notebooks/deepNER/data/small/train/sentences.txt', 'r').readline()
train_labels = open('/content/drive/My Drive/Colab Notebooks/deepNER/data/small/train/labels.txt', 'r').readline()

vocab, tag_map = get_vocab('/content/drive/My Drive/Colab Notebooks/deepNER/data/large/words.txt', '/content/drive/My Drive/Colab Notebooks/deepNER/data/large/tags.txt')
t_sentences, t_labels, t_size = get_params(vocab, tag_map, '/content/drive/My Drive/Colab Notebooks/deepNER/data/large/train/sentences.txt', '/content/drive/My Drive/Colab Notebooks/deepNER/data/large/train/labels.txt')
v_sentences, v_labels, v_size = get_params(vocab, tag_map, '/content/drive/My Drive/Colab Notebooks/deepNER/data/large/val/sentences.txt', '/content/drive/My Drive/Colab Notebooks/deepNER/data/large/val/labels.txt')
test_sentences, test_labels, test_size = get_params(vocab, tag_map, '/content/drive/My Drive/Colab Notebooks/deepNER/data/large/test/sentences.txt', '/content/drive/My Drive/Colab Notebooks/deepNER/data/large/test/labels.txt')


def data_generator(batch_size, x, y, pad, shuffle=False, verbose=False):
    '''
      Input:
        batch_size - integer describing the batch size
        x - list containing sentences where words are represented as integers
        y - list containing tags associated with the sentences
        shuffle - Shuffle the data order
        pad - an integer representing a pad character
        verbose - Print information during runtime
      Output:
        a tuple containing 2 elements:
        X - np.ndarray of dim (batch_size, max_len) of padded sentences
        Y - np.ndarray of dim (batch_size, max_len) of tags associated with the sentences in X
    '''

    num_lines = len(x)
    lines_index = [*range(num_lines)]

    if shuffle:
        rnd.shuffle(lines_index)

    index = 0 # tracks current location in x, y
    while True:
        buffer_x = [0] * batch_size # Temporal array to store the raw x data for this batch
        buffer_y = [0] * batch_size # Temporal array to store the raw y data for this batch

        max_len = 0
        for i in range(batch_size):
             # if the index is greater than or equal to the number of lines in x
            if index >= num_lines:
                # then reset the index to 0
                index = 0
                # re-shuffle the indexes if shuffle is set to True
                if shuffle:
                    rnd.shuffle(lines_index)
            buffer_x[i] = x[lines_index[index]]
            buffer_y[i] = y[lines_index[index]]

            lenx = len(x[lines_index[index]])    #length of current x[]
            if lenx > max_len:
                max_len = lenx                   #max_len tracks longest x[]

            # increment index by one
            index += 1


        X = np.full((batch_size, max_len), pad)
        Y = np.full((batch_size, max_len), pad)

        for i in range(batch_size):
            x_i = buffer_x[i]
            y_i = buffer_y[i]
            for j in range(len(x_i)):
                X[i, j] = x_i[j]
                Y[i, j] = y_i[j]

        if verbose: print("index=", index)
        yield((X,Y))


def NER(vocab_size=35181, d_model=50, tags=tag_map):
    '''
      Input:
        vocab_size - integer containing the size of the vocabulary
        d_model - integer describing the embedding size
      Output:
        model - a trax serial model
    '''
    model = tl.Serial(
      tl.Embedding(vocab_size, d_model), # Embedding layer
      tl.LSTM(d_model), # LSTM layer
      tl.Dense(len(tags)), # Dense layer with len(tags) units
      tl.LogSoftmax()  # LogSoftmax layer
      )
    return model



# initializing your model
model = NER()
# display your model
print(model)

rnd.seed(33)

batch_size = 64
# Create training data, mask pad id=35180 for training.
train_generator = trax.data.inputs.add_loss_weights(
#train_generator = trax.supervised.inputs.add_loss_weights(
    data_generator(batch_size, t_sentences, t_labels, vocab['<PAD>'], True),
    id_to_mask=vocab['<PAD>'])

# Create validation data, mask pad id=35180 for training.
eval_generator = trax.data.inputs.add_loss_weights(
#eval_generator = trax.supervised.inputs.add_loss_weights(
    data_generator(batch_size, v_sentences, v_labels, vocab['<PAD>'], True),
    id_to_mask=vocab['<PAD>'])


def train_model(NER, train_generator, eval_generator, train_steps=1, output_dir='model'):
    '''
    Input:
        NER - the model you are building
        train_generator - The data generator for training examples
        eval_generator - The data generator for validation examples,
        train_steps - number of training steps
        output_dir - folder to save your model
    Output:
        training_loop - a trax supervised training Loop
    '''
    train_task = training.TrainTask(
      train_generator, # A train data generator
      loss_layer = tl.CrossEntropyLoss(), # A cross-entropy loss function
      optimizer = trax.optimizers.Adam(0.01),  # The adam optimizer
    )

    eval_task = training.EvalTask(
      labeled_data = eval_generator, # A labeled data generator
      metrics = [tl.CrossEntropyLoss(), tl.Accuracy()], # Evaluate with cross-entropy loss and accuracy
      n_eval_batches = 10  # Number of batches to use on each evaluation
    )

    training_loop = training.Loop(
        NER, # A model to train
        train_task, # A train task
        eval_tasks = [eval_task], # The evaluation task
        output_dir = output_dir) # The output directory

    # Train with train_steps
    training_loop.run(n_steps = train_steps)
    return training_loop

train_steps = 100            # In coursera we can only train 100 steps
# Train the model
training_loop = train_model(NER(), train_generator, eval_generator, train_steps)

model = NER()
model.init(trax.shapes.ShapeDtype((1, 1), dtype=np.int32))

model.init_from_file("", weights_only=True)
x, y = next(data_generator(len(test_sentences), test_sentences, test_labels, vocab['<PAD>']))
print("input shapes", x.shape, y.shape)

tmp_pred = model(x)
print(type(tmp_pred))
print(f"tmp_pred has shape: {tmp_pred.shape}")

def evaluate_prediction(pred, labels, pad):
    """
    Inputs:
        pred: prediction array with shape
            (num examples, max sentence length in batch, num of classes)
        labels: array of size (batch_size, seq_len)
        pad: integer representing pad character
    Outputs:
        accuracy: float
    """
    outputs = np.argmax(pred, axis=2)
    print("outputs shape:", outputs.shape)
    mask = labels != pad
    print("mask shape:", mask.shape, "mask[0][20:30]:", mask[0][20:30])
    accuracy = np.sum(outputs == labels) / float(np.sum(mask))
    return accuracy

accuracy = evaluate_prediction(model(x), y, vocab['<PAD>'])
print("accuracy: ", accuracy)

def predict(sentence, model, vocab, tag_map):
    s = [vocab[token] if token in vocab else vocab['UNK'] for token in sentence.split(' ')]
    batch_data = np.ones((1, len(s)))
    batch_data[0][:] = s
    sentence = np.array(batch_data).astype(int)
    output = model(sentence)
    outputs = np.argmax(output, axis=2)
    labels = list(tag_map.keys())
    pred = []
    for i in range(len(outputs[0])):
        idx = outputs[0][i]
        pred_label = labels[idx]
        pred.append(pred_label)
    return pred