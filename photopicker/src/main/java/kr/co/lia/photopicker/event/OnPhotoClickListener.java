package kr.co.lia.photopicker.event;

import android.view.View;


public interface OnPhotoClickListener {

  void onClick(View v, int position, boolean showCamera);
}
