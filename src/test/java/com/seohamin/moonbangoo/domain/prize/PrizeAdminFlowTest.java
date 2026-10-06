package com.seohamin.moonbangoo.domain.prize;

import com.jayway.jsonpath.JsonPath;
import com.seohamin.moonbangoo.domain.pack.entity.Pack;
import com.seohamin.moonbangoo.domain.pack.repository.PackRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class PrizeAdminFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PackRepository packRepository;

    private Pack savePack(final String name) {
        return packRepository.save(Pack.builder().name(name).active(true).build());
    }

    private ResultActions createPrize(final String body) throws Exception {
        return mockMvc.perform(post("/api/v1/admin/prize")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }

    private ResultActions patch(final String url, final String body) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.patch(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }

    //10% 할인권 등록 (수량 10)
    private long createCoupon(final Pack pack) throws Exception {
        final String response = createPrize("""
                {
                  "packId":%d,"name":"10%% 할인권","rarity":"R",
                  "description":"매장 전 품목 10%% 할인","condition":"2만원 이상 구매 시 사용",
                  "total":10
                }
                """.formatted(pack.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.packId").value(pack.getId()))
                .andExpect(jsonPath("$.packName").value(pack.getName()))
                .andExpect(jsonPath("$.name").value("10% 할인권"))
                .andExpect(jsonPath("$.rarity").value("R"))
                .andExpect(jsonPath("$.condition").value("2만원 이상 구매 시 사용"))
                .andExpect(jsonPath("$.total").value(10))
                .andExpect(jsonPath("$.remaining").value(10))
                .andReturn().getResponse().getContentAsString();

        return ((Number) JsonPath.read(response, "$.id")).longValue();
    }

    @Test
    void 사장님이_경품을_등록하고_수정한다() throws Exception {
        final Pack pack = savePack("할인권");
        final long prizeId = createCoupon(pack);

        //보낸 값만 수정되고 빈 문자열은 지워짐
        patch("/api/v1/admin/prize/" + prizeId, """
                {"name":"15% 할인권","rarity":"SR","description":""}
                """)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("15% 할인권"))
                .andExpect(jsonPath("$.rarity").value("SR"))
                .andExpect(jsonPath("$.packId").value(pack.getId()))
                .andExpect(jsonPath("$.description").value(nullValue()))
                .andExpect(jsonPath("$.condition").value("2만원 이상 구매 시 사용"))
                .andExpect(jsonPath("$.total").value(10));

        //삭제 후 조회 불가
        mockMvc.perform(delete("/api/v1/admin/prize/" + prizeId))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/v1/admin/prize/" + prizeId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("PRIZE_NOT_EXIST"));
    }

    @Test
    void 경품을_다른_팩으로_옮길_수_있고_수량은_그대로_가져간다() throws Exception {
        final Pack from = savePack("할인권");
        final Pack to = savePack("베이커리");
        final long prizeId = createCoupon(from);
        patch("/api/v1/admin/prize/" + prizeId + "/remaining", """
                {"remaining":4}
                """).andExpect(status().isOk());

        patch("/api/v1/admin/prize/" + prizeId, """
                {"packId":%d}
                """.formatted(to.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.packId").value(to.getId()))
                .andExpect(jsonPath("$.packName").value("베이커리"))
                .andExpect(jsonPath("$.remaining").value(4));

        patch("/api/v1/admin/prize/" + prizeId, """
                {"packId":999999}
                """)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("PACK_NOT_EXIST"));
    }

    @Test
    void 남은_수량을_직접_바꾸거나_늘리고_줄인다() throws Exception {
        final long prizeId = createCoupon(savePack("할인권"));

        //직접 설정
        patch("/api/v1/admin/prize/" + prizeId + "/remaining", """
                {"remaining":3}
                """)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.remaining").value(3))
                .andExpect(jsonPath("$.total").value(10));

        //처음 수량보다 많게 설정하면 처음 수량도 같이 늘어남 (재고 보충)
        patch("/api/v1/admin/prize/" + prizeId + "/remaining", """
                {"remaining":15}
                """)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.remaining").value(15))
                .andExpect(jsonPath("$.total").value(15));

        //증감
        patch("/api/v1/admin/prize/" + prizeId + "/adjust", """
                {"delta":-5}
                """)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.remaining").value(10))
                .andExpect(jsonPath("$.total").value(15));
        patch("/api/v1/admin/prize/" + prizeId + "/adjust", """
                {"delta":1}
                """)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.remaining").value(11));

        //0보다 작아질 수 없음
        patch("/api/v1/admin/prize/" + prizeId + "/adjust", """
                {"delta":-12}
                """)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    }

    @Test
    void 처음_수량은_남은_수량보다_작게_바꿀_수_없다() throws Exception {
        final long prizeId = createCoupon(savePack("할인권"));

        patch("/api/v1/admin/prize/" + prizeId, """
                {"total":5}
                """)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));

        patch("/api/v1/admin/prize/" + prizeId + "/remaining", """
                {"remaining":4}
                """).andExpect(status().isOk());

        patch("/api/v1/admin/prize/" + prizeId, """
                {"total":5}
                """)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(5))
                .andExpect(jsonPath("$.remaining").value(4));
    }

    @Test
    void 경품_목록은_팩별로_걸러서_볼_수_있다() throws Exception {
        final Pack coupon = savePack("할인권");
        final Pack booster = savePack("부스터팩");
        createCoupon(coupon);
        createPrize("""
                {"packId":%d,"name":"부스터팩 1팩","rarity":"UR","total":2}
                """.formatted(booster.getId())).andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/admin/prizes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prizes", hasSize(2)))
                .andExpect(jsonPath("$.prizes[0].name").value("10% 할인권"))
                .andExpect(jsonPath("$.prizes[1].remaining").value(2));

        mockMvc.perform(get("/api/v1/admin/prizes").param("packId", String.valueOf(booster.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prizes", hasSize(1)))
                .andExpect(jsonPath("$.prizes[0].packName").value("부스터팩"));
    }

    @Test
    void 잘못된_값은_등록할_수_없다() throws Exception {
        final Pack pack = savePack("할인권");

        //필수 값 누락
        createPrize("""
                {"packId":%d,"rarity":"R","total":1}
                """.formatted(pack.getId()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));

        createPrize("""
                {"name":"할인권","rarity":"R","total":1}
                """)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));

        //수량은 음수 불가
        createPrize("""
                {"packId":%d,"name":"할인권","rarity":"R","total":-1}
                """.formatted(pack.getId()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));

        //없는 등급
        createPrize("""
                {"packId":%d,"name":"할인권","rarity":"SSR","total":1}
                """.formatted(pack.getId()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_ENUM_VALUE"));

        //없는 팩
        createPrize("""
                {"packId":999999,"name":"할인권","rarity":"R","total":1}
                """)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("PACK_NOT_EXIST"));

        //남은 수량 수정도 범위 검사
        final long prizeId = createCoupon(pack);
        patch("/api/v1/admin/prize/" + prizeId + "/remaining", """
                {"remaining":-1}
                """)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    }
}
