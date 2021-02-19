package com.topceo.post;

import com.topceo.objects.image.Item;

import java.util.ArrayList;

public interface UploadImageListener {
    void onUploadImageSuccess(String GUID, ArrayList<Item> itemContent);
}
