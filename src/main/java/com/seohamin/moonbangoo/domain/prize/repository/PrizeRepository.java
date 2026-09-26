package com.seohamin.moonbangoo.domain.prize.repository;

import com.seohamin.moonbangoo.domain.prize.entity.Prize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PrizeRepository extends JpaRepository<Prize, Long> {

    //어드민 목록용, 등록 순서대로
    List<Prize> findAllByOrderByIdAsc();

    //뽑을 수 있는 경품 (확률이 0보다 크고 재고가 남아있는 경품)
    @Query("""
        SELECT p FROM Prize p
        WHERE p.probability > 0
          AND (p.stock IS NULL OR p.stock > 0)
        ORDER BY p.id ASC
    """)
    List<Prize> findDrawable();
}
