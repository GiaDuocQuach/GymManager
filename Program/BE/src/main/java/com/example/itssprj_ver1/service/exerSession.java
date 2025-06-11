package com.example.itssprj_ver1.service;

import com.example.itssprj_ver1.model.customer;
import com.example.itssprj_ver1.model.exerciseSession;
import com.example.itssprj_ver1.model.staff;
import com.example.itssprj_ver1.repository.customerRepository;
import com.example.itssprj_ver1.repository.exerSessionRepository;
import com.example.itssprj_ver1.repository.staffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class exerSession implements exerSessionI {
    @Autowired
    private exerSessionRepository exerSessionRepository;
    @Autowired
    private customerRepository customerRepository;
    @Autowired
    private staffRepository staffRepository;
    @Override
    public List<Map<String, Object>> getAllSessions() {
        List<exerciseSession> sessions = exerSessionRepository.findByStaff_Userid_Role_Roleid(3);
        List<Map<String, Object>> sessionsList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (exerciseSession session : sessions) {
            Map<String, Object> sessionMap = new HashMap<>();
            sessionMap.put("sessionid", session.getId());

            // Thông tin khách hàng
            sessionMap.put("customer_name", session.getCustomer().getFirstname() + " " +
                    session.getCustomer().getLastname());

            // Thông tin huấn luyện viên
            sessionMap.put("trainer_name", session.getStaff().getFirstname() + " " +
                    session.getStaff().getLastname());

            // Thời gian
            LocalDateTime beginAt = session.getBeginAt();
            LocalDateTime endAt = session.getEndAt();

            sessionMap.put("begin_time", beginAt != null ? beginAt.format(formatter) : "Chưa xác định");
            sessionMap.put("end_time", endAt != null ? endAt.format(formatter) : "Chưa xác định");

            // Loại và mô tả
            sessionMap.put("exercise_type", session.getExerciseType());
            sessionMap.put("description", session.getDescription() != null ? session.getDescription() : "Chưa xác định");

            sessionsList.add(sessionMap);
        }

        return sessionsList;
    }

    @Override
    public List<Map<String, Object>> getAllSession(int customerId) {
        List<exerciseSession> sessions = exerSessionRepository.findByCustomer_Userid_Id(customerId);
        if (sessions == null || sessions.isEmpty()) {
            return null;
        }
        List<Map<String, Object>> sessionsList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        for ( exerciseSession session : sessions) {
            Map<String, Object> sessionMap = new HashMap<>();
            sessionMap.put("sessionid", session.getId());

            // Thông tin huấn luyện viên
            sessionMap.put("trainer_name", session.getStaff().getFirstname() + " " +
                    session.getStaff().getLastname());

            // Thời gian
            LocalDateTime beginAt = session.getBeginAt();
            LocalDateTime endAt = session.getEndAt();

            sessionMap.put("begin_time", beginAt != null ? beginAt.format(formatter) : "Chưa xác định");
            sessionMap.put("end_time", endAt != null ? endAt.format(formatter) : "Chưa xác định");

            // Loại và mô tả
            sessionMap.put("exercise_type", session.getExerciseType());
            sessionMap.put("description", session.getDescription() != null ? session.getDescription() : "Chưa xác định");

            sessionsList.add(sessionMap);
        }
        return sessionsList;
    }

    @Override
    public List<Map<String, Object>> getAllSessionByPT(int ptId) {
        List<exerciseSession> sessions = exerSessionRepository.findByStaff_Userid_Id (ptId);
        if (sessions == null || sessions.isEmpty()) {
            return null;
        }
        List<Map<String, Object>> sessionsList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        for ( exerciseSession session : sessions) {
            Map<String, Object> sessionMap = new HashMap<>();
            sessionMap.put("sessionid", session.getId());

            // Thông tin khách hàng
            sessionMap.put("customer_name", session.getCustomer().getFirstname() + " " +
                    session.getCustomer().getLastname());

            // Thời gian
            LocalDateTime beginAt = session.getBeginAt();
            LocalDateTime endAt = session.getEndAt();

            sessionMap.put("begin_time", beginAt != null ? beginAt.format(formatter) : "Chưa xác định");
            sessionMap.put("end_time", endAt != null ? endAt.format(formatter) : "Chưa xác định");

            // Loại và mô tả
            sessionMap.put("exercise_type", session.getExerciseType());
            sessionMap.put("description", session.getDescription() != null ? session.getDescription() : "Chưa xác định");

            sessionsList.add(sessionMap);
        }
        return sessionsList;
    }

    @Override
    public boolean addSession(int customerid, int ptid, String exerciseType, Timestamp beginAt) {
        customer customer = customerRepository.findById(customerid);
        if (customer == null) {
            return false;
        }

        staff staff = staffRepository.findById(ptid);
        if (staff == null) {
            return false;
        }
        if(customer.getUserid().isDeleted() == true && staff.getUserid().isDeleted() == true){
            return false;
        } else {
            try {

                // Tạo đối tượng exerciseSession sử dụng builder pattern
                exerciseSession session = exerciseSession.builder()
                        .customer(customer)
                        .staff(staff)
                        .ExerciseType(exerciseType)
                        .beginAt(beginAt.toLocalDateTime()) // <<< thêm dòng này
                        .build();

                // Lưu buổi tập vào database
                exerSessionRepository.save(session);

                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    @Override
    public boolean updateSession(int sessionid,String cufirstname, String culastname, String ptfirstname, String ptlastname, String exerciseType) {
        exerciseSession session = exerSessionRepository.findById(sessionid).orElse(null);
        if (session == null) {
            return false;
        }
        customer customer = customerRepository.findByFirstnameAndLastname(cufirstname, culastname);
        if (customer == null) {
            return false; // Không tìm thấy khách hàng
        }
        staff staff = staffRepository.findByFirstnameAndLastname(ptfirstname, ptlastname);
        if (staff == null) {
            return false; // Không tìm thấy huấn luyện viên
        }
        try {
            // Cập nhật các thuộc tính của đối tượng hiện có
            session.setCustomer(customer);
            session.setStaff(staff);
            session.setExerciseType(exerciseType);
            // Lưu buổi tập vào database
            exerSessionRepository.save(session);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateSessionPT(int sessionid, LocalDateTime beginAt, LocalDateTime endAt, String description) {
        exerciseSession session = exerSessionRepository.findById(sessionid).orElse(null);
        if (session == null) {
            return false;
        }
        try {
            session.setBeginAt(beginAt);
            session.setEndAt(endAt);
            session.setDescription(description);

            exerSessionRepository.save(session);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
