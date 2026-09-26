package com.seohamin.moonbangoo.domain.draw;

import com.jayway.jsonpath.JsonPath;
import com.seohamin.moonbangoo.domain.prize.entity.Prize;
import com.seohamin.moonbangoo.domain.prize.entity.Rarity;
import com.seohamin.moonbangoo.domain.prize.repository.PrizeRepository;
import com.seohamin.moonbangoo.domain.user.entity.User;
import com.seohamin.moonbangoo.support.TestAuthHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class DrawFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestAuthHelper testAuthHelper;

    @Autowired
    private PrizeRepository prizeRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = testAuthHelper.createUser("손님");
    }

    private Prize savePrize(final String name, final Rarity rarity, final String probability, final Integer stock) {
        return prizeRepository.save(Prize.builder()
                .name(name)
                .rarity(rarity)
                .category("coupon")
                .description(name + " 설명")
                .condition("현장 즉시 증정")
                .probability(new BigDecimal(probability))
                .stock(stock)
                .build());
    }

    private ResultActions draw() throws Exception {
        return mockMvc.perform(post("/api/v1/draw")
                .header("Authorization", testAuthHelper.bearer(user)));
    }

    @Test
    void 서로_다른_카드_5장을_등급_오름차순으로_뽑는다() throws Exception {
        savePrize("5% 할인권", Rarity.C, "0.4", null);
        savePrize("10% 할인권", Rarity.R, "0.25", null);
        savePrize("카드 씰 1매", Rarity.R, "0.2", 50);
        savePrize("아이스 아메리카노", Rarity.SR, "0.1", 30);
        savePrize("부스터팩 1팩", Rarity.UR, "0.05", 10);
        savePrize("볼펜", Rarity.C, "0.3", null);
        savePrize("스티커", Rarity.C, "0.3", 1);

        //확률 0, 재고 0인 경품은 뽑히면 안됨
        final Prize zeroProbability = savePrize("확률 0", Rarity.UR, "0", null);
        final Prize soldOut = savePrize("품절", Rarity.UR, "0.5", 0);

        for(int i = 0; i < 30; i++){
            final String response = draw()
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.cards", hasSize(5)))
                    .andReturn().getResponse().getContentAsString();

            final List<Number> ids = JsonPath.read(response, "$.cards[*].id");
            final List<String> rarities = JsonPath.read(response, "$.cards[*].rarity");

            final Set<Long> uniqueIds = new HashSet<>();
            ids.forEach(id -> uniqueIds.add(id.longValue()));

            assertThat(uniqueIds).hasSize(5);
            assertThat(uniqueIds).doesNotContain(zeroProbability.getId(), soldOut.getId());
            assertThat(rarities).isSortedAccordingTo(Comparator.comparing(Rarity::valueOf));
        }
    }

    @Test
    void 카드에는_확률과_재고가_보이지_않는다() throws Exception {
        savePrize("A", Rarity.C, "0.2", null);
        savePrize("B", Rarity.C, "0.2", null);
        savePrize("C", Rarity.R, "0.2", null);
        savePrize("D", Rarity.SR, "0.2", null);
        savePrize("E", Rarity.UR, "0.2", 3);

        draw()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cards[0].name").exists())
                .andExpect(jsonPath("$.cards[0].condition").value("현장 즉시 증정"))
                .andExpect(jsonPath("$.cards[0].probability").doesNotExist())
                .andExpect(jsonPath("$.cards[0].stock").doesNotExist());
    }

    @Test
    void 뽑을_수_있는_경품이_5개보다_적으면_실패() throws Exception {
        savePrize("A", Rarity.C, "0.4", null);
        savePrize("B", Rarity.R, "0.3", null);
        savePrize("C", Rarity.SR, "0.2", null);
        savePrize("D", Rarity.UR, "0.1", null);
        savePrize("품절", Rarity.C, "0.1", 0);

        draw()
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("NOT_ENOUGH_PRIZE"));
    }

    @Test
    void 로그인하지_않으면_뽑을_수_없다() throws Exception {
        mockMvc.perform(post("/api/v1/draw"))
                .andExpect(status().isUnauthorized());
    }
}
