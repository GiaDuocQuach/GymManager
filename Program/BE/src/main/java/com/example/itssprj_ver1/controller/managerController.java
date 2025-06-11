package com.example.itssprj_ver1.controller;

import com.example.itssprj_ver1.service.*;
import com.example.itssprj_ver1.repository.userRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.sql.Date;

@RestController
@RequestMapping("/manager")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class managerController {
    private final reviewService reviewService;
    private final roomEquipmentService roomEquipmentService;
    private final exerSession exerSession;
    private final paymentService paymentService;
    private final memRegService memRegService;
    private final customerService customerService;
    private final membershipService membershipService;
    private final roomService roomService;

    @GetMapping("/getReviews")
    public ResponseEntity<Map<String, Object>> getReview(@RequestHeader(value = "token", required = false) String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Kiểm tra token
            if (token == null || token.isEmpty()) {
                response.put("success", false);
                response.put("message", "Token is missing or invalid");
                return ResponseEntity.badRequest().body(response);
            }

            List<Map<String, Object>> reviews = reviewService.getReview();
            if (reviewService.getReview() != null) {
                response.put("status", "Lấy danh sách review thành công");
                response.put("data", reviews);
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "Không có review nào");
                return ResponseEntity.status(404).body(response);
            }
        } catch (Exception e) {
            response.put("message", "Đã xảy ra lỗi: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    
    @GetMapping("/getInfoCustomer")
    public ResponseEntity<Map<String, Object>> getInfoCustomer(@RequestHeader(value = "token", required = false) String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Kiểm tra token
            if (token == null || token.isEmpty()) {
                response.put("success", false);
                response.put("message", "Token is missing or invalid");
                return ResponseEntity.badRequest().body(response);
            }

            List<Map<String, Object>> customers = customerService.getAllCustomers();
            if (customers != null) {
                response.put("status", "Lấy danh sách khách hàng thành công");
                response.put("list", customers);
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "Không có khách hàng nào");
                return ResponseEntity.status(404).body(response);
            }
        } catch (Exception e) {
            response.put("message", "Đã xảy ra lỗi: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/addDevice")
    public ResponseEntity<Map<String, Object>> addDevice(@RequestHeader(value = "token", required = false) String token,
                                                        @RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Kiểm tra token
            if (token == null || token.isEmpty()) {
                response.put("success", false);
                response.put("message", "Token is missing or invalid");
                return ResponseEntity.badRequest().body(response);
            }

            String room_name = request.get("room_name");
            String equipment_name = request.get("equipment_name");
            int quantity = Integer.parseInt(request.get("quantity"));
            String status = request.get("status");

            if (roomEquipmentService.addRoomEquipment(room_name, equipment_name, quantity, status)) {
                response.put("status", "Thêm thiết bị thành công");
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "Thêm thiết bị không thành công");
                return ResponseEntity.status(400).body(response);
            }
        } catch (Exception e) {
            response.put("message", "Đã xảy ra lỗi: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}

@PostMapping("/updateDevice")
    public ResponseEntity<Map<String, Object>> updateDevice(@RequestHeader(value = "token", required = false) String token,
                                                            @RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Kiểm tra token
            if (token == null || token.isEmpty()) {
                response.put("success", false);
                response.put("message", "Token is missing or invalid");
                return ResponseEntity.badRequest().body(response);
            }

            String room_name = request.get("room_name");
            String equipment_name = request.get("equipment_name");
            String status = request.get("status");

            if (roomEquipmentService.updateRoomEquipment(room_name, equipment_name, status)) {
                response.put("status", "Cập nhật thiết bị thành công");
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "Cập nhật thiết bị không thành công");
                return ResponseEntity.status(400).body(response);
            }
        } catch (Exception e) {
            response.put("message", "Đã xảy ra lỗi: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

@PostMapping("/deleteDevice")
    public ResponseEntity<Map<String, Object>> deleteDevice(@RequestHeader(value = "token", required = false) String token,
                                                            @RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Kiểm tra token
            if (token == null || token.isEmpty()) {
                response.put("success", false);
                response.put("message", "Token is missing or invalid");
                return ResponseEntity.badRequest().body(response);
            }

            String room_name = request.get("room_name");
            String equipment_name = request.get("equipment_name");

            if (roomEquipmentService.deleteRoomEquipment(room_name, equipment_name)) {
                response.put("status", "Xóa thiết bị thành công");
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "Xóa thiết bị không thành công");
                return ResponseEntity.status(400).body(response);
            }
        } catch (Exception e) {
            response.put("message", "Đã xảy ra lỗi: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }