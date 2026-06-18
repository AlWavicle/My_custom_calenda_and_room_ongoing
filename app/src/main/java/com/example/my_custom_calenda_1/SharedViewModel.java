// SharedViewModel.java
package com.example.my_custom_calenda_1;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import java.util.List;

public class SharedViewModel extends ViewModel {
    // 쿼리문 작성 페이지의 데이터를 보관
    private final MutableLiveData<String> queryText = new MutableLiveData<>("");
    // 결과 페이지의 데이터를 보관
    private final MutableLiveData<String> resultText = new MutableLiveData<>("");

    // 쿼리 기록 저장용
    private final List<String> queryHistory = new ArrayList<>();
    private int historyIndex = -1;

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
        resultText.setValue(text);
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