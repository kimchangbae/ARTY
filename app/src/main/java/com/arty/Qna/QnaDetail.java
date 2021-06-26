package com.arty.Qna;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arty.R;
import com.bumptech.glide.Glide;

import java.io.Serializable;
import java.util.ArrayList;

public class QnaDetail extends QnaCommon implements Serializable {
    static final String TAG = "QnaDetail";

    private TextView    contentType, content, uploadDate
                        , commentCount, edit_coment
                        , commentFollow, btn_follow_cancel;
    private ImageView   image1, image2, image3;
    private Button      btn_update, btn_delete, btn_comment;

    private static String       uuId;
    private Qna                 qna;
    private boolean[]           haveImage;

    private QnaViewModel                    qnaViewModel;
    private RecyclerView                    recyclerView;
    private RecyclerView.LayoutManager      layoutManager;
    private CommentAdapter adapter;
    private long commentFollowNo;

    private static ArrayList<Comment> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"QnaDetail--ON CREATE");

        setContentView(R.layout.qna_detail);

        qnaViewModel = new ViewModelProvider(this,
                new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(QnaViewModel.class);

        contentType     = findViewById(R.id.tv_fb_dt_userId);
        content         = findViewById(R.id.tv_fb_dt_content);
        uploadDate      = findViewById(R.id.tv_fb_dt_uploadTime);
        image1          = findViewById(R.id.imageDetail_1);
        image2          = findViewById(R.id.imageDetail_2);
        image3          = findViewById(R.id.imageDetail_3);
        commentCount    = findViewById(R.id.tv_coment_count);
        edit_coment     = findViewById(R.id.edit_coment);
        commentFollow   = findViewById(R.id.tv_comment_followId);

        btn_update          = findViewById(R.id.btn_detail_update);
        btn_delete          = findViewById(R.id.btn_detail_delete);
        btn_comment         = findViewById(R.id.btn_write_qna_coment);
        btn_follow_cancel   = findViewById(R.id.btn_follow_cancel);

        image1.setVisibility(View.GONE);
        image2.setVisibility(View.GONE);
        image3.setVisibility(View.GONE);

        recyclerView = findViewById(R.id.qnaComentRecyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CommentAdapter();
        arrayList = new ArrayList<>();

        haveImage = new boolean[UPLOAD_MAXIMUM_SIZE];

        /*
         * 현재 접속중인 사용자 아이디 조회
         * */
        searchUserId(qnaViewModel);
        qnaViewModel.getUserId().observeForever(userId -> {
            Log.d(TAG,"userId --> " + userId);
            if(userId != null) this.userId = userId;
        });

        /*
         * 제공받은 문서id로 질문 정보 조회
         * */
        Intent intent = getIntent();
        if(intent.getStringExtra("uuId") != null) {
            uuId = intent.getStringExtra("uuId");
            Log.d(TAG,"조회할 문서 ID [" + uuId + "]");
            qnaViewModel.selectQnaItem(uuId);
            qnaViewModel.selectQnaCommentFirst(uuId);
            qnaViewModel.selectQnaItemComment(uuId);
        } else {
            Log.d(TAG,"조회할 문서 ID가 존재하지 않습니다.");
        }

        qnaViewModel.getQnaItem().observeForever(qnaItem -> {
            Log.d(TAG,"getQnaItem()---observeForever");
            if(qnaItem != null) {
                Log.d(TAG,"qnaItme ---> " + qnaItem.toString());
                drawingIntentData(qnaItem);
                qna = qnaItem;
            }
        });

        qnaViewModel.getComment().observeForever(comment -> {
            if(comment != null) {
                arrayList.add(comment);
            }
            setAdapter(arrayList);
        });

        qnaViewModel.getCommentLists().observeForever(commentList -> {
            if(!commentList.isEmpty()) {
               arrayList = commentList;
            }
            setAdapter(arrayList);
        });
    }

    public void setAdapter(ArrayList<Comment> comments) {
        adapter.notifyDataSetChanged();
        adapter.setCommentList(comments);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        Log.d(TAG,"QnaDetail--ON START");
        super.onStart();

        /*
         * 상세글 수정
         * */
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateQuestion();
            }
        });

        /*
         * 상세글 삭제
         * */
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteQuestion();
            }
        });

        /*
         * 특정 댓글에 대댓글 달기
         * */
        adapter.setCommentClickListener(new CommentClickListener() {
            @Override
            public void onCommentClick(CommentAdapter.ViewHolder holder, View view, int position) {
                Log.d(TAG,"누구한테 댓글 달꺼야? ---> " + adapter.getSeqNo(position) + " / " + adapter.getUserId(position));
                commentFollowNo = adapter.getSeqNo(position);
                setComment(adapter.getUserId(position));
            }
        });

        /*
         * 댓글 달기
         * */
        btn_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = edit_coment.getText().toString();
                String uploadTime = String.valueOf(System.currentTimeMillis());
                qnaViewModel.insertQnaComment(qna.getUuId(), commentFollowNo, userId, comment, uploadTime);
                edit_coment.setText("");
            }
        });

        /*
        * 댓글 기능 취소
        * */
        btn_follow_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentFollowNo = 0;
                findViewById(R.id.commentLayout).setVisibility(View.GONE);
            }
        });
    }

    private void setComment(String followId) {
        findViewById(R.id.commentLayout).setVisibility(View.VISIBLE);
        commentFollow.setText(followId + "님에게 댓글을 작성합니다.");
        commentFollow.setTextColor(Color.DKGRAY);
    }

    private void drawingIntentData(Qna qna) {
        content.setText(qna.getContent());
        contentType.setText(qna.getContentType());
        uploadDate.setText(qna.getUploadTime());

        if(qna.getImage1() != null) {
            haveImage[0]=true;
            image1.setVisibility(View.VISIBLE);
            Glide.with(this).load(qna.getImage1()).into(image1);
        }

        if(qna.getImage2() != null) {
            image2.setVisibility(View.VISIBLE);
            haveImage[1]=true;
            Glide.with(this).load(qna.getImage2()).into(image2);
        }

        if(qna.getImage3() != null) {
            image3.setVisibility(View.VISIBLE);
            haveImage[2]=true;
            Glide.with(this).load(qna.getImage3()).into(image3);
        }
    }

    public void backToHome(View view) {
        onBackPressed();
        finish();
    }

    private void updateQuestion() {
        if(userId.equals(qna.getUserId())) {
            Intent intent = new Intent(QnaDetail.this, QnaUpdate.class);
            intent.putExtra("qna",qna);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(getApplicationContext(),"권한없음",Toast.LENGTH_SHORT).show();
            Log.d(TAG,"권한없음");
        }
    }

    private void deleteQuestion() {
        if(userId.equals(qna.getUserId())) {
            qnaViewModel.deleteQuestion(qna.getUuId());
            for(int i = 0; i<haveImage.length;i++) {
                if(haveImage[i]) {
                    qnaViewModel.deleteImage(qna.getFilePath() + "/" + i);
                }
            }
            goToMainActivity();
        } else{
            Toast.makeText(getApplicationContext(),"권한없음",Toast.LENGTH_SHORT).show();
            Log.d(TAG,"권한없음");
        }
    }

    private void insertComment() {

        String comment = edit_coment.getText().toString();
    }
}