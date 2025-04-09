package com.example.demo.Controller;

import com.example.demo.Dto.RoomDto;
import com.example.demo.Service.RoomService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@AllArgsConstructor
@RestController
@RequestMapping("/api/room")
public class RoomController {

    @Autowired
    private final RoomService roomService;

    @GetMapping
    public List<RoomDto> getEpics() {
        return roomService.getRooms();
    }



}
