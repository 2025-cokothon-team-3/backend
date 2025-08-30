-- data.sql: 여행 성향 테스트 초기 데이터

-- 1. PLANNING 카테고리 (계획 성향) - 4문항
INSERT INTO questions (content, choice1, choice2, choice3, question_order, category, is_deleted, created_at, updated_at) VALUES
                                                                                                                             ('여행 계획을 세울 때 나는?',
                                                                                                                              '숙소랑 비행기만 예약하면 끝! 나머지는 그날 기분에 따라 생각하지 뭐 ㅋ',
                                                                                                                              '꼭 필요한 예약이랑 가고 싶은 곳 정도는 정리해 둘까?',
                                                                                                                              '여행을 망칠 수는 없지.. 엑셀에 분 단위로 계획 철저히 세워야지',
                                                                                                                              1, 'PLANNING', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

                                                                                                                             ('새로운 도시에서 첫날은?',
                                                                                                                              '일단 발 닿는 대로 걸으면서 분위기를 느끼자 ~',
                                                                                                                              '지도에 표시한 몇 군데만 가볼까?',
                                                                                                                              '일정표대로 바로 스팟 공략 가야지',
                                                                                                                              2, 'PLANNING', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

                                                                                                                             ('원래 가려고 했던 식당이 문을 닫았다. 당신의 반응은?',
                                                                                                                              '괜찮아. 오히려 재밌다! 즉흥이 매력',
                                                                                                                              '당황스럽지만 금방 대안 찾아서 움직임',
                                                                                                                              '하.. 스트레스야.. 일정이 망가졌잖아 ..!',
                                                                                                                              3, 'PLANNING', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

                                                                                                                             ('다음 일정으로 이동하다가 너무 멋진 바다를 발견했는데…',
                                                                                                                              '당장 주차해;;; 여기서 놀자',
                                                                                                                              '나중에 꼭 와야지 지도에 저장한다',
                                                                                                                              '남은 시간 중에 다시 올 수 있나? 일정을 정리해본다.',
                                                                                                                              4, 'PLANNING', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 2. BUDGET 카테고리 (예산 성향) - 4문항
INSERT INTO questions (content, choice1, choice2, choice3, question_order, category, is_deleted, created_at, updated_at) VALUES
                                                                                                                             ('내가 묶을 숙소는?',
                                                                                                                              '숙소가 뭐 중요하니 ~ 최대한 싸게! 1박 5만원 정도',
                                                                                                                              '편안한 호텔 1박 15만원 정도',
                                                                                                                              '뷰 맛집 호텔 1박 25만원 이상',
                                                                                                                              5, 'BUDGET', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

                                                                                                                             ('식사 스타일은?',
                                                                                                                              '현지 길거리 음식/노포 탐방이 짱',
                                                                                                                              '유명 맛집 + 현지 로컬 적절히',
                                                                                                                              '미슐랭/리뷰 높은 식당 위주로 확실히',
                                                                                                                              6, 'BUDGET', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

                                                                                                                             ('쇼핑 스타일은?',
                                                                                                                              '여행지에서 쇼핑은 거의 안 함',
                                                                                                                              '기념품/로컬 브랜드 위주로 조금',
                                                                                                                              '여기서 밖에 못 사~ 쇼핑 많이 해야지',
                                                                                                                              7, 'BUDGET', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

                                                                                                                             ('2박 3일 여행, 내 캐리어에 옷은 몇 벌?',
                                                                                                                              '편하면 장땡 ㅋ 단벌신사',
                                                                                                                              '하루에 한 벌 정도? 적당히 챙겨야지',
                                                                                                                              '인생샷을 위한 총알 준비 완. 하루에 2벌 이상',
                                                                                                                              8, 'BUDGET', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 3. ACTIVITY 카테고리 (활동 성향) - 4문항
INSERT INTO questions (content, choice1, choice2, choice3, question_order, category, is_deleted, created_at, updated_at) VALUES
                                                                                                                             ('하루 일과는?',
                                                                                                                              '아침 늦게 시작해서 여유롭게',
                                                                                                                              '오전 관광 + 오후 휴식 밸런스',
                                                                                                                              '이른 아침부터 빡빡하게 풀코스',
                                                                                                                              9, 'ACTIVITY', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

                                                                                                                             ('여행 가서 액티비티하자는 친구, 당신의 반응은?',
                                                                                                                              '액티비티보다는 차라리 카페·산책이 더 좋은데..',
                                                                                                                              '안전한 체험 위주로 가볍게 즐기고 싶어',
                                                                                                                              '번지점프? 다이빙? 무조건 도전!',
                                                                                                                              10, 'ACTIVITY', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

                                                                                                                             ('여행 가서 운동 유무는?',
                                                                                                                              '뭔 운동임? 여행가서는 쉬어야지',
                                                                                                                              '가벼운 유산소 정도는 하고 싶어. 산책길 좀 걸을까?',
                                                                                                                              '주변 헬스장이 어딨지.. 운동 해 줘야지',
                                                                                                                              11, 'ACTIVITY', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

                                                                                                                             ('바닷가에서 나의 모습은?',
                                                                                                                              '놀고와.. 난 여기서 짐 지키고 있을게',
                                                                                                                              '물놀이 드루와',
                                                                                                                              '물놀이는 기본이고 서핑이나 바나나보트까지 고????',
                                                                                                                              12, 'ACTIVITY', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 4. SOCIAL 카테고리 (사교 성향) - 4문항
INSERT INTO questions (content, choice1, choice2, choice3, question_order, category, is_deleted, created_at, updated_at) VALUES
                                                                                                                             ('여행 일정 끝 ~! 잠들기 전 나는?',
                                                                                                                              '나만의 시간이 필요해…시간이 늦었지만 영화 좀 보다가 잘까?',
                                                                                                                              '내일 일정을 위해서 빨리 자야지~ 체력 관리는 필수야!',
                                                                                                                              '우리 대화하자 히히',
                                                                                                                              13, 'SOCIAL', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

                                                                                                                             ('사진/기록 태도는?',
                                                                                                                              '몇 장만 찍고 직접 경험에 집중',
                                                                                                                              '사진과 기록 적당히 남겨두기',
                                                                                                                              '사진은 필수야 나 계속 찍어줘! 우리 릴스도 찍을까?',
                                                                                                                              14, 'SOCIAL', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

                                                                                                                             ('유명한 카페에 왔는데 웨이팅이… 2시간?!',
                                                                                                                              '장난하시나요? 응 안가요',
                                                                                                                              '근처에 비슷한 예쁜 카페 찾아서 고고',
                                                                                                                              '웨이팅이 있는 이유가 있음… 기다린다',
                                                                                                                              15, 'SOCIAL', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

                                                                                                                             ('만약 다이어트 중에 여행을 간다면…',
                                                                                                                              '아무리 여행이어도 관리는 해야지.. 계속 다이어트 고고',
                                                                                                                              '적당히 먹겠음. 너무 과하겐 노노',
                                                                                                                              '여행이니까 먹고 싶은 거 다 먹자 !!!',
                                                                                                                              16, 'SOCIAL', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 5. 여행 성향 타입 기본 데이터 (선택사항)
INSERT INTO travel_personalities (code, name, description, category, color_code, is_deleted, created_at, updated_at) VALUES
-- PLANNING 카테고리
('SPONTANEOUS', '즉흥형', '계획보다는 그 순간의 감정과 직감을 따라 여행하는 것을 좋아합니다. 예상치 못한 발견과 모험을 즐깁니다.', 'PLANNING', '#FF6B6B', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('PLANNER', '계획형', '여행 전 세세한 계획을 세우는 것을 선호하며, 일정에 따라 체계적으로 움직이는 것을 좋아합니다.', 'PLANNING', '#4ECDC4', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- BUDGET 카테고리
('BUDGET', '절약형', '가성비를 중시하며, 합리적인 소비로 알찬 여행을 만들어가는 것을 선호합니다.', 'BUDGET', '#45B7D1', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('LUXURY', '럭셔리형', '품질 높은 경험을 위해서는 비용 지출을 아끼지 않으며, 특별하고 프리미엄한 여행을 추구합니다.', 'BUDGET', '#F7DC6F', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- ACTIVITY 카테고리
('RELAXED', '휴식형', '여행에서 충분한 휴식과 여유로운 시간을 중시하며, 재충전을 위한 힐링 여행을 선호합니다.', 'ACTIVITY', '#BB8FCE', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('ACTIVE', '액티브형', '다양한 체험과 활동적인 여행을 즐기며, 새로운 도전과 스릴을 추구합니다.', 'ACTIVITY', '#58D68D', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- SOCIAL 카테고리
('INDIVIDUAL', '개인형', '혼자만의 시간과 공간을 중시하며, 개인적인 경험과 성찰을 위한 여행을 선호합니다.', 'SOCIAL', '#85C1E9', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('SOCIAL', '사교형', '사람들과의 만남과 소통을 즐기며, 함께하는 즐거움과 추억 만들기를 중시합니다.', 'SOCIAL', '#F8C471', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 6. 테스트용 사용자 데이터 (개발/테스트용)
INSERT INTO users (nickname, is_deleted, created_at, updated_at) VALUES
                                                                     ('여행러버', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                     ('즉흥여행가', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                     ('계획맨', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                     ('럭셔리트래블러', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                     ('백패커', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 7. 테스트용 테스트 결과 데이터 (개발/테스트용)
-- 사용자 1: 계획형 절약러
INSERT INTO test_results (user_id, planning_score, budget_score, activity_score, social_score, planning_type, budget_type, activity_type, social_type, dominant_type, is_deleted, created_at, updated_at) VALUES
    (1, 10, 6, 8, 9, '계획형', '절약형', '액티브형', '사교형', '계획형 절약러', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 사용자 2: 즉흥형 액티브러
INSERT INTO test_results (user_id, planning_score, budget_score, activity_score, social_score, planning_type, budget_type, activity_type, social_type, dominant_type, is_deleted, created_at, updated_at) VALUES
    (2, 5, 8, 11, 7, '즉흥형', '절약형', '액티브형', '사교형', '즉흥형 액티브러', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 사용자 3: 계획형 럭셔리러
INSERT INTO test_results (user_id, planning_score, budget_score, activity_score, social_score, planning_type, budget_type, activity_type, social_type, dominant_type, is_deleted, created_at, updated_at) VALUES
    (3, 12, 11, 6, 8, '계획형', '럭셔리형', '휴식형', '사교형', '계획형 럭셔리러', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 사용자 4: 즉흥형 절약러
INSERT INTO test_results (user_id, planning_score, budget_score, activity_score, social_score, planning_type, budget_type, activity_type, social_type, dominant_type, is_deleted, created_at, updated_at) VALUES
    (4, 4, 5, 9, 6, '즉흥형', '절약형', '액티브형', '개인형', '즉흥형 절약러', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 사용자 5: 계획형 럭셔리러 (휴식형)
INSERT INTO test_results (user_id, planning_score, budget_score, activity_score, social_score, planning_type, budget_type, activity_type, social_type, dominant_type, is_deleted, created_at, updated_at) VALUES
    (5, 11, 12, 5, 7, '계획형', '럭셔리형', '휴식형', '사교형', '계획형 럭셔리러', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 8. 테스트용 사용자 답변 데이터 (사용자 1 - 계획형 절약러 예시)
INSERT INTO user_answers (user_id, question_id, selected_choice, is_deleted, created_at, updated_at) VALUES
-- PLANNING (계획형으로 답변)
(1, 1, 3, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), -- 계획형
(1, 2, 3, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), -- 계획형
(1, 3, 2, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), -- 중간
(1, 4, 3, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), -- 계획형

-- BUDGET (절약형으로 답변)
(1, 5, 1, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), -- 절약형
(1, 6, 2, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), -- 중간
(1, 7, 1, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), -- 절약형
(1, 8, 2, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), -- 중간

-- ACTIVITY (액티브형으로 답변)
(1, 9, 3, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), -- 액티브형
(1, 10, 2, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), -- 중간
(1, 11, 3, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), -- 액티브형
(1, 12, 2, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), -- 중간

-- SOCIAL (사교형으로 답변)
(1, 13, 3, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), -- 사교형
(1, 14, 3, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), -- 사교형
(1, 15, 3, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), -- 사교형
(1, 16, 2, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP); -- 중간

-- 9. 데이터 검증 쿼리 (주석으로 제공)
/*
-- 데이터 확인용 쿼리들
SELECT COUNT(*) as total_questions FROM questions WHERE is_deleted = false;
SELECT category, COUNT(*) as count FROM questions WHERE is_deleted = false GROUP BY category;
SELECT * FROM users WHERE is_deleted = false;
SELECT u.nickname, tr.dominant_type, tr.planning_score, tr.budget_score, tr.activity_score, tr.social_score
FROM users u
JOIN test_results tr ON u.id = tr.user_id
WHERE u.is_deleted = false AND tr.is_deleted = false;
*/