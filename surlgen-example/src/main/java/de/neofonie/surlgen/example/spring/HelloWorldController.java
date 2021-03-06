/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Neofonie GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.neofonie.surlgen.example.spring;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

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
    public String doWithDate(@RequestParam Date date, Model model) {
        model.addAttribute("date", date);
        return "/index";
    }

    @RequestMapping("/collection")
    public String collection(@RequestParam List<String> stringList, Model model) {
        model.addAttribute("stringList", stringList);
        return "/index";
    }

    @RequestMapping("/doWithModel")
    public String doWithModel(@ModelAttribute HelloWorldCommand command, Model model) {
        return "/index";
    }

    @RequestMapping(path = "/accounts/{account}", method = RequestMethod.PUT)
    public String save(@ModelAttribute("account") Account account) {
        return "/index";
    }

    public String dieNicht() {
        return "";
    }
}
