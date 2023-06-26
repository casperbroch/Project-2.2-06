import pandas as pd
import pickle
from keras.preprocessing.text import Tokenizer
from keras.preprocessing.sequence import pad_sequences
from keras.models import Model
from keras.layers import Input, Embedding, LSTM, Dense, concatenate
from keras.optimizers import Adam
from keras.callbacks import EarlyStopping
from sklearn.model_selection import train_test_split

training_data = pd.read_csv('TrainedData/quora_duplicate_questions.tsv', delimiter='\t')

# Removes rows with missing values.
training_data.dropna(axis=0, inplace=True)

X = training_data[['question1', 'question2']]
y = training_data['is_duplicate']

X_train, X_valid, y_train, y_valid = train_test_split(X, y, test_size=0.2, random_state=42)

# Tokenize the questions
tokenizer = Tokenizer()
tokenizer.fit_on_texts(pd.concat([X_train['question1'], X_train['question2']]))
X_train_q1 = tokenizer.texts_to_sequences(X_train['question1'])
X_train_q2 = tokenizer.texts_to_sequences(X_train['question2'])
X_valid_q1 = tokenizer.texts_to_sequences(X_valid['question1'])
X_valid_q2 = tokenizer.texts_to_sequences(X_valid['question2'])

# Find max_len.
max_len = max(max(len(x) for x in X_train_q1), max(len(x) for x in X_train_q2))

X_train_q1 = pad_sequences(X_train_q1, maxlen=max_len)
X_train_q2 = pad_sequences(X_train_q2, maxlen=max_len)
X_valid_q1 = pad_sequences(X_valid_q1, maxlen=max_len)
X_valid_q2 = pad_sequences(X_valid_q2, maxlen=max_len)

# --------------- Architecture ---------------

# Siamese LSTM
input_dim = len(tokenizer.word_index) + 1
output_dim = 64
lstm_layer = LSTM(output_dim, dropout=0.2)

# Question 1 branch
skill = Input(shape=(max_len,))
skill_embed = Embedding(input_dim=input_dim, output_dim=output_dim, input_length=max_len)(q1_input)
skill_lstm = lstm_layer(skill_embed)
input = Input(shape=(max_len,))
input_embed = Embedding(input_dim=input_dim, output_dim=output_dim, input_length=max_len)(q2_input)
input_lstm = lstm_layer(input_embed)

# Combine the outputs of the two branches
combined = concatenate([skill_lstm, input_lstm])
dense = Dense(16, activation='relu')(combined)
output = Dense(1, activation='sigmoid')(dense)
model = Model(inputs=[skill, input], outputs=output)

# --------------- Training ---------------
model.compile(loss='binary_crossentropy', optimizer=Adam(), metrics=['accuracy'])
model.fit([X_train_q1, X_train_q2], y_train, epochs=10, validation_data=([X_valid_q1, X_valid_q2], y_valid),
          callbacks=[EarlyStopping(patience=5)], batch_size=32)

# Save both the model and tokenizer.
# model.save('siamese_lstm.h5')
# with open('TrainedData/tokenizer.pickle', 'wb') as handle:
# pickle.dump(tokenizer, handle, protocol=pickle.HIGHEST_PROTOCOL)
