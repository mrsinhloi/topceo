package com.topceo.objects.db;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class UserFollowing extends RealmObject {
    @PrimaryKey
    private String _id = UUID.randomUUID().toString();
    private long UserId;

    public long getUserId() {
        return UserId;
    }

    public void setUserId(long userId) {
        UserId = userId;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}
