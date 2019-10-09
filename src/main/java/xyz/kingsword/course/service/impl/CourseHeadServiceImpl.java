//package xyz.kingsword.course.service.impl;
//
//import com.github.pagehelper.PageHelper;
//import com.github.pagehelper.PageInfo;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.util.StringUtils;
//import xyz.kingsword.course.dao.SortCourseMapper;
//import xyz.kingsword.course.pojo.Course;
//import xyz.kingsword.course.pojo.SortCourse;
//import xyz.kingsword.course.service.CourseHeadService;
//import xyz.kingsword.course.service.SortCourseService;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//public class CourseHeadServiceImpl implements CourseHeadService {
//
//    @Autowired
//    private SortCourseService sortCourseService;
//
//    @Autowired
//    private SortCourseMapper sortcourseMapper;
//
//    @Override
//    public PageInfo getCourseList(Integer pageNumber, Integer pageSize) {
//        return PageHelper.startPage(pageNumber, pageSize).doSelectPageInfo(() -> sortcourseMapper.selectAll());
//    }
//
//    @Override
//    public PageInfo findCourseList(String name, String yId, Integer type, Integer pageNumber, Integer pageSize) {
//        return PageHelper.startPage(pageNumber, pageSize).doSelectPageInfo(() -> sortcourseMapper.findSortCourseHead(name, yId, type));
//    }
//
////    @Override
////    public boolean checkCourseId(List<Integer> sortCourseIdList) {
////        List<SortCourse> list = sortcourseMapper.getSortListByIdList(sortCourseIdList);
////        //获取所有课程id不同的个数
////        long idCount = list.stream()
////                .map(SortCourse::getCourse)
////                .map(Course::getId)
////                .distinct()
////                .count();
////        return idCount == 1;
////    }
//
//    @Override
//    public int mergeSortCourse(List<Integer> sortCourseIdList) {
//        List<SortCourse> sortCourseList = sortCourseService.getSortListByIdList(sortCourseIdList);
//        long check = sortCourseList.stream()
//                .map(SortCourse::getCourse)
//                .map(Course::getId)
//                .distinct()
//                .count();
//        if (check != 1) {
//            return -1;
//        }
//        SortCourse headSortCourse = sortCourseList.get(1);
//        sortCourseList.remove(1);
//        //计算学生总数
//        long otherStuNum = sortCourseList.stream()
//                .map(SortCourse::getStudentNum)
//                .reduce(Integer::sum).get();
//        headSortCourse.setStudentNum((int) (headSortCourse.getStudentNum() + otherStuNum));
//        //计算合并班级名称
//        String otherName = sortCourseList.stream().map(e -> {
//            StringBuilder className = new StringBuilder(e.getClassName());
//            if (!StringUtils.isEmpty(e.getOtherClassName())) {
//                className.append(" ")
//                        .append(e.getOtherClassName());
//            }
//            e.setOtherClassName(null);
//            e.setStatus(-1);
//            return className.toString();
//        }).collect(Collectors.joining(" "));
//        headSortCourse.setOtherClassName(otherName);
//        sortCourseList.add(headSortCourse);
//        return sortcourseMapper.updateByList(sortCourseList);
//    }
//
//    @Override
//    public int restoreSortCourse(Integer headId) {
//        SortCourse headSortCourse = sortcourseMapper.selectByPrimaryKey(headId);
//        List<String> otherClassNameList = Arrays.asList(headSortCourse.getOtherClassName().split(" "));
//        System.out.println(otherClassNameList);
//        System.out.println(headSortCourse.getCouId());
//        List<SortCourse> otherSrotCourse = sortcourseMapper.findSortCourseByCourseIdAndClassName(headSortCourse.getCouId(), otherClassNameList);
//        otherSrotCourse.forEach(e -> {
//            e.setStatus(0);
//            e.setOtherClassName(null);
//        });
//        System.out.println(otherSrotCourse);
//        long otherStuNum = otherSrotCourse.stream()
//                .map(SortCourse::getStudentNum)
//                .reduce(Integer::sum).get();
//        headSortCourse.setStudentNum((int) (headSortCourse.getStudentNum() - otherStuNum));
//        headSortCourse.setOtherClassName(null);
//        otherSrotCourse.add(headSortCourse);
//        return sortcourseMapper.updateByList(otherSrotCourse);
//    }
//}
