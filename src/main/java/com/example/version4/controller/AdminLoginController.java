package com.example.version4.controller;

import com.example.version4.domain.Account;
import com.example.version4.model.AccountDto;
import com.example.version4.model.AdminLoginDto;
import com.example.version4.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class AdminLoginController {
    @Autowired
    private AccountService accountService;

    @Autowired
    private HttpSession session;

    @GetMapping("alogin")
    public String login(ModelMap modelMap) {
        modelMap.addAttribute("account", new AdminLoginDto());
        return "admin/accounts/login";
    }

    @PostMapping("alogin")
    public ModelAndView login(ModelMap modelMap,
                              @Valid @ModelAttribute("account") AdminLoginDto dto,
                              BindingResult result){
        if(result.hasErrors()){
            return new ModelAndView("admin/accounts/login", modelMap);
        }
        Account account = accountService.login(dto.getUsername(), dto.getPassword());
        if (account == null){
            modelMap.addAttribute("message", "Invalid username or password");
            return new ModelAndView("admin/accounts/login", modelMap);
        }
        session.setAttribute("username", account.getUsername());
        return new ModelAndView("forward:admin/categories", modelMap);
    }
}
