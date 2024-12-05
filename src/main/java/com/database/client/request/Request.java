package com.griddynamics.client.request;

import com.google.gson.JsonElement;

public class Request {

    public String type;
    public JsonElement key;
    public JsonElement value;

    public Request(String type, JsonElement key, JsonElement value) {
        this.type = type;
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Request{" +
                "type='" + type + '\'' +
                ", key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
