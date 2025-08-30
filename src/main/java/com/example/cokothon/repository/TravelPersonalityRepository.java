package com.example.cokothon.repository;

import com.example.cokothon.entity.TravelPersonality;
import com.example.cokothon.entity.QuestionCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TravelPersonalityRepository extends JpaRepository<TravelPersonality, Long> {

    List<TravelPersonality> findByCategoryOrderByName(QuestionCategory category);

    List<TravelPersonality> findByIsDeletedFalseOrderByCategory();

    List<TravelPersonality> findByNameInAndIsDeletedFalse(List<String> names);
}