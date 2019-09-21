package com.android.bookdb.Database;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.android.bookdb.Listener.OnBookInfoAdded;
import com.android.bookdb.Listener.OnMediaUploaded;
import com.android.bookdb.Model.BookInformationModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
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

    private static BookInfoRepository instance;
    public ArrayList<BookInformationModel> bookInfoList = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static OnBookInfoAdded infoAdded;
    private static OnMediaUploaded mediaUploaded;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageReference;

    public static BookInfoRepository getRepositoryInstance(Context context) {

        if (instance == null) {
            instance = new BookInfoRepository();
        }
        infoAdded = (OnBookInfoAdded) context;
        mediaUploaded = (OnMediaUploaded) context;
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
                                mediaUploaded.uploaded(uri);
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
