package com.recognize.task.controller;

import com.recognize.task.service.ITaskRecognizeService;
import com.recognize.task.vo.TaskRecognizeVO;
import com.recognize.user.util.LoginUser;
import com.recognize.user.vo.BaseUserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/recognize")
public class TaskRecognizeController {
    @Autowired
    private ITaskRecognizeService taskRecognizeService;

    @PostMapping("/{taskId}/{screenId}")
    public ResponseEntity<TaskRecognizeVO> recognize(@PathVariable Long taskId,
                                                     @PathVariable Long screenId,
                                                     @LoginUser BaseUserVO currentUser,
                                                     @RequestParam(value = "file") MultipartFile file) throws IOException {
        return new ResponseEntity<>(taskRecognizeService.analysisScreenImage(taskId, screenId, file, currentUser), HttpStatus.OK);
    }
}
