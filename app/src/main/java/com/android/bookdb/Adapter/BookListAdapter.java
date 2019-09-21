package com.android.bookdb.Adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.bookdb.Model.BookInformationModel;
import com.android.bookdb.R;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class BookListAdapter extends RecyclerView.Adapter<BookListAdapter.BookListViewHolder> {
    private LayoutInflater layoutInflater;
    private Activity activity;
    private ArrayList<BookInformationModel> bookInfoList;

    public BookListAdapter(Activity activity, ArrayList<BookInformationModel> bookInfoList) {
        layoutInflater = LayoutInflater.from(activity);
        this.activity = activity;
        this.bookInfoList = bookInfoList;
    }

    @NonNull
    @Override
    public BookListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.layout_booklist_recycler_view_item, parent, false);
        return new BookListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BookListViewHolder holder, int position) {
        int pos = holder.getAdapterPosition();
        holder.tvBookName.setText(bookInfoList.get(pos).getName());
        holder.tvAuthorName.setText(bookInfoList.get(pos).getAuthor());
        Picasso.with(activity).load(Uri.parse(bookInfoList.get(pos).getCoverPageUri())).placeholder(R.drawable.oscover).into(holder.ivCoverPage);
    }

    @Override
    public int getItemCount() {
        return bookInfoList.size();
    }

    public class BookListViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivCoverPage;
        public TextView tvBookName, tvAuthorName;

        public BookListViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCoverPage = itemView.findViewById(R.id.ivCoverPage);
            tvBookName = itemView.findViewById(R.id.tvBookName);
            tvAuthorName = itemView.findViewById(R.id.tvAuthorName);
        }
    }
}
