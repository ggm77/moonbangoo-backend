package com.seohamin.moonbangoo.domain.draw.repository;

import com.seohamin.moonbangoo.domain.draw.entity.Draw;
import com.seohamin.moonbangoo.domain.draw.entity.DrawStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DrawRepository extends JpaRepository<Draw, Long> {

    //같은 뽑기를 동시에 두 번 확정하거나 취소하지 못하도록 행을 잠그고 조회
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT d FROM Draw d WHERE d.id = :id")
    Optional<Draw> findByIdForUpdate(@Param("id") Long id);

    //최근 기록 100개, 최신순
    List<Draw> findTop100ByStatusOrderByIdDesc(DrawStatus status);
}
