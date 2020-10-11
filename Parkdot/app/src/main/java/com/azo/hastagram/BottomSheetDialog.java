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

import com.azo.hastagram.Models.IparkNameUpdate;
import com.azo.hastagram.Models.Park;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetDialog extends BottomSheetDialogFragment {
    Park park;
    EditText bottomSheetEt;
    IparkNameUpdate iparkNameUpdate;
    Button saveParkNameBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.bottom_sheet_dialog, container, false);
        saveParkNameBtn = v.findViewById(R.id.edit_name_save);
        saveParkNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iparkNameUpdate.parkNameUpdate(bottomSheetEt.getText().toString());
            }
        });
        bottomSheetEt = v.findViewById(R.id.edit_name_textview);
        if (park != null && !TextUtils.isEmpty(park.getParkName()))
            bottomSheetEt.setText(park.getParkName());

        return v;
    }

    public BottomSheetDialog setPark(Park park) {
        this.park = park;

        return this;
    }
    public BottomSheetDialog setOnclickListener (IparkNameUpdate onclickListener){
        iparkNameUpdate = onclickListener;
        return this;
    }



}
