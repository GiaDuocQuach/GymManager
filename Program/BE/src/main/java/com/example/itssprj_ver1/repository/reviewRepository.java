package com.example.itssprj_ver1.repository;

import com.example.itssprj_ver1.model.review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface reviewRepository extends JpaRepository<review, Integer> {
    List<review> findByCustomer_Userid_Id(Integer customerUseridId);
}
