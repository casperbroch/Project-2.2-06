import sys
from transformers import AutoTokenizer, AutoModel
import torch


def getBERT_predict(skill, input):

    # Load both the model and the BERT tokenizer.
    model = AutoModel.from_pretrained('bert-base-uncased')
    tokenizer = AutoTokenizer.from_pretrained('bert-base-uncased')
    skillList = skill.split(",")
    finalcosine = 0

    for i in range(len(skillList)):
        skillList[i] = skillList[i].strip()
        # Get the embeddings for the sentences.
        inputs = tokenizer(skillList[i], return_tensors='pt', truncation=True, padding=True)
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
        
        if(cosine_similarity.item() > finalcosine):
            finalcosine = cosine_similarity.item()
    print(finalcosine)
    return finalcosine

param1 = sys.argv[1]
param2 = sys.argv[2]

getBERT_predict(param2, param1)
