package com.rahul.generativeai.controller;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.rahul.generativeai.exception.ResourceNotFoundException;
import com.rahul.generativeai.service.ChatService;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.azure.openai.AzureOpenAiAudioTranscriptionModel;
import org.springframework.ai.azure.openai.AzureOpenAiAudioTranscriptionOptions;
import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.azure.openai.AzureOpenAiImageModel;
import org.springframework.ai.azure.openai.AzureOpenAiImageOptions;
import org.springframework.ai.azure.openai.AzureOpenAiAudioTranscriptionOptions.TranscriptResponseFormat;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(
        origins = {"http://springaiassist.s3-website.eu-north-1.amazonaws.com", "https://delightful-tree-058892c0f.5.azurestaticapps.net", "https://main.d1roziyo3zgxn7.amplifyapp.com", "https://ashy-sky-0ba15c40f.5.azurestaticapps.net","http://localhost:5173", "http://localhost:3000","https://main.d23bkhxjqbyoh7.amplifyapp.com/"}
)
@RestController
@RequestMapping({"/api/v1"})
public class ChatController {
    private final AzureOpenAiChatModel chatModel;
    private final AzureOpenAiImageModel imageModel;
    private final AzureOpenAiAudioTranscriptionModel azureOpenAiAudioTranscriptionModel;
    private final AzureOpenAiImageOptions imageOptions;
    private static final Logger logger = Logger.getLogger(ChatController.class.getName());
    private final ChatService chatService;

    public ChatController(AzureOpenAiChatModel chatModel, AzureOpenAiImageModel imageModel, ChatService chatService) {
        this.chatModel = chatModel;
        this.imageModel = imageModel;
        String whisperApiKey = System.getenv("AZURE_OPENAI_WHISPER_KEY");
        String whisperEndpoint = System.getenv("AZURE_OPENAI_WHISPER_ENDPOINT");
        if (whisperApiKey != null && !whisperApiKey.isEmpty()) {
            if (whisperEndpoint != null && !whisperEndpoint.isEmpty()) {
                try {
                    OpenAIClient openAIClient = (new OpenAIClientBuilder()).credential(new AzureKeyCredential(whisperApiKey)).endpoint(whisperEndpoint).buildClient();
                    AzureOpenAiAudioTranscriptionOptions azureOpenAiAudioTranscriptionOptions = new AzureOpenAiAudioTranscriptionOptions();
                    azureOpenAiAudioTranscriptionOptions.setLanguage("en");
                    this.azureOpenAiAudioTranscriptionModel = new AzureOpenAiAudioTranscriptionModel(openAIClient, azureOpenAiAudioTranscriptionOptions);
                    this.imageOptions = AzureOpenAiImageOptions.builder().withN(1).withHeight(1024).withWidth(1024).build();
                } catch (Exception var8) {
                    Exception e = var8;
                    logger.severe("Error initializing OpenAI client: " + e.getMessage());
                    throw new RuntimeException("Failed to initialize ChatController due to OpenAI client error", e);
                }

                this.chatService = chatService;
            } else {
                logger.severe("AZURE_OPENAI_WHISPER_ENDPOINT environment variable is missing.");
                throw new RuntimeException("AZURE_OPENAI_WHISPER_ENDPOINT is required but not found.");
            }
        } else {
            logger.severe("AZURE_OPENAI_WHISPER_KEY environment variable is missing.");
            throw new RuntimeException("AZURE_OPENAI_WHISPER_KEY is required but not found.");
        }
    }

    @CrossOrigin(
            origins = {"http://springaiassist.s3-website.eu-north-1.amazonaws.com", "https://delightful-tree-058892c0f.5.azurestaticapps.net", "https://main.d1roziyo3zgxn7.amplifyapp.com", "https://ashy-sky-0ba15c40f.5.azurestaticapps.net","http://localhost:5173", "http://localhost:3000","https://main.d23bkhxjqbyoh7.amplifyapp.com/"}
    )
    @GetMapping({"/chat"})
    public ResponseEntity<Map<String, String>> generate(@RequestParam(value = "message",defaultValue = "Tell me a joke") String message) {
        if (message != null && !message.trim().isEmpty()) {
            String azureApiKey = System.getenv("AZURE_OPENAI_KEY");
            if (azureApiKey != null && !azureApiKey.isEmpty()) {
                try {
                    logger.info("Generating text for message: " + message);
                    Map<String, String> generation = Map.of("generation", this.chatModel.call(message));
                    return ResponseEntity.ok(generation);
                } catch (Exception var4) {
                    Exception e = var4;
                    logger.severe("Error generating text response: " + e.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Text generation failed"));
                }
            } else {
                logger.severe("AZURE_OPENAI_KEY environment variable is missing.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "API key for Azure OpenAI is missing."));
            }
        } else {
            throw new ResourceNotFoundException("Message is required for generating response");
        }
    }

    @CrossOrigin(
            origins = {"http://springaiassist.s3-website.eu-north-1.amazonaws.com", "https://delightful-tree-058892c0f.5.azurestaticapps.net", "https://main.d1roziyo3zgxn7.amplifyapp.com", "https://ashy-sky-0ba15c40f.5.azurestaticapps.net","http://localhost:5173"}
    )
    @GetMapping({"/generate-image"})
    public ResponseEntity<ImageResponse> generateImage(@RequestParam String message) {
        if (message != null && !message.trim().isEmpty()) {
            try {
                logger.info("Generating image for message: " + message);
                ImageResponse response = this.imageModel.call(new ImagePrompt(message, this.imageOptions));
                return ResponseEntity.ok(response);
            } catch (Exception var3) {
                Exception e = var3;
                logger.severe("Error generating image: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body((ImageResponse) null);
            }
        } else {
            throw new ResourceNotFoundException("Message is required for generating image");
        }
    }

    @CrossOrigin(
            origins = {"http://springaiassist.s3-website.eu-north-1.amazonaws.com", "https://delightful-tree-058892c0f.5.azurestaticapps.net", "https://main.d1roziyo3zgxn7.amplifyapp.com", "https://ashy-sky-0ba15c40f.5.azurestaticapps.net","http://localhost:5173"}
    )
    @GetMapping({"/transcribe"})
    public ResponseEntity<String> getText(@RequestParam MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            try {
                ByteArrayResource resource = new ByteArrayResource(file.getBytes());
                String response = this.azureOpenAiAudioTranscriptionModel.call(resource);
                return ResponseEntity.ok(response);
            } catch (IOException var4) {
                IOException e = var4;
                logger.severe("Error reading the file: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error reading the audio file.");
            } catch (Exception var5) {
                Exception e = var5;
                logger.severe("Error during transcription: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Transcription failed.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Audio file is required for transcription.");
        }
    }

    @CrossOrigin(
            origins = {"http://springaiassist.s3-website.eu-north-1.amazonaws.com", "https://delightful-tree-058892c0f.5.azurestaticapps.net", "https://main.d1roziyo3zgxn7.amplifyapp.com", "https://ashy-sky-0ba15c40f.5.azurestaticapps.net","http://localhost:5173"}
    )
    @PostMapping({"/send-MultiLangAudio"})
    public ResponseEntity<String> audioTranscribe(@RequestParam MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            try {
                File tempfile = File.createTempFile("audio", ".wav");
                file.transferTo(tempfile);
                AzureOpenAiAudioTranscriptionOptions transcriptionOptions = AzureOpenAiAudioTranscriptionOptions.builder().withResponseFormat(TranscriptResponseFormat.TEXT).withTemperature(0.0F).build();
                FileSystemResource audioFile = new FileSystemResource(tempfile);
                AudioTranscriptionPrompt transcriptionRequest = new AudioTranscriptionPrompt(audioFile, transcriptionOptions);
                AudioTranscriptionResponse response = this.azureOpenAiAudioTranscriptionModel.call(transcriptionRequest);
                tempfile.delete();
                return ResponseEntity.ok(response.getResult().getOutput());
            } catch (IOException var7) {
                IOException e = var7;
                logger.severe("Error during file upload or transcription: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Audio transcription failed.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Audio file is required for transcription.");
        }
    }

    @CrossOrigin(
            origins = {"http://springaiassist.s3-website.eu-north-1.amazonaws.com", "https://delightful-tree-058892c0f.5.azurestaticapps.net", "https://main.d1roziyo3zgxn7.amplifyapp.com", "https://ashy-sky-0ba15c40f.5.azurestaticapps.net","http://localhost:5173"}
    )
    @GetMapping("/get-recipe")
    public ResponseEntity<String> getRecipe(@RequestParam(defaultValue = "any") String cuisine,
                                            @RequestParam String ingredients,
                                            @RequestParam String dietaryRestrictions) {
        if (ingredients == null || ingredients.trim().isEmpty()) {
            throw new ResourceNotFoundException("Ingredients are required for recipe generation");
        }

        try {
            String template = """
                    I want to create a recipe using the following ingredients: {ingredients}.
                    The cuisine type I prefer is {cuisine}.
                    Please consider the following dietary restrictions: {dietaryRestrictions}.
                    Please provide me with a detailed recipe including title, list of ingredients, and cooking instructions.
                    Make sure you give me these details in JSON format with each field as a string.
                  """;

            PromptTemplate promptTemplate = new PromptTemplate(template);
            Map<String, Object> params = Map.of("ingredients", ingredients, "dietaryRestrictions", dietaryRestrictions, "cuisine", cuisine);
            Prompt prompt = promptTemplate.create(params);
            String recipe = chatModel.call(prompt).getResult().getOutput().getContent();
            return ResponseEntity.ok(recipe);
        } catch (Exception e) {
            logger.severe("Error generating recipe: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Recipe generation failed.");
        }
    }
    @CrossOrigin(
            origins = {"http://springaiassist.s3-website.eu-north-1.amazonaws.com", "https://delightful-tree-058892c0f.5.azurestaticapps.net", "https://main.d1roziyo3zgxn7.amplifyapp.com", "https://ashy-sky-0ba15c40f.5.azurestaticapps.net", "https://main.d1b7c33z3by11m.amplifyapp.com/","http://localhost:5173"}
    )
    @PostMapping({"/send"})
    public ResponseEntity<String> sendEmail(@RequestParam String to, @RequestParam String subject, @RequestParam String message) {
        this.chatService.sendEmail(to, subject, message);
        return ResponseEntity.status(HttpStatus.OK).body("Email sent successfully to " + to);
    }
}
