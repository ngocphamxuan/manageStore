package com.example.version4.controller.admin;



import com.example.version4.domain.Category;
import com.example.version4.domain.Product;
import com.example.version4.model.CategoryDto;
import com.example.version4.model.ProductDto;
import com.example.version4.service.CategoryService;
import com.example.version4.service.ProductService;
import com.example.version4.service.StorageService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("admin/products")
public class ProductController {

    @Autowired
    ProductService productService;

    @Autowired
    StorageService storageService;

    @Autowired
    CategoryService categoryService;

    //Get List category
    @ModelAttribute("categories")
    public List<CategoryDto> getCategories(){
        //get all categories and trans to string list, then map each element using lamda function
        return categoryService.findAll().stream().map(item->{
            CategoryDto dto = new CategoryDto();
            BeanUtils.copyProperties(item, dto);
            return dto;
        }).collect(Collectors.toList());
    }

    //Get page add product
    @GetMapping("add")
    public String add(Model model){
        ProductDto dto =  new ProductDto();
        dto.setIsEdit(false);
        model.addAttribute("product",dto);
        return "admin/products/addOrEdit";
    }

    //Get page edit product
    @GetMapping("edit/{productId}")
    public ModelAndView edit(ModelMap modelMap, @PathVariable("productId") Long productId){
        Optional<Product> opt = productService.findById(productId);
        ProductDto dto = new ProductDto();
        if (opt.isPresent()){
            Product entity = opt.get();
            BeanUtils.copyProperties(entity, dto);
            dto.setCategoryId(entity.getCategory().getCategoryId());

            dto.setIsEdit(true);
            modelMap.addAttribute("product", dto);
            return new ModelAndView( "/admin/products/addOrEdit", modelMap);
        }
        modelMap.addAttribute("message", "Product is not existed!");
        return new ModelAndView("forward:/admin/products", modelMap) ;
    }

    @PostMapping("saveOrUpdate")
    public ModelAndView saveOrUpdate(ModelMap model,
                                     @Valid @ModelAttribute("Product") ProductDto dto, BindingResult result){

        if(result.hasErrors()) {
            return new ModelAndView("admin/products/addOrEdit");
        }

        Product entity = new Product();
        BeanUtils.copyProperties(dto, entity);
        Category category = new Category();
        category.setCategoryId(dto.getCategoryId());
        entity.setCategory(category);

        if (!dto.getImageFile().isEmpty()){
            UUID uuid = UUID.randomUUID();
            String uuidString = uuid.toString();
            entity.setImage(storageService.getStoragedFilename(dto.getImageFile(),uuidString));
            storageService.store(dto.getImageFile(), entity.getImage());
        }

        Date date = new Date();
        entity.setEnteredDate(date);

        productService.save(entity);
        model.addAttribute("message", "Product is saved!");

        return new ModelAndView("forward:/admin/products", model) ;
    }

    @GetMapping("/images/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename){
        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @GetMapping("delete/{productId}")
    public ModelAndView delete(ModelMap map, @PathVariable("productId") Long productId) throws IOException {
        Optional<Product> opt = productService.findById(productId);
        if(opt.isPresent()){


            if(!StringUtils.isEmpty(opt.get().getImage()))
            {
                storageService.delete(opt.get().getImage());
            }
            productService.delete(opt.get());
            map.addAttribute("message", "Product is deleted!");
        }
        else {
            map.addAttribute("message", "Product is not exist!");
        }


        return new ModelAndView("forward:/admin/products", map) ;
    }

    @RequestMapping("")
    public String list(ModelMap modelMap){
        List<Product> list = productService.findAll();
        modelMap.addAttribute("products", list);
        return "admin/products/list";
    }

//    @GetMapping("search")
//    public String search(ModelMap modelMap, @RequestParam(name = "name", required = false) String name) {
//        List<Product> list = null;
//        if(StringUtils.hasText(name)){
//            //list = productService.findByNameContaining(name);
//        }
//        else {
//            list = productService.findAll();
//        }
//        modelMap.addAttribute("categories", list);
//        return "admin/products/search";
//    }

//    @GetMapping("searchpaginated")
//    public String search(ModelMap modelMap,
//                         @RequestParam(name = "name", required = false) String name,
//                         @RequestParam(name = "page", required = false) Optional<Integer> page,
//                         @RequestParam(name = "size", required = false) Optional<Integer> size) {
//        int currentPage = page.orElse(1);
//        int pageSize = size.orElse(5);
//        Pageable pageable = PageRequest.of(currentPage - 1, pageSize);
//
//        Page<Product> resultPage = null;
//
//        if(StringUtils.hasText(name)){
//            //resultPage = productService.findByNameContaining(name, pageable);
//            modelMap.addAttribute("name", name);
//        }
//        else {
//            resultPage = productService.findAll(pageable);
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
//        return "admin/categories/searchpaginated";
//    }



}
