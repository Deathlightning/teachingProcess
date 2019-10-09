package xyz.kingsword.course.controller;

import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.kingsword.course.pojo.Classes;

@Controller
public class ClassesController {

    @Autowired
    private xyz.kingsword.course.service.ClassesService ClassesService;


    @RequestMapping("classesInfo")
    public String ClassesInfo(@RequestParam(defaultValue = "1") Integer pageNumber,
                              @RequestParam(defaultValue = "15") Integer pageSize, Model model) {
        PageInfo<Classes> pageInfo = ClassesService.getAllClasses(pageNumber, pageSize);
        model.addAttribute("pageInfo", pageInfo);
        System.out.println(pageInfo);
        return "admin/classes";
    }

    @GetMapping("findClassesByName")
    public String findStudentByName(@RequestParam(defaultValue = "") String name,
                                    @RequestParam(defaultValue = "1") Integer pageNumber,
                                    @RequestParam(defaultValue = "15") Integer pageSize, Model model) {
        PageInfo<Classes> pageInfo = ClassesService.findByName(name, pageNumber, pageSize);
        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("keyword", name);
        return "admin/classes";
    }
    @PostMapping("addClasses")
    public String addClasses(Classes Classes) {
        ClassesService.addClasses(Classes);
        return "redirect:classesInfo";
    }

    @PostMapping("updateClasses")
    public String updateStudent(Classes Classes) {
        ClassesService.updateById(Classes);
        return "redirect:classesInfo";
    }

    @GetMapping("deleteClassesById")
    public String deleteClassesById(@RequestParam Integer id) {
        ClassesService.deleteById(id);
        return "redirect:classesInfo";
    }
}
