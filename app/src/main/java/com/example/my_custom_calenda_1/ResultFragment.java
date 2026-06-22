package com.example.my_custom_calenda_1;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.ViewModelProvider;

import com.example.my_custom_calenda_and_room.R;

public class ResultFragment extends Fragment {

    // 중복 선언 제거: viewModel은 하나만 있으면 됩니다.
    private SharedViewModel viewModel;
    private TextView resultTextView;
    private Button find_commit_btn;
    private Button find_cancel_btn;

    // 전달받은 쿼리 문자열을 저장할 전역 변수
    private String rawInsertQuery = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 이전 프래그먼트에서 보낸 쿼리문 데이터 대기
        getParentFragmentManager().setFragmentResultListener("DATA_KEY", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                String receivedData = bundle.getString("QUERY_DATA");
                if (receivedData != null) {
                    rawInsertQuery = receivedData;
                    if (resultTextView != null) {
                        resultTextView.setText(receivedData);
                    }
                } else {
                    if (resultTextView != null) {
                        resultTextView.setText("조회된 데이터가 없습니다.");
                    }
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result, container, false);

        // 1. 뷰 초기화
        resultTextView = view.findViewById(R.id.resultTextView);
        find_commit_btn = view.findViewById(R.id.commit_btn);
        find_cancel_btn = view.findViewById(R.id.cancel_btn);

        // 2. 🚀 [가장 중요] 버튼을 누르기 전에 뷰모델을 가장 먼저 초기화해서 가져옵니다!
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // 3. ViewModel의 LiveData 관찰 세팅 (화면 자동 업데이트)
        viewModel.getResultText().observe(getViewLifecycleOwner(), text -> {
            if (text != null && !text.isEmpty()) {
                resultTextView.setText(text);
            }
        });

        // ==========================================
        // 4. [커밋 버튼] 클릭 이벤트
        // ==========================================
        find_commit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 🚀 SharedViewModel의 공통 쓰레드(executor) 사용!
                viewModel.executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // 🚀 [수정] 혹시 모르니 DB 체크 (대부분 QueryFragment에서 열려있을 것임)
                            if (viewModel.mDb != null && viewModel.mDb.isOpen()) {
                                // 이미 걸려있는 트랜잭션에 커밋 도장 쾅!
                                viewModel.mDb.execSQL("COMMIT;");

                                // LiveData를 통해 메인 화면(UI) 업데이트 요청 (Handler 필요 없음!)
                                viewModel.setResultText("✅ 최종 커밋 완료! 데이터가 영구 저장되었습니다.");
                            } else {
                                viewModel.setResultText("⚠️ 에러: 실행 중인 쿼리가 없거나 이미 커밋되었습니다.");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            viewModel.setResultText("❌ 커밋 실패: " + e.getMessage());
                        } finally {
                            // 🚀 커밋/롤백이 끝난 후에만 DB를 닫아줍니다.
                            if (viewModel.mDb != null && viewModel.mDb.isOpen()) {
                                viewModel.mDb.close();
                                viewModel.mDb = null;
                            }
                        }
                    }
                });
            }
        });

        // ==========================================
        // 5. [취소(롤백) 버튼] 클릭 이벤트
        // ==========================================
        find_cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 🚀 똑같은 쓰레드(executor)에 작업을 던집니다.
                viewModel.executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (viewModel.mDb != null && viewModel.mDb.isOpen()) {
                                // 맘에 안 들면 실행 취소! (원상 복구)
                                viewModel.mDb.execSQL("ROLLBACK;");

                                viewModel.setResultText("🔙 취소 완료! 데이터가 원래대로 복구되었습니다.");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            viewModel.setResultText("❌ 롤백 실패: " + e.getMessage());
                        } finally {
                            // 🚀 롤백이 완전히 끝났으므로 DB 문을 닫아줍니다.
                            if (viewModel.mDb != null && viewModel.mDb.isOpen()) {
                                viewModel.mDb.close();
                                viewModel.mDb = null;
                            }
                        }
                    }
                });
            }
        });

        return view;
    }
}