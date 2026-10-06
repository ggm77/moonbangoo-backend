package com.seohamin.moonbangoo.domain.pack.repository;

import com.seohamin.moonbangoo.domain.pack.entity.Pack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PackRepository extends JpaRepository<Pack, Long> {

    //어드민 목록용, 등록 순서대로
    List<Pack> findAllByOrderByIdAsc();

    //손님에게 보여줄 팩
    List<Pack> findAllByActiveTrueOrderByIdAsc();
}
