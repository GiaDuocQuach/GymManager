package com.example.itssprj_ver1.service;

import com.example.itssprj_ver1.model.staff;
import com.example.itssprj_ver1.model.users;
import com.example.itssprj_ver1.repository.staffRepository;
import com.example.itssprj_ver1.repository.userRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class managerService implements managerServiceI{
    @Autowired
    private userRepository userRepository;
    @Autowired
    private staffRepository staffRepository;

    @Override
    public boolean addManager(String firstname, String lastname, String email, String phone, String gender, int age, int userid) {
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
    public boolean deleteManager(int id) {
        staff staff = staffRepository.findById(id);
        if (staff != null) {
            staffRepository.delete(staff);
            return true;
        }
        return false;
    }
}
