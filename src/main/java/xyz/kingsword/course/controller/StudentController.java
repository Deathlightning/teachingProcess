package xyz.kingsword.course.controller;

import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.kingsword.course.pojo.Student;
import xyz.kingsword.course.service.StudentService;

@Controller
public class StudentController {

    @Autowired
    private StudentService studentService;


    @RequestMapping("studentInfo")
    public String studentInfo(@RequestParam(defaultValue = "1") Integer pageNumber,
                              @RequestParam(defaultValue = "15") Integer pageSize, Model model) {
        PageInfo<Student> pageInfo = studentService.getAllStudent(pageNumber, pageSize);
        model.addAttribute("pageInfo", pageInfo);
        System.out.println(pageInfo);
        return "admin/student";
    }

    @GetMapping("findStudentByName")
    public String findStudentByName(@RequestParam(defaultValue = "") String name,
                                    @RequestParam(defaultValue = "1") Integer pageNumber,
                                    @RequestParam(defaultValue = "15") Integer pageSize, Model model) {
        PageInfo<Student> pageInfo = studentService.findByName(name, pageNumber, pageSize);
        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("keyword", name);
        return "admin/student";
    }

    @PostMapping("addStudent")
    public String addStudent(Student student) {
        studentService.addStudent(student);
        return "redirect:studentInfo";
    }

    @PostMapping("updateStudent")
    public String updateStudent(Student student) {
        studentService.updateById(student);
        return "redirect:studentInfo";
    }

    @GetMapping("deleteStudentById")
    public String deleteStudentById(@RequestParam String id) {
        studentService.deleteById(id);
        return "redirect:studentInfo";
    }

}
