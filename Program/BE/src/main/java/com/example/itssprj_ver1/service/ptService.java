package com.example.itssprj_ver1.service;

import com.example.itssprj_ver1.model.staff;
import com.example.itssprj_ver1.model.users;
import com.example.itssprj_ver1.repository.staffRepository;
import com.example.itssprj_ver1.repository.userRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ptService implements ptServiceI {
    @Autowired
    private staffRepository staffRepository;
    @Autowired
    private userRepository userRepository;

    @Override
    public boolean addPT(String firstname, String lastname, String email, String phone, String gender, int age, int userid) {
        users user = userRepository.findById(userid);
        if (user == null) {
            return false;
        }
        staff staff = new staff();
        staff.setFirstname(firstname);
        staff.setLastname(lastname);
        staff.setEmail(email);
        staff.setPhone(phone);
        staff.setGender(gender);
        staff.setAge(age);
        staff.setRank("Iron");
        staff.setUserid(user);
        try{
            staffRepository.save(staff);
            return true;
        }catch (Exception e) {
            return false;
        }
    }

    @Override
    public staff getPTById(int id) {
        staff staff = staffRepository.findByUserid_Id(id);
            return staff;
    }

    @Override
    public List<Map<String, Object>> findAll() {
        List<staff> staffs = staffRepository.findAll();
        if (staffs == null || staffs.isEmpty()) {
            return null;
        }
        List<Map<String, Object>> staffMap = new ArrayList<>();
        for (staff staff : staffs) {
            Map<String, Object> map = new HashMap<>();
            if(staff.getUserid().isDeleted() == false && staff.getUserid().getRole().getRoleid() == 3) {
                map.put("id", staff.getId());
                map.put("name", staff.getFirstname() + " " + staff.getLastname());
                map.put("email", staff.getEmail());
                map.put("phone", staff.getPhone());
                map.put("rank", staff.getRank());
                staffMap.add(map);
            } else {
                continue;
            }
        }
        return staffMap;
    }
}
