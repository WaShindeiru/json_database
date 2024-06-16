package client.args;

import com.beust.jcommander.Parameter;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;

public class Args {

    public List<String> parameters = new ArrayList<>();

    @Parameter(names = "-t")
    public String action = null;

    @Parameter(names = "-k", converter = ArgsConverter.class)
    public JsonElement key = null;

    @Parameter(names = "-v", converter = ArgsConverter.class)
    public JsonElement data = null;

    @Parameter(names = "-in")
    public String filePath = null;
}
