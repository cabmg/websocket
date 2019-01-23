package com.ggzn.websocket.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Author: cabmg
 * Date: 2019/1/2
 * Time: 15:12
 */
@RestController
public class WsController {


    @GetMapping("/ws")
    public ResponseEntity test(String msg){
        return ResponseEntity.ok(msg);
    }

}
