package com.seohamin.moonbangoo.global;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FrontPageTest {

    @Autowired
    private MockMvc mockMvc;

    //MockMvc는 forward를 따라가지 않으므로 어디로 보내는지만 확인
    @Test
    void 주소만_입력하면_메뉴_화면으로_보낸다() throws Exception {
        mockMvc.perform(get("/moonbangoo/"))
                .andExpect(forwardedUrl("/moonbangoo/index.html"));
        mockMvc.perform(get("/moonbangoo"))
                .andExpect(forwardedUrl("/moonbangoo/index.html"));
    }

    @Test
    void 메뉴_화면이_열린다() throws Exception {
        mockMvc.perform(get("/moonbangoo/index.html"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("class=\"game-menu\"")));
    }

    @Test
    void 카드뽑기_화면이_열린다() throws Exception {
        mockMvc.perform(get("/moonbangoo/card.html"))
                .andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", containsString("no-cache")));
    }

    @Test
    void assets는_오래_캐시한다() throws Exception {
        mockMvc.perform(get("/moonbangoo/assets/logo-OHy9O-Fu.svg"))
                .andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", containsString("max-age=31536000")));
    }
}
