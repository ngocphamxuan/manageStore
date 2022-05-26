package com.example.version4.controller.admin;



import com.example.version4.domain.Account;
import com.example.version4.model.AccountDto;

import com.example.version4.service.AccountService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("admin/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @RequestMapping("")
    public String list(ModelMap modelMap){
        List<Account> list = accountService.findAll();
        modelMap.addAttribute("accounts", list);
        return "admin/accounts/list";
    }

    @GetMapping("add")
    public String add(Model model){
        model.addAttribute("account", new AccountDto());
        return "admin/accounts/addOrEdit";
    }

    @GetMapping("edit/{username}")
    public ModelAndView edit(ModelMap modelMap, @PathVariable("username") String username){
        Optional<Account> opt = accountService.findById(username);
        AccountDto dto = new AccountDto();
        if (opt.isPresent()){
            Account entity = opt.get();
            BeanUtils.copyProperties(entity, dto);
            dto.setPassword("");
            dto.setIsEdit(true);
            modelMap.addAttribute("account", dto);
            return new ModelAndView( "admin/accounts/addOrEdit", modelMap);
        }
        modelMap.addAttribute("message", "account is not existed!");
        return new ModelAndView("forward:/admin/accounts", modelMap) ;
    }

    @PostMapping("saveOrUpdate")
    public ModelAndView saveOrUpdate(ModelMap model,
                                     @Valid @ModelAttribute("account") AccountDto dto, BindingResult result){

        if(result.hasErrors()) {
            return new ModelAndView("admin/accounts/addOrEdit");
        }
        Account entity = new Account();
        BeanUtils.copyProperties(dto, entity);
        accountService.save(entity);
        model.addAttribute("message", "account is saved");
        return new ModelAndView("forward:/admin/accounts", model) ;
    }

    @GetMapping("delete/{username}")
    public ModelAndView delete(ModelMap map, @PathVariable("username") String username){
        if(accountService.findById(username).isPresent()){
            accountService.deleteById(username);
            map.addAttribute("message", "account is deleted!");
        }
        else {
            map.addAttribute("message", "account is not exist!");
        }
        return new ModelAndView("forward:/admin/accounts", map) ;
    }


//    @GetMapping("search")
//    public String search(ModelMap modelMap, @RequestParam(name = "name", required = false) String name) {
//        List<account> list = null;
//        if(StringUtils.hasText(name)){
//            list = categoryService.findByNameContaining(name);
//        }
//        else {
//            list = categoryService.findAll();
//        }
//        modelMap.addAttribute("accounts", list);
//        return "admin/accounts/search";
//    }
//
//    @GetMapping("searchpaginated")
//    public String search(ModelMap modelMap,
//                         @RequestParam(name = "name", required = false) String name,
//                         @RequestParam(name = "page", required = false) Optional<Integer> page,
//                         @RequestParam(name = "size", required = false) Optional<Integer> size) {
//        int currentPage = page.orElse(1);
//        int pageSize = size.orElse(5);
//        Pageable pageable = PageRequest.of(currentPage - 1, pageSize);
//
//        Page<account> resultPage = null;
//
//        if(StringUtils.hasText(name)){
//            resultPage = categoryService.findByNameContaining(name, pageable);
//            modelMap.addAttribute("name", name);
//        }
//        else {
//            resultPage = categoryService.findAll(pageable);
//        }
//
//        int totalPages = resultPage.getTotalPages();
//
//        if(totalPages>0) {
//            int start = Math.max(1, currentPage-2);
//            int end = Math.min(currentPage+2, totalPages);
//
//            if(totalPages>5) {
//                if(end == totalPages) start = end -5;
//                else if (start==1) end = start +5;
//
//            }
//            List<Integer> pageNumbers = IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());
//            modelMap.addAttribute("pageNumbers", pageNumbers);
//        }
//
//        modelMap.addAttribute("categoryPage", resultPage);
//        return "admin/accounts/searchpaginated";
//    }
//
//

}
