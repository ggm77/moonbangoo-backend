package com.seohamin.moonbangoo.domain.draw;

import com.seohamin.moonbangoo.domain.draw.service.drawing.PrizeDrawer;
import com.seohamin.moonbangoo.domain.pack.entity.Pack;
import com.seohamin.moonbangoo.domain.prize.entity.Prize;
import com.seohamin.moonbangoo.domain.prize.entity.Rarity;
import com.seohamin.moonbangoo.global.exception.CustomException;
import com.seohamin.moonbangoo.global.exception.constants.ExceptionCode;
import org.junit.jupiter.api.Test;

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

    private static final Pack PACK = Pack.builder().name("팩").active(true).build();

    //시드 고정해서 결과가 매번 같도록
    private final PrizeDrawer prizeDrawer = new PrizeDrawer(new Random(20261005L));

    private static Prize prize(final String name, final Rarity rarity, final int remaining) {
        return Prize.builder()
                .pack(PACK)
                .name(name)
                .rarity(rarity)
                .total(remaining)
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
    void 남은_수량에_비례해서_뽑힌다() {
        final Prize a = prize("A", Rarity.C, 60);
        final Prize b = prize("B", Rarity.R, 30);
        final Prize c = prize("C", Rarity.UR, 10);

        final Map<Prize, Double> ratio = drawRatio(List.of(a, b, c));

        assertThat(ratio.get(a)).isCloseTo(0.6, within(0.01));
        assertThat(ratio.get(b)).isCloseTo(0.3, within(0.01));
        assertThat(ratio.get(c)).isCloseTo(0.1, within(0.01));
    }

    @Test
    void 남은_수량이_줄면_뽑히는_비율도_바뀐다() {
        final Prize a = prize("A", Rarity.C, 50);
        final Prize b = prize("B", Rarity.C, 50);
        b.updateRemaining(10);

        final Map<Prize, Double> ratio = drawRatio(List.of(a, b));

        assertThat(ratio.get(a)).isCloseTo(50 / 60.0, within(0.01));
        assertThat(ratio.get(b)).isCloseTo(10 / 60.0, within(0.01));
    }

    @Test
    void 남은_수량이_0인_경품은_뽑히지_않는다() {
        final Prize a = prize("A", Rarity.C, 5);
        final Prize soldOut = prize("품절", Rarity.UR, 0);

        final Map<Prize, Double> ratio = drawRatio(List.of(a, soldOut));

        assertThat(ratio.get(soldOut)).isZero();
    }

    @Test
    void 경품이_5종_이상이면_서로_다른_경품이_등급_오름차순으로_뽑힌다() {
        final List<Prize> candidates = List.of(
                prize("A", Rarity.UR, 1),
                prize("B", Rarity.SR, 2),
                prize("C", Rarity.R, 3),
                prize("D", Rarity.C, 10),
                prize("E", Rarity.R, 5),
                prize("F", Rarity.C, 7)
        );

        for(int i = 0; i < 1000; i++){
            final List<Prize> result = prizeDrawer.draw(candidates, 5);

            assertThat(result).hasSize(5).doesNotHaveDuplicates();
            assertThat(result).isSortedAccordingTo(Comparator.comparing(Prize::getRarity));
        }
    }

    @Test
    void 경품이_5종_미만이면_모든_종류를_넣고_중복으로_채운다() {
        final Prize a = prize("A", Rarity.C, 1);
        final Prize b = prize("B", Rarity.R, 1);
        final Prize c = prize("C", Rarity.SR, 8);
        final Prize soldOut = prize("품절", Rarity.UR, 0);

        for(int i = 0; i < 1000; i++){
            final List<Prize> result = prizeDrawer.draw(List.of(a, b, c, soldOut), 5);

            assertThat(result).hasSize(5);
            assertThat(result).contains(a, b, c).doesNotContain(soldOut);
            assertThat(result).isSortedAccordingTo(Comparator.comparing(Prize::getRarity));
        }
    }

    @Test
    void 경품이_하나뿐이면_같은_경품_5장() {
        final Prize only = prize("하나뿐", Rarity.R, 1);

        assertThat(prizeDrawer.draw(List.of(only), 5)).containsExactly(only, only, only, only, only);
    }

    @Test
    void 뽑을_경품이_없으면_PACK_SOLD_OUT() {
        final List<Prize> candidates = List.of(
                prize("품절1", Rarity.C, 0),
                prize("품절2", Rarity.R, 0)
        );

        assertThatThrownBy(() -> prizeDrawer.draw(candidates, 5))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> ((CustomException) ex).getExceptionCode())
                .isEqualTo(ExceptionCode.PACK_SOLD_OUT);

        assertThatThrownBy(() -> prizeDrawer.draw(List.of(), 5))
                .isInstanceOf(CustomException.class);
    }
}
