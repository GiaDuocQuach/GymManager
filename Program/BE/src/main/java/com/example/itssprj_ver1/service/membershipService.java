package com.example.itssprj_ver1.service;

import com.example.itssprj_ver1.model.customer;
import com.example.itssprj_ver1.model.memberRegister;
import com.example.itssprj_ver1.model.membership;
import com.example.itssprj_ver1.repository.customerRepository;
import com.example.itssprj_ver1.repository.memRegRepository;
import com.example.itssprj_ver1.repository.membershipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class membershipService implements membershipServiceI {

    @Autowired
    private membershipRepository membershipRepository;
    @Autowired
    private customerRepository customerRepository;
    @Autowired
    private memRegRepository memRegRepository;

    @Override
    public List<Map<String, Object>> getMembership() {
        List<membership> memberships = membershipRepository.findAll();
        if(memberships == null ||memberships.isEmpty()) {
            return null;
        }
        List<Map<String, Object>> mapList = new ArrayList<>();
        for(membership membership : memberships) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", membership.getId());
            map.put("namepackage", membership.getName());
            map.put("price", membership.getPrice());
            map.put("description", membership.getDescription());
            map.put("exercise_type", membership.getExerciseType());
            mapList.add(map);
        }
        return mapList;
    }

    @Override
    public List<Map<String, Object>> getMembership(int userId) {

        List<membership> allMemberships = membershipRepository.findAll();
        if (allMemberships == null || allMemberships.isEmpty()) {
            return null;
        }

        customer customer = customerRepository.findByUserid_Id(userId);
        if (customer == null) {
            return null;
        }

        List<memberRegister> registeredMemberships = memRegRepository.findAllByCustomer_Userid_Id(userId);

        Set<Integer> registeredMembershipIds = new HashSet<>();
        if (registeredMemberships != null) {
            for (memberRegister reg : registeredMemberships) {
                registeredMembershipIds.add(reg.getMembership().getId());
            }
        }

        List<Map<String, Object>> mapList = new ArrayList<>();
        for (membership membership : allMemberships) {
            if (!registeredMembershipIds.contains(membership.getId())) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", membership.getId());
                map.put("namepackage", membership.getName());
                map.put("price", membership.getPrice());
                map.put("description", membership.getDescription());
                map.put("exercise_type", membership.getExerciseType());
                mapList.add(map);
            }
        }

        return mapList;
    }
}
