package com.android.bookdb.Database;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.android.bookdb.Model.BookInformationModel;
import com.android.bookdb.ViewModel.BookInfoViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BookInfoRepository {

    public ArrayList<BookInformationModel> bookInfoList = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageReference;
    private BookInfoViewModel bookInfoViewModel;

    public BookInfoRepository(BookInfoViewModel bookInfoViewModel) {

        this.bookInfoViewModel = bookInfoViewModel;
    }

    public MutableLiveData<ArrayList<BookInformationModel>> getBookInfo() {

        getBookInfoFromDb();

        MutableLiveData<ArrayList<BookInformationModel>> data = new MutableLiveData<>();
        data.setValue(bookInfoList);
        return data;
    }

    public void setBookInfo(Object bookInfo) {
        db.collection("books").add(bookInfo).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d("my", "OnSuccess2:bookInfoAdded");
                bookInfoViewModel.setIsBookInfoUploaded(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("my", "OnFailure2" + e);
            }
        });
    }

    public void saveMediaInDB(Uri filepath) {
        storageReference = storage.getReference();

        final StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
        ref.putFile(filepath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Log.d("my", "OnSuccess3");
                                bookInfoViewModel.setIsBookMediaUploaded(uri);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("my", "OnFailure3" + e);
                    }
                });

    }

    private void getBookInfoFromDb() {

        db.collection("books").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                if (!queryDocumentSnapshots.isEmpty()) {
                    bookInfoList.clear();
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                    for (DocumentSnapshot documentSnapshot : list) {
                        bookInfoList.add(documentSnapshot.toObject(BookInformationModel.class));
                    }

                    Log.d("my", "OnSuccess:bookInfoAdded1");
                    bookInfoViewModel.setIsBookInfoFetched(true);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("my", "OnFailure1" + e);
            }
        });
    }
}
