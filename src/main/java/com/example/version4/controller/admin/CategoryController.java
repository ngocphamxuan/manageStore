package com.example.version4.controller.admin;



import com.example.version4.domain.Category;
import com.example.version4.model.CategoryDto;
import com.example.version4.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("admin/categories")
public class CategoryController {
    @Autowired
    CategoryService categoryService;


    @GetMapping("add")
    public String add(Model model){
        model.addAttribute("category", new CategoryDto());
        return "admin/categories/addOrEdit";
    }

    @GetMapping("edit/{categoryId}")
    public ModelAndView edit(ModelMap modelMap, @PathVariable("categoryId") Long categoryId){
        Optional<Category> opt = categoryService.findById(categoryId);
        CategoryDto dto = new CategoryDto();
        if (opt.isPresent()){
            Category entity = opt.get();
            BeanUtils.copyProperties(entity, dto);
            dto.setIsEdit(true);
            modelMap.addAttribute("category", dto);
            return new ModelAndView( "admin/categories/addOrEdit", modelMap);
        }
        modelMap.addAttribute("message", "Category is not existed!");
        return new ModelAndView("forward:/admin/categories", modelMap) ;
    }

    @PostMapping("saveOrUpdate")
    public ModelAndView saveOrUpdate(ModelMap model,
                                     @Valid @ModelAttribute("category") CategoryDto dto, BindingResult result){

        if(result.hasErrors()) {
            return new ModelAndView("admin/categories/addOrEdit");
        }
        Category entity = new Category();
        BeanUtils.copyProperties(dto, entity);
        categoryService.save(entity);

        model.addAttribute("message", "Category is saved");
        return new ModelAndView("forward:/admin/categories", model) ;
    }

    @GetMapping("delete/{categoryId}")
    public ModelAndView delete(ModelMap map, @PathVariable("categoryId") Long categoryId){
        if(categoryService.findById(categoryId).isPresent()){
            categoryService.deleteById(categoryId);
            map.addAttribute("message", "Category is deleted!");
        }
        else {
            map.addAttribute("message", "Category is not exist!");
        }


        return new ModelAndView("forward:/admin/categories", map) ;
    }

    @RequestMapping("")
    public String list(ModelMap modelMap){
        List<Category> list = categoryService.findAll();
        modelMap.addAttribute("categories", list);
        return "admin/categories/list";
    }
    @GetMapping("search")
    public String search(ModelMap modelMap, @RequestParam(name = "name", required = false) String name) {
        List<Category> list = null;
        if(StringUtils.hasText(name)){
            list = categoryService.findByNameContaining(name);
        }
        else {
            list = categoryService.findAll();
        }
        modelMap.addAttribute("categories", list);
        return "admin/categories/search";
    }

    @GetMapping("searchpaginated")
    public String search(ModelMap modelMap,
                         @RequestParam(name = "name", required = false) String name,
                         @RequestParam(name = "page", required = false) Optional<Integer> page,
                         @RequestParam(name = "size", required = false) Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize);

        Page<Category> resultPage = null;

        if(StringUtils.hasText(name)){
            resultPage = categoryService.findByNameContaining(name, pageable);
            modelMap.addAttribute("name", name);
        }
        else {
            resultPage = categoryService.findAll(pageable);
        }

        int totalPages = resultPage.getTotalPages();

        if(totalPages>0) {
            int start = Math.max(1, currentPage-2);
            int end = Math.min(currentPage+2, totalPages);

            if(totalPages>5) {
                if(end == totalPages) start = end -5;
                else if (start==1) end = start +5;

            }
            List<Integer> pageNumbers = IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());
            modelMap.addAttribute("pageNumbers", pageNumbers);
        }

        modelMap.addAttribute("categoryPage", resultPage);
        return "admin/categories/searchpaginated";
    }



}
