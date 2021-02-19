package com.topceo.ads;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class AdUtils {

	public static String OBJECT_ADS = "OBJECT_ADS";

	public static boolean writeListToFile(Context activity, Object obj, String fileName){
		boolean isSuccess=false;

		File myfile = activity.getFileStreamPath(fileName);
		try {
			if(myfile.exists() || myfile.createNewFile()){
				FileOutputStream fos = activity.openFileOutput(fileName, activity.MODE_PRIVATE);
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(obj);
				fos.close();
				isSuccess=true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return isSuccess;
	}

	public static Object loadListFromFile(Context activity, String fileName) {
		Object obj=null;

		if(!TextUtils.isEmpty(fileName)){
			File myfile = activity.getFileStreamPath(fileName);
			try {
				if(myfile.exists()){
					FileInputStream fis = activity.openFileInput(fileName);
					ObjectInputStream ois = new ObjectInputStream(fis);
					obj=ois.readObject();
					fis.close();
				}else {
					return null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}


		return obj;
	}
}
