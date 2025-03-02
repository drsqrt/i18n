package com.github.drsqrt;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;


public class Translate {

  /**
   * Other end points to use :
   * https://translate.flossboxin.org.in/translate, https://libretranslate.com/translate
   */
  @SuppressWarnings("JavadocLinkAsPlainText")
  private static final String API_URL = "http://localhost:5051/translate";
  private static final OkHttpClient client = new OkHttpClient();

  public static void main(String[] args) {
    String textToTranslate = "Hello, how are you?";

    translateText(textToTranslate, "ja"); // Japanese
    translateText(textToTranslate, "hi"); // Hindi
    /*translateText(textToTranslate, "de");*/ // German
  }

  private static void translateText(String text, String targetLanguage) {
    try {
      JSONObject jsonBody = new JSONObject();
      jsonBody.put("q", text);
      jsonBody.put("source", "en");
      jsonBody.put("target", targetLanguage);
      jsonBody.put("format", "text");
      RequestBody body = RequestBody.create(jsonBody.toString(),
        MediaType.get("application/json; charset=utf-8"));
      Request request = new Request.Builder()
        .url(API_URL)
        .post(body)
        .addHeader("Content-Type", "application/json")
        .build();

      Response response = client.newCall(request).execute();
      if (response.isSuccessful() && response.body() != null) {
        String responseBody = response.body().string();
        System.out.println("Translated (" + targetLanguage + "): " + responseBody);
      } else {
        System.out.println("Translation failed: " + response.code());
      }
      response.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
