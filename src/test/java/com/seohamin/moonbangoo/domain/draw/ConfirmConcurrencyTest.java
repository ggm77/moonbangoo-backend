package com.seohamin.moonbangoo.domain.draw;

import com.seohamin.moonbangoo.domain.draw.entity.Draw;
import com.seohamin.moonbangoo.domain.draw.repository.DrawRepository;
import com.seohamin.moonbangoo.domain.draw.service.DrawService;
import com.seohamin.moonbangoo.domain.pack.entity.Pack;
import com.seohamin.moonbangoo.domain.pack.repository.PackRepository;
import com.seohamin.moonbangoo.domain.prize.entity.Prize;
import com.seohamin.moonbangoo.domain.prize.entity.Rarity;
import com.seohamin.moonbangoo.domain.prize.repository.PrizeRepository;
import com.seohamin.moonbangoo.global.exception.CustomException;
import com.seohamin.moonbangoo.global.exception.constants.ExceptionCode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 여러 손님이 동시에 같은 경품을 확정해도 수량이 음수가 되거나 초과로 나가지 않는지 확인
 * 트랜잭션을 걸지 않고 실제 커밋으로 검증하므로 끝나면 데이터를 직접 지움
 */
@SpringBootTest
@ActiveProfiles("test")
class ConfirmConcurrencyTest {

    @Autowired
    private DrawService drawService;

    @Autowired
    private DrawRepository drawRepository;

    @Autowired
    private PackRepository packRepository;

    @Autowired
    private PrizeRepository prizeRepository;

    @AfterEach
    void cleanUp() {
        drawRepository.deleteAll();
        prizeRepository.deleteAll();
        packRepository.deleteAll();
    }

    @Test
    void 동시에_확정해도_남은_수량만큼만_성공한다() throws Exception {
        final int stock = 3;
        final int customers = 10;

        final Pack pack = packRepository.save(Pack.builder().name("카드").active(true).build());
        final Prize prize = prizeRepository.save(Prize.builder()
                .pack(pack).name("한정 경품").rarity(Rarity.UR).total(stock).build());

        //손님마다 같은 경품이 들어있는 뽑기를 하나씩 받은 상태
        final List<Long> drawIds = new ArrayList<>();
        for(int i = 0; i < customers; i++){
            drawIds.add(drawRepository.save(
                    new Draw(pack.getId(), pack.getName(), List.of(prize.getId()))).getId());
        }

        final AtomicInteger success = new AtomicInteger();
        final AtomicInteger soldOut = new AtomicInteger();
        final CountDownLatch ready = new CountDownLatch(customers);
        final CountDownLatch start = new CountDownLatch(1);

        try(ExecutorService executor = Executors.newFixedThreadPool(customers)){
            final List<Future<?>> futures = new ArrayList<>();
            for(final Long drawId : drawIds){
                futures.add(executor.submit(() -> {
                    ready.countDown();
                    start.await();
                    try {
                        drawService.confirm(drawId, prize.getId());
                        success.incrementAndGet();
                    } catch (final CustomException ex) {
                        assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.PRIZE_SOLD_OUT);
                        soldOut.incrementAndGet();
                    }
                    return null;
                }));
            }

            ready.await();
            start.countDown();

            for(final Future<?> future : futures){
                future.get(30, TimeUnit.SECONDS);
            }
        }

        assertThat(success.get()).isEqualTo(stock);
        assertThat(soldOut.get()).isEqualTo(customers - stock);
        assertThat(prizeRepository.findById(prize.getId()).orElseThrow().getRemaining()).isZero();
    }
}
