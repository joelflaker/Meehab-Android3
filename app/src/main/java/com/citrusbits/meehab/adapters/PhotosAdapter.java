/**
 * 
 */
package com.citrusbits.meehab.adapters;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.citrusbits.meehab.R;
import com.citrusbits.meehab.constants.Consts;
import com.citrusbits.meehab.utils.UtilityClass;
import com.squareup.picasso.Picasso;

/**
 * @author Qamar
 *
 */
public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.ImageViewHolder>{

	public static class ImageViewHolder extends RecyclerView.ViewHolder {
		protected final ImageView play;
		protected ImageView photo;

		ImageViewHolder(View itemView) {
			super(itemView);
			photo = (ImageView)itemView.findViewById(R.id.photo);
			play = (ImageView)itemView.findViewById(R.id.play);
		}
	}

	private ArrayList<String> urls = new ArrayList<String>();
	private Context context;
	private PhotoClickListener clickListener;
	private boolean isPhotoUrls;

	public PhotosAdapter(Context c, List<String> urls,PhotoClickListener l, boolean videoUrls){
		this.clickListener = l;
		this.context = c;
		this.urls = (ArrayList<String>) urls;
		this.isPhotoUrls = videoUrls;
	}

	@Override
	public int getItemCount() {
		return urls.size();
	}

	@Override
	public void onAttachedToRecyclerView(RecyclerView recyclerView) {
		super.onAttachedToRecyclerView(recyclerView);
	}

	@Override
	public void onBindViewHolder(ImageViewHolder holder,final int pos) {
		String url = urls.get(pos);

		if(isPhotoUrls){
			//image
			if(!TextUtils.isEmpty(url)) {
				Picasso.with(context).load(url)
						.placeholder(R.drawable.loading_img)
						.error(R.drawable.loading_img)
						.fit()
						.into(holder.photo);
			}else {
				Picasso.with(context).load(R.drawable.loading_img)
						.into(holder.photo);
			}

		}else{
			if(!TextUtils.isEmpty(url)){
				AsyncTaskCompat.executeParallel(new AsyncTask<Object, Void, Bitmap>() {
					WeakReference<ImageView> image;

					@Override
					protected Bitmap doInBackground(Object... params) {
						image = new WeakReference<>((ImageView) params[0]);
						String url = (String) params[1];
						return UtilityClass.snapFromUrl(url);
//					return ThumbnailUtils.createVideoThumbnail(url, MediaStore.Video.Thumbnails.MINI_KIND);
					}

					@Override
					protected void onPostExecute(Bitmap result) {
						//				super.onPostExecute(result);
						if (image.get() != null && result != null) {
							image.get().setImageBitmap(result);//UtilityClass.resize(result,rowHeight,rowHeight));
						}
					}
				}, new Object[]{holder.photo, url});
			}else {
				Picasso.with(context).load(R.drawable.loading_img)
						.into(holder.photo);
			}
		}

		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				clickListener.onPhotoClick(pos);

			}
		});
		//		holder.itemView.setPadding(pos == 0 ? 0 : context.getResources().getDimensionPixelSize(R.dimen.wall_margin),
		//				context.getResources().getDimensionPixelSize(R.dimen.wall_margin),0,
		//				context.getResources().getDimensionPixelSize(R.dimen.wall_margin));
	}

	@Override
	public ImageViewHolder onCreateViewHolder(ViewGroup viewGroup, int arg1) {
		View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.grid_item_photo, viewGroup, false);
		ImageViewHolder holder = new ImageViewHolder(v);
		if(!isPhotoUrls){
			holder.play.setVisibility(View.VISIBLE);
		}
		return holder;
	}

	public static interface PhotoClickListener {
		public void onPhotoClick(int position);
	}

}
