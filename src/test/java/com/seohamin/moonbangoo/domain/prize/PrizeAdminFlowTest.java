package com.seohamin.moonbangoo.domain.prize;

import com.jayway.jsonpath.JsonPath;
import com.seohamin.moonbangoo.domain.user.entity.User;
import com.seohamin.moonbangoo.support.TestAuthHelper;
import org.junit.jupiter.api.BeforeEach;
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
    private TestAuthHelper testAuthHelper;

    private User admin;
    private User user;

    @BeforeEach
    void setUp() {
        admin = testAuthHelper.createAdmin("사장님");
        user = testAuthHelper.createUser("손님");
    }

    private ResultActions createPrize(final User requester, final String body) throws Exception {
        return mockMvc.perform(post("/api/v1/admin/prize")
                .header("Authorization", testAuthHelper.bearer(requester))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }

    //10% 할인권 등록
    private long createCoupon() throws Exception {
        final String response = createPrize(admin, """
                {
                  "name":"10% 할인권","rarity":"R","category":"coupon",
                  "description":"매장 전 품목 10% 할인","condition":"2만원 이상 구매 시 사용",
                  "probability":0.25
                }
                """)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("10% 할인권"))
                .andExpect(jsonPath("$.rarity").value("R"))
                .andExpect(jsonPath("$.condition").value("2만원 이상 구매 시 사용"))
                .andExpect(jsonPath("$.probability").value(0.25))
                .andExpect(jsonPath("$.stock").value(nullValue()))
                .andReturn().getResponse().getContentAsString();

        return ((Number) JsonPath.read(response, "$.id")).longValue();
    }

    private ResultActions patch(final String url, final String body) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.patch(url)
                .header("Authorization", testAuthHelper.bearer(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }

    @Test
    void 사장님이_경품을_등록하고_수정한다() throws Exception {
        final long prizeId = createCoupon();

        //보낸 값만 수정되고 빈 문자열은 지워짐
        patch("/api/v1/admin/prize/" + prizeId, """
                {"name":"15% 할인권","rarity":"SR","description":""}
                """)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("15% 할인권"))
                .andExpect(jsonPath("$.rarity").value("SR"))
                .andExpect(jsonPath("$.category").value("coupon"))
                .andExpect(jsonPath("$.description").value(nullValue()))
                .andExpect(jsonPath("$.condition").value("2만원 이상 구매 시 사용"))
                .andExpect(jsonPath("$.probability").value(0.25));

        //확률 수정
        patch("/api/v1/admin/prize/" + prizeId + "/probability", """
                {"probability":0.123456}
                """)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.probability").value(0.123456));

        //재고 설정 후 다시 무제한으로
        patch("/api/v1/admin/prize/" + prizeId + "/stock", """
                {"stock":30}
                """)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(30));
        patch("/api/v1/admin/prize/" + prizeId + "/stock", """
                {"stock":null}
                """)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(nullValue()));

        //삭제 후 조회 불가
        mockMvc.perform(delete("/api/v1/admin/prize/" + prizeId)
                        .header("Authorization", testAuthHelper.bearer(admin)))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/v1/admin/prize/" + prizeId)
                        .header("Authorization", testAuthHelper.bearer(admin)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("PRIZE_NOT_EXIST"));
    }

    @Test
    void 경품_목록에_확률_합이_같이_나온다() throws Exception {
        createCoupon();
        createPrize(admin, """
                {"name":"부스터팩 1팩","rarity":"UR","category":"pack","probability":0.05,"stock":10}
                """).andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/admin/prizes")
                        .header("Authorization", testAuthHelper.bearer(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prizes", hasSize(2)))
                .andExpect(jsonPath("$.prizes[0].name").value("10% 할인권"))
                .andExpect(jsonPath("$.prizes[1].stock").value(10))
                .andExpect(jsonPath("$.totalProbability").value(0.3));
    }

    @Test
    void 일반_유저는_경품을_관리할_수_없다() throws Exception {
        createPrize(user, """
                {"name":"10% 할인권","rarity":"R","category":"coupon","probability":0.25}
                """).andExpect(status().isForbidden());

        mockMvc.perform(get("/api/v1/admin/prizes")
                        .header("Authorization", testAuthHelper.bearer(user)))
                .andExpect(status().isForbidden());

        //토큰 없으면 401
        mockMvc.perform(get("/api/v1/admin/prizes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 잘못된_값은_등록할_수_없다() throws Exception {
        //필수 값 누락
        createPrize(admin, """
                {"rarity":"R","category":"coupon","probability":0.25}
                """)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));

        //확률은 0 ~ 1
        createPrize(admin, """
                {"name":"할인권","rarity":"R","category":"coupon","probability":1.5}
                """)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));

        //확률은 소수점 6자리까지
        createPrize(admin, """
                {"name":"할인권","rarity":"R","category":"coupon","probability":0.1234567}
                """)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));

        //재고는 음수 불가
        createPrize(admin, """
                {"name":"할인권","rarity":"R","category":"coupon","probability":0.1,"stock":-1}
                """)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));

        //없는 등급
        createPrize(admin, """
                {"name":"할인권","rarity":"SSR","category":"coupon","probability":0.1}
                """)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_ENUM_VALUE"));

        //확률 수정도 범위 검사
        final long prizeId = createCoupon();
        patch("/api/v1/admin/prize/" + prizeId + "/probability", """
                {"probability":-0.1}
                """)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    }
}
