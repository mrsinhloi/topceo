package com.topceo.group;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.topceo.R;
import com.topceo.activity.MH02_PhotoDetailActivity;
import com.topceo.config.MyApplication;
import com.topceo.fragments.Fragment_1_Home_User;
import com.topceo.fragments.Fragment_2_Explorer;
import com.topceo.hashtag.HashTagActivity;
import com.topceo.objects.image.ImageItem;
import com.topceo.objects.image.ImageSize;
import com.topceo.objects.image.Item;
import com.topceo.objects.image.MediaObject;
import com.topceo.post.UploadImageListener;
import com.topceo.profile.Fragment_5_User_Profile_Grid;
import com.topceo.services.ReturnResult;
import com.topceo.services.SasChild;
import com.topceo.services.SasParent;
import com.topceo.services.Webservices;
import com.topceo.utils.MyUtils;
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
import java.net.URI;
import java.util.ArrayList;

import me.echodev.resizer.Resizer;

public class GroupCoverUtils {
    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    private Context context;
    public GroupCoverUtils(Context context) {
        this.context = context;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    //UPLOAD IMAGE
    private UploadImageListener imageListener;
    public void uploadImageToServer(String guid, Uri uri, UploadImageListener imageListener) {
        if (uri != null) {
            this.imageListener = imageListener;

            //replace with real path
            if (uri.toString().contains("content://")) {
                uri = FileUtils.parseUriImage(uri, context);
            }


            //chuan bi lay duong dan sas truoc khi upload anh len azure
//            ProgressUtils.show(context);
            ANRequest.PostRequestBuilder request = AndroidNetworking.post(Webservices.URL + "group/getUploadSAS")
                    .addBodyParameter("GroupGUID", guid)
                    .setOkHttpClient(MyApplication.getClient());

            String imgPath = uri.getPath();
            if (imgPath != null) {
                File file = new File(imgPath);
                final String extension = MyUtils.getFileExtension(file);
                request.addBodyParameter("Extension", extension);
            }

            final Uri mUri = uri;
            request.build().getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {

                    ReturnResult result = Webservices.parseJson(response, SasParent.class, false);
//                    ProgressUtils.hide();

                    if (result != null) {
                        if (result.getErrorCode() == ReturnResult.SUCCESS) {
                            //co sas thi upload len server
                            SasParent sas = (SasParent) result.getData();
                            if (sas != null) {
                                //Upload tung kich thuoc hinh len server
                                uploadImage(sas, mUri, guid);
                            }

                        } else {
                            MyUtils.showToast(context, result.getErrorMessage());
                        }
                    }
                }

                @Override
                public void onError(ANError ANError) {
//                    ProgressUtils.hide();
                }
            });
        } else {
            MyUtils.showToast(context, R.string.not_found);
        }
    }

    private void uploadImage(SasParent sasParent, Uri uri, String guid) {
        if (uploadTask == null || uploadTask.getStatus() == AsyncTask.Status.FINISHED) {
            uploadTask = new UploadImageTask(sasParent, uri, guid);
            uploadTask.execute();
        }
    }

    private UploadImageTask uploadTask;

    private class UploadImageTask extends AsyncTask<String, Void, Boolean> {

        SasParent sasParent;
        Uri uri;
        String guid;

        public UploadImageTask(SasParent sasParent, Uri uri, String guid) {
            this.sasParent = sasParent;
            this.uri = uri;
            this.guid = guid;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            itemContent.clear();
        }

        private ArrayList<Item> itemContent = new ArrayList<>();

        @Override
        protected Boolean doInBackground(String... strings) {
            String imgPath = uri.getPath();
            SasParent parent = sasParent;
            //correct images
            //neu xoay thi xoay lai va luu vao bo nho, khac 0 thi se xoay lai
//                int rotation = MyUtils.getRotationForImage(imgPath);
//                imgPath = MyUtils.saveBitmap(MyUtils.resizeImage(imgPath, rotation, ImageSize.ORIGINAL_WIDTH, ImageSize.ORIGINAL_HEIGHT), imgPath);

            //upload 1 item
            Item item = new Item();
            item.setItemType(Item.TYPE_IMAGE);

            //upload origin
            File fileTemp = null;
            SasChild large = parent.getOriginal();
            if (large != null) {
                MediaObject obj = uploadImageToAzure(imgPath, large, ImageSize.ORIGINAL_HEIGHT);
                if (obj != null) {
                    item.setLarge(obj);
                    fileTemp = obj.getFileTemp();
                }
            }

            //upload medium
                /*SasChild medium = parent.getMedium();
                if (medium != null) {
                    MediaObject obj = uploadImageToAzure(fileTemp.getAbsolutePath(), medium, ImageSize.MEDIUM_HEIGHT);
                    if (obj != null) {
                        item.setMedium(obj);
                        fileTemp = obj.getFileTemp();
                    }
                }*/

            //upload small
            SasChild small = parent.getSmall();
            if (small != null) {
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
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                imageListener.onUploadImageSuccess(guid, itemContent);
            } else {
                MyUtils.showToast(context, R.string.uploadfail);
            }
//            ProgressUtils.hide();
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

            String location = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES).getAbsolutePath();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                location = context.getExternalCacheDir().getAbsolutePath();
            }
            File temp = new Resizer(context)
                    .setTargetLength(targetWidth)
                    .setQuality(80)
                    .setOutputFormat("JPEG")
                    .setOutputFilename("resized_image")
                    .setOutputDirPath(location)
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


    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    void onUpdateImageItem(ImageItem item, JSONObject response) {
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


}
