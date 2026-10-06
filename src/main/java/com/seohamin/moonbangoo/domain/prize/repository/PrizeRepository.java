package com.seohamin.moonbangoo.domain.prize.repository;

import com.seohamin.moonbangoo.domain.prize.entity.Prize;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PrizeRepository extends JpaRepository<Prize, Long> {

    //어드민 전체 목록용, 등록 순서대로
    @Query("""
        SELECT p FROM Prize p JOIN FETCH p.pack
        ORDER BY p.id ASC
    """)
    List<Prize> findAllWithPack();

    //어드민 팩별 목록용, 등록 순서대로
    @Query("""
        SELECT p FROM Prize p JOIN FETCH p.pack
        WHERE p.pack.id = :packId
        ORDER BY p.id ASC
    """)
    List<Prize> findAllByPackId(@Param("packId") Long packId);

    //팩에서 뽑을 수 있는 경품 (남은 수량이 있는 경품)
    @Query("""
        SELECT p FROM Prize p JOIN FETCH p.pack
        WHERE p.pack.id = :packId
          AND p.remaining > 0
        ORDER BY p.id ASC
    """)
    List<Prize> findDrawableByPackId(@Param("packId") Long packId);

    //수량을 바꾸는 작업(확정, 취소, 어드민 수정)이 동시에 일어나도 어긋나지 않도록 행을 잠그고 조회
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Prize p WHERE p.id = :id")
    Optional<Prize> findByIdForUpdate(@Param("id") Long id);

    //팩에 경품이 있는지
    boolean existsByPackId(Long packId);

    //팩별 집계
    @Query("""
        SELECT new com.seohamin.moonbangoo.domain.prize.repository.PackStat(
            p.pack.id, COUNT(p), SUM(p.remaining), SUM(p.total))
        FROM Prize p
        GROUP BY p.pack.id
    """)
    List<PackStat> findPackStats();

    //모든 경품의 남은 수량을 처음 수량으로 되돌림
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Prize p SET p.remaining = p.total")
    int resetRemaining();
}
