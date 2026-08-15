package com.smartclassroom.service;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.smartclassroom.entity.Faculty;
import com.smartclassroom.entity.Room;
import com.smartclassroom.entity.Timetable;
import com.smartclassroom.entity.User;
import com.smartclassroom.repository.FacultyRepository;
import com.smartclassroom.repository.RoomRepository;
import com.smartclassroom.repository.TimetableRepository;
import com.smartclassroom.repository.UserRepository;

@Service
public class ExcelUploadService {

    @Autowired
    private TimetableRepository timetableRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuditLogService auditLogService;

    private String getCellValueAsString(Row row, int cellIndex, DataFormatter formatter) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) return "";
        return formatter.formatCellValue(cell).trim();
    }

    public String uploadFaculty(MultipartFile file) throws IOException {
        int successCount = 0;

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header

                String name = getCellValueAsString(row, 0, formatter);
                String dept = getCellValueAsString(row, 1, formatter);
                String email = getCellValueAsString(row, 2, formatter);
                String password = getCellValueAsString(row, 3, formatter);

                if (name.isEmpty() || email.isEmpty()) {
                    continue; // Skip invalid rows
                }

                if (password.isEmpty()) {
                    password = "faculty123";
                }

                // Check if user/faculty already exists
                User user = userRepository.findByEmail(email).orElseGet(() -> {
                    User newUser = new User();
                    newUser.setName(name);
                    newUser.setEmail(email);
                    newUser.setPassword("faculty123");
                    newUser.setRole("FACULTY");
                    return userRepository.save(newUser);
                });

                if (!password.equals("faculty123")) {
                    user.setPassword(password);
                    userRepository.save(user);
                }

                Faculty faculty = facultyRepository.findByEmailIgnoreCase(email).orElseGet(() -> {
                    Faculty newFaculty = new Faculty();
                    newFaculty.setEmail(email);
                    return newFaculty;
                });

                faculty.setFacultyName(name);
                faculty.setDepartment(dept.isEmpty() ? "General" : dept);
                faculty.setUser(user);

                facultyRepository.save(faculty);
                successCount++;
            }
        }

        auditLogService.saveLog("Excel Faculty Upload", successCount + " faculty added");
        return successCount + " Faculty records imported successfully!";
    }

    public String uploadRooms(MultipartFile file) throws IOException {
        int successCount = 0;

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header

                String roomNumber = getCellValueAsString(row, 0, formatter);
                String roomType = getCellValueAsString(row, 1, formatter);
                String status = getCellValueAsString(row, 2, formatter);
                String building = getCellValueAsString(row, 3, formatter);
                String capacityStr = getCellValueAsString(row, 4, formatter);

                if (roomNumber.isEmpty()) continue;

                if (roomType.isEmpty()) roomType = "Classroom";
                if (status.isEmpty()) status = "AVAILABLE";

                Integer capacity = 60;
                try {
                    if (!capacityStr.isEmpty()) capacity = Integer.parseInt(capacityStr);
                } catch (Exception ignored) {}

                String finalRoomNumber = roomNumber;
                Room room = roomRepository.findByRoomNumber(roomNumber).orElseGet(() -> {
                    Room newRoom = new Room();
                    newRoom.setRoomNumber(finalRoomNumber);
                    return newRoom;
                });

                room.setRoomType(roomType);
                room.setStatus(status.toUpperCase());
                if (!building.isEmpty()) room.setBuilding(building);
                room.setCapacity(capacity);

                roomRepository.save(room);
                successCount++;
            }
        }

        auditLogService.saveLog("Excel Room Upload", successCount + " rooms added");
        return successCount + " Room records imported successfully!";
    }

    public String uploadTimetable(MultipartFile file) throws IOException {
        int successCount = 0;

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header

                String subjectName = getCellValueAsString(row, 0, formatter);
                String facultyVal = getCellValueAsString(row, 1, formatter);
                String roomVal = getCellValueAsString(row, 2, formatter);
                String day = getCellValueAsString(row, 3, formatter);
                String startStr = getCellValueAsString(row, 4, formatter);
                String endStr = getCellValueAsString(row, 5, formatter);

                if (subjectName.isEmpty() || facultyVal.isEmpty() || roomVal.isEmpty() || day.isEmpty()) {
                    continue;
                }

                // Resolve Faculty (by ID, Email, or Name)
                Faculty faculty = null;
                try {
                    int facultyId = Integer.parseInt(facultyVal);
                    faculty = facultyRepository.findById(facultyId).orElse(null);
                } catch (NumberFormatException ignored) {}

                if (faculty == null) {
                    faculty = facultyRepository.findByEmailIgnoreCase(facultyVal).orElse(null);
                }
                if (faculty == null) {
                    faculty = facultyRepository.findByFacultyNameIgnoreCase(facultyVal).orElse(null);
                }
                if (faculty == null) {
                    throw new RuntimeException("Row " + (row.getRowNum() + 1) + ": Faculty '" + facultyVal + "' not found. Please upload Faculty first!");
                }

                // Resolve Room (by ID or Room Number)
                Room room = null;
                try {
                    int roomId = Integer.parseInt(roomVal);
                    room = roomRepository.findById(roomId).orElse(null);
                } catch (NumberFormatException ignored) {}

                if (room == null) {
                    room = roomRepository.findByRoomNumber(roomVal).orElse(null);
                }
                if (room == null) {
                    throw new RuntimeException("Row " + (row.getRowNum() + 1) + ": Room '" + roomVal + "' not found. Please upload Rooms first!");
                }

                LocalTime startTime = parseTime(startStr);
                LocalTime endTime = parseTime(endStr);

                Timetable timetable = new Timetable();
                timetable.setSubjectName(subjectName);
                timetable.setFaculty(faculty);
                timetable.setRoom(room);
                timetable.setDayOfWeek(day.toUpperCase());
                timetable.setStartTime(startTime);
                timetable.setEndTime(endTime);

                timetableRepository.save(timetable);
                successCount++;
            }
        }

        auditLogService.saveLog("Excel Timetable Upload", successCount + " timetable slots added");
        return successCount + " Timetable entries imported successfully!";
    }

    private LocalTime parseTime(String timeStr) {
        if (timeStr == null || timeStr.trim().isEmpty()) {
            return LocalTime.of(9, 0);
        }
        timeStr = timeStr.trim().toUpperCase();
        try {
            if (timeStr.length() == 5 && timeStr.contains(":")) { // e.g. 09:00
                return LocalTime.parse(timeStr + ":00");
            }
            if (timeStr.length() == 8 && timeStr.contains(":")) { // e.g. 09:00:00
                return LocalTime.parse(timeStr);
            }
            DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern("[h:mm a][hh:mm a][H:mm][HH:mm]")
                    .toFormatter(Locale.ENGLISH);
            return LocalTime.parse(timeStr, formatter);
        } catch (Exception e) {
            return LocalTime.of(9, 0);
        }
    }
}