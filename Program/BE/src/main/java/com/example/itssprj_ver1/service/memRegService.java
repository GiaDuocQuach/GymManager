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

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class memRegService implements memRegServiceI {

    @Autowired
    private memRegRepository memRegRepository;

    @Autowired
    private customerRepository customerRepository;

    @Autowired
    private membershipRepository membershipRepository;

    @Override
    public boolean addMemberReg(String phone, String namepackage, String status, Date beginAt, Date endAt) {
        try {
            customer customer = customerRepository.findByPhone(phone);
            if (customer == null) {
                return false;
            }
            membership membership = membershipRepository.findByName(namepackage);
            if (membership == null) {
                return false;
            }
            memberRegister newMemberReg = memberRegister.builder()
                    .customer(customer)
                    .membership(membership)
                    .createAt(new Date(System.currentTimeMillis()))
                    .status(status)
                    .beginAt(beginAt)
                    .endAt(endAt)
                    .build();
            memRegRepository.save(newMemberReg);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateMemberReg(int memberRegId, String status, Date beginAt, Date endAt) {
        try {
            memberRegister existingMemberReg = memRegRepository.findById(memberRegId).orElse(null);
            if (existingMemberReg == null) {
                return false;
            }

            // Update fields
            existingMemberReg.setStatus(status);
            existingMemberReg.setBeginAt(beginAt);
            existingMemberReg.setEndAt(endAt);
            existingMemberReg.setCreateAt(new Date( System.currentTimeMillis()));

            memRegRepository.save(existingMemberReg);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteMemberReg(int memberRegId, String status) {
        memberRegister existingMemberReg = memRegRepository.findById(memberRegId).orElse(null);
        if (existingMemberReg == null) {
            return false;
        }
        existingMemberReg.setStatus(status);
        memRegRepository.save(existingMemberReg);
        return true;
    }

    @Override
    public List<Map<String, Object>> getMemberRegByStatus(String status) {
        try {
            List<memberRegister> memberRegs = memRegRepository.findByStatus(status);

            List<Map<String, Object>> result = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            for (memberRegister memReg : memberRegs) {
                Map<String, Object> regInfo = new HashMap<>();
                regInfo.put("id", memReg.getId());
                regInfo.put("customerName", memReg.getCustomer().getFirstname() + " " + memReg.getCustomer().getLastname());
                regInfo.put("membershipName", memReg.getMembership().getName());
                regInfo.put("status", memReg.getStatus());
                regInfo.put("createAt", memReg.getCreateAt().toLocalDate().format(formatter));
                regInfo.put("beginAt", memReg.getBeginAt().toLocalDate().format(formatter));
                regInfo.put("endAt", memReg.getEndAt().toLocalDate().format(formatter));
                
                result.add(regInfo);
            }
            
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Map<String, Object>> getMemberRegByCreateAt(Date createAt) {
        try {
            List<memberRegister> memberRegs = memRegRepository.findByCreateAt(createAt);

            List<Map<String, Object>> result = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            for (memberRegister memReg : memberRegs) {
                Map<String, Object> regInfo = new HashMap<>();
                regInfo.put("id", memReg.getId());
                regInfo.put("customerName", memReg.getCustomer().getFirstname() + " " + memReg.getCustomer().getLastname());
                regInfo.put("membershipName", memReg.getMembership().getName());
                regInfo.put("status", memReg.getStatus());
                regInfo.put("createAt", memReg.getCreateAt().toLocalDate().format(formatter));
                regInfo.put("beginAt", memReg.getBeginAt().toLocalDate().format(formatter));
                regInfo.put("endAt", memReg.getEndAt().toLocalDate().format(formatter));

                result.add(regInfo);
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Map<String, Object>> getAllMemberReg() {
        try {
            List<memberRegister> allMemberRegs = memRegRepository.findAll();
            List<Map<String, Object>> memberRegisters = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            if (allMemberRegs != null) {
                memberRegisters = allMemberRegs.stream().map(memReg -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("id", memReg.getId());
                    result.put("customerName", memReg.getCustomer().getFirstname() + " " + memReg.getCustomer().getLastname());
                    result.put("membershipName", memReg.getMembership().getName());
                    Date endDate = memReg.getEndAt();
                    Date currentTime = new Date(System.currentTimeMillis());
                    if (endDate.after(currentTime) ) {
                        result.put("status", memReg.getStatus());
                    } else {
                        result.put("status", "Hết hạn");
                    }
                    result.put("createAt", memReg.getCreateAt().toLocalDate().format(formatter));
                    result.put("beginAt", memReg.getBeginAt().toLocalDate().format(formatter));
                    result.put("endAt", memReg.getEndAt().toLocalDate().format(formatter));
                    return result;
                }).collect(Collectors.toList());
            }
            return memberRegisters;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Map<String, Object>> getAllMemberRegByCustomer(int customerId) {
        List<memberRegister> allMemberRegs = memRegRepository.findAllByCustomer_Userid_Id(customerId);
        if(allMemberRegs == null || allMemberRegs.isEmpty()) {
            return null;
        }
        List<Map<String, Object>> mapList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for(memberRegister memReg : allMemberRegs) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", memReg.getId());
            map.put("customerName", memReg.getCustomer().getFirstname() + " " + memReg.getCustomer().getLastname());
            map.put("membershipName", memReg.getMembership().getName());
            Date endDate = memReg.getEndAt();
            Date currentTime = new Date(System.currentTimeMillis());
            if (endDate.after(currentTime) ) {
                map.put("status", memReg.getStatus());
            } else {
                map.put("status", "Hết hạn");
            }
            map.put("createAt", memReg.getCreateAt().toLocalDate().format(formatter));
            map.put("beginAt", memReg.getBeginAt().toLocalDate().format(formatter));
            map.put("endAt", memReg.getEndAt().toLocalDate().format(formatter));
            mapList.add(map);
        }
        return mapList;
    }

    @Override
    public List<Map<String, Object>> getMemberRegByStatusAndCreateAt(String status, Date createAt) {
        try {
            List<memberRegister> memberRegs = memRegRepository.findByStatusAndCreateAt(status, createAt);

            List<Map<String, Object>> result = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            for (memberRegister memReg : memberRegs) {
                Map<String, Object> regInfo = new HashMap<>();
                regInfo.put("id", memReg.getId());
                regInfo.put("customerName", memReg.getCustomer().getFirstname() + " " + memReg.getCustomer().getLastname());
                regInfo.put("membershipName", memReg.getMembership().getName());
                regInfo.put("status", memReg.getStatus());
                regInfo.put("createAt", memReg.getCreateAt().toLocalDate().format(formatter));
                regInfo.put("beginAt", memReg.getBeginAt().toLocalDate().format(formatter));
                regInfo.put("endAt", memReg.getEndAt().toLocalDate().format(formatter));

                result.add(regInfo);
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}