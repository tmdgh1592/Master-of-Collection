package com.app.buna.boxsimulatorforlol.Util;

import android.content.Context;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MyInfoSkillDecoration extends RecyclerView.ItemDecoration {

    private final int divWidth;

    public MyInfoSkillDecoration(int divWidth) {
        this.divWidth = divWidth;
    }


    @Override
    public void getItemOffsets
            (@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = divWidth;
    }

}
