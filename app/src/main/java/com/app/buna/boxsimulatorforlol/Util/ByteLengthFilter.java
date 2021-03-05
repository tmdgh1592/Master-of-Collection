package com.app.buna.boxsimulatorforlol.Util;

import android.text.InputFilter;
import android.text.Spanned;

import java.io.UnsupportedEncodingException;

public class ByteLengthFilter implements InputFilter {


    private String mCharset; //인코딩 문자셋

    protected int mMaxByte; // 입력가능한 최대 바이트 길이

    public ByteLengthFilter(int maxbyte, String charset) {
        this.mMaxByte = maxbyte;
        this.mCharset = charset;
    }

    /**
     * 이 메소드는 입력/삭제 및 붙여넣기/잘라내기할 때마다 실행된다.
     *
     * - source : 새로 입력/붙여넣기 되는 문자열(삭제/잘라내기 시에는 "")
     * - dest : 변경 전 원래 문자열
     */
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart,
                               int dend) {

        // 변경 후 예상되는 문자열
        String expected = new String();
        expected += dest.subSequence(0, dstart);
        expected += source.subSequence(start, end);
        expected += dest.subSequence(dend, dest.length());

        int keep = calculateMaxLength(expected) - (dest.length() - (dend - dstart));

        if (keep <= 0) {
            return ""; // source 입력 불가(원래 문자열 변경 없음)
        } else if (keep >= end - start) {
            return null; // keep original. source 그대로 허용
        } else {
            return source.subSequence(start, start + keep); // source중 일부만 입력 허용
        }
    }

    /**
     * 입력가능한 최대 문자 길이(최대 바이트 길이와 다름!).
     */
    protected int calculateMaxLength(String expected) {
        return mMaxByte - (getByteLength(expected) - expected.length());
    }

    /**
     * 문자열의 바이트 길이.
     * 인코딩 문자셋에 따라 바이트 길이 달라짐.
     * @param str
     * @return
     */
    public int getByteLength(String str) {
        try {
            return str.getBytes(mCharset).length;
        } catch (UnsupportedEncodingException e) {
            //e.printStackTrace();
        }
        return 0;
    }
}

