package com.example.itssprj_ver1.repository;

import com.example.itssprj_ver1.model.staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface staffRepository extends JpaRepository<staff, Integer> {
    staff findByUserid_Id(int useridId);

    staff findById(int id);

    staff findStaffByUserid_Id(Integer id);

    staff findByFirstnameAndLastname(String ptfirstname, String ptlastname);
}
