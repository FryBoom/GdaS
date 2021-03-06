package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.Content;
import com.pinyougou.pojo.ContentCategory;
import com.pinyougou.pojo.PageResult;
import com.pinyougou.service.ContentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/content")
public class ContentController {
    @Reference(timeout = 10000)
    private ContentService contentService;

    @RequestMapping("/findByPage")
    public PageResult findByPage(Content content,Integer page , Integer rows){
        try {
            return contentService.findByPage(content,page,rows);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @GetMapping("/findAll")
    public List<ContentCategory> findAll(){
        try {
            return contentService.findContentCategory();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping("/save")
    public boolean save(@RequestBody Content content){
        try {
            contentService.save(content);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @PostMapping("/update")
    public boolean update(@RequestBody Content content){
        try {
            contentService.update(content);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @GetMapping("/delete")
    public boolean delete(Long[] ids){
        try {
            contentService.deleteAll(ids);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
