/*
package com.ehubstar.post;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.ehubstar.R;
import com.ehubstar.activity.MH02_PhotoDetailActivity;
import com.ehubstar.config.MyApplication;
import com.ehubstar.fragments.Fragment_1_Home_User;
import com.ehubstar.fragments.Fragment_2_Explorer;
import com.ehubstar.hashtag.HashTagActivity;
import com.ehubstar.objects.image.ImageItem;
import com.ehubstar.objects.image.ImageSize;
import com.ehubstar.objects.image.Item;
import com.ehubstar.objects.image.MediaObject;
import com.ehubstar.profile.Fragment_5_User_Profile_Grid;
import com.ehubstar.services.ReturnResult;
import com.ehubstar.services.SasChild;
import com.ehubstar.services.SasParent;
import com.ehubstar.services.Webservices;
import com.ehubstar.utils.MyUtils;
import com.ehubstar.utils.ProgressUtils;
import com.google.gson.reflect.TypeToken;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.StorageUri;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PostUtils_Old {
    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    private Context context;

    public PostUtils_Old(Context context) {
        this.context = context;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    //UPLOAD IMAGE
    private UploadImageListener imageListener;

    public void uploadImageToServer(ArrayList<Uri> listImages, UploadImageListener imageListener) {
        if (listImages != null && listImages.size() > 0) {
            this.imageListener = imageListener;

            //chuan bi lay duong dan sas truoc khi upload anh len azure
            final String GUID = UUID.randomUUID().toString().toLowerCase();

            ProgressUtils.show(context);
            ANRequest.PostRequestBuilder request = AndroidNetworking.post(Webservices.URL + "image/getUploadSAS")
                    .addBodyParameter("ItemGUID", GUID)
                    .setOkHttpClient(MyApplication.getClient());

            for (int i = 0; i < listImages.size(); i++) {
                String imgPath = listImages.get(i).getPath();
                if (imgPath != null) {
                    File file = new File(imgPath);
                    final String extension = MyUtils.getFileExtension(file);
                    request.addBodyParameter("ImageExtensions[" + i + "]", extension);
                }
            }

            request.build().getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    Type type = new TypeToken<ArrayList<SasParent>>() {
                    }.getType();

                    ReturnResult result = Webservices.parseJson(response, type, true);
                    ProgressUtils.hide();

                    if (result != null) {
                        if (result.getErrorCode() == ReturnResult.SUCCESS) {
                            //co sas thi upload len server
                            ArrayList<SasParent> list = (ArrayList<SasParent>) result.getData();
                            if (list != null && list.size() > 0) {
                                //Upload tung kich thuoc hinh len server
                                uploadImage(list, GUID, listImages);
                            }

                        } else {
                            MyUtils.showToast(context, result.getErrorMessage());
                        }
                    }
                }

                @Override
                public void onError(ANError ANError) {
                    ProgressUtils.hide();
                }
            });
        } else {
            MyUtils.showToast(context, R.string.not_found);
        }
    }

    private void uploadImage(ArrayList<SasParent> list, String guid, ArrayList<Uri> listImages) {
        if (uploadTask == null || uploadTask.getStatus() == AsyncTask.Status.FINISHED) {
            uploadTask = new UploadImageTask(list, guid, listImages);
            uploadTask.execute();
        }
    }

    private UploadImageTask uploadTask;

    private class UploadImageTask extends AsyncTask<String, Void, Boolean> {

        String GUID = "";
        ArrayList<SasParent> list;
        ArrayList<Uri> listImages;

        public UploadImageTask(ArrayList<SasParent> list, String guid, ArrayList<Uri> listImages) {
            this.list = list;
            GUID = guid;
            this.listImages = listImages;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressUtils.show(context);
            itemContent.clear();
        }

        private ArrayList<Item> itemContent = new ArrayList<>();

        @Override
        protected Boolean doInBackground(String... strings) {
            for (int i = 0; i < listImages.size(); i++) {
                String fileTemp = MyUtils.createImageFile(context).getAbsolutePath();
                String imgPath = listImages.get(i).getPath();
                SasParent parent = list.get(i);
                //correct images
                //neu xoay thi xoay lai va luu vao bo nho, khac 0 thi se xoay lai
//                int rotation = MyUtils.getRotationForImage(imgPath);
//                imgPath = MyUtils.saveBitmap(MyUtils.resizeImage(imgPath, rotation, ImageSize.ORIGINAL_WIDTH, ImageSize.ORIGINAL_HEIGHT), imgPath);

                //upload 1 item
                int uploadNumber = 0;
                Item item = new Item();
                item.setItemType(Item.TYPE_IMAGE);

                boolean isSquare = false;
                //upload origin
                SasChild large = parent.getLarge();
                if (large != null) {

                    Bitmap b = MyUtils.resizeAndRotateImage(imgPath, ImageSize.ORIGINAL_WIDTH, ImageSize.ORIGINAL_HEIGHT);
                    if (b.getWidth() == b.getHeight()) {
                        isSquare = true;
                    }
                    MyUtils.log("image_size " + b.getWidth() + " - " + b.getHeight());
                    fileTemp = MyUtils.saveBitmap(b, fileTemp);

                    boolean isSuccessed = uploadImageToAzure(large, fileTemp);
                    if (isSuccessed) {
                        uploadNumber += 1;
                    }

                    //tao item
                    MediaObject obj = new MediaObject();
                    obj.setLink(large.getLink());
                    obj.setWidth(b.getWidth());
                    obj.setHeight(b.getHeight());

                    File f = new File(fileTemp);
                    long size = f.length() / 1024;
                    obj.setSizeInKb(size);

                    item.setLarge(obj);
                }

                //upload medium
                SasChild medium = parent.getMedium();
                if (medium != null) {

                    Bitmap b = MyUtils.resizeAndRotateImage(imgPath, ImageSize.MEDIUM_WIDTH, isSquare ? ImageSize.MEDIUM_WIDTH : ImageSize.MEDIUM_HEIGHT);
                    MyUtils.log("image_size " + b.getWidth() + " - " + b.getHeight());
                    fileTemp = MyUtils.saveBitmap(b, fileTemp);

                    boolean isSuccessed = uploadImageToAzure(medium, fileTemp);
                    if (isSuccessed) {
                        uploadNumber += 1;
                    }

                    //tao item
                    MediaObject obj = new MediaObject();
                    obj.setLink(medium.getLink());
                    obj.setWidth(b.getWidth());
                    obj.setHeight(b.getHeight());

                    File f = new File(fileTemp);
                    long size = f.length() / 1024;
                    obj.setSizeInKb(size);

                    item.setMedium(obj);
                }

                //upload small
                SasChild small = parent.getSmall();
                if (small != null) {

                    Bitmap b = MyUtils.resizeAndRotateImage(fileTemp, ImageSize.SMALL_WIDTH, isSquare ? ImageSize.SMALL_WIDTH : ImageSize.SMALL_HEIGHT);
                    MyUtils.log("image_size " + b.getWidth() + " - " + b.getHeight());
                    fileTemp = MyUtils.saveBitmap(b, fileTemp);

                    boolean isSuccessed = uploadImageToAzure(small, fileTemp);
                    if (isSuccessed) {
                        uploadNumber += 1;
                    }

                    //tao item
                    MediaObject obj = new MediaObject();
                    obj.setLink(small.getLink());
                    obj.setWidth(b.getWidth());
                    obj.setHeight(b.getHeight());

                    File f = new File(fileTemp);
                    long size = f.length() / 1024;
                    obj.setSizeInKb(size);

                    item.setSmall(obj);
                }

                itemContent.add(item);

                //neu == 3 thi thanh cong
                */
/*if (uploadNumber == 3) {
                    return true;
                } else {
                    return false;
                }*//*

                //xoa file tam
                try {
                    File f = new File(fileTemp);
                    if (f.exists()) {
                        f.delete();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                imageListener.onUploadImageSuccess(GUID, itemContent);
            } else {
                MyUtils.showToast(context, R.string.uploadfail);
            }
            ProgressUtils.hide();
        }
    }

    private boolean uploadImageToAzure(SasChild child, String imgPath) {
        if (child != null) {
            String sasLink = child.getSAS();
            try {
                //Return a reference to the blob using the SAS URI.
                CloudBlockBlob blob = new CloudBlockBlob(new StorageUri(URI.create(sasLink)));
                //blob.setStreamWriteSizeInBytes(256 * 1024);//256 k


                OutputStream oStream = blob.openOutputStream();
                File f = new File(imgPath);
                FileInputStream inputStream = new FileInputStream(f);
                blob.upload(inputStream, f.length());

                //upload properties
                blob.getProperties().setContentType("image/jpeg");//File : "application/octet-stream"
                blob.uploadProperties();

                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return false;
    }
    //UPLOAD IMAGE


    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////

    //UPLOAD VIDEO
    String videoPath = "";
    private UploadVideoListener videoListener;

    public void uploadVideoToServer(String videoPath, String imgPath, UploadVideoListener listener) {
        if (!TextUtils.isEmpty(videoPath)) {
            this.videoPath = videoPath;
            videoListener = listener;

            File file = new File(videoPath);
            //chuan bi lay duong dan sas truoc khi upload anh len azure
            final String GUID = UUID.randomUUID().toString().toLowerCase();
            final String extension = MyUtils.getFileExtension(file);

            ProgressUtils.show(context);
            AndroidNetworking.post(Webservices.URL + "image/getUploadSAS")
                    .addBodyParameter("ItemGUID", GUID)
                    .addBodyParameter("ImageExtensions[]", extension)//array
                    .setOkHttpClient(MyApplication.getClient())
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Type type = new TypeToken<ArrayList<SasParent>>() {
                            }.getType();

                            ReturnResult result = Webservices.parseJson(response, type, true);
                            ProgressUtils.hide();

                            if (result != null) {
                                if (result.getErrorCode() == ReturnResult.SUCCESS) {//co sas thi upload len server
                                    ArrayList<SasParent> list = (ArrayList<SasParent>) result.getData();
                                    if (list != null && list.size() > 0) {
                                        //Upload tung kich thuoc hinh len server
                                        //link1: video, link2:jpg, link3: jpg thumbnail
                                        uploadVideo(list.get(0), GUID, extension, imgPath);
                                    }

                                } else {
                                    MyUtils.showToast(context, result.getErrorMessage());
                                }
                            }
                        }

                        @Override
                        public void onError(ANError ANError) {
                            ProgressUtils.hide();
                        }
                    });
        } else {
            MyUtils.showToast(context, R.string.not_found);
        }
    }


    private void uploadVideo(SasParent parent, String guid, String extension, String imgPath) {
        if (uploadVideoTask == null || uploadVideoTask.getStatus() == AsyncTask.Status.FINISHED) {
            uploadVideoTask = new UploadVideoTask(parent, guid, extension, imgPath);
            uploadVideoTask.execute();
        }
    }

    private UploadVideoTask uploadVideoTask;

    private class UploadVideoTask extends AsyncTask<String, Void, Boolean> {

        String GUID = "", extension = "";
        SasParent parent;
        String imgPath = "";

        public UploadVideoTask(SasParent p, String guid, String ext, String imgPath) {
            parent = p;
            GUID = guid;
            extension = ext;
            this.imgPath = imgPath;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressUtils.show(context);
        }

        private ArrayList<Item> itemContent = new ArrayList<>();

        @Override
        protected Boolean doInBackground(String... strings) {
            int uploadNumber = 0;

            Item item = new Item();
            item.setItemType(Item.TYPE_VIDEO);

            boolean isSquare = true;
            //upload origin
            SasChild large = parent.getLarge();
            if (large != null) {

                boolean isSuccessed = uploadVideoToAzure(large, videoPath);
                if (isSuccessed) {
                    uploadNumber += 1;
                }

                //tao item
                MediaObject obj = new MediaObject();
                obj.setLink(large.getLink());

                //lay size cua video dua vao thumnail
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(imgPath, opts);

                obj.setWidth(opts.outWidth);
                obj.setHeight(opts.outHeight);

                //dung luong video
                File f = new File(videoPath);
                long size = f.length() / 1024;
                obj.setSizeInKb(size);

                item.setLarge(obj);

            }


            //upload medium
            SasChild medium = parent.getMedium();
            if (medium != null) {

                Bitmap b = MyUtils.resizeAndRotateImage(imgPath, ImageSize.MEDIUM_WIDTH, isSquare ? ImageSize.MEDIUM_WIDTH : ImageSize.MEDIUM_HEIGHT);
                MyUtils.log("image_size " + b.getWidth() + " - " + b.getHeight());
                imgPath = MyUtils.saveBitmap(b, imgPath);

                boolean isSuccessed = uploadImageToAzure(medium, imgPath);
                if (isSuccessed) {
                    uploadNumber += 1;
                }

                //tao item
                MediaObject obj = new MediaObject();
                obj.setLink(medium.getLink());
                obj.setWidth(b.getWidth());
                obj.setHeight(b.getHeight());

                File f = new File(imgPath);
                long size = f.length() / 1024;
                obj.setSizeInKb(size);

                item.setMedium(obj);
            }

            //upload small
            SasChild small = parent.getSmall();
            if (small != null) {

                Bitmap b = MyUtils.resizeAndRotateImage(imgPath, ImageSize.SMALL_WIDTH, isSquare ? ImageSize.SMALL_WIDTH : ImageSize.SMALL_HEIGHT);
                MyUtils.log("image_size " + b.getWidth() + " - " + b.getHeight());
                imgPath = MyUtils.saveBitmap(b, imgPath);

                boolean isSuccessed = uploadImageToAzure(small, imgPath);
                if (isSuccessed) {
                    uploadNumber += 1;
                }

                //tao item
                MediaObject obj = new MediaObject();
                obj.setLink(small.getLink());
                obj.setWidth(b.getWidth());
                obj.setHeight(b.getHeight());

                File f = new File(imgPath);
                long size = f.length() / 1024;
                obj.setSizeInKb(size);

                item.setSmall(obj);
            }

            itemContent.add(item);

            //neu == 3 thi thanh cong
            if (uploadNumber == 3) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                videoListener.onUploadVideoSuccess(GUID, itemContent);
            } else {
                MyUtils.showToast(context, R.string.uploadfail);
            }
            ProgressUtils.hide();
        }
    }

    private boolean uploadVideoToAzure(SasChild child, String videoPath) {
        if (child != null) {
            String sasLink = child.getSAS();
            try {
                //Return a reference to the blob using the SAS URI.
                CloudBlockBlob blob = new CloudBlockBlob(new StorageUri(URI.create(sasLink)));
                //blob.setStreamWriteSizeInBytes(256 * 1024);//256 k


                OutputStream oStream = blob.openOutputStream();
                File f = new File(videoPath);
                FileInputStream inputStream = new FileInputStream(f);
                blob.upload(inputStream, f.length());

                //upload properties
                blob.getProperties().setContentType("video/mp4");//File : "application/octet-stream"
                blob.uploadProperties();

                return true;
            } catch (StorageException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    //UPLOAD VIDEO
    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////////////////////////////
    public void updateDescription(ImageItem item, String description, List<String> hashtags, List<String> mentions, UpdateCompletListener updateCompletListener) {
        ProgressUtils.show(context);
        AndroidNetworking.post(Webservices.URL_GRAPHQL)
                .addQueryParameter("query", Webservices.UPDATE_IMAGE_ITEM_DESCRIPTION(item.getImageItemId(), description, hashtags, mentions, item.getItemData(),"", 0.0,0.0))
                .setOkHttpClient(MyApplication.getClient())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject objP = response.getJSONObject("data");
                            JSONObject obj = objP.getJSONObject("UpdateImageItem");

                            double imageItemId = obj.getDouble("ImageItemId");
                            if (imageItemId > 0) {
                                MyUtils.showToast(context, R.string.update_success);

                                //update home list
                                Intent intent = new Intent(Fragment_1_Home_User.ACTION_UPDATE_ITEM);
                                intent.putExtra(ImageItem.IMAGE_ITEM, item);
                                context.sendBroadcast(intent);


                                //update man hinh chi tiet MH02_PhotoDetailActivity
                                intent = new Intent(MH02_PhotoDetailActivity.ACTION_UPDATE_ITEM);
                                intent.putExtra(ImageItem.IMAGE_ITEM, item);
                                context.sendBroadcast(intent);

                                //update grid user
                                intent = new Intent(Fragment_5_User_Profile_Grid.ACTION_UPDATE_ITEM);
                                intent.putExtra(ImageItem.IMAGE_ITEM, item);
                                context.sendBroadcast(intent);

                                //update grid image by hashtag
                                intent = new Intent(HashTagActivity.ACTION_UPDATE_ITEM);
                                intent.putExtra(ImageItem.IMAGE_ITEM, item);
                                context.sendBroadcast(intent);

                                //update grid explorer
                                intent = new Intent(Fragment_2_Explorer.ACTION_UPDATE_ITEM);
                                intent.putExtra(ImageItem.IMAGE_ITEM, item);
                                context.sendBroadcast(intent);

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        updateCompletListener.onUpdateSuccess();
                        ProgressUtils.hide();
                    }

                    @Override
                    public void onError(ANError ANError) {
                        ProgressUtils.hide();
                        MyUtils.log(ANError.getMessage());
                        if (ANError.getErrorCode() == ReturnResult.ERROR_CODE_UNAUTHORIZED) {
                        }
                    }
                });

    }
    //////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////////////////////////////


}
*/
