package com.axxessapp;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aands.sim.simtoolkit.firebase.DatabaseHelper;
import com.axxessapp.Model.CommentModel;
import com.axxessapp.adapters.CommentAdapter;
import com.axxessapp.adapters.CommentShowAdapter;
import com.axxessapp.adapters.ImageUrlBindingAdapter;
import com.google.firebase.database.DatabaseError;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ShapeDetailActivity extends AppCompatActivity implements ShapeDetailsViewModel.databaseCallBack {

    private ShapeDetailsViewModel shapeDetailsViewModel;
    private ShapeModelData model;
    private RecyclerView recyclerCommentList;
    private EditText edtComment;
    private TextView emptyView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shape_details);
        getIntentData();
        initViewModel();
        setView();
    }

    private void initViewModel() {
        shapeDetailsViewModel = ViewModelProviders.of(this).get(ShapeDetailsViewModel.class);
    }

    private void setView() {
        edtComment = findViewById(R.id.edtComment);
        ImageView imageView = findViewById(R.id.imageView);
        recyclerCommentList = findViewById(R.id.recyclerCommentList);
        emptyView = findViewById(R.id.emptyView);
        if(model!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            setTitle(model.title);
            ImageUrlBindingAdapter.setImageUrl(imageView,model.link);
            getComments();
        }
        findViewById(R.id.btnSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String comment = edtComment.getText().toString();
               if(comment.isEmpty()){
                   Utils.INSTANCE.showToast(ShapeDetailActivity.this,getString(R.string.enter_comment_message));
               }else {
                   shapeDetailsViewModel.addComments(ShapeDetailActivity.this,model.id,comment,ShapeDetailActivity.this);
                   edtComment.setText("");
               }
            }
        });
    }

    private void getComments(){
        if(model!=null){
            shapeDetailsViewModel.getCommentFromDatabase(this,model.id,this);
        }
    }

    public void setUpRecyclerView(ArrayList<CommentModel> models){
        if(models!=null && !models.isEmpty()){
            emptyView.setVisibility(View.GONE);
            recyclerCommentList.setVisibility(View.VISIBLE);
            CommentShowAdapter commentAdapter = new CommentShowAdapter();
            commentAdapter.setList(models);
            LinearLayoutManager layoutManager = new  LinearLayoutManager(this);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerCommentList.setLayoutManager(layoutManager);
            recyclerCommentList.setAdapter(commentAdapter);
        }else {
            emptyView.setVisibility(View.VISIBLE);
            recyclerCommentList.setVisibility(View.GONE);
        }
    }

    private void getIntentData() {
        Serializable serializable = getIntent().getSerializableExtra(Utils.SHAPE_MODEL);
        if(serializable!=null && serializable instanceof ShapeModelData){
            model = (ShapeModelData) serializable;
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCallBack(@Nullable Boolean success, @Nullable List<? extends CommentModel> data, @Nullable DatabaseError t) {
        ArrayList<CommentModel>  dataList = new ArrayList<CommentModel>();
        if(data!=null){
            dataList = new ArrayList<CommentModel>(data);
        }
        setUpRecyclerView(dataList);
    }
}
