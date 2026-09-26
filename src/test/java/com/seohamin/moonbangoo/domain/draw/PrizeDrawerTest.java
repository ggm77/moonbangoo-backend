package com.seohamin.moonbangoo.domain.draw;

import com.seohamin.moonbangoo.domain.draw.service.drawing.PrizeDrawer;
import com.seohamin.moonbangoo.domain.prize.entity.Prize;
import com.seohamin.moonbangoo.domain.prize.entity.Rarity;
import com.seohamin.moonbangoo.global.exception.CustomException;
import com.seohamin.moonbangoo.global.exception.constants.ExceptionCode;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

class PrizeDrawerTest {

    private static final int TRIALS = 100_000;

    //시드 고정해서 결과가 매번 같도록
    private final PrizeDrawer prizeDrawer = new PrizeDrawer(new Random(20260926L));

    private static Prize prize(final String name, final Rarity rarity, final String probability) {
        return Prize.builder()
                .name(name)
                .rarity(rarity)
                .category("coupon")
                .probability(new BigDecimal(probability))
                .build();
    }

    //한 장씩 여러 번 뽑아서 경품별로 뽑힌 비율 계산
    private Map<Prize, Double> drawRatio(final List<Prize> candidates) {
        final Map<Prize, Integer> counts = new HashMap<>();
        for(int i = 0; i < TRIALS; i++){
            counts.merge(prizeDrawer.draw(candidates, 1).getFirst(), 1, Integer::sum);
        }

        final Map<Prize, Double> ratio = new HashMap<>();
        candidates.forEach(p -> ratio.put(p, counts.getOrDefault(p, 0) / (double) TRIALS));
        return ratio;
    }

    @Test
    void 확률대로_뽑힌다() {
        final Prize c = prize("5% 할인권", Rarity.C, "0.6");
        final Prize r = prize("10% 할인권", Rarity.R, "0.3");
        final Prize ur = prize("부스터팩", Rarity.UR, "0.1");

        final Map<Prize, Double> ratio = drawRatio(List.of(c, r, ur));

        assertThat(ratio.get(c)).isCloseTo(0.6, within(0.01));
        assertThat(ratio.get(r)).isCloseTo(0.3, within(0.01));
        assertThat(ratio.get(ur)).isCloseTo(0.1, within(0.01));
    }

    @Test
    void 확률_합이_1이_아니면_합_기준_비율로_뽑힌다() {
        final Prize a = prize("A", Rarity.C, "0.3");
        final Prize b = prize("B", Rarity.C, "0.1");

        final Map<Prize, Double> ratio = drawRatio(List.of(a, b));

        assertThat(ratio.get(a)).isCloseTo(0.75, within(0.01));
        assertThat(ratio.get(b)).isCloseTo(0.25, within(0.01));
    }

    @Test
    void 확률이_0인_경품은_뽑히지_않는다() {
        final Prize a = prize("A", Rarity.C, "0.5");
        final Prize zero = prize("확률 0", Rarity.UR, "0");

        final Map<Prize, Double> ratio = drawRatio(List.of(a, zero));

        assertThat(ratio.get(zero)).isZero();
    }

    @Test
    void 서로_다른_경품이_등급_오름차순으로_뽑힌다() {
        final List<Prize> candidates = List.of(
                prize("A", Rarity.UR, "0.05"),
                prize("B", Rarity.SR, "0.1"),
                prize("C", Rarity.R, "0.2"),
                prize("D", Rarity.C, "0.4"),
                prize("E", Rarity.R, "0.25"),
                prize("F", Rarity.C, "0.3")
        );

        for(int i = 0; i < 1000; i++){
            final List<Prize> result = prizeDrawer.draw(candidates, 5);

            assertThat(result).hasSize(5).doesNotHaveDuplicates();
            assertThat(result).isSortedAccordingTo(Comparator.comparing(Prize::getRarity));
        }
    }

    @Test
    void 후보가_부족하면_NOT_ENOUGH_PRIZE() {
        final List<Prize> candidates = List.of(
                prize("A", Rarity.C, "0.5"),
                prize("B", Rarity.R, "0.5"),
                prize("확률 0", Rarity.UR, "0")
        );

        assertThatThrownBy(() -> prizeDrawer.draw(candidates, 3))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> ((CustomException) ex).getExceptionCode())
                .isEqualTo(ExceptionCode.NOT_ENOUGH_PRIZE);
    }
}
