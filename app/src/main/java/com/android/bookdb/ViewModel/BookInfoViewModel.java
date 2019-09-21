package com.android.bookdb.ViewModel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.bookdb.Database.BookInfoRepository;
import com.android.bookdb.Model.BookInformationModel;

import java.util.ArrayList;

public class BookInfoViewModel extends AndroidViewModel {

    private MutableLiveData<ArrayList<BookInformationModel>> bookInfoLiveData;

    public BookInfoViewModel(@NonNull Application application) {
        super(application);
    }

    public void init(Context context) {
        bookInfoLiveData = BookInfoRepository.getRepositoryInstance(context).getBookInfo();
    }

    public LiveData<ArrayList<BookInformationModel>> getBookInfo() {
        return bookInfoLiveData;
    }

    public void setBookInfo(Context context, Object bookInfo) {
       BookInfoRepository.getRepositoryInstance(context).setBookInfo(bookInfo);
    }
}
