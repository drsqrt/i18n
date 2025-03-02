package com.github.drsqrt;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncTranslate {

  private static final String API_URL = "http://localhost:5051/translate";
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
      JSONObject jsonBody = new JSONObject();
      jsonBody.put("q", text);
      jsonBody.put("source", "auto");
      jsonBody.put("target", targetLanguage);
      jsonBody.put("format", "text");

      RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.get("application/json; charset=utf-8"));
      Request request = new Request.Builder()
        .url(API_URL)
        .post(body)
        .addHeader("Content-Type", "application/json")
        .build();

      Response response = client.newCall(request).execute();
      if (response.isSuccessful() && response.body() != null) {
        String responseBody = response.body().string();
        JSONObject jsonResponse = new JSONObject(responseBody);
        System.out.println("Translated (" + targetLanguage + "): " + jsonResponse.getString("translatedText"));
      } else {
        System.out.println("Translation failed (" + targetLanguage + "): " + response.code());
      }
      response.close();
    } catch (IOException e) {
      System.err.println("Error translating text: " + e.getMessage());
    }
  }
}

