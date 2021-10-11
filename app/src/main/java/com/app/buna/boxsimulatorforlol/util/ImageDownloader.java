package com.app.buna.boxsimulatorforlol.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.app.buna.boxsimulatorforlol.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageDownloader extends AsyncTask<String, Void, Void> {

    private String fileName;
    private Context context;
    private final String SAVE_FOLDER = "/master_of_collection";


    public ImageDownloader(Context context){
        this.context = context;
    }

    @Override
    protected Void doInBackground(String... strings) {
        //다운로드 경로를 지정
        String savePath = Environment.getExternalStorageDirectory().toString() + SAVE_FOLDER;

        File dir = new File(savePath);

        //상위 디렉토리가 존재하지 않을 경우 생성
        if (!dir.exists()) {
            dir.mkdirs();
        }

        //파일 이름 :날짜_시간
        Date day = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.KOREA);
        fileName = String.valueOf(sdf.format(day));

        //웹 서버 쪽 파일이 있는 경로
        String fileUrl = strings[0];

        //다운로드 폴더에 동일한 파일명이 존재하는지 확인
        if(new File(savePath + "/" + fileName).exists() == false) {
        } else {
        }

        String localPath = savePath + "/" + fileName + ".jpg";

        try {
            URL imgUrl = new URL(fileUrl);
            //서버와 접속하는 클라이언트 객체 생성
            HttpURLConnection conn = (HttpURLConnection)imgUrl.openConnection();
            int len = conn.getContentLength();
            byte[] tmpByte = new byte[len];
            //입력 스트림을 구한다
            InputStream is = conn.getInputStream();
            File file = new File(localPath);
            //파일 저장 스트림 생성
            FileOutputStream fos = new FileOutputStream(file);
            int read;
            //입력 스트림을 파일로 저장
            for (;;) {
                read = is.read(tmpByte);
                if (read <= 0) {
                    break;
                }
                fos.write(tmpByte, 0, read); //file 생성
            }
            is.close();
            fos.close();
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        Toast.makeText(context, context.getString(R.string.download_success), Toast.LENGTH_SHORT).show();
        //저장한 이미지 열기
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        String targetDir = Environment.getExternalStorageDirectory().toString() + SAVE_FOLDER;
        File file = new File(targetDir + "/" + fileName + ".jpg");
        //type 지정 (이미지)
        Uri photoUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
        intent.setDataAndType(photoUri, "image/*");

        context.startActivity(intent);
        //이미지 스캔해서 갤러리 업데이트

        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, photoUri));
    }

    @Override
    protected void onCancelled(Void aVoid) {
        super.onCancelled(aVoid);
        Toast.makeText(context, context.getString(R.string.download_fail), Toast.LENGTH_SHORT).show();
    }


}
