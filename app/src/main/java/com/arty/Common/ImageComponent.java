package com.arty.Common;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageComponent extends AppCompatActivity {
    // 사진 촬영
    public Uri takingPicture() {
        Uri photoURI = null;
        File photoFile = null;
        String imgaeFilePath;
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

}
