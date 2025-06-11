package com.example.itssprj_ver1.service;

import com.example.itssprj_ver1.model.review;

import java.util.List;
import java.util.Map;

public interface reviewServiceI {
    List<Map<String, Object>> getReview();
    boolean addReview(int customerid, String review);
    List<Map<String, Object>> getReview(int customerid);
}
