package com.arty.Qna;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.arty.Common.TimeComponent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class QnaRepository {
    private final static String TAG = "QnaRepository";

    private Application     app;
    private TimeComponent   timeComponent;

    private MutableLiveData<ArrayList<Qna>>     qnaLists;
    private MutableLiveData<Qna>                qnaItem;
    private MutableLiveData<Boolean>            imgUpResult, update;
    private MutableLiveData<String>             userId;
    private MutableLiveData<ArrayList<Comment>> commentLists;
    private MutableLiveData<Comment>            comment;

    private FirebaseFirestore   mStore;
    private FirebaseStorage     mStorage;
    private FirebaseDatabase    mDB;

    public QnaRepository(Application app) {
        this.app = app;

        qnaLists        = new MutableLiveData<>();
        imgUpResult     = new MutableLiveData<>();
        update          = new MutableLiveData<>();
        qnaItem         = new MutableLiveData<>();
        userId          = new MutableLiveData<>();
        commentLists    = new MutableLiveData<>();
        comment         = new MutableLiveData<>();

        mStore      = FirebaseFirestore.getInstance();
        mStorage    = FirebaseStorage.getInstance();
        mDB         = FirebaseDatabase.getInstance();

        timeComponent = new TimeComponent();
    }

    public MutableLiveData<ArrayList<Qna>> getQnaLists() {
        return qnaLists;
    }

    public MutableLiveData<Qna> getQnaItem() {
        return qnaItem;
    }

    public MutableLiveData<String> getUserId() {
        return userId;
    }

    public MutableLiveData<Boolean> getImgUpResult() {
        return imgUpResult;
    }

    public MutableLiveData<Boolean> getUpdate() {
        return update;
    }



    public void selectQnaList() {
        ArrayList<Qna> arrayList = new ArrayList<>();

        mStore.collection("QNA_BOARD")
        .orderBy("uploadTime", Query.Direction.DESCENDING)
        .get()
        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    Log.d(TAG,"QNA 게시판 조회");
                    for(QueryDocumentSnapshot document : task.getResult()) {
                        Qna qna = document.toObject(Qna.class);
                        qna.setUuId(document.getId());
                        String time = timeComponent.switchTime(qna.getUploadTime());
                        qna.setUploadTime(time);
                        arrayList.add(qna);
                    }
                    qnaLists.postValue(arrayList);
                }
            }
        }).addOnFailureListener(e -> Log.e(TAG,e.getMessage()));
    }

    public void selectQnaItem(String uuId) {
        mStore.collection("QNA_BOARD")
        .document(uuId)
        .get()
        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(Task<DocumentSnapshot> task) {
                Qna qna = task.getResult().toObject(Qna.class);
                String time = timeComponent.switchTime(qna.getUploadTime());
                qna.setUploadTime(time);
                qnaItem.postValue(qna);
            }
        }).addOnFailureListener(e -> Log.d(TAG,"데이터 조회 실패 ... " + e.getMessage()));
    }



    public void insertQuestion(String uuId, String type, String content, String userId, String uploadTime, String filePath) {
        Map<String, Object> map = new HashMap<>();
        map.put("uuId",         uuId);
        map.put("contentType",  type);
        map.put("content",      content);
        map.put("userId",       userId);
        map.put("uploadTime",   uploadTime);
        map.put("filePath",     filePath);
        map.put("lastComtNo",   0);

        mStore.collection("QNA_BOARD")
        .document(uuId)
        .set(map, SetOptions.merge())
        .addOnCompleteListener(task -> {
            Log.d(TAG,"Question Document 등록 완료------------");
            map.clear();
        });
    }

    public void updateQuestion(String uuId, String content, String uploadTime) {
        Map<String, Object> map = new HashMap<>();
        map.put("content",      content);
        map.put("uploadTime",   uploadTime);

        mStore
        .collection("QNA_BOARD")
        .document(uuId)
        .update(map)
        .addOnCompleteListener(task -> {
            Log.d(TAG,"Question Document 수정 완료------------");
            update.postValue(true);
            map.clear();
        });
    }

    public void deleteQuestion(String uuId) {
        mStore.collection("QNA_BOARD")
        .document(uuId)
        .delete()
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.d(TAG,"문서 삭제 실패 [문서번호 : " + uuId+ "]");
                Log.d(TAG, e.getMessage());
            }
        });
    }

    public void insertImage(String uuId, String filePath, Uri uri, int index) {
        Map<String, Object> data = new HashMap<>();
        StorageReference mReference = mStorage.getReference().child(filePath + "/" + index);

        UploadTask uploadTask = mReference.putFile(uri);
        uploadTask
        .addOnSuccessListener(taskSnapshot -> {
            Task<Uri> t = uploadTask.continueWithTask(task -> {
                return mReference.getDownloadUrl();
            })
            .addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    data.put("image"+(index+1), task.getResult().toString());

                    mStore.collection("QNA_BOARD")
                    .document(uuId)
                    .set(data,SetOptions.merge())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            Log.d(TAG,"Question 이미지 "+index+"번 등록 완료------------");
                            imgUpResult.postValue(true);
                            data.clear();
                        }
                    });
                }
            }).addOnFailureListener(e -> e.printStackTrace());
        });
    }

    public void deleteImage(String filePath) {
        StorageReference ref = mStorage.getReference().child(filePath);
        ref.delete().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.d(TAG,"이미지 삭제 실패 [이미지 경로 : " + filePath+ "]");
            }
        });
    }

    public void deleteImage(String uuId, String filePath, int index) {
        Map<String, Object> data = new HashMap<>();
        StorageReference mReference = mStorage.getReference().child(filePath + "/" + index);
        mReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                data.put("image"+(index+1),null);

                mStore.collection("QNA_BOARD")
                .document(uuId)
                .set(data,SetOptions.merge())
                .addOnCompleteListener(task1 -> {
                    Log.d(TAG,"Question 이미지 "+index+"번 삭제 완료------------");
                    imgUpResult.postValue(true);
                    data.clear();
                });
            }
        });
    }

    public void getUserId(String email) {
        mStore.collection("USER_ACCOUNT")
        .whereEqualTo("email",email)
        .get()
        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String user = (String) document.getData().get("userId");
                        userId.postValue(user);
                        Log.d(TAG, "DB 에서 검색된 사용자 아이디(파이어베이스) : " + user);
                    }
                }
            }
        }).addOnFailureListener(e -> e.printStackTrace());
    }

    public void getUserId(long kakaoId) {
        mStore.collection("USER_ACCOUNT")
        .whereEqualTo("kakaoId",kakaoId)
        .get()
        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String user = (String) document.getData().get("userId");
                        userId.postValue(user);
                        Log.d(TAG, "DB 에서 검색된 사용자 아이디(카카오) : " + user);
                    }
                }
            }
        }).addOnFailureListener(e -> e.printStackTrace());
    }
}
