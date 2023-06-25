# Naive Bayes and Sentiment Classification
import math
import re
import sys
import numpy as np
from collections import Counter

class_action = [
    "The sun is shining | what lecture do we have on monday at 8?",
    "I love eating ice cream | what lecture do we have on monday at 6?",
    "Python is a powerful programming language | How are earth you?",
    "She walked down the street | What is he up to?",
    "The cat is sleeping on the mat | What on earth earth?"
]

def train(class_action, input):
    logprior = []
    full_vocab_t = []
    big_doc = []
    counter = 0
    all_sentences = []
    all_actions = []

    for class_id in class_action:
        n_doc = len(class_action)
        n_c = 1
        p_c = n_c / n_doc
        logprior.append(math.log10(p_c))

        parts = class_id.split('|')
        result = parts[1].strip()
        all_actions.append(parts[0])
        # Remove stop words
        stopwords = ["a", "an", "the", "in", "on", "at", "to", "for", "is", "are", "was", "were", "am", "be",
                     "being", "been", "and", "or", "but"]
        # Iterate through the list of sentences and remove stopwords + additional preprocessing.
        words = result.split()
        filtered_words = [word for word in words if word.lower() not in stopwords]
        filtered_sentence = ' '.join(filtered_words)
        filtered_sentence = filtered_sentence.replace("whats ", "what is ")
        filtered_sentence = filtered_sentence.replace("'ve", " have ")
        filtered_sentence = filtered_sentence.replace(" can't ", " cannot ")
        filtered_sentence = filtered_sentence.replace("n't ", " not ")
        filtered_sentence = filtered_sentence.replace("i'm ", "i am ")
        filtered_sentence = filtered_sentence.replace("'ll' ", "will ")
        filtered_sentence = filtered_sentence.replace(" e - mail ", " email ")
        filtered_sentence = re.sub(r'[^\w\s]', '', filtered_sentence)
        # For every word in the question, calculate count of that word.
        all_sentences.append(filtered_sentence)
        full_vocab_t.extend(filtered_sentence.split())
        full_vocab_t = list(set(full_vocab_t))
        counter = counter + 1
    log_likelihood = np.zeros((len(full_vocab_t), len(class_action)))
    for i in range(len(class_action)):
        word_counts = Counter(all_sentences[i].split())
        final_count = 0
        for word, count in word_counts.items():
            final_count = final_count + count
        for word, count in word_counts.items():
            counter_x = 0
            for j in full_vocab_t:
                if j == word:
                    log_likelihood[counter_x][i] = math.log10((count + 1) / (final_count + 1))
                counter_x = counter_x + 1

    print(log_likelihood)
    print(logprior)
    print(full_vocab_t)

    test(full_vocab_t, logprior, log_likelihood, input, all_actions)

    return full_vocab_t, logprior, log_likelihood, all_actions


def test(full_vocab, logprior, log_likelihood, test, all_actions):
    sum = np.zeros(len(logprior))
    for i in range(len(logprior)):
        sum[i] = logprior[i]
        test_k = re.sub(r'[^\w\s]', '', test)
        test_k = test_k.split()
        for j in range(len(test_k)):
            if test_k[j] in full_vocab:
                index = full_vocab.index(test_k[j])
                sum[i] = sum[i] + log_likelihood[index][i]
    index_final = np.argmin(sum)
    print(all_actions[index_final])

    return all_actions[index_final]

# List of classes and documents
param1 = sys.argv[1]
# Input
param2 = sys.argv[2]

train(param1, param2)
