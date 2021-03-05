package com.app.buna.boxsimulatorforlol.Fragment;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.app.buna.boxsimulatorforlol.Adapter.CollectionAdapter;
import com.app.buna.boxsimulatorforlol.DB.DBHelper;
import com.app.buna.boxsimulatorforlol.DTO.CollectData;
import com.app.buna.boxsimulatorforlol.DTO.ItemFragment;
import com.app.buna.boxsimulatorforlol.R;
import com.app.buna.boxsimulatorforlol.Util.CollectionItemDecoration;

import java.util.ArrayList;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.app.buna.boxsimulatorforlol.VO.CollectionType.COLLECTION_CHAMP_ITEM;
import static com.app.buna.boxsimulatorforlol.VO.CollectionType.COLLECTION_SKIN_ITEM;

public class SkinCollectFragment extends Fragment {

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
        View view = inflater.inflate(R.layout.fragment_skin_collect, container, false);
        itemLoadBar = view.findViewById(R.id.skin_collection_item_loading_bar);
        return view;
    }

    public static ChampCollectFragment newInstance() {
        return new ChampCollectFragment();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        itemList = getCollectData();
        setFirstData();

        recyclerView = view.findViewById(R.id.collection_recycler_view);
        recyclerView.addItemDecoration(new CollectionItemDecoration(25));
        adapter = new CollectionAdapter(getActivity(), tempList, COLLECTION_SKIN_ITEM);
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

        Cursor cursor = db.rawQuery("SELECT * FROM skin_table", null);

        Log.d("", "getCollectData TOTAL COUNT: " + cursor.getCount());

        while(cursor.moveToNext()){
            CollectData collectData = new CollectData(cursor.getString(0), cursor.getString(1), cursor.getString(2));
            data.add(collectData);
        }

        return data;
    }

    private void setFirstData() {
        int defaultSize = itemList.size();
        int rotationSize;

        /*loadMoreSize보다 적게 있을 경우엔 있는 것만 가져옴*/
        if(defaultSize < loadMoreSize){
            rotationSize = defaultSize;
        }else {
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

                Log.d(TAG, "run: currentSize : " + currentSize);
                Log.d(TAG, "run: totalSize : " + totalSize);
                Log.d(TAG, "run: nextLimit : " + nextLimit);

                for(int i=currentSize; i<nextLimit; i++){
                    Log.d(TAG, "i : " + i);
                    if(i == totalSize){
                        adapter.onDataSetChanged(tempList);
                        isLoading = false;
                        Log.d(TAG, "run: currentSize is the end : i = " + i + " / totalSize : " + totalSize);
                        return;
                    }
                    tempList.add(itemList.get(i));
                }
                adapter.onDataSetChanged(tempList);
                isLoading = false;
            }
        },1200);
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

                GridLayoutManager gridLayoutManager = (GridLayoutManager)recyclerView.getLayoutManager();

                if(!isLoading){
                    if((gridLayoutManager != null) && (gridLayoutManager.findLastCompletelyVisibleItemPosition() == tempList.size()-1)){ // 화면의 마지막까지 스크롤 했을 때 데이터 더 불러옴, 로딩바 보여줌
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
