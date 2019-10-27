package xyz.kingsword.course.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.kingsword.course.dao.ResearchRoomMapper;
import xyz.kingsword.course.pojo.ResearchRoom;
import xyz.kingsword.course.service.ResearchRoomService;

import java.util.List;

@Service
public class ResearchRoomServiceImpl implements ResearchRoomService {

    @Autowired
    private ResearchRoomMapper researchroomMapper;


    @Override
    public void insert(ResearchRoom researchRoom) {
        researchroomMapper.insert(researchRoom);
    }

    @Override
    public void delete(String name) {
        researchroomMapper.delete(name);
    }

    @Override
    public void update(ResearchRoom researchRoom) {
        researchroomMapper.update(researchRoom);
    }

    @Override
    public List<ResearchRoom> select() {
        return researchroomMapper.select();
    }
}
