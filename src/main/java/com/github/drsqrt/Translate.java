package com.github.drsqrt;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Translate {

  /**
   * Other end points to use :
   * https://translate.flossboxin.org.in/translate, https://libretranslate.com/translate
   */
  @SuppressWarnings("JavadocLinkAsPlainText")
  private static final String API_URL = "http://localhost:5051/translate";
  private static final OkHttpClient client = new OkHttpClient();
  private static final Logger logger = Logger.getLogger(Translate.class.getName());

  public static void main(String[] args) {
    String textToTranslate = "Hello, how are you?";

    Translate translate = new Translate();
    translate.translateText(textToTranslate, "hi");
    translate.translateText(textToTranslate, "ja");
  }

  public String translateText(String text, String targetLanguage) {
    try {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("q", text);
      jsonObject.addProperty("source", "en");
      jsonObject.addProperty("target", targetLanguage);
      jsonObject.addProperty("format", "text");
      RequestBody body = RequestBody.create(jsonObject.toString(),
        MediaType.get("application/json; charset=utf-8"));
      Request request = new Request.Builder()
        .url(API_URL)
        .post(body)
        .addHeader("Content-Type", "application/json")
        .build();

      try (Response response = client.newCall(request).execute()) {
        if (response.isSuccessful() && response.body() != null) {
          JsonObject jsonResponse = JsonParser.parseString(response.body().string()).getAsJsonObject();
          String translatedText = jsonResponse.get("translatedText").getAsString();
          logger.info("Translated (" + targetLanguage + "): " + translatedText);
          return translatedText;
        }
        logger.log(Level.WARNING, "Translation failed with status: " + response.code());
        return "404";
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error occurred while translating text: " + e.getMessage(), e);
      return "404";
    }
  }
}
