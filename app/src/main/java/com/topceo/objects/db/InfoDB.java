package com.topceo.objects.db;

import com.topceo.objects.image.Info;

import io.realm.RealmObject;

public class InfoDB extends RealmObject{

    private String Icon;
    private String Text;
    private String Link;
    private String Target;

    public Info copy(){
        Info item = new Info();
        item.setIcon(Icon);
        item.setText(Text);
        item.setLink(Link);
        item.setTarget(Target);
        return item;
    }

    public String getIcon() {
        return Icon;
    }

    public void setIcon(String icon) {
        Icon = icon;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }

    public String getLink() {
        return Link;
    }

    public void setLink(String link) {
        Link = link;
    }

    public String getTarget() {
        return Target;
    }

    public void setTarget(String target) {
        Target = target;
    }


}
