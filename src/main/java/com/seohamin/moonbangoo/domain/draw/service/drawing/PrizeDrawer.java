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
 * 경품들의 남은 수량에 비례해서 카드를 뽑는다.
 * 남은 경품 1개가 추첨권 1장이라고 보면 되고, 남은 수량이 많은 경품일수록 잘 뽑힘
 */
@Component
public class PrizeDrawer {

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
     * 남은 수량에 비례해서 count장을 뽑는 메서드
     * 경품이 count종 이상이면 서로 다른 경품만 뽑음 (한 장 뽑을 때마다 뽑힌 경품을 빼고 다시 뽑는 비복원 추출)
     * count종 미만이면 모든 종류를 한 장씩 넣고, 남은 자리는 남은 수량에 비례해서 중복으로 채움
     * 손님은 한 장만 가져가고 한 장만 차감하므로 남은 수량보다 많이 보여줘도 됨
     * @param candidates 뽑을 수 있는 경품 목록 (남은 수량이 0인 경품은 제외하고 사용)
     * @param count 뽑을 카드 수
     * @return 뽑힌 경품 (등급 오름차순, 레어 카드가 뒤쪽)
     */
    public List<Prize> draw(final List<Prize> candidates, final int count) {

        // 1) 남은 수량이 있는 경품만 후보로 사용
        final List<Prize> remaining = new ArrayList<>(candidates.stream()
                .filter(prize -> prize.getRemaining() > 0)
                .toList());

        // 2) 뽑을 경품이 없으면 팩이 품절
        if(remaining.isEmpty()){
            throw new CustomException(ExceptionCode.PACK_SOLD_OUT);
        }

        final List<Prize> result = new ArrayList<>(count);

        if(remaining.size() >= count){
            // 3-1) 서로 다른 경품 count개를 비복원 추출
            for(int i = 0; i < count; i++){
                final Prize picked = pick(remaining);
                remaining.remove(picked);
                result.add(picked);
            }
        } else {
            // 3-2) 종류가 부족하면 모든 종류를 한 장씩 넣고 남은 자리는 중복을 허용해서 추출
            result.addAll(remaining);
            while(result.size() < count){
                result.add(pick(remaining));
            }
        }

        // 4) 카드 넘기는 순서대로 등급 오름차순 정렬
        result.sort(Comparator.comparing(Prize::getRarity));

        return result;
    }

    //남은 수량을 가중치로 경품 하나를 뽑음 (뽑힌 경품을 빼지는 않음)
    private Prize pick(final List<Prize> prizes) {
        final long totalWeight = prizes.stream().mapToLong(Prize::getRemaining).sum();
        long pick = random.nextLong(totalWeight);

        final Iterator<Prize> iterator = prizes.iterator();
        Prize last = null;
        while(iterator.hasNext()){
            last = iterator.next();
            pick -= last.getRemaining();
            if(pick < 0){
                return last;
            }
        }

        //가중치 합 안에서 뽑았으므로 여기까지 오지 않음
        return last;
    }
}
