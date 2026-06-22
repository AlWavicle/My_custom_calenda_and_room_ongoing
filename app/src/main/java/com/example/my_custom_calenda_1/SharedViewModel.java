// SharedViewModel.java
package com.example.my_custom_calenda_1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SharedViewModel extends ViewModel {
    // 쿼리문 작성 페이지의 데이터를 보관
    private final MutableLiveData<String> queryText = new MutableLiveData<>("select * from events");
    // 결과 페이지의 데이터를 보관
    private final MutableLiveData<String> resultText = new MutableLiveData<>("");

    // 쿼리 기록 저장용
    private final List<String> queryHistory = new ArrayList<>();
    private int historyIndex = -1;


    // 프래그먼트가 아니라 뷰모델이 일꾼과 DB를 가집니다.
    public final ExecutorService executor = Executors.newSingleThreadExecutor();
    public SQLiteDatabase mDb = null;

    public final Handler handler = new Handler(Looper.getMainLooper());

    // DB가 없거나 닫혀있으면 새로 열어서 반환하는 헬퍼 메서드
    public SQLiteDatabase getOrOpenDatabase(Context context, String dbName) {
        if (mDb == null || !mDb.isOpen()) {
            // openOrCreateDatabase는 실행한 쓰레드에서 즉시 반환하므로 동기적으로 처리 가능
            mDb = context.openOrCreateDatabase(dbName, Context.MODE_PRIVATE, null);
        }
        return mDb;
    }

    // 뷰모델이 파괴될 때(앱 종료 등) 안전하게 닫아주는 역할
    @Override
    protected void onCleared() {
        super.onCleared();
        if (mDb != null && mDb.isOpen()) {
            mDb.close();
        }
        executor.shutdown();
    }




    public MutableLiveData<String> getQueryText() {
        return queryText;
    }

    public void setQueryText(String text) {
        queryText.setValue(text);
    }

    public MutableLiveData<String> getResultText() {
        return resultText;
    }

    public void setResultText(String text) {
        // 백그라운드 쓰레드(executor)에서 값을 전달할 때는 반드시 postValue를 써야 합니다.
        // 안드로이드가 알아서 메인 쓰레드로 택배를 보내 화면을 바꿔줍니다.
        resultText.postValue(text);
    }

    // 쿼리 실행 시 기록에 추가
    public void addQueryToHistory(String query) {
        if (query == null || query.trim().isEmpty()) return;
        
        // 마지막 기록과 중복되지 않을 때만 추가
        if (queryHistory.isEmpty() || !queryHistory.get(queryHistory.size() - 1).equals(query)) {
            queryHistory.add(query);
        }
        historyIndex = queryHistory.size(); // 새 쿼리 추가 후 인덱스를 끝으로 보냄
    }

    // 이전 쿼리 가져오기
    public String getPreviousQuery() {
        if (queryHistory.isEmpty()) return null;
        
        if (historyIndex > 0) {
            historyIndex--;
        } else {
            historyIndex = 0;
        }
        return queryHistory.get(historyIndex);
    }

    // 다음 쿼리 가져오기
    public String getNextQuery() {
        if (queryHistory.isEmpty()) return null;

        if (historyIndex < queryHistory.size() - 1) {
            historyIndex++;
            return queryHistory.get(historyIndex);
        } else {
            historyIndex = queryHistory.size();
            return ""; // 마지막 이후는 빈 화면
        }
    }
}