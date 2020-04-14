package com.dsy.blog.controller.admin;

import com.dsy.blog.po.Tag;
import com.dsy.blog.service.TagService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Created on 2020/4/5
 * Package com.dsy.blog.controller.admin
 *
 * @author dsy
 */
@Controller
@RequestMapping(value = "/admin")
public class TagController {

    @Autowired
    private TagService tagService;

    @GetMapping(value = "tags")
    public String tags(@RequestParam(required = false,defaultValue = "1") String page,
                       Model model){
        PageHelper.startPage(Integer.parseInt(page),10);
        List<Tag> tags = tagService.getTagByPage();
        PageInfo<Tag> pageInfo = new PageInfo<Tag>(tags);
        model.addAttribute("pageInfo",pageInfo);
        return "admin/tags";
    }

    /**
     * 跳转新增页面
     * @return
     */
    @GetMapping(value = "tags/input")
    public String input(Model model){
        model.addAttribute("tag",new Tag());
        return "admin/tags-input";
    }

    /**
     * 跳转编辑tag页面
     * @param id
     * @param model
     * @return
     */
    @GetMapping(value = "tags/{id}/input")
    public String editPage(@PathVariable String id,Model model){
        Tag tag = tagService.getTagById(Integer.valueOf(id));
        if (tag!=null){
            model.addAttribute("tag",tag);
            return "admin/tags-input";
        }
        return "redirect:/admin/tags";
    }

    /**
     * 添加标签
     * @param tag
     * @param attributes
     * @param result
     * @return
     */
    @PostMapping(value = "tags/input")
    public String addTag(Tag tag, RedirectAttributes attributes, BindingResult result){
        Tag t = tagService.getTagByTagName(tag.getName());
        if (t!=null){
            result.rejectValue("name","nameError","标签名称不能重复");
            return "admin/tags-input";
        }
        Tag saveTag = tagService.saveTag(tag);
        if (saveTag==null){
            attributes.addFlashAttribute("message","添加失败");
        }else {
            attributes.addFlashAttribute("message","添加成功");
        }
        return "redirect:/admin/tags";
    }

    /**
     * 修改tag
     * @param tag
     * @param attributes
     * @param result
     * @return
     */
    @PostMapping(value = "tags/edit")
    public String editTag(Tag tag,RedirectAttributes attributes, BindingResult result){
        Tag t = tagService.getTagByTagName(tag.getName());
        if (t!=null&& !t.getName().equals(tag.getName())){
            result.rejectValue("name","nameError","标签名称不能重复");
            return "admin/tags-input";
        }
        Tag updateTag = tagService.updateTag(tag);
        if (updateTag==null){
            attributes.addFlashAttribute("message","修改失败");
        }else {
            attributes.addFlashAttribute("message","修改成功");
        }
        return "redirect:/admin/tags";
    }

    /**
     * 删除tag
     * @param id
     * @param attributes
     * @return
     */
    @GetMapping(value = "/tags/{id}/delete")
    public String deleteTag(@PathVariable String id,RedirectAttributes attributes){
        int i = tagService.deleteTagById(Integer.valueOf(id));
        if (i==1){
            attributes.addFlashAttribute("message","删除成功");
        }else{
            attributes.addFlashAttribute("message","删除失败,该标签不可删除");
        }
        return "redirect:/admin/tags";
    }

}
