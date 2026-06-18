// SharedViewModel.java
package com.example.my_custom_calenda_1;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    // 쿼리문 작성 페이지의 데이터를 보관
    private final MutableLiveData<String> queryText = new MutableLiveData<>("");
    // 결과 페이지의 데이터를 보관
    private final MutableLiveData<String> resultText = new MutableLiveData<>("");

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
}