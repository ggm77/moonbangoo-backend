package com.seohamin.moonbangoo.domain.draw;

import com.jayway.jsonpath.JsonPath;
import com.seohamin.moonbangoo.domain.draw.repository.DrawRepository;
import com.seohamin.moonbangoo.domain.pack.entity.Pack;
import com.seohamin.moonbangoo.domain.pack.repository.PackRepository;
import com.seohamin.moonbangoo.domain.prize.entity.Prize;
import com.seohamin.moonbangoo.domain.prize.entity.Rarity;
import com.seohamin.moonbangoo.domain.prize.repository.PrizeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class DrawAdminFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PackRepository packRepository;

    @Autowired
    private PrizeRepository prizeRepository;

    @Autowired
    private DrawRepository drawRepository;

    private Pack pack;
    private Prize booster;
    private Prize coupon;

    private void setUpPrizes() {
        pack = packRepository.save(Pack.builder().name("카드").active(true).build());
        booster = prizeRepository.save(Prize.builder()
                .pack(pack).name("부스터팩").rarity(Rarity.UR).total(1).build());
        coupon = prizeRepository.save(Prize.builder()
                .pack(pack).name("할인권").rarity(Rarity.C).total(9).build());
    }

    //손님이 뽑기를 하고 경품을 확정함, 뽑기 아이디 반환
    private long drawAndConfirm(final Prize prize) throws Exception {
        final String response = mockMvc.perform(post("/api/v1/packs/" + pack.getId() + "/draw"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        final Number drawId = JsonPath.read(response, "$.drawId");

        mockMvc.perform(post("/api/v1/draws/" + drawId + "/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"prizeId\":" + prize.getId() + "}"))
                .andExpect(status().isOk());

        return drawId.longValue();
    }

    @Test
    void 확정된_뽑기만_당첨_기록에_최신순으로_나온다() throws Exception {
        setUpPrizes();

        //확정하지 않은 뽑기는 기록에 없음
        mockMvc.perform(post("/api/v1/packs/" + pack.getId() + "/draw")).andExpect(status().isOk());

        final long first = drawAndConfirm(coupon);
        final long second = drawAndConfirm(coupon);

        mockMvc.perform(get("/api/v1/admin/draws"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.draws", hasSize(2)))
                .andExpect(jsonPath("$.draws[0].id").value(second))
                .andExpect(jsonPath("$.draws[1].id").value(first))
                .andExpect(jsonPath("$.draws[0].status").value("CONFIRMED"))
                .andExpect(jsonPath("$.draws[0].packName").value("카드"))
                .andExpect(jsonPath("$.draws[0].selectedPrizeName").value("할인권"))
                .andExpect(jsonPath("$.draws[0].shownPrizeIds", hasSize(5)))
                .andExpect(jsonPath("$.draws[0].confirmedAt").exists());
    }

    @Test
    void 확정을_되돌리면_수량이_복원되고_기록에서_빠진다() throws Exception {
        setUpPrizes();
        final long drawId = drawAndConfirm(coupon);
        assertThat(prizeRepository.findById(coupon.getId()).orElseThrow().getRemaining()).isEqualTo(8);

        mockMvc.perform(post("/api/v1/admin/draw/" + drawId + "/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        assertThat(prizeRepository.findById(coupon.getId()).orElseThrow().getRemaining()).isEqualTo(9);

        mockMvc.perform(get("/api/v1/admin/draws"))
                .andExpect(jsonPath("$.draws", hasSize(0)));

        //두 번 되돌릴 수 없고 없는 기록도 되돌릴 수 없음
        mockMvc.perform(post("/api/v1/admin/draw/" + drawId + "/cancel"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DRAW_NOT_CONFIRMED"));
        mockMvc.perform(post("/api/v1/admin/draw/999999/cancel"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("DRAW_NOT_EXIST"));
    }

    @Test
    void 경품을_삭제해도_당첨_기록은_남는다() throws Exception {
        setUpPrizes();
        drawAndConfirm(coupon);

        prizeRepository.delete(coupon);
        prizeRepository.flush();

        mockMvc.perform(get("/api/v1/admin/draws"))
                .andExpect(jsonPath("$.draws", hasSize(1)))
                .andExpect(jsonPath("$.draws[0].selectedPrizeName").value("할인권"));
    }

    @Test
    void 팩별_등급별_남은_수량을_요약한다() throws Exception {
        setUpPrizes();
        drawAndConfirm(coupon);

        mockMvc.perform(get("/api/v1/admin/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.remaining").value(9))
                .andExpect(jsonPath("$.total").value(10))
                .andExpect(jsonPath("$.rarities", hasSize(4)))
                .andExpect(jsonPath("$.rarities[0].rarity").value("C"))
                .andExpect(jsonPath("$.rarities[0].remaining").value(8))
                .andExpect(jsonPath("$.rarities[0].total").value(9))
                .andExpect(jsonPath("$.rarities[3].rarity").value("UR"))
                .andExpect(jsonPath("$.rarities[3].remaining").value(1))
                .andExpect(jsonPath("$.packs", hasSize(1)))
                .andExpect(jsonPath("$.packs[0].name").value("카드"))
                .andExpect(jsonPath("$.packs[0].remaining").value(9));
    }

    @Test
    void 초기화하면_수량이_처음으로_돌아가고_기록이_지워진다() throws Exception {
        setUpPrizes();
        drawAndConfirm(coupon);
        drawAndConfirm(coupon);
        drawAndConfirm(booster);

        mockMvc.perform(post("/api/v1/admin/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"confirm\":true}"))
                .andExpect(status().isNoContent());

        assertThat(drawRepository.count()).isZero();
        assertThat(prizeRepository.findById(coupon.getId()).orElseThrow().getRemaining()).isEqualTo(9);
        assertThat(prizeRepository.findById(booster.getId()).orElseThrow().getRemaining()).isEqualTo(1);
    }

    @Test
    void 확인_값_없이는_초기화할_수_없다() throws Exception {
        setUpPrizes();
        drawAndConfirm(coupon);

        mockMvc.perform(post("/api/v1/admin/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));

        mockMvc.perform(post("/api/v1/admin/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"confirm\":false}"))
                .andExpect(status().isBadRequest());

        assertThat(drawRepository.count()).isEqualTo(1);
        assertThat(prizeRepository.findById(coupon.getId()).orElseThrow().getRemaining()).isEqualTo(8);
    }
}
