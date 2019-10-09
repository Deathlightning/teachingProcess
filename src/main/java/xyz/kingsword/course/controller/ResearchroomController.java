package xyz.kingsword.course.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import xyz.kingsword.course.VO.ResultVO;
import xyz.kingsword.course.pojo.Researchroom;
import xyz.kingsword.course.service.ResearchroomService;
import xyz.kingsword.course.util.ResultVOUtil;

import java.util.List;

@Controller
public class ResearchroomController {
    @Autowired
    private ResearchroomService researchroomService;


    @RequestMapping("researchroomInfo")
    public String researchroomInfo(@RequestParam(defaultValue = "1") Integer pageNumber,
                                   @RequestParam(defaultValue = "10") Integer pageSize, Model model) {
        PageInfo<Researchroom> pageInfo = researchroomService.getAllResearchroom(pageNumber, pageSize);
        model.addAttribute("pageInfo", pageInfo);
        System.out.println(pageInfo);
        return "admin/researchroom";
    }

    @RequestMapping("deleteResearchroom")
    public String findResearchRoomByName(@RequestParam String id,
                                         @RequestParam(defaultValue = "1") Integer pageNumber,
                                         @RequestParam(defaultValue = "10") Integer pageSize, Model model) {
        researchroomService.deleteResearchroomById(id);
        return "redirect:researchroomInfo?pageNumber=" + pageNumber + "&pageSize=" + pageSize;
    }

    @PostMapping("addResearchroom")
    public String addResearchRoom(Researchroom researchroom) {
        researchroomService.addResearchroom(researchroom);
        return "redirect:researchroomInfo";
    }

    @PostMapping("updateResearchroom")
    public String updateResearchRoom(Researchroom researchroom) {
        researchroomService.updateResearchroom(researchroom);
        return "redirect:researchroomInfo";
    }

    @GetMapping("findResearchroomByName")
    public String findResearchroom(@RequestParam String name,
                                   @RequestParam(defaultValue = "1") Integer pageNumber,
                                   @RequestParam(defaultValue = "10") Integer pageSize, Model model) {
        PageInfo<Researchroom> pageInfo = researchroomService.findResearchroomByName(name, pageNumber, pageSize);
        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("keyword", name);
        return "admin/researchroom";
    }

    @RequestMapping("findAllTeachersById")
    public String findAllTeachersById(@RequestParam Integer id, Model model) {
        Researchroom researchroom = researchroomService.findResearchroomById(id);
        model.addAttribute("researchroom", researchroom);
        return "admin/researchroom-teachers";
    }

    @PostMapping("addTeachers")
    @ResponseBody
    public ResultVO addTeachers(@RequestBody String data) {
        JSONObject jsonObject = JSONObject.parseObject(data);
        JSONArray array = jsonObject.getJSONArray("teaIds");
        String id = jsonObject.getString("researchroomId");
        List<String> list = array.toJavaList(String.class);
        researchroomService.addTeachers(list, id);
        return ResultVOUtil.success();
    }


    @RequestMapping("removeTeacher")
    @ResponseBody
    public ResultVO removeTeacher(@RequestParam String teaId) {
        researchroomService.removeTeacher(teaId);
        return ResultVOUtil.success();
    }

    @RequestMapping("getAllResearchRoom")
    @ResponseBody
    public ResultVO getAllResearchRoom() {
        List<Researchroom> list = researchroomService.getAllResearchroom();
        return ResultVOUtil.success(list);
    }
}
