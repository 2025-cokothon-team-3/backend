package com.example.cokothon.service;

import com.example.cokothon.entity.User;
import com.example.cokothon.entity.TestResult;
import com.example.cokothon.repository.UserRepository;
import com.example.cokothon.repository.TestResultRepository;
import com.example.cokothon.dto.UserSearchDto;
import com.example.cokothon.common.response.ApiResponse;
import com.example.cokothon.dto.TestResultDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final TestResultRepository testResultRepository;

    @Transactional
    public ApiResponse<User> loginCheck(String nickname){
    	
    	if(nickname.length()>15) {
    		return ApiResponse.failure("닉네임은 15자 이내로 작성해주세요.",null);
    	}else if(userRepository.existsByNickname(nickname)) {
    		return ApiResponse.failure("이미 존재하는 닉네임입니다.",null);
    	}else if(nickname.equals("")||nickname == null) {
    		return ApiResponse.failure("원하는 닉네임을 입력해주세요",null);
    	}
    	
    	User newUser = User.builder()
                .nickname(nickname)
                .build();

        userRepository.save(newUser);

        return ApiResponse.success("로그인 성공", newUser);
    }

    /**
     * 닉네임으로 사용자 검색 (테스트 완료자만)
     */
    public List<UserSearchDto> searchUsersByNickname(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }

        List<User> users = userRepository.findUsersWithTestResultsByNickname(keyword.trim());
        return users.stream()
                .map(this::convertToSearchDto)
                .collect(Collectors.toList());
    }

    /**
     * 전체 테스트 완료 사용자 조회 (최신순)
     */
    public List<UserSearchDto> getAllUsersWithTestResults() {
        List<User> users = userRepository.findActiveUsersOrderByCreatedAtDesc();
        return users.stream()
                .map(this::convertToSearchDto)
                .filter(dto -> dto.getTestResult() != null) // 테스트 결과가 있는 사용자만
                .collect(Collectors.toList());
    }

    /**
     * 사용자 ID로 조회
     */
    public UserSearchDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));
        return convertToSearchDto(user);
    }

    /**
     * 사용자 존재 여부 확인
     */
    public boolean existsByNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    /**
     * User를 UserSearchDto로 변환
     */
    private UserSearchDto convertToSearchDto(User user) {
        Optional<TestResult> testResult = testResultRepository.findTopByUserIdOrderByCreatedAtDesc(user.getId());

        TestResultDto testResultDto = null;
        if (testResult.isPresent()) {
            TestResult result = testResult.get();
            testResultDto = TestResultDto.builder()
                    .planningType(result.getPlanningType())
                    .budgetType(result.getBudgetType())
                    .activityType(result.getActivityType())
                    .socialType(result.getSocialType())
                    .planningScore(result.getPlanningScore())
                    .budgetScore(result.getBudgetScore())
                    .activityScore(result.getActivityScore())
                    .socialScore(result.getSocialScore())
                    .dominantType(result.getDominantType())
                    .build();
        }

        return UserSearchDto.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .testResult(testResultDto)
                .createdAt(user.getCreatedAt())
                .build();
    }

}

