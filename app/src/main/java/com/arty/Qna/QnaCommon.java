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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.arty.Main.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.kakao.sdk.auth.AuthApiClient;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class QnaCommon extends AppCompatActivity {
    static final    String      TAG                     = "QnaCommon";
    static final    int         UPLOAD_MAXIMUM_SIZE     = 3;   // 최대 이미지 등록 갯수
    static final    String      IMAGE_FILE_PRE_PATH     = "QNA_BOARD_IMG/";

    public          String      imgaeFilePath;

    protected       FirebaseStorage     storage;
    protected       FirebaseAuth        mAuth;
    protected       UserApiClient       mKakao;
    protected       FirebaseFirestore   mDB;


    protected       String      userId;
    public          int         imageCount;

    public QnaCommon() {
        Log.d(TAG,"QnaCommon 생성자");
    }

    public void searchUserId(QnaViewModel qnaViewModel) {
        mAuth       = FirebaseAuth.getInstance();
        mKakao      = UserApiClient.getInstance();

        Log.d(TAG,"QNA.searchUserId");
        if(mAuth.getCurrentUser() != null) {
            Log.d(TAG,"mAuth ID ---> " + mAuth.getCurrentUser().getEmail());
            qnaViewModel.getUserId(mAuth.getCurrentUser().getEmail());
        } else if(AuthApiClient.getInstance().hasToken()) {
            mKakao.me(new Function2<User, Throwable, Unit>() {
                @Override
                public Unit invoke(User user, Throwable throwable) {
                    if(user != null) {
                        Log.d(TAG,"mKakao ID ---> " + user.getId());
                        qnaViewModel.getUserId(user.getId());
                    }
                    return null;
                }
            });
        }
    }

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

    // 사진 촬영
    public Uri takingPicture() {
        Uri photoURI = null;
        File photoFile = null;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(intent.resolveActivity(getPackageManager()) != null) {
            try {
                photoFile = createImageFile();

            }catch (IOException e) {
                e.printStackTrace();
            }

            if(photoFile != null) {
                imgaeFilePath = photoFile.getAbsolutePath();
                photoURI = FileProvider.getUriForFile(getApplicationContext(),"com.arty.Qna.fileprovider",photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intent,101);
            }
        }
        return photoURI;
    }

    private File createImageFile() throws IOException {
        String timeStamp       = new SimpleDateFormat("yyMMdd_HH:mm:ss").format(new Date());
        //String imageFileName    = "JPEG_" + timeStamp + "_";
        String imageFileName    = timeStamp + "_";
        File storageDir         = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image              = File.createTempFile(imageFileName,".jpg",storageDir);

        return image;
    }

    // 사진 가져오기 버튼 클릭 이벤트
    public void choosePicture() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
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
            } else {
                Log.d(TAG,i+"번째 변동없음");
            }
        }
        Log.d(TAG,"총 이미지 수정작업횟수 : " + j);
        return j;
    }

    public void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    protected void goToDetailActivity(String uuId) {
        Intent intent = new Intent(this, QnaDetail.class);
        intent.putExtra("uuId", uuId);
        startActivity(intent);
        finish();
    }
}
