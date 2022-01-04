package kr.co.lia.photopicker;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import kr.co.lia.photopicker.R;
import kr.co.lia.photopicker.entity.Photo;
import kr.co.lia.photopicker.event.OnItemCheckListener;
import kr.co.lia.photopicker.fragment.ImagePagerFragment;
import kr.co.lia.photopicker.fragment.PhotoPickerFragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.widget.Toast.LENGTH_LONG;

public class PhotoPickerActivity extends AppCompatActivity {
    private final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 7;
    private final int MY_PERMISSIONS_REQUEST_CAMERA = 8;

    private PhotoPickerFragment pickerFragment;
    private ImagePagerFragment imagePagerFragment;

    public final static String EXTRA_MAX_COUNT = "MAX_COUNT";
    public final static String EXTRA_SHOW_CAMERA = "SHOW_CAMERA";
    public final static String EXTRA_SHOW_GIF = "SHOW_GIF";
    public final static String EXTRA_CHECK_BOX_ONLY = "CHECK_BOX_ONLY";
    public final static String KEY_SELECTED_PHOTOS = "SELECTED_PHOTOS";
    public final static String EXTRA_MAX_GRIDE_ITEM_COUNT = "MAX_GRIDE_IMAGE_COUNT";
    public final static String EXTRA_SET_PHOTO_QUALITY = "SET_PHOTO_QUALITY"; //2020-03-24 최다영 추가

    private boolean showCamera = true;
    private int m_sQuality = 0;


    private MenuItem menuDoneItem;

    public final static int DEFAULT_MAX_COUNT = 9;
    public final static int DEFAULT_MAX_GRIDE_ITEM_COUNT = 3;

    private int maxCount = DEFAULT_MAX_COUNT;
    public int maxGrideItemCount = DEFAULT_MAX_GRIDE_ITEM_COUNT;
    public boolean isCheckBoxOnly = false;

    /**
     * to prevent multiple calls to inflate menu
     */
    private boolean menuIsInflated = false;
    private boolean showGif = false;

    //2020-03-24 최다영 추가
    private String m_saveImagePath;
    private ArrayList<String> resultList = new ArrayList<String>();
    private ProgressDialog dialog;
    private ArrayList<String> selectedPhotos;


    String gps_data ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.util_activity_photo_picker);

        //아래 패키지명이 아닌 경우 종료
        /*if (!getApplication().getPackageName().equals("kr.co.doortodoor.hms")||
                getApplication().getPackageName().equals("kr.co.doortodoor.hms.dev")||
                getApplication().getPackageName().equals("kr.co.doortodoor.nplus")||
                getApplication().getPackageName().equals("kr.co.doortodoor.nplus.dev")){
            finish();
        }*/
        init();


        String cachePath = Environment.getExternalStorageDirectory().getAbsolutePath();

        File file = new File(getCacheDir().toString()); // 내부저장소로 변경

        //m_saveImagePath = cachePath + File.separator + "selectPhoto";
        m_saveImagePath = file.getPath() + File.separator + "selectPhoto";
        File fDocument = new File(m_saveImagePath);
        if (fDocument.exists() == false) {
            fDocument.mkdirs();
        }
        //checkExternalStoragePermission();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void checkExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
        } else {
            checkCameraPermission();
        }
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
            }
        } else {
            init();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkCameraPermission();
                } else {
                    Toast.makeText(PhotoPickerActivity.this, "You do not have read permissions.", Toast.LENGTH_LONG).show();
                    finish();
                }
                return;
            }

            case MY_PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    init();
                } else {
                    Toast.makeText(PhotoPickerActivity.this, "There is no camera permissions.", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private void init() {
        showCamera = getIntent().getBooleanExtra(EXTRA_SHOW_CAMERA, false);
        isCheckBoxOnly = getIntent().getBooleanExtra(EXTRA_CHECK_BOX_ONLY, false);
        maxGrideItemCount = getIntent().getIntExtra(EXTRA_MAX_GRIDE_ITEM_COUNT, DEFAULT_MAX_GRIDE_ITEM_COUNT);
        m_sQuality = getIntent().getIntExtra(EXTRA_SET_PHOTO_QUALITY, 0);
        showGif = getIntent().getBooleanExtra(EXTRA_SHOW_GIF, false);
        setShowGif(showGif);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.y_photopicker_image_select_title);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            actionBar.setElevation(25);
        }

        maxCount = getIntent().getIntExtra(EXTRA_MAX_COUNT, DEFAULT_MAX_COUNT);

        setPickerFragment();
    }

    private void setPickerFragment() {

        if (pickerFragment == null) {
            pickerFragment = (PhotoPickerFragment) getSupportFragmentManager().findFragmentById(R.id.photoPickerFragment);

            pickerFragment.getPhotoGridAdapter().setShowCamera(showCamera);
            pickerFragment.getPhotoGridAdapter().setOnItemCheckListener(new OnItemCheckListener() {
                @Override
                public boolean OnItemCheck(int position, Photo photo, final boolean isCheck, int selectedItemCount) {

                    int total = selectedItemCount + (isCheck ? -1 : 1);

                    menuDoneItem.setEnabled(total > 0);

                    if (maxCount <= 1) {
                        List<Photo> photos = pickerFragment.getPhotoGridAdapter().getSelectedPhotos();
                        if (!photos.contains(photo)) {
                            photos.clear();
                            pickerFragment.getPhotoGridAdapter().notifyDataSetChanged();
                        }
                        return true;
                    }

                    if (total > maxCount) {
                        Toast.makeText(getActivity(), getString(R.string.y_photopicker_over_max_count_tips, maxCount),
                                LENGTH_LONG).show();
                        return false;
                    }
                    menuDoneItem.setTitle(getString(R.string.y_photopicker_done_with_count, total, maxCount));
                    return true;
                }
            });
        } else {
            pickerFragment.getPhotoGridAdapter().notifyDataSetChanged();
        }
    }

    /**
     * Overriding this method allows us to run our exit animation first, then exiting
     * the activity when it complete.
     */
    @Override
    public void onBackPressed() {
  /*      if (imagePagerFragment != null && imagePagerFragment.isVisible()) {// view 투명해지는 증상 있던 곳
            imagePagerFragment.runExitAnimation(new Runnable() {
                public void run() {
                    if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                        getSupportFragmentManager().popBackStack();
                    }
                }
            });
        } else {
            super.onBackPressed();
        }*/

        super.onBackPressed();
    }


    public void addImagePagerFragment(ImagePagerFragment imagePagerFragment) {
        this.imagePagerFragment = imagePagerFragment;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, this.imagePagerFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!menuIsInflated) {
            getMenuInflater().inflate(R.menu.menu_picker, menu);
            menuDoneItem = menu.findItem(R.id.done);
            menuDoneItem.setEnabled(false);
            menuIsInflated = true;

            return true;
        }
        return false;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            return true;
        }

        if (item.getItemId() == R.id.done) {

            Intent intent = new Intent();
            selectedPhotos = pickerFragment.getPhotoGridAdapter().getSelectedPhotoPaths();
            intent.putStringArrayListExtra(KEY_SELECTED_PHOTOS, selectedPhotos);
            //Log.d("???", "selectedPhotos : " + selectedPhotos);


            MakeNewPhoto makeNewPhoto = new MakeNewPhoto();
            makeNewPhoto.execute();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void backPress() {

        //생성된 ArrayList경로 mainActivity로 넘기기
        Intent i = new Intent();
        i.putExtra("path", resultList);
        setResult(RESULT_OK, i);


        finish();

        super.onBackPressed();
    }

    //2020-03-24 최다영 추가
    //해상도 낮춘 이미지 파일생성 -> 해당 이미지 경로 ArrayList에 넣기
    public List MakeNewPhotoList(ArrayList list) throws IOException {
        ArrayList<String> selectList = list;
        int selectCount = selectList.size();
        String imagePath;
        String fileName;
        int dstSize = 4;// 1 보다 큰 값, N일때는 1/N
        int resizeH;
        int resizeW;


        long time = System.currentTimeMillis();
        SimpleDateFormat dayTime = new SimpleDateFormat("yyyyMMddHHmmss");
        String getCurTime = dayTime.format(new Date(time)); //저장되는 파일명
        for (int i = 0; i < selectCount; i++) {
            imagePath = selectList.get(i);// 현재 변경하려는 파일 경로

            //가로세로 확인
            ExifInterface exif = new ExifInterface(imagePath);
            //사진 속성
            String a = exif.getAttribute(ExifInterface.TAG_DATETIME); // 파일이 수정된 시간
            String a1 = exif.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL); // 원본 파일 시간

            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int exifDegree = exifOrientationToDegrees(exifOrientation);

            int REQUIRED_SIZE = m_sQuality;


            // 축소된 이미지 생성
            BitmapFactory.Options options = new BitmapFactory.Options();
            //options.inSampleSize = 1;
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imagePath, options);
            int width_tmp = options.outWidth, height_tmp = options.outHeight;

            int scale = 1;

            if (REQUIRED_SIZE != 0 && (REQUIRED_SIZE < width_tmp || REQUIRED_SIZE < height_tmp)) {
                // 요구사이즈보다 크다면 줄이는 작업을 한다.

                int maxSize = Math.max(width_tmp, height_tmp);

                scale = maxSize / REQUIRED_SIZE;
            }

            options.inJustDecodeBounds = false;
            options.inSampleSize = scale;
            options.inPurgeable = true;

            Log.d("MinkTrace", "scale : " + scale);

            fileName = getCurTime + "_" + i + ".jpg";
            File newFile = new File(m_saveImagePath, fileName); // 크기가 수정된 파일을 저장할 경로!
            //Log.d("???", "m_saveImagePath : " + m_saveImagePath + "\n\t\tfileName : " + fileName);


            Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);        //Bitmap 읽어오기


            width_tmp = options.outWidth;
            height_tmp = options.outHeight;

            int width_scale = 0;
            int height_scale = 0;

            if (REQUIRED_SIZE != 0 && (REQUIRED_SIZE < width_tmp || REQUIRED_SIZE < height_tmp)) {
                // 크기 다시 확인해서 줄인다.
                if (width_tmp < height_tmp) {
                    // 세로가 큰 경우
                    height_scale = REQUIRED_SIZE;
                    width_scale = width_tmp - (int) (width_tmp * (double) (1 - (double) ((REQUIRED_SIZE * 1.0) / height_tmp)));
                } else {
                    // 가로가 큰 경우
                    width_scale = REQUIRED_SIZE;
                    height_scale = height_tmp - (int) (height_tmp * (double) (1 - (double) ((REQUIRED_SIZE * 1.0) / width_tmp)));
                }

                if (exifDegree == 90 || exifDegree == 270) {
                    // 가로세로가 바뀐 경우
                }

                bitmap = Bitmap.createScaledBitmap(bitmap, width_scale, height_scale, true);
            }

            Log.d("MinkTrace", "exifDegree : " + exifDegree);
            if (exifDegree == 90 || exifDegree == 180 || exifDegree == 270) {
                //  bitmap = rotate(bitmap, exifDegree);
                // 이미지 중심으로 exifDegree도 회전 Matrix
                Matrix matrix = new Matrix();
                matrix.preRotate(exifDegree, 0, 0);


                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);


            }


            try {

                // 자동으로 빈 파일을 생성합니다.
                newFile.createNewFile();

                // 파일을 쓸 수 있는 스트림을 준비합니다.
                FileOutputStream out = new FileOutputStream(newFile);

                // compress 함수를 사용해 스트림에 비트맵을 저장합니다.
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

                // 스트림 사용후 닫아줍니다.
                out.close();

            } catch (FileNotFoundException e) {
                Log.e("MyTag", "FileNotFoundException : " + e.getMessage());
            } catch (IOException e) {
                Log.e("MyTag", "IOException : " + e.getMessage());
            }
            String path = m_saveImagePath + "/" + fileName;

            resultList.add(path);
        }

        //dialog.dismiss();
        //Log.d("???", "resultList : " + resultList);

        return resultList;
    }

    public PhotoPickerActivity getActivity() {
        return this;
    }

    public boolean isShowGif() {
        return showGif;
    }

    public void setShowGif(boolean showGif) {
        this.showGif = showGif;
    }


    /**
     * EXIF정보를 회전각도로 변환하는 메서드
     *
     * @param exifOrientation EXIF 회전각
     * @return 실제 각도
     */
    public int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private void showProgressDialog() {
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("이미지 저장중...");
        dialog.show();
    }

    private void closeProgressDialog() {
        if ((dialog != null) && (dialog.isShowing())) {
            dialog.dismiss();
        }
        dialog = null;
    }



    private class MakeNewPhoto extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog = new ProgressDialog(PhotoPickerActivity.this);

        @Override
        protected void onPreExecute(){
            dialog.setCancelable(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("이미지 저장중...");
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //해상도 낮춘 이미지 파일생성 -> 해당 이미지 경로 ArrayList에 넣기
            try {
                MakeNewPhotoList(selectedPhotos);
            } catch (IOException e) {
                e.printStackTrace();
            }


            // dialog.dismiss();
            //finish();*/
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            dialog.dismiss();
            super.onPreExecute();

            backPress();
        }
    }

    //사진 촬영시 메타데이터 넣기
    /*private void setMetadata(File file) {
        try {
            ExifInterface exif = new ExifInterface(file.getCanonicalPath());

            String latitude = "";
            String longitude = "";

            String[] gps = gps_data.split("/");
            latitude = gps[0];
            longitude = gps[1];

            double lat = gpsClass.getLatitude();
            double alat = Math.abs(lat);
            String dms = Location.convert(alat, Location.FORMAT_SECONDS);
            String[] splits = dms.split(":");
            String[] secnds = (splits[2]).split("\\.");
            String seconds;

            if(secnds.length==0)
            {
                seconds = splits[2];
            }
            else
            {
                seconds = secnds[0];
            }
            double sec = Double.parseDouble(splits[2])*1000;
            seconds = String.valueOf(sec);

            String latitudeStr = splits[0] + "/1," + splits[1] + "/1," + seconds + "/1000";
            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, latitudeStr);

            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, lat>0?"N":"S");

            double lon = gpsClass.getLongitude();
            double alon = Math.abs(lon);


            dms = Location.convert(alon, Location.FORMAT_SECONDS);
            splits = dms.split(":");
            secnds = (splits[2]).split("\\.");

            if(secnds.length==0)
            {
                seconds = splits[2];
            }
            else
            {
                seconds = secnds[0];
            }

            sec = Double.parseDouble(splits[2])*1000;
            seconds = String.valueOf(sec);

            String longitudeStr = splits[0] + "/1," + splits[1] + "/1," + seconds + "/1000";

            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, longitudeStr);
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, lon>0?"E":"W");

            // 촬영시간 넣기
            long time = System.currentTimeMillis();
            SimpleDateFormat photoDateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
            String takePhotoTime = photoDateFormat.format(new Date(time));
            exif.setAttribute(ExifInterface.TAG_DATETIME_ORIGINAL, takePhotoTime); // 원본 촬영 시간 넣기

            exif.saveAttributes();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }*/
}
