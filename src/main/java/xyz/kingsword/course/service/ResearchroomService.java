package xyz.kingsword.course.service;

import com.github.pagehelper.PageInfo;
import xyz.kingsword.course.pojo.Researchroom;

import java.util.List;

public interface ResearchroomService {

    Researchroom findResearchroomById(Integer id);

    int addResearchroom(Researchroom researchroom);

    int removeTeacher(String teaId);

    int deleteResearchroomById(String id);

    int updateResearchroom(Researchroom researchroom);

    PageInfo<Researchroom> getAllResearchroom(Integer pageNumber, Integer pageSize);

    PageInfo<Researchroom> findResearchroomByName(String name,Integer pageNumber,Integer pageSize);

    List<Researchroom> getAllResearchroom();

    int addTeachers(List<String> teaIds,String researchroomId);
}
