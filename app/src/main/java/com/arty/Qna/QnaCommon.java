package com.arty.Qna;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class QnaCommon extends AppCompatActivity {
    static final    String      TAG             = "QnaCommon";
    private         Uri         photoURI;
    public          String      imgaeFilePath;
    protected       String      timeStamp       = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss").format(new Date());

    static final    int         UPLOAD_MAXIMUM_SIZE     = 3;   // 최대 이미지 등록 갯수

    static final    String      COLLECTION_NAME         = "QNA_BOARD";
    static final    String      IMAGE_FILE_PRE_PATH     = "QNA_BOARD_IMG/";

    int imageCount;

    public void changeImgUpCount(TextView textView, String str) {
        if(str.equals("up")) {
            imageCount++;
        } else if(str.equals("down")) {
            imageCount--;
        }
        textView.setText(imageCount + " / " + UPLOAD_MAXIMUM_SIZE);

        if(imageCount == 3) textView.setTextColor(Color.RED);
        else textView.setTextColor(Color.WHITE);
    }

    
    // 사진 촬영 버튼 클릭 이벤트
    public Uri takingPicture(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(intent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            }catch (IOException e) {
                e.printStackTrace();
            } finally {

            }

            if(photoFile != null) {
                photoURI = FileProvider.getUriForFile(getApplicationContext(),"com.arty.Qna.fileprovider",photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                // imgUri = photoURI;
                startActivityForResult(intent,101);
                return photoURI;
            }
        }
        return null;
    }

    private File createImageFile() throws IOException {
        String timeStamp       = new SimpleDateFormat("yyMMdd_HH:mm:ss").format(new Date());
        //String imageFileName    = "JPEG_" + timeStamp + "_";
        String imageFileName    = timeStamp + "_";
        File storageDir         = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image              = File.createTempFile(imageFileName,".jpg",storageDir);

        imgaeFilePath           = image.getAbsolutePath();
        //image.deleteOnExit();
        return image;
    };

    // 사진 가져오기 버튼 클릭 이벤트
    public void choosePicture(View view) {
        // Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        //intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 201);
    }

    public int exifOrientationToDegress(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    public Bitmap rotate(Bitmap bitmap, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public void setImageCacheSetting(ImageView imageView1, ImageView imageView2, ImageView imageView3) {
        imageView1.setDrawingCacheEnabled(true);
        imageView1.buildDrawingCache();
        imageView2.setDrawingCacheEnabled(true);
        imageView2.buildDrawingCache();
        imageView3.setDrawingCacheEnabled(true);
        imageView3.buildDrawingCache();
    }

    public boolean isImageFull(ImageView image1, ImageView image2, ImageView image3) {
        // Not Null 은 섬네일 존재함을 의미.
        if(image1.getDrawable() != null && image2.getDrawable() != null && image3.getDrawable() != null) {
            return false;
        }
        return true;
    }

    public boolean isImageEmpty(ImageView image1, ImageView image2, ImageView image3) {
        // Not Null 은 섬네일 존재함을 의미.
        if(image1.getDrawable() == null && image2.getDrawable() == null && image3.getDrawable() == null) {
            return true;
        }
        return false;
    }

    public int getUriCount(Uri[] uris) {
        int j = 0;
        for (int i = 0; i < uris.length; i++) {
            if(uris[i] != null) {
                j++;
            }
        }
        return j;
    }

    public int getUriCount(Uri[] uris, Uri[] beforeUris, boolean[] isSwitch) {
        int j = 0;
        for (int i = 0; i < uris.length; i++) {

            if(uris[i] != null && !(uris[i].equals(beforeUris[i]))) {
                Log.d(TAG,i+"번째 수정");
                j++;

            } else if(uris[i] == null && isSwitch[i]) {
                Log.d(TAG,i+"번째 삭제");
                j++;

            }
        }
        return j;
    }

}
