package kr.co.lia.photopicker.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;

import androidx.viewpager.widget.PagerAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import kr.co.lia.photopicker.PhotoPickerActivity;
import kr.co.lia.photopicker.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PhotoPagerAdapter extends PagerAdapter {

  private List<String> paths = new ArrayList<>();
  private Context mContext;
  private LayoutInflater mLayoutInflater;


  public PhotoPagerAdapter(Context mContext, List<String> paths) {
    this.mContext = mContext;
    this.paths = paths;
    mLayoutInflater = LayoutInflater.from(mContext);
  }

  @Override public Object instantiateItem(ViewGroup container, int position) {

    View itemView = mLayoutInflater.inflate(R.layout.util_item_pager, container, false);

    ImageView imageView = (ImageView) itemView.findViewById(R.id.iv_pager);

    final String path = paths.get(position);
    final Uri uri;

    final File file = new File(path);
    File uri1 = file;
   /* if (path.startsWith("http")) {
      uri = Uri.parse(path);
    } else {
      Log.d("???", "path  ::  " + path + "\n\t\t\tfile  ::  " + file);
      uri = FileProvider.getUriForFile(mContext, "com.yongbeam.y_photopicker.fileprovider", file);

    }*/

    Glide.with(mContext)
            .load(uri1)
            .thumbnail(0.4f)
            .apply(new RequestOptions()
                    .placeholder(R.color.img_loding_placeholder)
                    .error(R.drawable.ic_place_holder))
            .into(imageView);

    imageView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        if (mContext instanceof PhotoPickerActivity) {
          if (!((Activity) mContext).isFinishing()) {
            ((Activity) mContext).onBackPressed();
          }
        }
      }
    });

    container.addView(itemView);

    return itemView;
  }


  @Override public int getCount() {
    return paths.size();
  }


  @Override public boolean isViewFromObject(View view, Object object) {
    return view == object;
  }


  @Override
  public void destroyItem(ViewGroup container, int position, Object object) {
    container.removeView((View) object);
  }

  @Override
  public int getItemPosition (Object object) { return POSITION_NONE; }

}
