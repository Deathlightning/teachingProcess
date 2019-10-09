package xyz.kingsword.course.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.kingsword.course.dao.ResearchroomMapper;
import xyz.kingsword.course.pojo.Researchroom;
import xyz.kingsword.course.service.ResearchroomService;

import java.util.List;

@Service
public class ResearchroomServiceImpl implements ResearchroomService {

    @Autowired
    private ResearchroomMapper researchroomMapper;

    @Override
    public Researchroom findResearchroomById(Integer id) {
        return researchroomMapper.findResearchroomById(id);
    }

    @Override
    public int addResearchroom(Researchroom researchroom) {
        return researchroomMapper.insert(researchroom);
    }

    @Override
    public int removeTeacher(String teaId) {
        return researchroomMapper.removeTeacher(teaId);
    }

    @Override
    public int deleteResearchroomById(String id) {
        return researchroomMapper.deleteById(id);
    }

    @Override
    public int updateResearchroom(Researchroom researchroom) {
        return researchroomMapper.updateById(researchroom);
    }

    @Override
    public PageInfo<Researchroom> getAllResearchroom(Integer pageNumber,Integer pageSize) {
        return PageHelper.startPage(pageNumber,pageSize)
                .doSelectPageInfo(()->researchroomMapper.selectAll());
    }

    @Override
    public PageInfo<Researchroom> findResearchroomByName(String name,  Integer pageNumber,Integer pageSize) {
        return PageHelper.startPage(pageNumber,pageSize).doSelectPageInfo(()->researchroomMapper.findByName(name));
    }

    @Override
    public List<Researchroom> getAllResearchroom() {
        return researchroomMapper.getAllResearchroom();
    }

    @Override
    public int addTeachers(List<String> teaIds, String researchroomId) {
        return researchroomMapper.addTeacher(teaIds,researchroomId);
    }
}
