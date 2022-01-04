package kr.co.lia.photopicker.event;


import kr.co.lia.photopicker.entity.Photo;

public interface OnItemCheckListener {

  /***
   *
   * @param position Select Image postion
   * @param path     Select image path
   *@param isCheck   Image status
   * @param selectedItemCount  Select image count
   * @return enable check
   */
  boolean OnItemCheck(int position, Photo path, boolean isCheck, int selectedItemCount);

}
