package com.github.drsqrt;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ResourceTranslator {

  private static final Logger logger = Logger.getLogger(ResourceTranslator.class.getName());
  private static final String OUTPUT = "bundle.translate.json";
  private static final String[] languages = new String[]{"hi", "ja"};

  public static void main(String[] args) {
    ResourceTranslator resourceTranslator = new ResourceTranslator();
    resourceTranslator.execute();
  }

  private void execute() {
    ClassLoader classLoader = this.getClass().getClassLoader();
    File bundle = new File(Objects.requireNonNull(classLoader.getResource("bundle.json")).getFile());
    try (Reader jsonReader = new FileReader(bundle)) {
      Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
      JsonElement jsonElement = JsonParser.parseReader(jsonReader);
      JsonObject jsonObject = jsonElement.getAsJsonObject();
      JsonObject outputJson = new JsonObject();

      String bundleName = jsonObject.get("bundleName").getAsString();
      logger.info("Parsing Bundle " + bundleName);
      outputJson.addProperty("bundleName", bundleName);
      JsonArray values = jsonObject.getAsJsonArray("values");
      JsonArray outputValues = new JsonArray();
      for (JsonElement ele : values) {
        JsonObject value = ele.getAsJsonObject();
        String key = value.get("key").getAsString();
        String en = value.getAsJsonObject("translation").get("en").getAsString();
        JsonObject outputValue = new JsonObject();
        outputValue.addProperty("key", key);
        JsonObject outputTranslation = new JsonObject();
        outputTranslation.addProperty("en", en);
        Translate translate = new Translate();
        for (String lang : languages) {
          String translateText = translate.translateText(en, lang);
          outputTranslation.addProperty(lang, translateText);
        }
        outputValue.add("translation", outputTranslation);
        outputValues.add(outputValue);
      }
      outputJson.add("values", outputValues);
      String resourceDir = Objects.requireNonNull(classLoader.getResource(".")).getPath();
      try (FileWriter writer = new FileWriter(resourceDir + OUTPUT)) {
        writer.write(gson.toJson(outputJson));
        logger.info(OUTPUT + " saved successfully!");
      }
    } catch (IOException e) {
      logger.log(Level.SEVERE, "error while translating", e);
    }
  }
}