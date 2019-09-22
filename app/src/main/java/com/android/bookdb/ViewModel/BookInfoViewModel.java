package com.android.bookdb.ViewModel;

import android.app.Application;
import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.bookdb.Database.BookInfoRepository;
import com.android.bookdb.Model.BookInformationModel;

import java.util.ArrayList;

public class BookInfoViewModel extends AndroidViewModel {

    private MutableLiveData<ArrayList<BookInformationModel>> bookInfoLiveData;
    private MutableLiveData<Boolean> isBookInfoFetched = new MutableLiveData<>();
    private MutableLiveData<Boolean> isBookInfoUploaded = new MutableLiveData<>();
    private MutableLiveData<Uri> isBookMediaUploaded = new MutableLiveData<>();

    public BookInfoViewModel(@NonNull Application application) {
        super(application);
        isBookInfoFetched.setValue(false);
        isBookInfoUploaded.setValue(false);
        isBookMediaUploaded.setValue(null);
    }

    public void init() {
        bookInfoLiveData = new BookInfoRepository(this).getBookInfo();
    }

    public LiveData<ArrayList<BookInformationModel>> getBookInfo() {
        return bookInfoLiveData;
    }

    public void setBookInfo(Object bookInfo) {
        new BookInfoRepository(this).setBookInfo(bookInfo);
    }

    public void saveMediaInDB(Uri filepath) {
        new BookInfoRepository(this).saveMediaInDB(filepath);
    }

    public MutableLiveData<Boolean> getIsBookInfoFetched() {
        return isBookInfoFetched;
    }

    public void setIsBookInfoFetched(Boolean isBookInfoFetched) {
        this.isBookInfoFetched.setValue(isBookInfoFetched);
    }

    public MutableLiveData<Boolean> getIsBookInfoUploaded() {
        return isBookInfoUploaded;
    }

    public void setIsBookInfoUploaded(Boolean isBookInfoUploaded) {
        this.isBookInfoUploaded.setValue(isBookInfoUploaded);
    }

    public MutableLiveData<Uri> getIsBookMediaUploaded() {
        return isBookMediaUploaded;
    }

    public void setIsBookMediaUploaded(Uri imageUri) {
        this.isBookMediaUploaded.setValue(imageUri);
    }
}
