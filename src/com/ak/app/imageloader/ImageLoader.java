package com.ak.app.imageloader;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Stack;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.app.bookexchange.R;

public class ImageLoader {
	private static MemoryCache memoryCache = new MemoryCache();
	//private static FileCache fileCache;
	private static boolean isImageLoaderClass = true;
	private static ImageLoader imageLoader = null;
	final static int stub_id = R.drawable.ic_launcher;
	private static Bitmap defaultBitmap = null;
	private static Context mContext;

	/**
	 * This is constructor of ImageLoader Class make the background thread low
	 * priority. This way it will not affect the UI performance *
	 * 
	 * @param mContext
	 *            application context
	 */
	private ImageLoader(Context context) {
		mContext = context;

		photoLoaderThread.setPriority(Thread.NORM_PRIORITY - 1);

		//fileCache = new FileCache(mContext);
	}

	/**
	 * This Method is used to implement singleton concept in this class
	 * 
	 * @param context
	 *            application context
	 * @return Class object of this class
	 */
	public static ImageLoader getInstanceImageLoader(Context context) {
		if (isImageLoaderClass) {
			isImageLoaderClass = false;
			imageLoader = new ImageLoader(context);
			return imageLoader;
		}
		return imageLoader;
	}

	/**
	 * This method is used to set image in image view
	 * 
	 * @param url
	 *            from this URL, image will be down-load
	 * @param imageView
	 *            in this view set the image bitmap
	 * @param progressBar
	 *            which is show when image is displaying
	 */

	public static void DisplayImage(String url, ImageView imageView,
			ProgressBar progressBar) {

		if (url == null || url.equals("")) {
			imageView.setVisibility(View.VISIBLE);
			imageView.setImageBitmap(getDefaultBitmap());
			progressBar.setVisibility(View.INVISIBLE);
		} else if (memoryCache.get(url) != null) {
			imageView.setVisibility(View.VISIBLE);
			imageView.setImageBitmap(memoryCache.get(url));
			progressBar.setVisibility(View.INVISIBLE);

		} else {
			queuePhoto(url, imageView, progressBar);
		}
	}

	/**
	 * this method is used to get default bitmap if image url is null
	 * 
	 * @return bitmap
	 */
	private static Bitmap getDefaultBitmap() {
		try{
		defaultBitmap = BitmapFactory.decodeResource(mContext.getResources(),
				stub_id);
		int width = defaultBitmap.getWidth();
		int height = defaultBitmap.getHeight();
		return Bitmap.createScaledBitmap(defaultBitmap, width/2, height/2,
				true);
		}
		catch(OutOfMemoryError e)
		{
			e.printStackTrace();
		}
				catch(Exception e){
			e.printStackTrace();
		}
		return null;
		
		
	}
	

	/**
	 * If bitmap is not downloaded then add in queue
	 * 
	 * @param url
	 *            from this URL, image will be down-load
	 * @param imageView
	 *            in this view set the image bitmap
	 * @param progressBar
	 *            which is show when image is displaying
	 */
	private static void queuePhoto(String url, ImageView imageView,
			ProgressBar progressBar) {
		photosQueue.Clean(imageView);
		PhotoToLoad p = new PhotoToLoad(url, imageView, progressBar);
		synchronized (photosQueue.photosToLoad) {
			photosQueue.photosToLoad.push(p);
			photosQueue.photosToLoad.notifyAll();
		}

		// start thread if it's not started yet
		if (photoLoaderThread.getState() == Thread.State.NEW)
			photoLoaderThread.start();
	}

	/**
	 * this method is used to download bitmap
	 * 
	 * @param url
	 *            from this URL, image will be down-load
	 * @return bitmap
	 */
	private static Bitmap getBitmap(String url) {

		URL myFileUrl = null;
		Bitmap bmImg = null;
		try {
			myFileUrl = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try {
			HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bmImg = BitmapFactory.decodeStream(is);

		} catch (IOException e) {
			e.printStackTrace();
		}
		catch(OutOfMemoryError e)
		{
			e.printStackTrace();
		}
		try
		{
		if (bmImg != null)
		{
		int width = bmImg.getWidth();
		int height = bmImg.getHeight();
		bmImg = Bitmap.createScaledBitmap(bmImg, width,height, false);
		}
		}
		catch(OutOfMemoryError e)
		{
			e.printStackTrace();
		}
		return bmImg;
	}

	/**
	 * This class is used to manage url, imageview and progressBar
	 */
	private static class PhotoToLoad {
		public String url;
		public ImageView imageView;
		public ProgressBar view;

		public PhotoToLoad(String u, ImageView i, ProgressBar v) {
			url = u;
			imageView = i;
			view = v;
		}
	}

	private static PhotosQueue photosQueue = new PhotosQueue();

	private static void stopThread() {
		photoLoaderThread.interrupt();
	}

	/**
	 * this class is used to store list of photoes
	 */
	private static class PhotosQueue {
		private Stack<PhotoToLoad> photosToLoad = new Stack<PhotoToLoad>();

		// removes all instances of this ImageView
		public void Clean(ImageView image) {
			for (int j = 0; j < photosToLoad.size();) {
				if (photosToLoad.get(j).imageView == image)
					photosToLoad.remove(j);
				else
					++j;
			}
		}
	}

	/**
	 * This thread is used to set photoes in imageView
	 */
	private static class PhotosLoader extends Thread {
		@Override
		public void run() {
			try {
				while (true) {
					// thread waits until there are any images to load in the
					// queue
					if (photosQueue.photosToLoad.size() == 0) {
						synchronized (photosQueue.photosToLoad) {
							photosQueue.photosToLoad.wait();
						}
					}
					if (photosQueue.photosToLoad.size() != 0) {
						PhotoToLoad photoToLoad;
						synchronized (photosQueue.photosToLoad) {
							photoToLoad = photosQueue.photosToLoad.pop();
						}
						Bitmap bmp;
						try {
							bmp = getBitmap(photoToLoad.url);
							memoryCache.put(photoToLoad.url, bmp);
							if (((String) photoToLoad.imageView.getTag())
									.equals(photoToLoad.url)) {
								BitmapDisplayer bd = new BitmapDisplayer(bmp,
										photoToLoad.imageView, photoToLoad.view);
								Activity a = (Activity) photoToLoad.imageView
										.getContext();
								a.runOnUiThread(bd);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					if (Thread.interrupted())
						break;
				}
			} catch (InterruptedException e) {
				// allow thread to exit
			}
		}
	}

	

	private static PhotosLoader photoLoaderThread = new PhotosLoader();

	/**
	 * Used to display bitmap in the UI thread
	 */
	private static class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		ImageView imageView;
		ProgressBar view;

		public BitmapDisplayer(Bitmap b, ImageView i, ProgressBar v) {
			bitmap = b;
			imageView = i;
			view = v;
		}

		public void run() {
			if (bitmap != null) {
				imageView.setVisibility(View.VISIBLE);
				imageView.setImageBitmap(bitmap);
				view.setVisibility(View.INVISIBLE);
			}
		}
	}

	/**
	 * this function is used to clear all stored data
	 */
	public static void clearCache() {
		stopThread();

		// clear memory cache
		memoryCache.clear();

		// clear SD cache
		/*if (fileCache != null) {
			fileCache.clear();
		}*/
	}
}
