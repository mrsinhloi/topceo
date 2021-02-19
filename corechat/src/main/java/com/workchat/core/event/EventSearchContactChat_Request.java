package com.workchat.core.event;

public class EventSearchContactChat_Request {
    private String keyword = "";

    public EventSearchContactChat_Request(String query) {
        keyword = query;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
