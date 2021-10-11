package com.app.buna.boxsimulatorforlol.fragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.app.buna.boxsimulatorforlol.adapter.CollectionAdapter;
import com.app.buna.boxsimulatorforlol.db.DBHelper;
import com.app.buna.boxsimulatorforlol.dto.CollectData;
import com.app.buna.boxsimulatorforlol.R;
import com.app.buna.boxsimulatorforlol.util.CollectionItemDecoration;

import java.util.ArrayList;

import static com.app.buna.boxsimulatorforlol.vo.CollectionType.COLLECTION_CHAMP_ITEM;

public class ChampCollectFragment extends Fragment {

    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private CollectionAdapter adapter;

    private ArrayList<CollectData> itemList;
    private ArrayList<CollectData> tempList = new ArrayList<>();
    private ProgressBar itemLoadBar;
    private int loadMoreSize = 28;
    private boolean isLoading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_champ_collect, container, false);
        itemLoadBar = view.findViewById(R.id.champ_collection_item_loading_bar);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        itemList = getCollectData();
        setFirstData();

        recyclerView = view.findViewById(R.id.collection_recycler_view);
        recyclerView.addItemDecoration(new CollectionItemDecoration(25));
        adapter = new CollectionAdapter(getActivity(), itemList, COLLECTION_CHAMP_ITEM);
        gridLayoutManager = new GridLayoutManager(getActivity(), 4);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 1;
            }
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(gridLayoutManager);

        initScrollListener();
        itemLoadBar.setVisibility(View.GONE);
    }

    private ArrayList<CollectData> getCollectData() {
        ArrayList<CollectData> data = new ArrayList<>();
        DBHelper dbHelper = new DBHelper(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM champ_table", null);
        while(cursor.moveToNext()){
            CollectData collectData = new CollectData(cursor.getString(0), cursor.getString(1), cursor.getString(2));
            data.add(collectData);
        }

        return data;
    }

    private void setFirstData(){
        int defaultSize = itemList.size();
        int rotationSize;

        if(defaultSize < loadMoreSize){
            rotationSize = defaultSize;
        }else{
            rotationSize = loadMoreSize;
        }

        for(int i=0; i<rotationSize; i++){
            tempList.add(itemList.get(i));
        }
    }

    private void dataMore(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int currentSize = tempList.size();
                int totalSize = itemList.size();
                int nextLimit = currentSize + loadMoreSize;

                for(int i=currentSize; i<nextLimit; i++){
                    if(i == totalSize){
                        adapter.notifyDataSetChanged();
                        isLoading = false;
                        return;
                    }
                    tempList.add(itemList.get(i));
                }
                adapter.notifyDataSetChanged();
                isLoading = false;
            }
        }, 1200);
    }

    private void initScrollListener(){
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                GridLayoutManager gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();

                if(isLoading == false){
                    if((gridLayoutManager != null) && (gridLayoutManager.findLastCompletelyVisibleItemPosition() == tempList.size()-1)){
                        dataMore();
                        isLoading = true;
                        itemLoadBar.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                itemLoadBar.setVisibility(View.GONE);
                            }
                        }, 1200);
                    }
                }
            }
        });
    }
}
