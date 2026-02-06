package com.springboot.aldiabackjava.repositories.lecturas;

import com.springboot.aldiabackjava.models.lecturas.MultimediaLecturas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface IMultimediaLecturas extends JpaRepository<MultimediaLecturas, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM MultimediaLecturas")
    void deleteAllMultimediaLecturas();
}
