package com.griddynamics.server.session;

import com.google.gson.JsonElement;

public class Response {

    public String response;
    public JsonElement value;
    public String reason;

    public Response(String response, JsonElement value, String reason) {
        this.response = response;
        this.value = value;
        this.reason = reason;
    }
}
