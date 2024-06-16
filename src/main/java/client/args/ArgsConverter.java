package client.args;

import com.beust.jcommander.IStringConverter;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class ArgsConverter implements IStringConverter<JsonElement> {
   @Override
   public JsonElement convert(String s) {
      String jsonValue = new Gson().toJson(s);
      return JsonParser.parseString(jsonValue);
   }
}
