package com.lxy.iamgeloader;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * Created by lxy on 2015/12/3. 
 * SD card cache
 */
public class DiskCache implements ImageCache {

	static String PATH = "/ImageLoader/cache/";
	// 设置文件存储路径
	static String ALBUM_PATH = ImageLoader.ALBUM_PATH + PATH;

	@Override
	public void put(String url, Bitmap bitmap) {
		BufferedOutputStream bos = null;
		try {
			File dirFile = new File(ALBUM_PATH);
			if (!dirFile.exists()) {
				dirFile.mkdirs();
			}

			File myCaptureFile = new File(ALBUM_PATH + MD5Utils.encode(url));
			bos = new BufferedOutputStream(
					new FileOutputStream(myCaptureFile));
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
			Log.i("lxy", "文件缓存成功");
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				bos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public Bitmap get(String url) {
		return BitmapFactory.decodeFile(ALBUM_PATH
				+ MD5Utils.encode(url));
	}

}
