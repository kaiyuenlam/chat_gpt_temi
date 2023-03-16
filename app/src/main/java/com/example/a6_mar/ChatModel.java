package com.example.a6_mar;

public class ChatModel {
    private boolean isChatGDP;
    private String stringText;

    public ChatModel(boolean isChatGDP, String stringText) {
        this.isChatGDP = isChatGDP;
        this.stringText = stringText;
    }

    public boolean isChatGDP() {
        return isChatGDP;
    }

    public String getStringText() {
        return stringText;
    }
}
