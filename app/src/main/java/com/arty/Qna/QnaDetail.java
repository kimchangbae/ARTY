package com.arty.Qna;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.arty.Comment.CommentFragment;
import com.arty.Comment.CommentViewModel;
import com.arty.R;
import com.bumptech.glide.Glide;

import java.io.Serializable;

public class QnaDetail extends QnaCommon implements Serializable {
    private static final String TAG = "QnaDetail";

    private TextView    contentUserId , contentType , content , uploadTime
                        , edit_coment , btn_follow_cancel;
    private ImageView   image1 , image2 , image3;
    private Button      btn_update , btn_delete;
    public Button       btn_comment;

    public static String       uuId;
    private Qna                 qna;
    private boolean[]           haveImage;

    private QnaViewModel            qnaViewModel;
    private CommentViewModel commentViewModel;

    private String        commentParentId;
    private String        sortId;

    private FragmentManager         fragmentManager;
    private FragmentTransaction     transaction;
    private CommentFragment commentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"QnaDetail--ON CREATE");

        haveImage       = new boolean[UPLOAD_MAXIMUM_SIZE];
        qnaViewModel    = new ViewModelProvider(this,
                new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(QnaViewModel.class);
        commentViewModel    = new ViewModelProvider(this,
                new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(CommentViewModel.class);

        setContentView(R.layout.qna_detail);
        initViewData();

        try {
            /*
             * ?????? ???????????? ????????? ????????? ??????
             * */
            searchUserId(qnaViewModel);
            qnaViewModel.getUserId().observeForever(userId -> {
                Log.d(TAG,"userId --> " + userId);
                if(userId != null) this.userId = userId;
            });

            /*
             * ???????????? ??????id??? ?????? ?????? ??????
             * */
            Intent intent = getIntent();
            if(intent.getStringExtra("uuId") != null) {
                uuId = intent.getStringExtra("uuId");
                Log.d(TAG,"????????? ?????? ID [" + uuId + "]");
                qnaViewModel.selectQnaItem(uuId);
            } else {
                Log.d(TAG,"????????? ?????? ID??? ???????????? ????????????.");
            }

            qnaViewModel.getQnaItem().observeForever(qnaItem -> {
                if(qnaItem != null) {
                    qna = qnaItem;
                    createQuestion();
                    createCommentFrame();
                    Log.d(TAG,"Question ---> " + qna.toString());
                }
            });

        } catch (Exception e) {
            Log.e(TAG,"--------------AI?????? ??????????????? ????????????--------------");
            e.printStackTrace();

            goToMainActivity();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        try{
            /*
             * ????????? ??????
             * */
            btn_update.setOnClickListener(v -> updateQuestion());

            /*
             * ????????? ??????
             * */
            btn_delete.setOnClickListener(v -> deleteQuestion());

            /*
             * ?????? ??????
             * */
            btn_comment.setOnClickListener(v -> insertComment());

            /*
             * ????????? ??????
             * */
            btn_follow_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    findViewById(R.id.commentLayout).setVisibility(View.GONE);
                    commentParentId = null;
                    sortId = null;
                }
            });
        } catch (Exception e) {
            Log.e(TAG,"--------------AI?????? ??????????????? ????????????--------------");
            e.printStackTrace();

            goToMainActivity();
        }
    }

    private void initViewData() {
        contentType     = findViewById(R.id.tv_qna_detail_type);
        contentUserId   = findViewById(R.id.tv_qna_detail_userId);
        content         = findViewById(R.id.tv_qna_detail_content);
        uploadTime      = findViewById(R.id.tv_qna_detail_uploadTime);
        image1          = findViewById(R.id.imageDetail_1);
        image2          = findViewById(R.id.imageDetail_2);
        image3          = findViewById(R.id.imageDetail_3);
        edit_coment     = findViewById(R.id.edit_coment);

        btn_update          = findViewById(R.id.btn_detail_update);
        btn_delete          = findViewById(R.id.btn_detail_delete);
        btn_comment         = findViewById(R.id.btn_write_qna_comment);
        btn_follow_cancel   = findViewById(R.id.btn_follow_cancel);

        image1.setVisibility(View.GONE);
        image2.setVisibility(View.GONE);
        image3.setVisibility(View.GONE);
    }

    private void createCommentFrame() {
        fragmentManager     = getSupportFragmentManager();
        transaction         = fragmentManager.beginTransaction();
        commentFragment     = new CommentFragment(uuId);
        transaction.replace(R.id.CommentFrameLayout,commentFragment).commit();
    }

    private void createQuestion() {
        contentType.setText(qna.getContentType());
        contentUserId.setText(qna.getUserId());
        content.setText(qna.getContent());
        uploadTime.setText(qna.getUploadTime());

        if(qna.getImage1() != null) {
            image1.setVisibility(View.VISIBLE);
            Glide.with(this).load(qna.getImage1()).into(image1);
            haveImage[0]=true;
        }

        if(qna.getImage2() != null) {
            image2.setVisibility(View.VISIBLE);
            Glide.with(this).load(qna.getImage2()).into(image2);
            haveImage[1]=true;
        }

        if(qna.getImage3() != null) {
            image3.setVisibility(View.VISIBLE);
            Glide.with(this).load(qna.getImage3()).into(image3);
            haveImage[2]=true;
        }
    }

    public void backToHome(View view) {
        onBackPressed();
        finish();
    }

    private void updateQuestion() {
        try{
            if(userId != null && userId.equals(qna.getUserId())) {
                Intent intent = new Intent(QnaDetail.this, QnaUpdate.class);
                intent.putExtra("qna",qna);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(getApplicationContext(),"????????????",Toast.LENGTH_SHORT).show();
            }
        } catch(Exception e) {
            Log.d(TAG,e.getMessage());
        }
    }

    private void deleteQuestion() {
        if(userId != null && userId.equals(qna.getUserId())) {
            qnaViewModel.deleteQuestion(qna.getUuId());
            qnaViewModel.deleteComment(qna.getUuId());
            for(int i = 0; i<haveImage.length;i++) {
                if(haveImage[i]) {
                    qnaViewModel.deleteImage(qna.getFilePath() + "/" + i);
                }
            }
            goToMainActivity();
        } else{
            Toast.makeText(getApplicationContext(),"????????????",Toast.LENGTH_SHORT).show();
            Log.d(TAG,"????????????");
        }
    }

    private void insertComment() {
        try{
            if(userId != null) {
                String comment      = edit_coment.getText().toString();
                String createTime   = String.valueOf(System.currentTimeMillis());

                if(comment.length() > 0 && !comment.equals("")) {
                    if(commentParentId != null) commentViewModel.insertChildComment(qna.getUuId(), userId, comment, createTime, commentParentId, sortId);
                    else {
                        commentViewModel.insertParentComment(qna.getUuId(), userId, comment, createTime);
                    }
                    edit_coment.setText("");
                }
            } else {
                Toast.makeText(getApplicationContext(),"????????? ??? ?????? ?????? ???????????????.",Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            findViewById(R.id.commentLayout).setVisibility(View.GONE);
            commentViewModel.selectQnaComment(qna.getUuId());
        }
    }

    public String getCommentParentId() {
        return commentParentId;
    }

    public void setCommentParentId(String commentParentId) {
        this.commentParentId = commentParentId;
    }

    public String getSortId() {
        return sortId;
    }

    public void setSortId(String sortId) {
        this.sortId = sortId;
    }
}