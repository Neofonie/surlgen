package de.neofonie.surlgen.processor.spring;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class HelloWorldController {

    public static final String CONST_URL = "/fooiii";

    @RequestMapping("/foo")
    public String foo() {
        return "/index";
    }

    @RequestMapping("/")
    public String index(Model model) {
        return "/index";
    }

    @RequestMapping(value = CONST_URL)
    public String foo(@RequestParam("pp") Long pp,
                      @RequestParam("a") Long a,
                      @RequestParam(value = "b", required = false) String b,
                      @RequestParam("c") Integer c,
                      @RequestParam("d") long d,
                      HttpServletRequest request,
                      Model model) {

        return "/index";

    }

    @RequestMapping(value = "fooB")
    public String foo(@RequestParam("g") Long g,
                      @RequestParam("h") Long h,
                      @RequestParam("i") Integer i,
                      @RequestParam(value = "message", required = false) String j,
                      HttpServletRequest request, Model model) {

        return "/index";
    }

    @RequestMapping("/date")
    public String doWithDate(@RequestParam Date date) {
        return "/index";
    }

    @RequestMapping("/doWithModel")
    public String doWithModel(@ModelAttribute HelloWorldCommand command) {
        return "/index";
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }

    @RequestMapping(path = "/accounts/{account}", method = RequestMethod.PUT)
    public String save(@ModelAttribute("account") Account account) {
        return "/index";
    }

    public String dieNicht() {
        return "";
    }
}
