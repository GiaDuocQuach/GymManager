package com.example.itssprj_ver1.service;

import com.example.itssprj_ver1.model.staff;
import com.example.itssprj_ver1.repository.staffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class staffService implements staffServiceI {
    @Autowired
    private staffRepository staffRepository;

    @Override
    public boolean updateStaff(int staffid, String firstname, String lastname, String email, String phone, int age, String rank) {
        staff staff = staffRepository.findById(staffid);
        if (staff != null) {
            staff.setFirstname(firstname);
            staff.setLastname(lastname);
            staff.setEmail(email);
            staff.setPhone(phone);
            staff.setAge(age);
            staff.setRank(rank);
            try {
                staffRepository.save(staff);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }
}
