package com.eazybytes.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.eazybytes.model.Notice;


@Repository
public interface NoticeRepository extends CrudRepository<Notice, Long> {

    @Query("from Notice n where CURRENT_DATE BETWEEN n.noticBegDt AND n.noticEndDt")
    List<Notice> findAllActiveNotices();


}