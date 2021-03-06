package kr.co.lia.photopicker.adapter;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;


import kr.co.lia.photopicker.R;
import kr.co.lia.photopicker.entity.Photo;
import kr.co.lia.photopicker.entity.PhotoDirectory;
import kr.co.lia.photopicker.event.OnItemCheckListener;
import kr.co.lia.photopicker.event.OnPhotoClickListener;
import kr.co.lia.photopicker.utils.MediaStoreHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PhotoGridAdapter extends SelectableAdapter<PhotoGridAdapter.PhotoViewHolder> {

    private LayoutInflater inflater;

    private Context mContext;

    private OnItemCheckListener onItemCheckListener = null;
    private OnPhotoClickListener onPhotoClickListener = null;
    private View.OnClickListener onCameraClickListener = null;

    public final static int ITEM_TYPE_CAMERA = 100;
    public final static int ITEM_TYPE_PHOTO = 101;

    private boolean hasCamera = true;
    private boolean isPreview = true;
    private boolean mIsCheckBoxOnly = false;

    public PhotoGridAdapter(Context mContext, List<PhotoDirectory> photoDirectories, boolean isCheckBoxOnly) {
        this.photoDirectories = photoDirectories;
        this.mContext = mContext;
        this.mIsCheckBoxOnly = isCheckBoxOnly;
        inflater = LayoutInflater.from(mContext);
    }


    @Override
    public int getItemViewType(int position) {
        return (showCamera() && position == 0) ? ITEM_TYPE_CAMERA : ITEM_TYPE_PHOTO;
    }


    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.util_item_photo, parent, false);
        PhotoViewHolder holder = new PhotoViewHolder(itemView);
        if (viewType == ITEM_TYPE_CAMERA) {
            holder.vSelected.setVisibility(View.GONE);
            holder.ivPhoto.setScaleType(ImageView.ScaleType.CENTER);
            holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onCameraClickListener != null) {
                        onCameraClickListener.onClick(view);
                    }
                }
            });
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(final PhotoViewHolder holder, int position) {

        if (getItemViewType(position) == ITEM_TYPE_PHOTO) {

            List<Photo> photos = getCurrentPhotos();
            final Photo photo;

            if (showCamera()) {
                photo = photos.get(position - 1);
            } else {
                photo = photos.get(position);
            }
//????????? ?????? ??? ????????? ????????? ?????? ??????
//      Uri uri = FileProvider.getUriForFile(mContext, "com.yongbeam.y_photopicker.fileprovider", new File(photo.getPath()));
            File uri = new File(photo.getPath());
            Glide.with(mContext)
                    .load(uri)
                    .apply(new RequestOptions()
                            .placeholder(R.color.img_loding_placeholder)
                            .error(R.color.image_loading_error_color)
                            .centerCrop())
                    .into(holder.ivPhoto);


            final boolean isChecked = isSelected(photo);

            holder.vSelected.setSelected(isChecked);
            holder.ivPhoto.setSelected(isChecked);

            //?????? ?????? ??????
            if (showPreview()) {
                holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (mIsCheckBoxOnly) {
                            boolean isEnable = true;
                            if (onItemCheckListener != null) {
                                isEnable = onItemCheckListener.OnItemCheck(holder.getAdapterPosition(), photo, isChecked, getSelectedPhotos().size());
                            }
                            if (isEnable) {
                                toggleSelection(photo);
                                notifyItemChanged(holder.getAdapterPosition());
                            }
                        }
                        //????????? ?????? ??? ??????(????????? ?????? ??????)
                        else {
                            if (onPhotoClickListener != null) {
                                onPhotoClickListener.onClick(view, holder.getAdapterPosition(), showCamera());
                            }
                        }
                    }
                });
            }else {
                holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        boolean isEnable = true;

                        if (onItemCheckListener != null) {
                            isEnable = onItemCheckListener.OnItemCheck(holder.getAdapterPosition(), photo, isChecked, getSelectedPhotos().size());
                        }
                        if (isEnable) {
                            toggleSelection(photo);
                            notifyItemChanged(holder.getAdapterPosition());
                        }
                    }
                });
            }

            holder.vSelected.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                   boolean isEnable = true;

                    if (onItemCheckListener != null) {
                        isEnable = onItemCheckListener.OnItemCheck(holder.getAdapterPosition(), photo, isChecked, getSelectedPhotos().size());
                    }
                    if (isEnable) {
                        toggleSelection(photo);
                        notifyItemChanged(holder.getAdapterPosition());
                    }
                }
            });

        } else {
            holder.ivPhoto.setImageResource(R.drawable.camera);
        }
    }


    @Override
    public int getItemCount() {
        int photosCount =
                photoDirectories.size() == 0 ? 0 : getCurrentPhotos().size();
        if (showCamera()) {
            return photosCount + 1;
        }
        return photosCount;
    }


    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivPhoto;
        private View vSelected;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            ivPhoto = (ImageView) itemView.findViewById(R.id.iv_photo);
            vSelected = itemView.findViewById(R.id.v_selected);
        }
    }


    public void setOnItemCheckListener(OnItemCheckListener onItemCheckListener) {
        this.onItemCheckListener = onItemCheckListener;
    }


    public void setOnPhotoClickListener(OnPhotoClickListener onPhotoClickListener) {
        this.onPhotoClickListener = onPhotoClickListener;
    }


    public void setOnCameraClickListener(View.OnClickListener onCameraClickListener) {
        this.onCameraClickListener = onCameraClickListener;
    }


    public ArrayList<String> getSelectedPhotoPaths() {
        ArrayList<String> selectedPhotoPaths = new ArrayList<>(getSelectedItemCount());

        for (Photo photo : selectedPhotos) {
            selectedPhotoPaths.add(photo.getPath());
        }

        return selectedPhotoPaths;
    }


    public void setShowCamera(boolean hasCamera) {
        this.hasCamera = hasCamera;
    }

    public void setShowPreview(boolean isPreview) {
        this.isPreview = isPreview;
    }

    public boolean showCamera() {
        return (hasCamera && currentDirectoryIndex == MediaStoreHelper.INDEX_ALL_PHOTOS);
    }

    public boolean showPreview() {
        return isPreview ;
    }
}
