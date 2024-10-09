package vn.ptit.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ptit.model.ResponseJSON;

@RestController
@SuppressWarnings("unused")
@RequestMapping("/api/user")
public class UserController {

    @GetMapping(value = "/info")
    public ResponseEntity<?> getUserInfo() {

        return ResponseJSON.ok("o");
    }
}
