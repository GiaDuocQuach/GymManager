package com.example.itssprj_ver1.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface exerSessionI {
    List<Map<String,Object>> getAllSessions();
    List<Map<String,Object>> getAllSession(int customerId);
    List<Map<String,Object>> getAllSessionByPT(int ptId);
    boolean addSession(int customerid, int ptid, String exerciseType, Timestamp beginAt);
    boolean updateSession(int sessionid,String cufirstname, String culastname, String ptfirstname, String ptlastname, String exerciseType);
    boolean updateSessionPT(int sessionid, LocalDateTime beginAt, LocalDateTime endAt, String description);
}
