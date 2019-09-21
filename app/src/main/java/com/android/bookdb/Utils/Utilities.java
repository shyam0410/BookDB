package com.android.bookdb.Utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import com.android.bookdb.Listener.DialogDataListener;
import com.android.bookdb.R;

public class Utilities {

    private static androidx.appcompat.app.AlertDialog.Builder dialogBuilder;


    public static void createDialog(final Activity context, final String negativeBtnText, final String positiveBtnText, final DialogDataListener listener) {
        dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(context);

        LayoutInflater layoutInflater = context.getLayoutInflater();
        final View alertLayout = layoutInflater.inflate(R.layout.layout_add_book_info, null);
        final EditText tieName = alertLayout.findViewById(R.id.tieName);

        final EditText tieAuthor = alertLayout.findViewById(R.id.tieAuthor);

        dialogBuilder.setView(alertLayout);
        dialogBuilder.setCancelable(false);
        dialogBuilder.setPositiveButton(positiveBtnText, null);
        dialogBuilder.setNegativeButton(negativeBtnText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog dialog = dialogBuilder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button btn = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (tieName.getText().toString().trim().isEmpty()) {
                            tieName.setError(context.getResources().getString(R.string.required_field));
                        }

                        if (tieAuthor.getText().toString().trim().isEmpty()) {
                            tieAuthor.setError(context.getResources().getString(R.string.required_field));
                        }

                        if (!tieName.getText().toString().trim().isEmpty() && !tieAuthor.getText().toString().trim().isEmpty()) {
                            listener.getData(tieName.getText().toString().trim(), tieAuthor.getText().toString().trim());
                            dialog.dismiss();
                        }
                    }
                });
            }
        });

        dialog.show();
    }
}
