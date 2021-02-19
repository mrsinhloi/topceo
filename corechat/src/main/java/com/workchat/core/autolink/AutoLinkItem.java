package com.workchat.core.autolink;

/**
 * Tham khảo: https://viblo.asia/p/click-link-trong-textview-3Q75wpR3KWb
 * Tiếp đến là tạo Object để chứa thuộc tính của đoạn text bạn muốn khởi tạo link
 */
public class AutoLinkItem {
    // Properties of Link
    private AutoLinkMode autoLinkMode;
    // String link text
    private String matchedText;
    // Position start and end of string in TextView.
    private int startPoint,endPoint;

    AutoLinkItem(int startPoint, int endPoint, String matchedText, AutoLinkMode autoLinkMode) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.matchedText = matchedText;
        this.autoLinkMode = autoLinkMode;
    }

    // Write method setter, getter
    public AutoLinkMode getAutoLinkMode() {
        return autoLinkMode;
    }

    public void setAutoLinkMode(AutoLinkMode autoLinkMode) {
        this.autoLinkMode = autoLinkMode;
    }

    public String getMatchedText() {
        return matchedText;
    }

    public void setMatchedText(String matchedText) {
        this.matchedText = matchedText;
    }

    public int getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(int startPoint) {
        this.startPoint = startPoint;
    }

    public int getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(int endPoint) {
        this.endPoint = endPoint;
    }
}
