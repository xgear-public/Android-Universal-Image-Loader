package com.nostra13.example.universalimageloader;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.disc.impl.DefaultDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.FIFOLimitedCache;
import com.nostra13.universalimageloader.imageloader.DisplayImageOptions;
import com.nostra13.universalimageloader.imageloader.ImageLoader;
import com.nostra13.universalimageloader.imageloader.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.imageloader.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.StorageUtils;

/** Activity for {@link ImageLoader} testing */
public class UILActivity extends ListActivity {

	public ImageLoader imageLoader;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
		File cacheDir = StorageUtils.getCacheDirectory(getApplicationContext(), "UniversalImageLoader/Cache");

		// This configuration tuning is full. You don't have to tune every option. 
		// You may tune some of them or create default configuration by 
		//  ImageLoaderConfiguration.createDefault()
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
			.maxImageWidthForMemoryCache(displayMetrics.widthPixels)
			.maxImageHeightForMemoryCache(displayMetrics.heightPixels)
			.httpConnectTimeout(5000)
			.httpReadTimeout(30000)
			.threadPoolSize(5)
			.memoryCache(new FIFOLimitedCache(2000000))
			.discCache(new DefaultDiscCache(cacheDir))
			.defaultDisplayImageOptions(DisplayImageOptions.createSimple())
			.build();
		imageLoader = ImageLoader.getInstance(config);

		ListView listView = getListView();
		listView.setAdapter(new ItemAdapter());
	}

	@Override
	protected void onDestroy() {
		imageLoader.stop();
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.item_clear_memory_cache:
				imageLoader.clearMemoryCache();
				return true;
			case R.id.item_clear_disc_cache:
				imageLoader.clearDiscCache();
				return true;
			default:
				return false;
		}
	}

	class ItemAdapter extends BaseAdapter {

		private List<String> imageUrls;

		private ItemAdapter() {
			String[] heavyImages = getResources().getStringArray(R.array.heavy_images);
			String[] lightImages = getResources().getStringArray(R.array.light_images);

			imageUrls = new ArrayList<String>(heavyImages.length + lightImages.length);
			imageUrls.addAll(Arrays.asList(heavyImages));
			imageUrls.addAll(Arrays.asList(lightImages));
		}

		public int getCount() {
			return imageUrls.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public class ViewHolder {
			public TextView text;
			public ImageView image;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			View view = convertView;
			final ViewHolder holder;
			if (convertView == null) {
				view = getLayoutInflater().inflate(R.layout.list_item, null);
				holder = new ViewHolder();
				holder.text = (TextView) view.findViewById(R.id.text);
				holder.image = (ImageView) view.findViewById(R.id.image);
				view.setTag(holder);
			} else
				holder = (ViewHolder) view.getTag();

			holder.text.setText("Item " + position);
			
			// Full "displayImage" method using.
			// You can use simple call:
			//  imageLoader.displayImage(imageUrls.get(position), holder.image);
			// instead of.
			DisplayImageOptions options = new DisplayImageOptions.Builder().showStubImage(R.drawable.stub_image).cacheInMemory().cacheOnDisc().build();
			imageLoader.displayImage(imageUrls.get(position), holder.image, options, new ImageLoadingListener() {
				@Override
				public void onLoadingStarted() {
					holder.text.setText("...loading...");
				}
				@Override
				public void onLoadingComplete() {
					holder.text.setText("Item " + position);
				}
			});

			return view;
		}
	}
}