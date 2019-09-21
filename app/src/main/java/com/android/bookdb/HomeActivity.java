package com.android.bookdb;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.android.bookdb.Adapter.BookListAdapter;
import com.android.bookdb.Listener.DialogDataListener;
import com.android.bookdb.Listener.OnBookInfoAdded;
import com.android.bookdb.Model.BookInformationModel;
import com.android.bookdb.R;
import com.android.bookdb.Utils.Utilities;
import com.android.bookdb.ViewModel.BookInfoViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, OnBookInfoAdded {

    private FloatingActionButton fabAdd;
    private ArrayList<BookInformationModel> bookInfoList;
    private BookListAdapter bookListAdapter;
    private RecyclerView rvBookList;
    private BookInfoViewModel bookInfoViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        FirebaseApp.initializeApp(this);
        fabAdd = findViewById(R.id.fabAdd);
        rvBookList = findViewById(R.id.rvBookList);
        bookInfoViewModel = ViewModelProviders.of(this).get(BookInfoViewModel.class);
        bookInfoViewModel.init(this);
        bookInfoList = new ArrayList<>();
        initViews();
    }

    private void initViews() {

        bookInfoList = bookInfoViewModel.getBookInfo().getValue();
        bookListAdapter = new BookListAdapter(this, bookInfoList);
        rvBookList.setAdapter(bookListAdapter);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false);
        rvBookList.setLayoutManager(gridLayoutManager);
        fabAdd.setOnClickListener(this);
        rvBookList.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
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
            Utilities.createDialog(this, "Cancel", "Okay", new DialogDataListener() {
                @Override
                public void getData(String bookName, String authorName) {
                    BookInformationModel bookInformationModel = new BookInformationModel();
                    bookInformationModel.setName(bookName);
                    bookInformationModel.setAuthor(authorName);
                    bookInfoList.add(bookInformationModel);

                    Map<String, Object> bookInfo = new HashMap<>();
                    bookInfo.put("name", bookName);
                    bookInfo.put("author", authorName);
                    bookInfoViewModel.setBookInfo(HomeActivity.this, bookInfo);
                }
            });
        }
    }

    @Override
    public void bookInfoAdded() {
        bookListAdapter.notifyDataSetChanged();
    }
}
