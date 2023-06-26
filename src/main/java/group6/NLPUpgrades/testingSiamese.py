import pickle
from keras.preprocessing.sequence import pad_sequences
from keras.models import load_model


def getLSTM_predict(skill, input):
    # Load the tokenizer
    with open('TrainedData/tokenizer.pickle', 'rb') as handle:
        tokenizer = pickle.load(handle)

    # Load the model
    model = load_model('TrainedData/siamese_lstm.h5')

    max_len = 237

    # Tokenize and pad the questions.
    skill_sequence = tokenizer.texts_to_sequences([skill])
    input_sequence = tokenizer.texts_to_sequences([input])

    skill_pad = pad_sequences(skill_sequence, maxlen=max_len)
    input_pad = pad_sequences(input_sequence, maxlen=max_len)

    # Generate prediction.
    prediction = model.predict([skill_pad, input_pad])
    is_same = prediction[0][0]

    print(is_same)
    return is_same