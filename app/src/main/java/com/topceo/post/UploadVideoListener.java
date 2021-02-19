package com.topceo.post;

import com.topceo.objects.image.Item;

import java.util.ArrayList;

public interface UploadVideoListener {
    void onUploadVideoSuccess(String GUID, ArrayList<Item> itemContent);
}
