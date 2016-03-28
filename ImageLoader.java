package com.lxy.iamgeloader;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.ImageView;

/**
 * Created by lxy on 2015/11/30.
 */
public class ImageLoader {
	
	/**
	 * how to use it ?
	 */
	
	
//	 1.first: settings
//	 set cache to ImageLoader , 
//	 for example :
//	 ImageLoader.getInstance().setImageCache(new DoubleCache());(MemoryCache or DiskCache or DoubleCache )
//	 and you can custom cache but must implements ImageCache interface;
	
//	 2.second: use it
//	 display image: 
//	 ImageLoader.getInstance().displayImage(mUrl, view);
	
//	 download image to local : ps:this method will return string file path when end of load in local
//	 ImageLoader.getInstance().downloadImage(
//			mUrl, 
//			filePath, 
//			iamgeName,
//			new LoadImageLocalRequst() {
//
//				@Override
//				public void loadSuccess() {
//					Message msg = mHandler.obtainMessage();
//					msg.what = MARK_1;
//					mHandler.sendMessage(msg);
//				}
//
//				@Override
//				public void loadFailure() {
//					Message msg = mHandler.obtainMessage();
//					msg.what = MARK_2;
//					mHandler.sendMessage(msg);
//				}
//			});

	public static String IMAGELOADER_JPEG = ".jpg";

	public static String IMAGELOADER_PNG = ".png";

	public static String ALBUM_PATH = Environment.getExternalStorageDirectory()
			.getPath();

	private Bitmap bitmapLocal;

	// 线程池，线程数量为CPU数量
	private ExecutorService mExecutorService = Executors
			.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

	// 默认使用内存缓存
	public ImageCache mCache = new MemoryCache();
	

	static volatile ImageLoader imageLoader;
	
	public static ImageLoader getInstance(){
		if (imageLoader == null) {
			Class arg0 = ImageLoader.class;
			synchronized (arg0) {
				if (imageLoader==null) {
					imageLoader = new ImageLoader();
				}
			}
		}
		return imageLoader;
	}
	private ImageLoader(){
	}
	
	// 设置ImageLoader缓存策略
	public void setImageCache(ImageCache mImageCache) {
		this.mCache = mImageCache;
	}

	public void displayImage(final String imageURL, final ImageView mView) {
		Bitmap bitmap = mCache.get(imageURL);
		if (bitmap != null) {
			mView.setImageBitmap(bitmap);
			return;
		}
		submitLoadRequest(imageURL, mView);
	}

	// 没有缓存的情况下，交给线程池下载
	private void submitLoadRequest(final String imageURL, final ImageView mView) {
		mView.setTag(imageURL);
		mExecutorService.submit(new Runnable() {
			@Override
			public void run() {
				final Bitmap bitmap = downloadImage(imageURL);
				if (bitmap == null) {
					return;
				}
				if (mView.getTag().equals(imageURL)) {
					Activity activity = (Activity) mView.getContext();

					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							mView.setImageBitmap(bitmap);
						}
					});
				}
				mCache.put(imageURL, bitmap);
			}
		});
	}

	// 从网络上获取图片流数据，并转换为Bitmap
	private Bitmap downloadImage(String imageURL) {
		Bitmap bitmap = null;
		try {
			
			URL url = new URL(imageURL);
			
			// 启动请求
			final HttpURLConnection conn = (HttpURLConnection) url
					.openConnection();
			// 获取输入流并写入bitmap
			
			bitmap = BitmapFactory.decodeStream(conn.getInputStream());
			// 关闭请求
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bitmap;
	}

	public String downloadImage(final String imageURL,
			final String picturePath, final String pictureName, final LoadImageLocalRequst mloadRequest) {
		if (TextUtils.isEmpty(imageURL)) {
			return null;
		}
		if (TextUtils.isEmpty(picturePath)) {
			return null;
		}
		if (TextUtils.isEmpty(pictureName)) {
			return null;
		}
		if (mloadRequest == null) {
			return null;
		}
		if (mCache.get(imageURL) != null) {
			loadImageLocal(picturePath, pictureName,
					mCache.get(imageURL), mloadRequest);

		} else {
			mExecutorService.submit(new Runnable() {
				@Override
				public void run() {
					bitmapLocal = downloadImage(imageURL);
					if (bitmapLocal != null) {
						loadImageLocal(picturePath, pictureName,
								bitmapLocal, mloadRequest);
					} else {
						mloadRequest.loadFailure();
					}
				}
			});
		}
		return picturePath + pictureName + IMAGELOADER_JPEG;
	}

	private void loadImageLocal(String picturePath, String pictureName, Bitmap bitmap,
			final LoadImageLocalRequst mloadRequest) {

		try {
			File dirFile = new File(picturePath);
			if (!dirFile.exists()) {
				dirFile.mkdirs();
			}

			File myCaptureFile = new File(picturePath + pictureName
					+ IMAGELOADER_JPEG);
			if (myCaptureFile.exists()) {
				mloadRequest.loadSuccess();
				return;
			}

			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();

			mloadRequest.loadSuccess();
			
		} catch (Exception e) {
			e.printStackTrace();
			mloadRequest.loadFailure();
		} 
	}

	public interface LoadImageLocalRequst {
		void loadSuccess();

		void loadFailure();
	}
}
