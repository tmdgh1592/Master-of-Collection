package com.app.buna.boxsimulatorforlol.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.graphics.Rect;
import android.icu.text.RelativeDateTimeFormatter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.buna.boxsimulatorforlol.Adapter.RankingAdapter;
import com.app.buna.boxsimulatorforlol.DB.DBHelper;
import com.app.buna.boxsimulatorforlol.DTO.RankData;
import com.app.buna.boxsimulatorforlol.R;
import com.app.buna.boxsimulatorforlol.Util.LangUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class RankActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RankingAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private TextView rankText, nameText, champCntText, skinCntText;
    private SharedPreferences setting;

    private int myRank = 0;

    List<RankData> rankDataArrayList;

    private DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("rank");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LangUtil.setLang(this);
        setContentView(R.layout.activity_rank);

        setting = getSharedPreferences("setting", MODE_PRIVATE);

        rankText = findViewById(R.id.main_rank_text);
        nameText = findViewById(R.id.main_rank_nickname_text);
        champCntText = findViewById(R.id.main_rank_champ_count_text);
        skinCntText = findViewById(R.id.main_rank_skin_count_text);
        recyclerView = findViewById(R.id.rank_recycler);
        setRecycler();
    }

    private void setRecycler(){
        /*ranking data 가져오기*/

        setMyRanking();
        rankDataArrayList = new ArrayList<>();
        setRankDataArray();

        Log.d("", "setRecycler: " + rankDataArrayList.size());

        adapter = new RankingAdapter(RankActivity.this, rankDataArrayList);
        adapter.setHasStableIds(true);
        linearLayoutManager = new LinearLayoutManager(RankActivity.this);

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom = 18;
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void setMyRanking(){

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                final String nickname = setting.getString("nickname", "");


                Query query = myRef.orderByChild("totalCount").limitToLast(1000000);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            /*firebase에서 가져올 땐 Map형태로 가져와야함*/
                            Map<String, Object> map = (HashMap<String, Object>)snapshot.getValue();
                            myRank++;

                            if(map.get("nickname").toString().equals(nickname)) {
                                rankText.setText("" + (dataSnapshot.getChildrenCount() - myRank + 1));  // 본인 랭킹
                                champCntText.setText("" + ((Long) map.get("champCount")).intValue());
                                skinCntText.setText("" + ((Long) map.get("skinCount")).intValue());
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        DBHelper dbHelper = new DBHelper(RankActivity.this);
                        rankText.setText("-");  // 본인 랭킹
                        champCntText.setText("" + dbHelper.getChampCount());
                        skinCntText.setText("" + dbHelper.getSkinCount());
                        dbHelper.close();
                    }
                });
                nameText.setText(setting.getString("nickname", getString(R.string.temp_user_name)));
            }
        });

    }

    /* 랭킹 데이터를 가져와서 전역변수 arraylist에 값을 넣음*/
    /*it isn't working at singValueEventListener*/
    private void setRankDataArray() {

        Query query = myRef.orderByChild("totalCount").limitToLast(30);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    /*firebase에서 가져올 땐 Map형태로 가져와야함*/
                    Map<String, Object> map = (HashMap<String, Object>) snapshot.getValue();
                    RankData data;
                    try {
                         data = new RankData(map.get("nickname").toString(),
                                (int) (long) map.get("champCount"),
                                (int) (long) map.get("skinCount"),
                                (int) (long) map.get("totalCount"),
                                (long) map.get("created"));
                    }catch (NullPointerException e){
                        data = new RankData(map.get("nickname").toString(),
                                0,
                                0,
                                0,
                                0);
                    }
                    rankDataArrayList.add(data);

                }
                //랭킹 순위를 위해 등록한 순으로 2차 정렬
                Collections.sort(rankDataArrayList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume() { // 화면이 onResume 되었을 때 restart
        super.onResume();
        if(MainActivity.bgmService != null && setting.getBoolean("BgmState", true) == true) {
            MainActivity.bgmService.restartBgm();
        }
    }

    @Override
    protected void onUserLeaveHint() {  // 홈버튼 누르거나 액티비티 전환할 때
        if(MainActivity.bgmService != null) {
            MainActivity.bgmService.pauseBgm();      // 노래 멈춤
        }
        super.onUserLeaveHint();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finish();
    }

}
