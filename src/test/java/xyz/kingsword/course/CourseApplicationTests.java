package xyz.kingsword.course;


import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.resource.ClassPathResource;
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
import xyz.kingsword.course.dao.BookMapper;
import xyz.kingsword.course.pojo.TeachingContent;
import xyz.kingsword.course.service.TrainingProgramService;
import xyz.kingsword.course.service.calendarExport.CalendarData;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class CourseApplicationTests {
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private TrainingProgramService trainingProgramService;

    @Test
    public void contextLoads() {
        Map<Integer, Long> collect = IntStream.of(1, 2, 3, 4).boxed().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
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
