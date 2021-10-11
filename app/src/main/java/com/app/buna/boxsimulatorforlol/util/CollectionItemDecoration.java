package com.app.buna.boxsimulatorforlol.util;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CollectionItemDecoration extends RecyclerView.ItemDecoration {

    private final int marginBottom;

    public CollectionItemDecoration(int marginBottom){
        this.marginBottom = marginBottom;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = marginBottom;
    }
}
