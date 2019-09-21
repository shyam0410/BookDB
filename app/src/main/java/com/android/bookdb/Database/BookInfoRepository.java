package com.android.bookdb.Database;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.android.bookdb.Listener.OnBookInfoAdded;
import com.android.bookdb.Model.BookInformationModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class BookInfoRepository {

    private static BookInfoRepository instance;
    public ArrayList<BookInformationModel> bookInfoList = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static OnBookInfoAdded infoAdded;

    public static BookInfoRepository getRepositoryInstance(Context context) {

        if (instance == null) {
            instance = new BookInfoRepository();
        }
        infoAdded = (OnBookInfoAdded) context;
        return instance;
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
                infoAdded.bookInfoAdded();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("my", "OnFailure2" + e);
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
                    infoAdded.bookInfoAdded();
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
