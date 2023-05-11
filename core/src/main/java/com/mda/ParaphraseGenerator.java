package com.mda;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import com.theokanning.openai.completion.CompletionChoice;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.image.CreateImageRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// Class to generate paraphrases using ChatGPT 3.5

// We use TheoKanning's Java API
// https://github.com/TheoKanning/openai-java

// Services are paid for (you can use this class as much as you want - it is cheap):
// 	~ GPT 3.5-TURBO = $0.002 / 1K tokens
//      ~ Other models may cost more so please stick to this model

public class ParaphraseGenerator {
        static String result ="";

        // Class generates an amount of paraphrases
        //
        // Input: Sentence you want to paraphrease, and the amount of paraphrases you want
        // Output: An array of paraphrases

        public static String[] generateParaphrases(String sentence, int amount){
                
                // Token retrieved from account
                String token = "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";
                
                // New service
                OpenAiService service = new OpenAiService(token);

                final List<ChatMessage> messages = new ArrayList<>();

                // Request
                final ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), "Give me " +amount +" simple paraphrases of this sentence: "+sentence);
                messages.add(systemMessage);

                // Start service
                ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
                        .builder()
                        .model("gpt-3.5-turbo") //Chosen model
                        .messages(messages)
                        .n(1) // Number of answers
                        .maxTokens(250) // Max tokens, increase if result is getting cut off
                        .logitBias(new HashMap<>())
                        .build();

                // Store result
                service.createChatCompletion(chatCompletionRequest).getChoices().forEach(choice -> {
                        result =choice.getMessage().getContent();
                });
                 
                // Close service
                service.shutdownExecutor();

                //System.out.println(result);

                // Split result into array
                String[] lines = result.split("\n");

                // Removes numbering from paraphrases
                for (int i = 0; i < lines.length; i++) {
                        if(i>8){
                                lines[i] = lines[i].substring(4);
                        }else{
                                lines[i] = lines[i].substring(3);
                        }
                        

                }

                return lines;
        }

        // TESTING
        public static void main(String... args) {
                String[] str =generateParaphrases("I love animals", 15);

                for (int i = 0; i <str.length; i++) {
                        System.out.println(str[i]); 
                }
        }
}




