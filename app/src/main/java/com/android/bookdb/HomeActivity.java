package com.android.bookdb;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.bookdb.Adapter.BookListAdapter;
import com.android.bookdb.Model.BookInformationModel;
import com.android.bookdb.ViewModel.BookInfoViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private FloatingActionButton fabAdd;
    private ArrayList<BookInformationModel> bookInfoList;
    private BookListAdapter bookListAdapter;
    private RecyclerView rvBookList;
    private ImageView ivCoverPage;
    private EditText tieName, tieAuthor;
    private BookInfoViewModel bookInfoViewModel;
    private androidx.appcompat.app.AlertDialog.Builder dialogBuilder;
    private final int PICK_IMAGE_REQUEST = 70;
    private Uri filepath;
    private Map<String, Object> bookInfo;
    private BookInformationModel bookInformationModel;
    private AlertDialog dialog;
    private boolean isImageUploaded = false;
    private Uri coverPageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        FirebaseApp.initializeApp(this);
        fabAdd = findViewById(R.id.fabAdd);
        rvBookList = findViewById(R.id.rvBookList);
        bookInfoViewModel = ViewModelProviders.of(this).get(BookInfoViewModel.class);
        bookInfoViewModel.init();
        bookInfoList = new ArrayList<>();
        initViews();
    }

    private void initViews() {

        bookInfoList = bookInfoViewModel.getBookInfo().getValue();
        bookListAdapter = new BookListAdapter(this, bookInfoList);
        rvBookList.setAdapter(bookListAdapter);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false);
        rvBookList.setLayoutManager(gridLayoutManager);
        bookInfoViewModel.getIsBookInfoFetched().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isBookInfoFetched) {
                if (isBookInfoFetched) {
                    bookListAdapter.notifyDataSetChanged();
                }
            }
        });
        fabAdd.setOnClickListener(this);
        rvBookList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    fabAdd.hide();
                    return;
                }
                if (dy < 0) {
                    fabAdd.show();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fabAdd) {
            createDialog();
        }
    }


    public void createDialog() {
        dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this);

        LayoutInflater layoutInflater = this.getLayoutInflater();
        final View alertLayout = layoutInflater.inflate(R.layout.layout_add_book_info, null);
        tieName = alertLayout.findViewById(R.id.tieName);
        tieAuthor = alertLayout.findViewById(R.id.tieAuthor);
        final FrameLayout flCoverPage = alertLayout.findViewById(R.id.flCoverPage);
        ivCoverPage = alertLayout.findViewById(R.id.ivCoverPage);
        bookInfo = new HashMap<>();
        bookInformationModel = new BookInformationModel();

        dialogBuilder.setView(alertLayout);
        dialogBuilder.setCancelable(false);
        dialogBuilder.setPositiveButton("Okay", null);
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        flCoverPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });


        dialog = dialogBuilder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button btn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (tieName.getText().toString().trim().isEmpty()) {
                            tieName.setError(getResources().getString(R.string.required_field));
                        }

                        if (tieAuthor.getText().toString().trim().isEmpty()) {
                            tieAuthor.setError(getResources().getString(R.string.required_field));
                        }

                        if (isImageUploaded) {
                            if (!tieName.getText().toString().trim().isEmpty() && !tieAuthor.getText().toString().trim().isEmpty()) {
                                dialog.dismiss();
                                bookInformationModel.setName(tieName.getText().toString().trim());
                                bookInformationModel.setAuthor(tieAuthor.getText().toString().trim());
                                bookInformationModel.setCoverPageUri(coverPageUri.toString());
                                bookInfoList.add(bookInformationModel);

                                bookInfo.put("name", tieName.getText().toString().trim());
                                bookInfo.put("author", tieAuthor.getText().toString().trim());
                                bookInfo.put("coverPageUri", coverPageUri.toString());
                                bookInfoViewModel.setBookInfo(bookInfo);
                                bookInfoViewModel.getIsBookInfoUploaded().observe(HomeActivity.this, new Observer<Boolean>() {
                                    @Override
                                    public void onChanged(Boolean aBoolean) {
                                        bookListAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        } else {
                            Toast.makeText(HomeActivity.this, "Please upload the cover page first.", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });

        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filepath = data.getData();
            Picasso.with(this).load(filepath).placeholder(R.drawable.book_cover_place_holder).into(ivCoverPage);
            bookInfoViewModel.saveMediaInDB(filepath);
            bookInfoViewModel.getIsBookMediaUploaded().observe(this, new Observer<Uri>() {
                @Override
                public void onChanged(Uri uri) {
                    isImageUploaded = true;
                    coverPageUri = uri;
                }
            });
        }
    }

}
