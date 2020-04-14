package com.dsy.blog.controller.admin;

import com.dsy.blog.po.Type;
import com.dsy.blog.service.TypeService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Created on 2020/4/4
 * Package com.dsy.blog.controller.admin
 *
 * @author dsy
 */
@Controller
@RequestMapping(value = "/admin")
public class TypeController {

    @Autowired
    private TypeService typeService;

    /**
     * 分页查询types
     * @param model  视图渲染
     * @param page 页码
     * @return
     */
    @GetMapping("types")
    public String findAllTypes(Model model,
                               @RequestParam(required = false,defaultValue = "1") String page){
        PageHelper.startPage(Integer.parseInt(page),10);
        PageInfo<Type> pageInfo = new PageInfo<>(typeService.listType());
        model.addAttribute("pageInfo",pageInfo);
        return "admin/types";
    }

    /**
     * 跳转到新增type页面
     * @return
     */
    @GetMapping(value = "types/input")
    public String input(Model model){
        model.addAttribute("type",new Type());
        return "admin/types-input";
    }


    /**
     * 跳转编辑type页面
     * @param id
     * @return
     */
    @GetMapping(value = "types/{id}/input")
    public String editPage(@PathVariable String id,Model model){
        Type type = typeService.getType(Integer.valueOf(id));
        model.addAttribute("type",type);
        return "admin/types-input";
    }



    /**
     * 新增type
     * @param type
     * @return
     */
    @PostMapping("types/input")
    public String post(Type type, BindingResult result, RedirectAttributes attributes){
        Type type1 = typeService.getTypeByName(type.getName());
        if (type1 != null) {
            result.rejectValue("name", "nameError", "不能添加重复的分类名称");
        }
        if (result.hasErrors()) {
            return "/admin/types-input";
        }
        Type type2 = typeService.saveType(type);
        if (type2 == null) {
            attributes.addFlashAttribute("message", "操作失败");
        } else {
            attributes.addFlashAttribute("message", "操作成功");
        }
        return "redirect:/admin/types";
    }

    /**
     * 修改type
     * @param type
     * @param result
     * @param attributes
     * @return
     */
    @PostMapping(value = "types/edit")
    public String edit(Type type,BindingResult result, RedirectAttributes attributes){
        Type t = typeService.getTypeByName(type.getName());
        if (t!=null){
            result.rejectValue("name","nameError","不能添加重复的分类名称");
        }
        if (result.hasErrors()){
            return "/admin/types-input";
        }
        Type type1 = typeService.updateType(type);
        if (type1!=null){
            attributes.addAttribute("message","操作成功");
        }else {
            attributes.addFlashAttribute("message","操作失败");
        }
        return "redirect:/admin/types";
    }


    /**
     * 删除type
     * @param id  type的id
     * @param attributes  重定向后返回操作信息（成功，失败）
     * @return
     */
    @GetMapping(value = "types/{id}/delete")
    public String deleteType(@PathVariable String id,RedirectAttributes attributes){
        int i = typeService.deleteType(Integer.valueOf(id));
        if (i==1){
            attributes.addFlashAttribute("message","删除成功");
        }else {
            attributes.addFlashAttribute("message","删除失败,该类别不可删除");
        }
        return "redirect:/admin/types";
    }

}
