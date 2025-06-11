package com.example.itssprj_ver1.repository;

import com.example.itssprj_ver1.model.exerciseSession;
import com.example.itssprj_ver1.service.exerSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface exerSessionRepository extends JpaRepository<exerciseSession, Integer> {
    List<exerciseSession> findByStaff_Userid_Role_Roleid(Integer roleId);

    List<exerciseSession> findByCustomer_Id(Integer customerId);

    List<exerciseSession> findByCustomer_Userid_Id(Integer customerUseridId);

    List<exerciseSession> findByStaff_Userid_Id(Integer staffUseridId);
}
