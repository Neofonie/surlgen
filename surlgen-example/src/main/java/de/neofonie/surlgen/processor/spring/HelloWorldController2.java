package de.neofonie.surlgen.processor.spring;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/hotels/{hotel}")
public class HelloWorldController2 {

    public static final String CONST_URL = "/fooiii";

    @RequestMapping("/bookings/{booking}")
    public String getBooking(@PathVariable String booking) {
        return "/index";
    }

    @RequestMapping("/pets/{petId}")
    public String findPet(@PathVariable String hotel, @PathVariable String petId, Model model) {
        return "/index";
    }

    @RequestMapping(path = "/pets/{petId}", method = RequestMethod.GET)
    public String findPet2(@PathVariable String petId, @MatrixVariable int q) {
        return "/index";
    }
}
