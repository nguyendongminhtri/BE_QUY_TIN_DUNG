package com.example.demo.controller;

import com.example.demo.dto.response.ResponMessage;
import com.example.demo.model.CategoryEntity;
import com.example.demo.model.NewsEntity;
import com.example.demo.service.category.ICategoryService;
import com.example.demo.service.news.INewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/category")
@CrossOrigin(origins = "*")
public class CategoryController {
    @Autowired
    private ICategoryService categoryService;
    @Autowired
    private INewsService newsService;
    @GetMapping
    public ResponseEntity<?> showListCategory(){
        return new ResponseEntity<>(categoryService.findAll(), HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> detailCategory(@PathVariable Long id){
        Optional<CategoryEntity> category = categoryService.findById(id);
        if(!category.isPresent()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(category, HttpStatus.OK);
    }
    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody CategoryEntity category){
        if (categoryService.existsByName(category.getName())){
            return  new ResponseEntity<>(new ResponMessage("name_exist"), HttpStatus.OK);
        }
        categoryService.save(category);
        return  new ResponseEntity<>(new ResponMessage("success"), HttpStatus.OK);
    }
    @GetMapping("/page")
    public ResponseEntity<?> pageCategory(@PageableDefault(size = 3) Pageable pageable){
        return new ResponseEntity<>(categoryService.findAll(pageable), HttpStatus.OK);
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @RequestBody CategoryEntity category){
        Optional<CategoryEntity> optionalCategory = categoryService.findById(id);
        if (!optionalCategory.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponMessage("category_not_found"));
        }
        if (categoryService.existsByName(category.getName())){
            return  new ResponseEntity<>(new ResponMessage("name_exist"), HttpStatus.OK);
        }
        CategoryEntity updatedCategory = optionalCategory.get();
        updatedCategory.setName(category.getName());
        updatedCategory.setType(category.getType());
        categoryService.save(updatedCategory);
        return new ResponseEntity<>(new ResponMessage("update_success"), HttpStatus.OK);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id){
        Optional<CategoryEntity> category = categoryService.findById(id);

        if (!category.isPresent()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<NewsEntity> newsEntityList = newsService.findAllByCategoryId(id);
        for (int i = 0; i < newsEntityList.size(); i++) {
            newsService.deleteById(newsEntityList.get(i).getId());
        }
        categoryService.deleteById(id);
        return new ResponseEntity<>(new ResponMessage("delete_success"),HttpStatus.OK);
    }
}