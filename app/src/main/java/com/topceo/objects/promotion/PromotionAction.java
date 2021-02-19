package com.topceo.objects.promotion;

public class PromotionAction {
    //    "Kiểu hành động:
    public static final String ACCEPT = "ACCEPT";//: show 2 button Accept & Refuse để User nhận Promo, nếu accept thì gọi setPromotion, refuse thì gọi refusePromotion
    public static final String DEEPLINK = "DEEPLINK";//: Deeplink đến màn hình mong muốn trong app, dựa vào ActionLink để chuyển đến màn hình, gọi accept hoặc refuse cũng đc để lần sau ko hiện nữa
    public static final String LINK = "LINK";//: link web bên ngoài theo giá trị ActionLink, gọi accept hoặc refuse cũng đc để lần sau ko hiện nữa"
}
