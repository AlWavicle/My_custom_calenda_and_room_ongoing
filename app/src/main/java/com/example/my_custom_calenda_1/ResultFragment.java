package com.example.my_custom_calenda_1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.ViewModelProvider;

import com.example.my_custom_calenda_and_room.R;

public class ResultFragment extends Fragment {

    private SharedViewModel viewModel;
    private TextView resultTextView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ==========================================
        // 핵심: "DATA_KEY"라는 주소로 데이터가 날아오는지 대기합니다.
        // ==========================================
        getParentFragmentManager().setFragmentResultListener("DATA_KEY", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                // 데이터가 도착하면 상자를 열어서 텍스트뷰에 세팅!
                String receivedData = bundle.getString("QUERY_DATA");
                if (resultTextView != null) {
                    resultTextView.setText(receivedData != null ? receivedData : "조회된 데이터가 없습니다."  );
                }
            }
        });
    }






    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // fragment_result.xml 레이아웃 인플레이트
        View view = inflater.inflate(R.layout.fragment_result, container, false);

        // 뷰 초기화
        resultTextView = view.findViewById(R.id.resultTextView);

        // MainActivity를 생명주기(Scope)로 하는 SharedViewModel 가져오기
        // 이렇게 해야 쿼리 작성 페이지(QueryFragment)와 동일한 뷰모델 인스턴스를 공유합니다.
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // ViewModel의 resultText(LiveData)를 관찰(Observe)합니다.
        // 데이터가 변경되면 내부 코드가 자동으로 실행되어 UI를 갱신합니다.
        viewModel.getResultText().observe(getViewLifecycleOwner(), text -> {
            if (text != null && !text.isEmpty()) {
                resultTextView.setText(text);
            } else {
                resultTextView.setText("실행된 쿼리 결과가 여기에 표시됩니다.");
            }
        });

        return view;
    }
}