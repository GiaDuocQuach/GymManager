package com.example.itssprj_ver1.service;

import com.example.itssprj_ver1.model.customer;
import com.example.itssprj_ver1.model.review;
import com.example.itssprj_ver1.repository.customerRepository;
import com.example.itssprj_ver1.repository.reviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class reviewService implements reviewServiceI {
    @Autowired
    private reviewRepository reviewRepository;
    @Autowired
    private customerRepository customerRepository;

    @Override
    public List<Map<String, Object>> getReview() {
        List<review> reviews = reviewRepository.findAll();

        List<Map<String, Object>> reviewData = new ArrayList<>();

        for (review review : reviews) {
            Map<String, Object> reviewMap = new HashMap<>();
            reviewMap.put("reviewId", review.getId());
            reviewMap.put("customer", review.getCustomer().getFirstname() + " " + review.getCustomer().getLastname());
            reviewMap.put("text", review.getText());
            reviewMap.put("date", review.getCreateAt());
            reviewData.add(reviewMap);
        }
        return reviewData;
    }

    @Override
    public boolean addReview(int customerid, String review) {
        customer customer = customerRepository.findByUserid_Id(customerid);
        review review1 = new review();
        review1.setText(review);
        review1.setCustomer(customer);
        Date currentDate = new Date(System.currentTimeMillis());
        review1.setCreateAt(currentDate);
        try {
            reviewRepository.save(review1);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<Map<String, Object>> getReview(int customerid) {
        List<review> reviews = reviewRepository.findByCustomer_Userid_Id(customerid);
        if (reviews == null || reviews.isEmpty()) {
            return null;
        }
        List<Map<String, Object>> mapList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (review review : reviews) {
            Map<String, Object> reviewMap = new HashMap<>();
            reviewMap.put("reviewId", review.getId());
            reviewMap.put("customer", review.getCustomer().getFirstname() + " " + review.getCustomer().getLastname());
            reviewMap.put("text", review.getText());
            reviewMap.put("date", review.getCreateAt().toLocalDate().format(formatter));
            mapList.add(reviewMap);
        }
        return mapList;
    }
}
