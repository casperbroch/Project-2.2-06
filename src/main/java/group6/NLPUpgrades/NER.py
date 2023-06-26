import spacy

# Load SpaCy's English NLP model
nlp = spacy.load('en_core_web_sm')

# The text that we want to examine
text = "Apple Inc. is planning to buy U.K. startup for $1 billion"

# Process the text
doc = nlp(text)

# Print the entities detected
for ent in doc.ents:
    print(f'Entity: {ent.text}, Label: {ent.label_}, Start Char: {ent.start_char}, End Char: {ent.end_char}')