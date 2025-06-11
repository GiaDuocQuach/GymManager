package com.example.itssprj_ver1.repository;

import com.example.itssprj_ver1.model.customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface customerRepository extends JpaRepository<customer,Integer> {

    customer findByFirstnameAndLastname(String firstname, String lastname);

    customer findById(int customerId);

    customer findByUserid_Id(Integer useridId);

    customer findByPhone(String phone);
}
