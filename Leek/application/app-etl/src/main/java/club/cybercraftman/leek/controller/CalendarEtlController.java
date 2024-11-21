package club.cybercraftman.leek.controller;

import club.cybercraftman.leek.domain.financedata.CalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/etl/calendar")
public class CalendarEtlController {

    @Autowired
    private CalendarService calendarService;

}
