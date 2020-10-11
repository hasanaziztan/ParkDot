package com.azo.hastagram;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.azo.hastagram.Models.IonClicled;
import com.azo.hastagram.Models.User;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetNameEditDialog extends BottomSheetDialogFragment {
    User user;
    EditText bottomSheetMaplistNameEt;
    EditText bottomSheetMaplistSurnameEt;
    Button saveBtn;
    View v;
    IonClicled ionClicled;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.bottom_sheet_name_edit, container, false);

        saveBtn = v.findViewById(R.id.edit_userName_bottomSheetsaveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ionClicled.onClicked(bottomSheetMaplistNameEt.getText().toString(),
                        bottomSheetMaplistSurnameEt.getText().toString());
                dismiss();
            }
        });
        bottomSheetMaplistNameEt = v.findViewById(R.id.edit_userName_bottomSheet);
        bottomSheetMaplistSurnameEt = v.findViewById(R.id.edit_surname_bottomSheet);
        if (user != null && !TextUtils.isEmpty(user.getName()) && !TextUtils.isEmpty(user.getSurname()))
            bottomSheetMaplistNameEt.setText(user.getName());
        bottomSheetMaplistSurnameEt.setText(user.getSurname());
        return v;
    }

    public BottomSheetNameEditDialog setPark(User user) {
        this.user = user;
        return this;
    }

    public BottomSheetNameEditDialog setOnclickListener(IonClicled onclickListener) {
        ionClicled = onclickListener;
        return this;
    }

}
