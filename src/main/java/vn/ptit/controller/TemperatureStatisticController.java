package vn.ptit.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.ptit.model.ResponseJSON;

@Controller
@SuppressWarnings("unused")
@RequestMapping("/statistic")
public class TemperatureStatisticController {

    @GetMapping
    public ResponseEntity<?> getStatistic() {

        return ResponseJSON.ok("Get statistic successfully");
    }
}
