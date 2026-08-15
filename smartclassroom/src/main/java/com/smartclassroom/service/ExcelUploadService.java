package com.smartclassroom.service;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalTime;

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
import com.smartclassroom.repository.FacultyRepository;
import com.smartclassroom.repository.RoomRepository;
import com.smartclassroom.repository.TimetableRepository;

@Service
public class ExcelUploadService {

    @Autowired
    private TimetableRepository timetableRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private RoomRepository roomRepository;

    public String uploadTimetable(MultipartFile file) throws IOException {

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);

            DataFormatter formatter = new DataFormatter();

            for (Row row : sheet) {

                if (row.getRowNum() == 0) {
                    continue;
                }

                String subjectName =
                        formatter.formatCellValue(row.getCell(0));

                Integer facultyId =
                        (int) row.getCell(1).getNumericCellValue();

                Integer roomId =
                        (int) row.getCell(2).getNumericCellValue();

                String day =
                        formatter.formatCellValue(row.getCell(3));

                String start =
                        formatter.formatCellValue(row.getCell(4));

                String end =
                        formatter.formatCellValue(row.getCell(5));

                Faculty faculty =
                        facultyRepository.findById(facultyId)
                                .orElseThrow(() ->
                                        new RuntimeException("Faculty Not Found"));

                Room room =
                        roomRepository.findById(roomId)
                                .orElseThrow(() ->
                                        new RuntimeException("Room Not Found"));

                Timetable timetable = new Timetable();

                timetable.setSubjectName(subjectName);
                timetable.setFaculty(faculty);
                timetable.setRoom(room);
                timetable.setDayOfWeek(day);
                timetable.setStartTime(LocalTime.parse(start));
                timetable.setEndTime(LocalTime.parse(end));

                timetableRepository.save(timetable);
            }
        }

        return "Timetable Uploaded Successfully";
    }
}