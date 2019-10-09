package xyz.kingsword.course.dao;

import org.apache.ibatis.annotations.Param;
import xyz.kingsword.course.pojo.Researchroom;

import java.util.List;

public interface ResearchroomMapper {
    int insert(Researchroom record);

    int deleteById(String id);

    int removeTeacher(@Param("teaId")String teaId);

    int updateById(Researchroom researchroom);

    Researchroom findResearchroomById(Integer id);

    List<Researchroom> findByName(String name);

    List<Researchroom> selectAll();

    List<Researchroom> getAllResearchroom();

    int addTeacher(@Param("teaIds")List<String> teaIds,@Param("researchroomId") String id);

}