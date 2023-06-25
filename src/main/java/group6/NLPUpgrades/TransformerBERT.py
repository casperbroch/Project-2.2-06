import sys
from transformers import AutoTokenizer, AutoModel
import torch


def getBERT_predict(skill, input):
    # Load both the model and the BERT tokenizer.
    model = AutoModel.from_pretrained('bert-base-uncased')
    tokenizer = AutoTokenizer.from_pretrained('bert-base-uncased')

    # Get the embeddings for the sentences.
    inputs = tokenizer(skill, return_tensors='pt', truncation=True, padding=True)
    with torch.no_grad():
        outputs = model(**inputs)
    skill_embedded = outputs[0][:, 0, :].detach().numpy()

    inputs = tokenizer(input, return_tensors='pt', truncation=True, padding=True)
    with torch.no_grad():
        outputs = model(**inputs)
    input_embedded = outputs[0][:, 0, :].detach().numpy()

    # Calculate the cosine similarity between both embeddings.
    cosine_similarity = torch.nn.functional.cosine_similarity(torch.tensor(skill_embedded),
                                                              torch.tensor(input_embedded))

    print(cosine_similarity.item())
    return cosine_similarity.item()

param1 = sys.argv[1]
param2 = sys.argv[2]

getBERT_predict(param1, param2)
