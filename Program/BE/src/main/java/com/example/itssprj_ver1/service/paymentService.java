package com.example.itssprj_ver1.service;

import com.example.itssprj_ver1.model.customer;
import com.example.itssprj_ver1.model.payment;
import com.example.itssprj_ver1.repository.customerRepository;
import com.example.itssprj_ver1.repository.paymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.Date;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class paymentService implements paymentServiceI {
    @Autowired
    private customerRepository customerRepository;
    @Autowired
    private paymentRepository paymentRepository;
    @Override
    public boolean addPayment(String phone, String method, Float amount, Boolean paid) {
        customer customer = customerRepository.findByPhone(phone);
        if (customer == null) {
            return false; // Customer not found
        }
        try{
            payment newpayment = payment.builder()
                    .customer(customer)
                    .method(method)
                    .amount(amount)
                    .paid(paid)
                    .build();
            paymentRepository.save(newpayment);
            return true; // Payment added successfully
        }
        catch (Exception e){
            return false; // Error occurred while adding payment
        }
    }

    @Override
    public List<Map<String, Object>> getAllPayment(int customerId) {
        List<payment> payments = paymentRepository.findByCustomer_Userid_Id(customerId);
        if (payments == null || payments.isEmpty()) {
            return null; // No payments found
        }
        List<Map<String, Object>> mapList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        for (payment payment : payments) {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", payment.getId());
            map.put("method", payment.getMethod());
            map.put("amount", payment.getAmount());
            map.put("paid", payment.getPaid());
            map.put("createAt", payment.getCreateAt().format(formatter));
            mapList.add(map);
        }
        return mapList;
    }
}
