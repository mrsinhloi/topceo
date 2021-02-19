package com.topceo.crop.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.desmond.squarecamera.myproject.APIConstants;
import com.desmond.squarecamera.myproject.ImageSize;
import com.topceo.R;
import com.topceo.activity.ImageUtil;
import com.topceo.crop.MonitoredActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Util {

	private static int sScreenWidthDP = -1;
	private static int sScreenWidth = -1;
	private static int sScreenHeight = -1;

	public static boolean checkInternetConnection(Context context) {
		boolean b = false;

		if (context != null) {
			ConnectivityManager conMgr = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (conMgr != null) {
				NetworkInfo nw = conMgr.getActiveNetworkInfo();
				if (nw != null
						&& conMgr.getActiveNetworkInfo().isAvailable()
						&& conMgr.getActiveNetworkInfo()
						.isConnectedOrConnecting()) {

					b = true;

				}
			}
		}

		return b;

	}



	/*public static String getAccountGmail(Context context) {
		try {
			Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
			Account[] accounts = AccountManager.get(context).getAccounts();
			for (Account account : accounts) {
				if (emailPattern.matcher(account.name).matches()) {
					return account.name;
				}
			}
		} catch (Exception ex) {
		}

		return "";
	}*/

	public static void updateLanguage(SharedPreferences mPrefs, Context context) {
		try {
			String language = mPrefs.getString(APIConstants.LANGUAGE, APIConstants.LANGUAGE_VIETNAM);
			Locale locale = new Locale(language);
			Locale.setDefault(locale);
			Configuration config = new Configuration();
			config.locale = locale;
			context.getResources().updateConfiguration(config, null);
		} catch (Exception e) {
		}
	}

	public static String EncodeString(String str) {
		byte[] encoded = android.util.Base64.encode(str.getBytes(), 0);
		String encodedString = new String(encoded);
		return encodedString;
	}

	public static String DecodeString(String str) {
		byte[] decoded = android.util.Base64.decode(str.getBytes(), 0);
		String decodedString = new String(decoded);
		return decodedString;
	}

	public static int convertDpToPixel(Context context, int dp) {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		int px = (int) (dp * (metrics.densityDpi / 160f));
		return px;
	}

	public static String getTimePostImage(Date dt, Context context) {
		String result = "";
		try {
			Calendar c = Calendar.getInstance();
			long tmp = (c.getTime().getTime() - dt.getTime());// /(1000*60*60*24);
			if (tmp < (long) (1000 * 60 * 60 * 24L)) {
				if (tmp < (long) (1000 * 60 * 60L)) {
					if (tmp < (long) (1000 * 60L)) {
						result = String.valueOf(Math.abs(tmp / (long) (1000L))) + " " + context.getString(R.string.time_s);
					} else {
						result = String.valueOf(Math.abs(tmp / (long) (1000 * 60L))) + " " + context.getString(R.string.time_mi);
					}
				} else {
					result = String.valueOf(Math.abs(tmp / (long) (1000 * 60 * 60L))) + " " + context.getString(R.string.time_h);
				}
			} else {
				if (tmp < (long) (1000 * 60 * 60 * 24 * 30L)) {
					result = String.valueOf(Math.abs(tmp / (long) (1000 * 60 * 60 * 24L))) + " " + context.getString(R.string.time_d);
				} else if (tmp < (long) (1000 * 60 * 60 * 24 * 365L) && tmp > (long) (1000 * 60 * 60 * 24 * 30L)) {
					result = String.valueOf(Math.abs(tmp / (long) (1000 * 60 * 60 * 24 * 30L))) + " " + context.getString(R.string.time_mo);
				} else {
					result = String.valueOf(Math.abs(tmp / (long) (1000 * 60 * 60 * 24 * 365L))) + " " + context.getString(R.string.time_y);
				}

			}
		} catch (Exception e) {
			result = "n/a";
		}
		return result;
	}

	public static void overrideFonts(final Context context, final View v, Typeface tf) {
		try {
			if (v instanceof ViewGroup) {
				ViewGroup vg = (ViewGroup) v;
				for (int i = 0; i < vg.getChildCount(); i++) {
					View child = vg.getChildAt(i);
					overrideFonts(context, child, tf);
				}
			} else if (v instanceof TextView) {
				((TextView) v).setTypeface(tf);
			} else if (v instanceof Button) {
				((Button) v).setTypeface(tf);
			} else if (v instanceof EditText) {
				((EditText) v).setTypeface(tf);
			}
		} catch (Exception e) {
		}
	}
	

	
	public static String getRandomString(final int sizeOfRandomString)
    {
    	String ALLOWED_CHARACTERS ="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	    final Random random = new Random();
	    final StringBuilder sb=new StringBuilder(sizeOfRandomString);
	    for(int i = 0;i < sizeOfRandomString; ++i)
	    	sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
	    return sb.toString();
    }

    
	

	

	
	public static Bitmap resizeImage(Bitmap baseImage, int newWidth, int newHeight, Bitmap.Config config, boolean doRecycleBase) {
		if (baseImage == null) {
			throw new RuntimeException("baseImage is null");
		}
		/*try {
			System.runFinalization();
    	    Runtime.getRuntime().gc();
			System.gc();
		} catch (Exception e) {
		}*/

		int width = newWidth;

		Bitmap resizedBitmap = null;
		try {
			resizedBitmap = Bitmap.createScaledBitmap(baseImage, width, width, true);
		} catch (Exception e) {
		} catch (OutOfMemoryError e) {
		}

		if (doRecycleBase) {
			baseImage.recycle();
			baseImage = null;
		}
		return resizedBitmap;
	}
	
	public static Bitmap resizeImage(Bitmap baseImage, int newWidth, int newHeight, boolean doRecycleBase) {
		if (baseImage == null) {
			throw new RuntimeException("baseImage is null");
		}
		/*try {
			System.runFinalization();
    	    Runtime.getRuntime().gc();
			System.gc();
		} catch (Exception e) {
		}*/

		Bitmap resizedBitmap = null;
		try {
			resizedBitmap = Bitmap.createScaledBitmap(baseImage, newWidth, newHeight, true);
		} catch (Exception e) {
		} catch (OutOfMemoryError e) {
		}

		if (doRecycleBase) {
			baseImage.recycle();
			baseImage = null;
		}
		return resizedBitmap;
	}
	
	public static int convertToPixels(Context context, int nDP)
	{
	    final float conversionScale = context.getResources().getDisplayMetrics().density;

	    return (int) ((nDP * conversionScale) + 0.5f) ;

	}
	

    
    public static String getContentfromAssets(Context context, String filename) {
    	StringBuilder buf = new StringBuilder();
    	try {
    		String str = "";
			InputStream json = context.getAssets().open(filename);
			BufferedReader in = new BufferedReader(new InputStreamReader(json, "UTF-8"));
			

			while ((str = in.readLine()) != null) {
			  buf.append(str + " ");
			}

			in.close();
		} catch (UnsupportedEncodingException e) {
		} catch (IOException e) {
		}
        
        return buf.toString();
    }
    
    public static int getScreenHeight(Context context) {
        if (sScreenHeight == -1) {
            Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            sScreenHeight = size.y;
        }
        return sScreenHeight;
    }

    public static int getScreenHeightInDp(Context context) {
        if (sScreenHeight == -1) {
            Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            sScreenHeight = size.y;
        }
        return pixelsToDp(context, sScreenHeight);
    }

    public static int getScreenWidthInDp(Context context) {
        if (sScreenWidthDP == -1) {
            Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            sScreenWidthDP = pixelsToDp(context, size.x);
        }
        return sScreenWidthDP;
    }
    public static int getScreenWidth(Context context) {
        if (sScreenWidth <=0) {
            Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            sScreenWidth = size.x;

        }

        return sScreenWidth;
    }
	public static float dpToPixels(Context context, int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static int pixelsToDp(Context context, float pixels) {
        float density = context.getResources().getDisplayMetrics().densityDpi;
        return Math.round(pixels / (density / 160f));
    }
    

    
    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            return null;
        }
    }
    


	public static void howLong(long timeStart, String message){
		long end= System.currentTimeMillis()-timeStart;
		double second=(double) end/1000;
		Log.d("test","time_second="+second+" : "+message);
		if(second>=3){
			Log.e("test","time_second="+second+" : "+message);
		}
	}

	public static void loadImageAvatar(Context context, String url, ImageView view){
		int imgWidth=200;
		//avatar
		Glide.with(context)
				.load(url)
				.diskCacheStrategy(ImageUtil.cacheType)
				.override(imgWidth,imgWidth)
				.placeholder(R.drawable.icon_no_image)
				.into(view);
	}

	public static void loadImageFull(Context context, String url, int imgWidth, ImageView view){
		if(imgWidth==-1) imgWidth= ImageSize.ORIGINAL_WIDTH;
		Glide.with(context)
				.load(url)
				.diskCacheStrategy(ImageUtil.cacheType)
				.override(imgWidth,imgWidth)
				.placeholder(R.drawable.no_media)
				.into(view);
	}

	public static String getAddress(Context context, double lat, double lon) {
		Geocoder geocoder = new Geocoder(context, Locale.getDefault());
		String ret = "";
		try {
			List<Address> addresses = geocoder.getFromLocation(lat,lon, 10);
			if (addresses != null && addresses.size() > 0) {
				Address returnedAddress = addresses.get(0);
				StringBuilder strReturnedAddress = new StringBuilder();//context.getText(R.string.address) + ": "
				/*for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
					if (i == returnedAddress.getMaxAddressLineIndex() - 1) {
						strReturnedAddress.append(returnedAddress.getAddressLine(i));
					} else {
						strReturnedAddress.append(returnedAddress.getAddressLine(i)).append(", ");
					}
				}*/
				strReturnedAddress.append(returnedAddress.getAddressLine(0));
				ret = strReturnedAddress.toString();
			} else {
				ret = "No Address returned!";
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ret = "Can't get Address!";
		}
		return ret;
	}

	public static List<Address> getListAddress(Context context, double lat, double lon, int numberResults) {
		List<Address> addresses=new ArrayList<>();
		Geocoder geocoder = new Geocoder(context, Locale.getDefault());
		try {
			addresses = geocoder.getFromLocation(lat, lon, numberResults);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return addresses;
	}

	public static void startBackgroundJob(MonitoredActivity activity,
										  String title, String message, Runnable job, Handler handler) {
		ProgressDialog dialog = ProgressDialog.show(activity, title, message,
				true, false);
		new Thread(new BackgroundJob(activity, job, dialog, handler)).start();
	}
	private static class BackgroundJob extends
			MonitoredActivity.LifeCycleAdapter implements Runnable {

		private final MonitoredActivity mActivity;
		private final ProgressDialog mDialog;
		private final Runnable mJob;
		private final Handler mHandler;
		private final Runnable mCleanupRunner = new Runnable() {
			public void run() {

				mActivity.removeLifeCycleListener(BackgroundJob.this);
				if (mDialog.getWindow() != null)
					mDialog.dismiss();
			}
		};

		public BackgroundJob(MonitoredActivity activity, Runnable job,
							 ProgressDialog dialog, Handler handler) {

			mActivity = activity;
			mDialog = dialog;
			mJob = job;
			mActivity.addLifeCycleListener(this);
			mHandler = handler;
		}

		public void run() {

			try {
				mJob.run();
			} finally {
				mHandler.post(mCleanupRunner);
			}
		}

		@Override
		public void onActivityDestroyed(MonitoredActivity activity) {
			// We get here only when the onDestroyed being called before
			// the mCleanupRunner. So, run it now and remove it from the queue
			mCleanupRunner.run();
			mHandler.removeCallbacks(mCleanupRunner);
		}

		@Override
		public void onActivityStopped(MonitoredActivity activity) {

			mDialog.hide();
		}

		@Override
		public void onActivityStarted(MonitoredActivity activity) {

			mDialog.show();
		}
	}

}
