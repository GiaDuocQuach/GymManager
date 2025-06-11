package com.example.itssprj_ver1.controller;

import com.example.itssprj_ver1.model.customer;
import com.example.itssprj_ver1.repository.customerRepository;
import com.example.itssprj_ver1.repository.memRegRepository;
import com.example.itssprj_ver1.repository.membershipRepository;
import com.example.itssprj_ver1.repository.userRepository;
import com.example.itssprj_ver1.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.itssprj_ver1.config.GenToken.generateToken;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class customerController {

    private final customerService customerService;
    private final membershipService membershipService;
    private final reviewService reviewService;
    private final paymentService paymentService;
    private final exerSession exerSession;
    private final memRegService memRegService;

    @PostMapping("/getCustomer")
    public ResponseEntity<Map<String, Object>> getCustomer(@RequestHeader(value = "token", required = false) String token,
                                                            @RequestBody Map<String, String> request) {
        Map<String, Object> resp = new HashMap<>();
        try {
            if (token == null || token.isEmpty()) {
                resp.put("status", "error");
                resp.put("message", "Token is missing");
                return ResponseEntity.badRequest().body(resp);
            }
            int userId = Integer.parseInt(request.get("userid"));

            if (userId <= 0) {
                resp.put("status", "error");
                resp.put("message", "User ID is missing");
                return ResponseEntity.badRequest().body(resp);
            }
            customer customer = customerService.infoCustomer(userId);
            if (customer != null) {
                resp.put("status", "success");
                resp.put("customerId" , customer.getId());
                resp.put("name" , customer.getFirstname() + " " + customer.getLastname());
                resp.put("phone" , customer.getPhone());
                resp.put("email" , customer.getEmail());
                resp.put("age" , customer.getAge());
                resp.put("gender" , customer.getGender());
                return ResponseEntity.ok(resp);
            } else {
                resp.put("status", "error");
                resp.put("message", "Customer not found");
                return ResponseEntity.status(404).body(resp);
            }
        } catch (Exception e) {
            resp.put("status", "error");
            resp.put("message", "An error occurred: " + e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    @PostMapping("/addReview")
    public ResponseEntity<Map<String, Object>> addReview(@RequestHeader(value = "token", required = false) String token,
                                                         @RequestBody Map<String, String> request) {
        Map<String, Object> resp = new HashMap<>();
        try {
            if (token == null || token.isEmpty()) {
                resp.put("status", "error");
                resp.put("message", "Token is missing");
                return ResponseEntity.badRequest().body(resp);
            }
            int userId = Integer.parseInt(request.get("userid"));
            String text = request.get("text");
            if (text == null || text.isEmpty()) {
                resp.put("status", "error");
                resp.put("message", "Review text is missing");
                return ResponseEntity.badRequest().body(resp);
            }
            boolean success = reviewService.addReview(userId, text);
            if (success) {
                resp.put("status", "success");
                resp.put("message", "Review added successfully");
                return ResponseEntity.ok(response);
            } else {
                resp.put("status", "error");
                resp.put("message", "Failed to add review");
                return ResponseEntity.status(500).body(resp);
            }
        } catch (Exception e) {
            resp.put("status", "error");
            resp.put("message", "An error occurred: " + e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    @PostMapping("/getReviews")
    public ResponseEntity<Map<String, Object>> getReview(@RequestHeader(value = "token", required = false) String token,
                                                         @RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Kiểm tra token
            if (token == null || token.isEmpty()) {
                response.put("success", false);
                response.put("message", "Token is missing or invalid");
                return ResponseEntity.badRequest().body(response);
            }

            int userid = Integer.parseInt(request.get("userid"));
            List<Map<String, Object>> reviews = reviewService.getReview(userid);
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

    @PostMapping("/getPayment")
    public ResponseEntity<Map<String, Object>> getPayment(@RequestHeader (value = "token", required = false) String token,
                                                          @RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Kiểm tra token
            if (token == null || token.isEmpty()) {
                response.put("success", false);
                response.put("message", "Token is missing or invalid");
                return ResponseEntity.badRequest().body(response);
            }

            int userId = Integer.parseInt(request.get("userid"));

            List<Map<String, Object>> payments = paymentService.getAllPayment(userId);
            if (payments != null) {
                response.put("status", "Thành công");
                response.put("data", payments);
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "Thất bại");
                return ResponseEntity.status(500).body(response);
            }
        } catch (Exception e) {
            response.put("message", "Đã xảy ra lỗi: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/getSession")
    public ResponseEntity<Map<String, Object>> getSession(@RequestHeader (value = "token", required = false) String token,
                                                          @RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Kiểm tra token
            if (token == null || token.isEmpty()) {
                response.put("success", false);
                response.put("message", "Token is missing or invalid");
                return ResponseEntity.badRequest().body(response);
            }

            int userId = Integer.parseInt(request.get("userid"));

            List<Map<String, Object>> sessions = exerSession.getAllSession(userId);
            if (sessions != null) {
                response.put("status", "Thành công");
                response.put("data", sessions);
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "Thất bại");
                return ResponseEntity.status(500).body(response);
            }
        } catch (Exception e) {
            response.put("message", "Đã xảy ra lỗi: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/getpackages")
    public ResponseEntity<Map<String,Object>> getMemberships(@RequestHeader(value = "token", required = false) String token,
                                                             @RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Kiểm tra token
            if (token == null || token.isEmpty()) {
                response.put("success", false);
                response.put("message", "Token is missing or invalid");
                return ResponseEntity.badRequest().body(response);
            }

            int userId = Integer.parseInt(request.get("userid"));
            List<Map<String, Object>> memberships = membershipService.getMembership(userId);
            if (memberships != null) {
                response.put("status", "Lấy danh sách gói tập thành công");
                response.put("list", memberships);
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "Không có gói tập nào");
                return ResponseEntity.status(404).body(response);
            }
        } catch (Exception e) {
            response.put("message", "Đã xảy ra lỗi: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/getMemberRegistration")
    public ResponseEntity<Map<String, Object>> getMemberRegistration(@RequestHeader(value = "token", required = false) String token,
                                                                     @RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Kiểm tra token
            if (token == null || token.isEmpty()) {
                response.put("success", false);
                response.put("message", "Token is missing or invalid");
                return ResponseEntity.badRequest().body(response);
            }

            int userId = Integer.parseInt(request.get("userid"));

            List<Map<String, Object>> memReg = memRegService.getAllMemberRegByCustomer(userId);
            if (memReg != null) {
                response.put("status", "Thành công");
                response.put("data", memReg);
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "Thất bại");
                return ResponseEntity.status(500).body(response);
            }
        } catch (Exception e) {
            response.put("message", "Đã xảy ra lỗi: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }


}

