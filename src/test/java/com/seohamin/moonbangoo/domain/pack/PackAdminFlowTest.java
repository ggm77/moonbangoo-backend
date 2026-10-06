package com.seohamin.moonbangoo.domain.pack;

import com.jayway.jsonpath.JsonPath;
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
class PackAdminFlowTest {

    @Autowired
    private MockMvc mockMvc;

    private ResultActions createPack(final String body) throws Exception {
        return mockMvc.perform(post("/api/v1/admin/pack")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }

    private ResultActions patch(final String url, final String body) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.patch(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }

    private ResultActions createPrize(final long packId, final String name, final int total) throws Exception {
        return mockMvc.perform(post("/api/v1/admin/prize")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"packId":%d,"name":"%s","rarity":"C","total":%d}
                        """.formatted(packId, name, total)));
    }

    private long createBakery() throws Exception {
        final String response = createPack("""
                {"name":"베이커리 이용권","image":"https://example.com/bakery.png","icon":"bakery"}
                """)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("베이커리 이용권"))
                .andExpect(jsonPath("$.image").value("https://example.com/bakery.png"))
                .andExpect(jsonPath("$.icon").value("bakery"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.prizeCount").value(0))
                .andExpect(jsonPath("$.remaining").value(0))
                .andReturn().getResponse().getContentAsString();

        return ((Number) JsonPath.read(response, "$.id")).longValue();
    }

    @Test
    void 사장님이_팩을_등록하고_수정하고_삭제한다() throws Exception {
        final long packId = createBakery();

        //보낸 값만 수정되고 빈 문자열은 지워짐
        patch("/api/v1/admin/pack/" + packId, """
                {"name":"빵집 이용권","image":"","active":false}
                """)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("빵집 이용권"))
                .andExpect(jsonPath("$.image").value(nullValue()))
                .andExpect(jsonPath("$.icon").value("bakery"))
                .andExpect(jsonPath("$.active").value(false));

        mockMvc.perform(get("/api/v1/admin/pack/" + packId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("빵집 이용권"));

        //경품이 없으면 삭제됨
        mockMvc.perform(delete("/api/v1/admin/pack/" + packId))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/v1/admin/pack/" + packId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("PACK_NOT_EXIST"));
    }

    @Test
    void 팩_목록에_비활성_팩과_경품_수량_합이_같이_나온다() throws Exception {
        final long bakery = createBakery();
        createPrize(bakery, "식빵 이용권", 10).andExpect(status().isOk());
        createPrize(bakery, "케이크 이용권", 5).andExpect(status().isOk());

        createPack("""
                {"name":"숨김 팩","active":false}
                """).andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/admin/packs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.packs", hasSize(2)))
                .andExpect(jsonPath("$.packs[0].prizeCount").value(2))
                .andExpect(jsonPath("$.packs[0].remaining").value(15))
                .andExpect(jsonPath("$.packs[0].total").value(15))
                .andExpect(jsonPath("$.packs[1].active").value(false))
                .andExpect(jsonPath("$.packs[1].prizeCount").value(0));
    }

    @Test
    void 경품이_남아있는_팩은_삭제할_수_없다() throws Exception {
        final long bakery = createBakery();
        final String response = createPrize(bakery, "식빵 이용권", 10)
                .andReturn().getResponse().getContentAsString();
        final Number prizeId = JsonPath.read(response, "$.id");

        mockMvc.perform(delete("/api/v1/admin/pack/" + bakery))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("PACK_NOT_EMPTY"));

        //경품을 지우면 팩도 지울 수 있음
        mockMvc.perform(delete("/api/v1/admin/prize/" + prizeId))
                .andExpect(status().isNoContent());
        mockMvc.perform(delete("/api/v1/admin/pack/" + bakery))
                .andExpect(status().isNoContent());
    }

    @Test
    void 잘못된_값은_등록할_수_없다() throws Exception {
        //이름 누락, 공백
        createPack("""
                {"icon":"bakery"}
                """)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));

        createPack("""
                {"name":" "}
                """)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));

        //길이 초과
        createPack("""
                {"name":"%s"}
                """.formatted("가".repeat(51)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));

        //없는 팩
        mockMvc.perform(delete("/api/v1/admin/pack/999999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("PACK_NOT_EXIST"));
    }
}
