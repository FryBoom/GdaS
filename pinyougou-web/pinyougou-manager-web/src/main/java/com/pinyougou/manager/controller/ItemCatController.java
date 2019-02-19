package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.ItemCat;
import com.pinyougou.pojo.PageResult;
import com.pinyougou.service.ItemCatService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/itemCat")
public class ItemCatController {
    @Reference(timeout = 10000)
    private ItemCatService itemCatService;

    /*@GetMapping("/findItemCatByParentId")
    public List<ItemCat> findItemCatByParentId(Long parentId){
        return itemCatService.findItemCatByParentId(parentId);
    }*/

    @GetMapping("/findItemCatByParentId")
    public PageResult findItemCatByParentId(Integer page,Integer rows, Long parentId){
        if(parentId == null){
            parentId = Long.valueOf(0);
        }
        PageResult itemCatByParentId = itemCatService.findItemCatByParentId(page, rows, parentId);
        return itemCatByParentId;
    }

    @PostMapping("/save")
    public boolean save(@RequestBody ItemCat itemCat){
         try {
             itemCatService.saveItemCat(itemCat);
             return true;
         }  catch (Exception e){
             e.printStackTrace();
             return false;
         }
    }
    @PostMapping("/update")
    public boolean update(@RequestBody ItemCat itemCat){
        try{
            itemCatService.updateItemCat(itemCat);
            return true;
        }catch(Exception ex){
            ex.printStackTrace();
            return false;
        }
    }

    @GetMapping("/delete")
    public boolean delete(Long[] ids){
        try {
            itemCatService.deleteItemCat(ids);
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
