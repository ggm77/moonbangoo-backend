package com.seohamin.moonbangoo.domain.draw;

import com.jayway.jsonpath.JsonPath;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
class DrawFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PackRepository packRepository;

    @Autowired
    private PrizeRepository prizeRepository;

    private Pack savePack(final String name, final boolean active) {
        return packRepository.save(Pack.builder().name(name).icon("icon").active(active).build());
    }

    private Prize savePrize(final Pack pack, final String name, final Rarity rarity, final int total) {
        return prizeRepository.save(Prize.builder()
                .pack(pack)
                .name(name)
                .rarity(rarity)
                .description(name + " 설명")
                .condition("현장 즉시 증정")
                .total(total)
                .build());
    }

    //경품 6종이 들어있는 팩 (품절 경품 포함하면 7종)
    private Pack saveFullPack(final String name) {
        final Pack pack = savePack(name, true);
        savePrize(pack, name + " A", Rarity.C, 10);
        savePrize(pack, name + " B", Rarity.R, 5);
        savePrize(pack, name + " C", Rarity.R, 5);
        savePrize(pack, name + " D", Rarity.SR, 2);
        savePrize(pack, name + " E", Rarity.UR, 1);
        savePrize(pack, name + " F", Rarity.C, 3);
        return pack;
    }

    private ResultActions draw(final Long packId) throws Exception {
        return mockMvc.perform(post("/api/v1/packs/" + packId + "/draw"));
    }

    private ResultActions confirm(final Number drawId, final Long prizeId) throws Exception {
        return mockMvc.perform(post("/api/v1/draws/" + drawId + "/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"prizeId\":" + prizeId + "}"));
    }

    @Test
    void 활성_팩만_보이고_남은_경품이_없으면_품절로_표시된다() throws Exception {
        final Pack full = saveFullPack("베이커리");
        final Pack soldOutPack = savePack("음료", true);
        savePrize(soldOutPack, "아메리카노", Rarity.R, 0);
        final Pack emptyPack = savePack("빈 팩", true);
        savePack("숨김", false);

        mockMvc.perform(get("/api/v1/packs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.packs", hasSize(3)))
                .andExpect(jsonPath("$.packs[0].id").value(full.getId()))
                .andExpect(jsonPath("$.packs[0].name").value("베이커리"))
                .andExpect(jsonPath("$.packs[0].available").value(true))
                .andExpect(jsonPath("$.packs[1].id").value(soldOutPack.getId()))
                .andExpect(jsonPath("$.packs[1].available").value(false))
                .andExpect(jsonPath("$.packs[2].id").value(emptyPack.getId()))
                .andExpect(jsonPath("$.packs[2].available").value(false))
                .andExpect(jsonPath("$.packs[0].remaining").doesNotExist());
    }

    @Test
    void 선택한_팩_안에서만_서로_다른_카드_5장을_등급_오름차순으로_뽑는다() throws Exception {
        final Pack pack = saveFullPack("베이커리");
        final Pack other = saveFullPack("음료");
        final Prize soldOut = savePrize(pack, "품절", Rarity.UR, 0);

        final Set<Long> packPrizeIds = new HashSet<>();
        prizeRepository.findAllByPackId(pack.getId()).forEach(p -> packPrizeIds.add(p.getId()));
        packPrizeIds.remove(soldOut.getId());

        for(int i = 0; i < 30; i++){
            final String response = draw(pack.getId())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.drawId").exists())
                    .andExpect(jsonPath("$.cards", hasSize(5)))
                    .andExpect(jsonPath("$.cards[0].packId").value(pack.getId()))
                    .andExpect(jsonPath("$.cards[0].packName").value("베이커리"))
                    .andReturn().getResponse().getContentAsString();

            final List<Number> ids = JsonPath.read(response, "$.cards[*].id");
            final List<String> rarities = JsonPath.read(response, "$.cards[*].rarity");

            final Set<Long> uniqueIds = new HashSet<>();
            ids.forEach(id -> uniqueIds.add(id.longValue()));

            assertThat(uniqueIds).hasSize(5);
            assertThat(packPrizeIds).containsAll(uniqueIds);
            assertThat(rarities).isSortedAccordingTo(Comparator.comparing(Rarity::valueOf));
        }

        //다른 팩 경품은 하나도 나오지 않았고 수량도 그대로
        prizeRepository.findAllByPackId(other.getId())
                .forEach(p -> assertThat(p.getRemaining()).isEqualTo(p.getTotal()));
    }

    @Test
    void 뽑기만_해서는_수량이_차감되지_않고_카드에_수량이_보이지_않는다() throws Exception {
        final Pack pack = saveFullPack("베이커리");

        draw(pack.getId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cards[0].condition").value("현장 즉시 증정"))
                .andExpect(jsonPath("$.cards[0].remaining").doesNotExist())
                .andExpect(jsonPath("$.cards[0].total").doesNotExist());

        prizeRepository.findAllByPackId(pack.getId())
                .forEach(p -> assertThat(p.getRemaining()).isEqualTo(p.getTotal()));
    }

    @Test
    void 경품이_5종_미만이면_같은_경품이_중복으로_나온다() throws Exception {
        final Pack pack = savePack("카드", true);
        final Prize a = savePrize(pack, "A", Rarity.C, 1);
        final Prize b = savePrize(pack, "B", Rarity.UR, 1);

        final String response = draw(pack.getId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cards", hasSize(5)))
                .andReturn().getResponse().getContentAsString();

        final List<Number> ids = JsonPath.read(response, "$.cards[*].id");
        final Set<Long> uniqueIds = new HashSet<>();
        ids.forEach(id -> uniqueIds.add(id.longValue()));

        assertThat(uniqueIds).containsExactlyInAnyOrder(a.getId(), b.getId());
    }

    @Test
    void 뽑을_수_없는_팩은_실패한다() throws Exception {
        final Pack soldOutPack = savePack("품절 팩", true);
        savePrize(soldOutPack, "품절", Rarity.C, 0);
        final Pack inactivePack = saveFullPack("숨김");
        inactivePack.updateActive(false);

        draw(soldOutPack.getId())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("PACK_SOLD_OUT"));

        draw(inactivePack.getId())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("PACK_INACTIVE"));

        draw(999999L)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("PACK_NOT_EXIST"));
    }

    @Test
    void 고른_경품만_수량이_1_차감된다() throws Exception {
        final Pack pack = saveFullPack("베이커리");

        final String response = draw(pack.getId()).andReturn().getResponse().getContentAsString();
        final Number drawId = JsonPath.read(response, "$.drawId");
        final List<Number> ids = JsonPath.read(response, "$.cards[*].id");
        final Long pickedId = ids.getFirst().longValue();

        final int before = prizeRepository.findById(pickedId).orElseThrow().getRemaining();

        confirm(drawId, pickedId)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(pickedId));

        assertThat(prizeRepository.findById(pickedId).orElseThrow().getRemaining()).isEqualTo(before - 1);

        //고르지 않은 경품은 그대로
        final long unchanged = prizeRepository.findAllByPackId(pack.getId()).stream()
                .filter(p -> !p.getId().equals(pickedId))
                .filter(p -> p.getRemaining() == p.getTotal())
                .count();
        assertThat(unchanged).isEqualTo(5);
    }

    @Test
    void 같은_뽑기를_두_번_확정할_수_없다() throws Exception {
        final Pack pack = saveFullPack("베이커리");

        final String response = draw(pack.getId()).andReturn().getResponse().getContentAsString();
        final Number drawId = JsonPath.read(response, "$.drawId");
        final Long pickedId = ((Number) JsonPath.read(response, "$.cards[0].id")).longValue();

        confirm(drawId, pickedId).andExpect(status().isOk());
        final int afterFirst = prizeRepository.findById(pickedId).orElseThrow().getRemaining();

        confirm(drawId, pickedId)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DRAW_ALREADY_CONFIRMED"));

        assertThat(prizeRepository.findById(pickedId).orElseThrow().getRemaining()).isEqualTo(afterFirst);
    }

    @Test
    void 뽑은_카드에_없던_경품은_고를_수_없다() throws Exception {
        final Pack pack = savePack("카드", true);
        final Prize shown = savePrize(pack, "A", Rarity.C, 1);
        final Prize other = savePrize(savePack("다른 팩", true), "다른 경품", Rarity.C, 5);

        final Number drawId = JsonPath.read(
                draw(pack.getId()).andReturn().getResponse().getContentAsString(), "$.drawId");

        confirm(drawId, other.getId())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("PRIZE_NOT_IN_DRAW"));

        confirm(999999L, shown.getId())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("DRAW_NOT_EXIST"));

        assertThat(other.getRemaining()).isEqualTo(5);
    }

    @Test
    void 뽑은_뒤에_소진된_경품은_확정할_수_없다() throws Exception {
        final Pack pack = savePack("카드", true);
        final Prize only = savePrize(pack, "하나뿐", Rarity.R, 1);

        final Number first = JsonPath.read(
                draw(pack.getId()).andReturn().getResponse().getContentAsString(), "$.drawId");
        final Number second = JsonPath.read(
                draw(pack.getId()).andReturn().getResponse().getContentAsString(), "$.drawId");

        //둘 다 같은 경품을 봤고 한 명이 먼저 가져감
        confirm(first, only.getId()).andExpect(status().isOk());

        confirm(second, only.getId())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("PRIZE_SOLD_OUT"));

        assertThat(only.getRemaining()).isZero();
    }

    @Test
    void 같은_이름의_경품도_팩이_다르면_수량을_공유하지_않는다() throws Exception {
        final Pack bakery = savePack("베이커리", true);
        final Pack drink = savePack("음료", true);
        final Prize inBakery = savePrize(bakery, "10% 할인권", Rarity.R, 3);
        final Prize inDrink = savePrize(drink, "10% 할인권", Rarity.R, 3);

        final Number drawId = JsonPath.read(
                draw(bakery.getId()).andReturn().getResponse().getContentAsString(), "$.drawId");
        confirm(drawId, inBakery.getId()).andExpect(status().isOk());

        assertThat(prizeRepository.findById(inBakery.getId()).orElseThrow().getRemaining()).isEqualTo(2);
        assertThat(prizeRepository.findById(inDrink.getId()).orElseThrow().getRemaining()).isEqualTo(3);
    }
}
