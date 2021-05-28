package com.topceo.post;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.text.TextUtils;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.topceo.R;
import com.topceo.config.MyApplication;
import com.topceo.objects.image.ImageItem;
import com.topceo.objects.image.ImageSize;
import com.topceo.objects.image.Item;
import com.topceo.objects.image.MediaObject;
import com.topceo.services.ReturnResult;
import com.topceo.services.SasChild;
import com.topceo.services.SasParent;
import com.topceo.services.Webservices;
import com.topceo.utils.MyUtils;
import com.topceo.utils.ProgressUtils;
import com.google.gson.reflect.TypeToken;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.StorageUri;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.smartapp.collage.FileUtils;
import com.smartapp.collage.UtilsKt;
import com.smartapp.sizes.ISize;
import com.smartapp.sizes.SizeFromImage;
import com.smartapp.sizes.SizeFromVideoFile;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import me.echodev.resizer.Resizer;

public class PostUtils {
    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    private Context context;

    public PostUtils(Context context) {
        this.context = context;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    //UPLOAD IMAGE
    private UploadImageListener imageListener;

    public void uploadImageToServer(long groupId, ArrayList<Uri> listImages, UploadImageListener imageListener) {
        if (listImages != null && listImages.size() > 0) {
            this.imageListener = imageListener;

            //replace with real path
            for (int i = 0; i < listImages.size(); i++) {
                Uri uri = listImages.get(i);
                if (uri.toString().contains("content://")) {
                    Uri uriImage = FileUtils.parseUriImage(uri, context);
                    listImages.set(i, uriImage);
                }
            }


            //chuan bi lay duong dan sas truoc khi upload anh len azure
            final String GUID = UUID.randomUUID().toString().toLowerCase();

            ProgressUtils.show(context);
            ANRequest.PostRequestBuilder request = AndroidNetworking.post(Webservices.API_URL + "image/getUploadSAS")
                    .addBodyParameter("ItemGUID", GUID)
                    .setOkHttpClient(MyApplication.getClient());

            for (int i = 0; i < listImages.size(); i++) {
                Uri uri = listImages.get(i);
                String imgPath = uri.getPath();
                if (imgPath != null) {
                    File file = new File(imgPath);
                    final String extension = MyUtils.getFileExtension(file);
                    request.addBodyParameter("ImageExtensions[" + i + "]", extension);
                }
            }

            if (groupId > 0) {
                request.addBodyParameter("GroupId", String.valueOf(groupId));
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
//                String fileTemp = MyUtils.createImageFile(context).getAbsolutePath();

                String imgPath = listImages.get(i).getPath();
                SasParent parent = list.get(i);
                //correct images
                //neu xoay thi xoay lai va luu vao bo nho, khac 0 thi se xoay lai
//                int rotation = MyUtils.getRotationForImage(imgPath);
//                imgPath = MyUtils.saveBitmap(MyUtils.resizeImage(imgPath, rotation, ImageSize.ORIGINAL_WIDTH, ImageSize.ORIGINAL_HEIGHT), imgPath);

                //upload 1 item
                Item item = new Item();
                item.setItemType(Item.TYPE_IMAGE);

                //upload origin
                File fileTemp = null;
                SasChild large = parent.getLarge();
                if (large != null) {
                    MediaObject obj = uploadImageToAzure(imgPath, large, ImageSize.ORIGINAL_HEIGHT);
                    if (obj != null) {
                        item.setLarge(obj);
                        fileTemp = obj.getFileTemp();
                    }
                }

                if(fileTemp==null){
                    return false;
                }else{
                    //upload medium
                    SasChild medium = parent.getMedium();
                    if (medium != null && fileTemp != null) {
                        MediaObject obj = uploadImageToAzure(fileTemp.getAbsolutePath(), medium, ImageSize.MEDIUM_HEIGHT);
                        if (obj != null) {
                            item.setMedium(obj);
                            fileTemp = obj.getFileTemp();
                        }
                    }

                    //upload small
                    SasChild small = parent.getSmall();
                    if (small != null && fileTemp != null) {
                        MediaObject obj = uploadImageToAzure(fileTemp.getAbsolutePath(), small, ImageSize.SMALL_HEIGHT);
                        if (obj != null) {
                            item.setSmall(obj);
                            fileTemp = obj.getFileTemp();
                            if (fileTemp != null) {
                                if (fileTemp.exists()) {
                                    fileTemp.deleteOnExit();
                                }
                            }
                        }
                    }

                    itemContent.add(item);
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

    private MediaObject uploadImageToAzure(String imgPath, SasChild sas, int targetWidth) {
        MediaObject obj = null;
        try {
            String tempFile = MyUtils.createImageFile(context).getAbsolutePath();
            long start = SystemClock.elapsedRealtime();

            //neu hinh co bi rotate thi xoay lai
            int degree = MyUtils.getRotationForImage(imgPath);
            if (degree > 0) {
                Bitmap b = BitmapFactory.decodeFile(imgPath);
                Bitmap rotate = MyUtils.rotateImage(b, degree);
                tempFile = MyUtils.saveBitmap(rotate, tempFile, 100);

                if (!b.isRecycled()) {
                    b.recycle();
                }
                if (!rotate.isRecycled()) {
                    rotate.recycle();
                }
            } else {
                tempFile = imgPath;
            }

            File temp = new Resizer(context)
                    .setTargetLength(targetWidth)
                    .setQuality(80)
                    .setOutputFormat("JPEG")
                    .setOutputFilename("resized_image")
                    .setOutputDirPath(context.getExternalCacheDir().getAbsolutePath())
                    .setSourceImage(new File(tempFile))
                    .getResizedFile();

            boolean isSuccessed = uploadImageToAzure(sas, temp.getAbsolutePath());

            //lay size
            String path = temp.getAbsolutePath();
            boolean isVideo = UtilsKt.isVideoFile(path);
            ISize size = null;
            if (isVideo) {
                size = new SizeFromVideoFile(path);
            } else {
                size = new SizeFromImage(path);
            }
            int width = size.width();
            int height = size.height();

            //tao item
            obj = new MediaObject();
            obj.setLink(sas.getLink());
            obj.setWidth(width);
            obj.setHeight(height);
            obj.setFileTemp(temp);

            //size kb
            long kb = temp.length() / 1024;
            obj.setSizeInKb(kb);


            //xoa file tam
            try {
                File f = new File(tempFile);
                if (f.exists()) {
                    f.deleteOnExit();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            long time = SystemClock.elapsedRealtime() - start;
            MyUtils.log(String.format("image size = %s kb, width = %s, height = %s, time = %s", kb, width, height, time / 1000));

        } catch (IOException e) {
            e.printStackTrace();
        }

        return obj;
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
            AndroidNetworking.post(Webservices.API_URL + "image/getUploadSAS")
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

        description = MyUtils.replaceDescriptionForServer(description);
        AndroidNetworking.post(Webservices.URL_GRAPHQL)
                .addQueryParameter("query", Webservices.UPDATE_IMAGE_ITEM_DESCRIPTION(item.getImageItemId(), description, hashtags, mentions, item.getItemData(), null, 0.0, 0.0))
                .addBodyParameter("Description", description)
                .setOkHttpClient(MyApplication.getClient())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        onUpdateImageItem(item, response);

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
    void onUpdateImageItem(ImageItem item, JSONObject response) {
        try {
            JSONObject objP = response.getJSONObject("data");
            JSONObject obj = objP.getJSONObject("UpdateImageItem");

            double imageItemId = obj.getDouble("ImageItemId");
            if (imageItemId > 0) {
                MyUtils.showToast(context, R.string.update_success);

                //update UI
                MyUtils.updateImageItem(context, item, true);



            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    public void updateDescriptionAndLocation(
            ImageItem item,
            String description,
            String Location,
            double Lat,
            double Long,
            List<String> hashtags,
            List<String> mentions,
            UpdateCompletListener updateCompletListener) {

        ProgressUtils.show(context);
        description = MyUtils.replaceDescriptionForServer(description);
        String query = Webservices.UPDATE_IMAGE_ITEM_DESCRIPTION(item.getImageItemId(), description, hashtags, mentions, item.getItemData(), Location, Lat, Long);
        AndroidNetworking.post(Webservices.URL_GRAPHQL)
                .addQueryParameter("query", query)
                .addBodyParameter("Description", description)
                .setOkHttpClient(MyApplication.getClient())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        onUpdateImageItem(item, response);

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


}
