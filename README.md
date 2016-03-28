# CustomImageLoader
CustomImageLoader

how to use it ?
	
1.first: settings

	 set cache to ImageLoader , for example :
	 ImageLoader.getInstance().setImageCache(new DoubleCache());(MemoryCache or DiskCache or DoubleCache )
	 and you can custom cache but must implements ImageCache interface;

2.second: use it

	 display image: 
	 ImageLoader.getInstance().displayImage(mUrl, view);

	 download image to local : ps:this method will return string file path when end of load in local
	 ImageLoader.getInstance().downloadImage(
			mUrl, 
			filePath, 
			iamgeName,
			new LoadImageLocalRequst() {

				@Override
				public void loadSuccess() {
					Message msg = mHandler.obtainMessage();
					msg.what = MARK_1;
					mHandler.sendMessage(msg);
				}

				@Override
				public void loadFailure() {
					Message msg = mHandler.obtainMessage();
					msg.what = MARK_2;
					mHandler.sendMessage(msg);
				}
			});
