package com.smartclassroom.service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.smartclassroom.entity.Room;
import com.smartclassroom.entity.Timetable;
import com.smartclassroom.repository.RoomRepository;
import com.smartclassroom.repository.TimetableRepository;

@Service
public class OccupancyService {

    @Autowired
    private TimetableRepository timetableRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Scheduled(fixedRate = 60000)
    public void updateRoomOccupancy() {

        LocalTime currentTime = LocalTime.now();

        DayOfWeek currentDay =
                LocalDateTime.now().getDayOfWeek();

        List<Timetable> timetableList =
                timetableRepository.findAll();

        for (Timetable timetable : timetableList) {

            Room room = timetable.getRoom();

            if (timetable.getDayOfWeek()
                    .equalsIgnoreCase(currentDay.toString())) {

                if (currentTime.isAfter(timetable.getStartTime())
                        && currentTime.isBefore(timetable.getEndTime())) {

                    room.setStatus("OCCUPIED");

                } else {

                    room.setStatus("AVAILABLE");
                }

                roomRepository.save(room);
            }
        }
    }
}