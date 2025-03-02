package com.github.drsqrt;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AsyncTranslate {

  private static final String API_URL = "http://localhost:5051/translate";
  private static final Logger logger = Logger.getLogger(AsyncTranslate.class.getName());
  private static final OkHttpClient client = new OkHttpClient();
  private static final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

  public static void main(String[] args) {
    List<String> sentences = List.of(
      "The quick brown fox jumps over the lazy dog.",
      "How do you do?",
      "A journey of a thousand miles begins with a single step."
    );

    translateBatch(sentences, "ja");
    translateBatch(sentences, "hi");
  }

  private static void translateBatch(List<String> sentences, String targetLanguage) {
    CompletableFuture.allOf(
      sentences.stream()
        .map(sentence -> CompletableFuture.runAsync(() -> translateText(sentence, targetLanguage), executor))
        .toArray(CompletableFuture[]::new)
    ).join();
  }

  private static void translateText(String text, String targetLanguage) {
    try {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("q", text);
      jsonObject.addProperty("source", "auto");
      jsonObject.addProperty("target", targetLanguage);
      jsonObject.addProperty("format", "text");

      RequestBody body = RequestBody.create(jsonObject.toString(), MediaType.get("application/json; charset=utf-8"));
      Request request = new Request.Builder()
        .url(API_URL)
        .post(body)
        .addHeader("Content-Type", "application/json")
        .build();

      Response response = client.newCall(request).execute();
      if (response.isSuccessful() && response.body() != null) {
        String responseBody = response.body().string();
        JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
        logger.info("Translated (" + targetLanguage + "): " + jsonResponse.get("translatedText"));
      } else {
        logger.warning("Translation failed (" + targetLanguage + "): " + response.code());
      }
      response.close();
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Error translating text", e);
    }
  }
}

