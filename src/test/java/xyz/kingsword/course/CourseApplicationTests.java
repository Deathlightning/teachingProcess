package xyz.kingsword.course;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.lang.Console;
import cn.hutool.core.text.StrBuilder;
import com.alibaba.fastjson.JSON;
import com.deepoove.poi.data.RowRenderData;
import com.deepoove.poi.data.TextRenderData;
import com.deepoove.poi.data.style.Style;
import com.deepoove.poi.data.style.TableStyle;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STJc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ResourceUtils;
import xyz.kingsword.course.dao.SortCourseMapper;
import xyz.kingsword.course.pojo.SortCourse;
import xyz.kingsword.course.pojo.TeachingContent;
import xyz.kingsword.course.pojo.param.sortCourse.SearchParam;
import xyz.kingsword.course.service.calendarExport.CalendarData;
import xyz.kingsword.course.service.impl.ExecuteServiceImpl;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.RandomAccess;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class CourseApplicationTests {

    @Autowired
    private SortCourseMapper sortCourseMapper;
    @Autowired
    private ExecuteServiceImpl executeService;

    @Test
    public void contextLoads() throws IOException {
//        List<String> random = new ArrayList<>();
//        for (int i = 0; i < 1000000; i++) {
//            random.add(UUID.randomUUID().toString());
//        }
//        for (int i = 0; i < 1000; i++) {
//            TimeInterval timer = DateUtil.timer();
//            List<String> list = new ArrayList<>();
////            random.stream().filter(v -> v.contains("hs")).collect(Collectors.toList());
//            for (String s : random) {
//                if (s.contains("hs")) {
//                    list.add(s);
//                }
//            }
//            Console.log(timer.interval());
//        }

//        timer = DateUtil.timer();
//        collect.stream().filter(v -> v / 2 == 0).collect(Collectors.toList());
//        Console.log(timer.interval());
    }

    @Test
    public void exportWord() throws IOException {
//        XWPFTemplate template = calendarService.export(5);
//        template.writeToFile("C:\\Users\\wang1\\Desktop\\result.docx");
    }


    public CalendarData getData() {
        CalendarData calendarData = new CalendarData();
        calendarData.setCollege("软件学院");
        calendarData.setTableData(getTeachingContent());
        return calendarData;
    }

    //构建表格体
    public List<RowRenderData> getTeachingContent() {
        ClassPathResource jsonData = new ClassPathResource("json/calendar.json");
        FileReader fileReader = new FileReader(jsonData.getFile());
        Style cellStyle = new Style("宋体", 11);
        TableStyle tableStyle = new TableStyle();
        tableStyle.setAlign(STJc.CENTER);
        DateFormat simpleDateFormat = new SimpleDateFormat("MM dd");
        List<TeachingContent> teachingContentList = JSON.parseArray(fileReader.readString(), TeachingContent.class);
        List<RowRenderData> rowRenderDataList = new ArrayList<>(teachingContentList.size());
        for (TeachingContent teachingContent : teachingContentList) {
            TextRenderData t1 = new TextRenderData(simpleDateFormat.format(teachingContent.getDate()), cellStyle);
            TextRenderData t2 = new TextRenderData(String.valueOf(teachingContent.getWeekNum()), cellStyle);
            TextRenderData t3 = new TextRenderData(String.valueOf(teachingContent.getIndex()), cellStyle);
            TextRenderData t4 = new TextRenderData(teachingContent.getTeachingContent(), cellStyle);
            TextRenderData t5 = new TextRenderData(String.valueOf(teachingContent.getStudyTime()), cellStyle);
            TextRenderData t6 = new TextRenderData(teachingContent.getHomeworkOrLab(), cellStyle);
            RowRenderData rowRenderData = RowRenderData.build(t1, t2, t3, t4, t5, t6);
            rowRenderData.setRowStyle(tableStyle);
            rowRenderDataList.add(rowRenderData);
        }

        return rowRenderDataList;
    }

    @Test
    public void excelImportTest() throws Exception {
//        String filePath = "E:\\教学任务管理平台\\2015-软件工程-培养方案.xlsx";
//        InputStream inputStream = new FileInputStream(filePath);
//        List<TrainingProgram> trainingProgramList = trainingProgramService.excelImport(inputStream);
//        trainingProgramList.stream().filter(v -> v.getCollegesOrDepartments().equals("院考")).forEach(System.out::println);
    }
}
