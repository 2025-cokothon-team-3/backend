package com.example.cokothon.service;

import com.example.cokothon.entity.TravelPersonality;
import com.example.cokothon.entity.QuestionCategory;
import com.example.cokothon.repository.TravelPersonalityRepository;
import com.example.cokothon.dto.TestResultDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TravelPersonalityService {

    private final TravelPersonalityRepository personalityRepository;

    /**
     * 전체 성향 타입 조회
     */
    public List<TravelPersonality> getAllPersonalities() {
        return personalityRepository.findByIsDeletedFalseOrderByCategory();
    }

    /**
     * 카테고리별 성향 타입 조회
     */
    public List<TravelPersonality> getPersonalitiesByCategory(QuestionCategory category) {
        return personalityRepository.findByCategoryOrderByName(category);
    }
}