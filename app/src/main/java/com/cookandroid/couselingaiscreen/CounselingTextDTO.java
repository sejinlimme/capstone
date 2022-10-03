package com.cookandroid.couselingaiscreen;

import com.google.gson.annotations.SerializedName;

public class CounselingTextDTO {
    @SerializedName("chatItem")
    public String chatItem;

    public String getChatItem() {
        return chatItem;
    }

    public void setChatItem(String chatItem) {
        this.chatItem = chatItem;
    }

    public CounselingTextDTO(String chatItem) {
        this.chatItem = chatItem;
    }
}
