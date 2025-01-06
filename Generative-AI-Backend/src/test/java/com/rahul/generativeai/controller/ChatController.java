package com.rahul.generativeai.controller;

import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;

import com.rahul.generativeai.exception.ResourceNotFoundException;
import com.rahul.generativeai.service.ChatService;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.azure.openai.*;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Map;
import java.util.logging.Logger;

import java.io.File;
import java.io.IOException;

@CrossOrigin(origins = {
        "http://springaiassist.s3-website.eu-north-1.amazonaws.com",
        "https://delightful-tree-058892c0f.5.azurestaticapps.net",
        "https://main.d1roziyo3zgxn7.amplifyapp.com",
        "https://ashy-sky-0ba15c40f.5.azurestaticapps.net"
})
@RestController
@RequestMapping("/api/v1")
public class ChatController {

    private final AzureOpenAiChatModel chatModel;
    private final AzureOpenAiImageModel imageModel;
    private final AzureOpenAiAudioTranscriptionModel azureOpenAiAudioTranscriptionModel;
    private final AzureOpenAiImageOptions imageOptions;;

    private static final Logger logger = Logger.getLogger(ChatController.class.getName());
    private final ChatService chatService;

    public ChatController(
            AzureOpenAiChatModel chatModel,
            AzureOpenAiImageModel imageModel, ChatService chatService) {

        this.chatModel = chatModel;
        this.imageModel = imageModel;

        String whisperApiKey = System.getenv("AZURE_OPENAI_WHISPER_KEY");
        String whisperEndpoint = System.getenv("AZURE_OPENAI_WHISPER_ENDPOINT");

        // Log and throw exception if environment variables are not set
        if (whisperApiKey == null || whisperApiKey.isEmpty()) {
            logger.severe("AZURE_OPENAI_WHISPER_KEY environment variable is missing.");
            throw new RuntimeException("AZURE_OPENAI_WHISPER_KEY is required but not found.");
        }
        if (whisperEndpoint == null || whisperEndpoint.isEmpty()) {
            logger.severe("AZURE_OPENAI_WHISPER_ENDPOINT environment variable is missing.");
            throw new RuntimeException("AZURE_OPENAI_WHISPER_ENDPOINT is required but not found.");
        }

        try {
            var openAIClient = new OpenAIClientBuilder()
                    .credential(new AzureKeyCredential(whisperApiKey))
                    .endpoint(whisperEndpoint)
                    .buildClient();

            AzureOpenAiAudioTranscriptionOptions azureOpenAiAudioTranscriptionOptions = new AzureOpenAiAudioTranscriptionOptions();
            azureOpenAiAudioTranscriptionOptions.setLanguage("en");

            this.azureOpenAiAudioTranscriptionModel = new AzureOpenAiAudioTranscriptionModel(openAIClient, azureOpenAiAudioTranscriptionOptions);
            this.imageOptions = AzureOpenAiImageOptions.builder().withN(1).withHeight(1024).withWidth(1024).build();
        } catch (Exception e) {
            logger.severe("Error initializing OpenAI client: " + e.getMessage());
            throw new RuntimeException("Failed to initialize ChatController due to OpenAI client error", e);
        }
        this.chatService = chatService;
    }
    @CrossOrigin(origins = {
            "http://springaiassist.s3-website.eu-north-1.amazonaws.com",
            "https://delightful-tree-058892c0f.5.azurestaticapps.net",
            "https://main.d1roziyo3zgxn7.amplifyapp.com",
            "https://ashy-sky-0ba15c40f.5.azurestaticapps.net"
    })
     @GetMapping("/chat")
    public ResponseEntity<Map<String, String>> generate(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        if (message == null || message.trim().isEmpty()) {
            throw new ResourceNotFoundException("Message is required for generating response");
        }

        // Ensure API key is available
        String azureApiKey = System.getenv("AZURE_OPENAI_KEY");
        if (azureApiKey == null || azureApiKey.isEmpty()) {
            logger.severe("AZURE_OPENAI_KEY environment variable is missing.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "API key for Azure OpenAI is missing."));
        }

        try {
            logger.info("Generating text for message: " + message);
            Map<String, String> generation = Map.of("generation", this.chatModel.call(message));
            return ResponseEntity.ok(generation);
        } catch (Exception e) {
            logger.severe("Error generating text response: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Text generation failed"));
        }
    }

    @CrossOrigin(origins = {
            "http://springaiassist.s3-website.eu-north-1.amazonaws.com",
            "https://delightful-tree-058892c0f.5.azurestaticapps.net",
            "https://main.d1roziyo3zgxn7.amplifyapp.com",
            "https://ashy-sky-0ba15c40f.5.azurestaticapps.net"
    })
   @GetMapping("/generate-image")
    public ResponseEntity<ImageResponse> generateImage(@RequestParam String message) {
        if (message == null || message.trim().isEmpty()) {
            throw new ResourceNotFoundException("Message is required for generating image");
        }

        try {
            logger.info("Generating image for message: " + message);
            ImageResponse response = imageModel.call(new ImagePrompt(message, imageOptions));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.severe("Error generating image: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @CrossOrigin(origins = {
            "http://springaiassist.s3-website.eu-north-1.amazonaws.com",
            "https://delightful-tree-058892c0f.5.azurestaticapps.net",
            "https://main.d1roziyo3zgxn7.amplifyapp.com",
            "https://ashy-sky-0ba15c40f.5.azurestaticapps.net"
    })
  @GetMapping("/transcribe")
    public ResponseEntity<String> getText(@RequestParam MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Audio file is required for transcription.");
        }
        try {
            // Convert MultipartFile to ByteArrayResource
            ByteArrayResource resource = new ByteArrayResource(file.getBytes());

            // Call the transcription model with the resource
            String response = azureOpenAiAudioTranscriptionModel.call(resource);

            // Return the transcription response
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            logger.severe("Error reading the file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error reading the audio file.");
        } catch (Exception e) {
            logger.severe("Error during transcription: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Transcription failed.");
        }
    }
    @CrossOrigin(origins = {
            "http://springaiassist.s3-website.eu-north-1.amazonaws.com",
            "https://delightful-tree-058892c0f.5.azurestaticapps.net",
            "https://main.d1roziyo3zgxn7.amplifyapp.com",
            "https://ashy-sky-0ba15c40f.5.azurestaticapps.net"
    })
    @PostMapping("/send-MultiLangAudio")
    public ResponseEntity<String> audioTranscribe(@RequestParam MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Audio file is required for transcription.");
        }
        try {
            File tempfile = File.createTempFile("audio", ".wav");
            file.transferTo(tempfile);

            AzureOpenAiAudioTranscriptionOptions transcriptionOptions = AzureOpenAiAudioTranscriptionOptions.builder()
                    .withResponseFormat(AzureOpenAiAudioTranscriptionOptions.TranscriptResponseFormat.TEXT)
                    .withTemperature(0f)
                    .build();

            var audioFile = new FileSystemResource(tempfile);
            AudioTranscriptionPrompt transcriptionRequest = new AudioTranscriptionPrompt(audioFile, transcriptionOptions);
            AudioTranscriptionResponse response = azureOpenAiAudioTranscriptionModel.call(transcriptionRequest);

            tempfile.delete();
            return ResponseEntity.ok(response.getResult().getOutput());
        } catch (IOException e) {
            logger.severe("Error during file upload or transcription: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Audio transcription failed.");
        }
    }


    @CrossOrigin(origins = {
            "http://springaiassist.s3-website.eu-north-1.amazonaws.com",
            "https://delightful-tree-058892c0f.5.azurestaticapps.net",
            "https://main.d1roziyo3zgxn7.amplifyapp.com",
            "https://ashy-sky-0ba15c40f.5.azurestaticapps.net"
    })
    @GetMapping("/get-recipe")
    public ResponseEntity<String> getRecipe(@RequestParam(defaultValue = "any") String cuisine,
                                            @RequestParam String ingredients,
                                            @RequestParam String dietaryRestrictions) {
        Logger logger = Logger.getLogger(getClass().getName());

        if (ingredients == null || ingredients.trim().isEmpty()) {
            throw new ResourceNotFoundException("Ingredients are required for recipe generation");
        }

        try {
            // Constructing the prompt for generating a recipe
            String template = """
            I want to create a recipe using the following ingredients: {ingredients},
            The cuisine type I prefer is {cuisine}.
            Please consider the following dietary restrictions: {dietaryRestrictions}.
            Please provide me with a detailed recipe including title, list of ingredients, and cooking instructions.
            Make sure you give me these details in json format.
            """;

            PromptTemplate promptTemplate = new PromptTemplate(template);
            Map<String, Object> params = Map.of("ingredients", ingredients, "dietaryRestrictions", dietaryRestrictions, "cuisine", cuisine);
            Prompt prompt = promptTemplate.create(params);
            String recipe = chatModel.call(prompt).getResult().getOutput().getContent();

            // Parse the generated recipe (assuming it might not always be in the same format)
            JSONObject recipeJson = new JSONObject(recipe);

            // Standardize the recipe structure
            JSONObject standardizedRecipe = new JSONObject();
            standardizedRecipe.put("title", recipeJson.optString("title", "Unknown Title"));

            // Standardizing the ingredients part
            JSONArray standardizedIngredients = new JSONArray();
            JSONArray ingredientsArray = recipeJson.optJSONArray("ingredients");
            if (ingredientsArray != null) {
                for (int i = 0; i < ingredientsArray.length(); i++) {
                    JSONObject ingredient = ingredientsArray.getJSONObject(i);
                    String ingredientName = ingredient.optString("ingredient_name", ingredient.optString("item", ""));
                    String quantity = ingredient.optString("quantity", "");
                    String measurement = ingredient.optString("measurement", ingredient.optString("type", ""));
                    JSONObject standardizedIngredient = new JSONObject();
                    standardizedIngredient.put("ingredient_name", ingredientName);
                    standardizedIngredient.put("quantity", quantity);
                    standardizedIngredient.put("measurement", measurement);
                    standardizedIngredients.put(standardizedIngredient);
                }
            }

            standardizedRecipe.put("ingredients", standardizedIngredients);

            // Standardizing the instructions part
            JSONArray standardizedInstructions = new JSONArray();
            JSONArray instructionsArray = recipeJson.optJSONArray("instructions");
            if (instructionsArray == null) {
                instructionsArray = recipeJson.optJSONArray("cookingInstructions");
            }
            if (instructionsArray != null) {
                for (int i = 0; i < instructionsArray.length(); i++) {
                    JSONObject instruction = instructionsArray.getJSONObject(i);
                    String instructionText = instruction.optString("instruction", instruction.optString("detail", ""));
                    JSONObject standardizedInstruction = new JSONObject();
                    standardizedInstruction.put("step", i + 1);
                    standardizedInstruction.put("instruction", instructionText);
                    standardizedInstructions.put(standardizedInstruction);
                }
            }

            standardizedRecipe.put("instructions", standardizedInstructions);

            // Return the standardized JSON as the response
            return ResponseEntity.ok(standardizedRecipe.toString());

        } catch (Exception e) {
            logger.severe("Error generating recipe: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Recipe generation failed.");
        }
    }
    @CrossOrigin(origins = {
            "http://springaiassist.s3-website.eu-north-1.amazonaws.com",
            "https://delightful-tree-058892c0f.5.azurestaticapps.net",
            "https://main.d1roziyo3zgxn7.amplifyapp.com",
            "https://ashy-sky-0ba15c40f.5.azurestaticapps.net",
            "https://main.d1b7c33z3by11m.amplifyapp.com/"
    })
    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestParam String to,
                                            @RequestParam String subject,
                                            @RequestParam String message) {
        chatService.sendEmail(to, subject, message);
        return ResponseEntity.status(HttpStatus.OK).body("Email sent successfully to " + to);
    }
}
