package com.seohamin.moonbangoo.domain.draw.service.drawing;

import com.seohamin.moonbangoo.domain.prize.entity.Prize;
import com.seohamin.moonbangoo.global.exception.CustomException;
import com.seohamin.moonbangoo.global.exception.constants.ExceptionCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.random.RandomGenerator;

/**
 * 경품들의 확률에 따라 카드를 뽑는다.
 * 확률은 가중치로 사용하므로 경품들의 확률 합이 1이 아니어도 합 기준으로 비율대로 뽑힘
 */
@Component
public class PrizeDrawer {

    //확률은 소수점 6자리까지 저장되므로 정수 가중치로 바꿔서 오차 없이 계산
    private static final int PROBABILITY_SCALE = 6;

    private final RandomGenerator random;

    //결과를 예측할 수 없도록 SecureRandom 사용
    @Autowired
    public PrizeDrawer() {
        this(new SecureRandom());
    }

    //테스트에서 시드 고정한 난수를 넣기 위한 생성자
    public PrizeDrawer(final RandomGenerator random) {
        this.random = random;
    }

    /**
     * 확률에 따라 서로 다른 경품을 count개 뽑는 메서드
     * 한 장 뽑을 때마다 뽑힌 경품을 빼고 남은 경품들의 확률 합 기준으로 다시 뽑음 (비복원 추출)
     * 확률이 0인 경품은 뽑히지 않음
     * @param candidates 뽑을 수 있는 경품 목록
     * @param count 뽑을 카드 수
     * @return 뽑힌 경품 (등급 오름차순, 레어 카드가 뒤쪽)
     */
    public List<Prize> draw(final List<Prize> candidates, final int count) {

        // 1) 확률이 0보다 큰 경품만 후보로 사용
        final List<Prize> remaining = new ArrayList<>(candidates.stream()
                .filter(prize -> toWeight(prize) > 0)
                .toList());

        // 2) 서로 다른 경품을 뽑아야 하므로 후보가 부족하면 뽑을 수 없음
        if(remaining.size() < count){
            throw new CustomException(ExceptionCode.NOT_ENOUGH_PRIZE);
        }

        // 3) 남은 후보들의 가중치 합 안에서 난수를 뽑아 해당 구간의 경품 선택
        final List<Prize> result = new ArrayList<>(count);
        for(int i = 0; i < count; i++){
            final long totalWeight = remaining.stream().mapToLong(PrizeDrawer::toWeight).sum();
            long pick = random.nextLong(totalWeight);

            final Iterator<Prize> iterator = remaining.iterator();
            while(iterator.hasNext()){
                final Prize prize = iterator.next();
                pick -= toWeight(prize);
                if(pick < 0){
                    result.add(prize);
                    iterator.remove();
                    break;
                }
            }
        }

        // 4) 카드 넘기는 순서대로 등급 오름차순 정렬
        result.sort(Comparator.comparing(Prize::getRarity));

        return result;
    }

    //확률을 정수 가중치로 변환 (ex. 0.05 -> 50000)
    private static long toWeight(final Prize prize) {
        return prize.getProbability().movePointRight(PROBABILITY_SCALE).longValue();
    }
}
