package xyz.kingsword.course.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Validator;
import cn.hutool.poi.excel.ExcelReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.stereotype.Service;
import xyz.kingsword.course.pojo.TrainingProgram;
import xyz.kingsword.course.service.ExcelService;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 执行计划读取excel
 */
@Slf4j
@Service("TrainingProgramImport")
public class TrainingProgramImport implements ExcelService<TrainingProgram> {
    /**
     * @param inputStream excel输入流
     */
    @Override
    public List<TrainingProgram> excelImport(InputStream inputStream) {
        ExcelReader excelReader = new ExcelReader(inputStream, 0, true);
        Sheet sheet = excelReader.getSheet();
//        重置合并单元格并读取数据
        resetMergeValue(sheet);
        List<List<Object>> lists = excelReader.read();
        List<TrainingProgram> trainingProgramList = new ArrayList<>(lists.size());
        for (int i = 2; i < lists.size(); i++) {
            TrainingProgram trainingProgram = trainingProgramWrapper(lists.get(i).toArray());
            log.debug("import trainingProgram {}", trainingProgram.toString());
            trainingProgramList.add(trainingProgram);
        }
        return trainingProgramList;
    }


    private TrainingProgram trainingProgramWrapper(Object[] objects) {
        System.out.println(Arrays.toString(objects));
        TrainingProgram trainingProgram = new TrainingProgram();
        trainingProgram.setCourseId(objects[2].toString());
        trainingProgram.setCourseName(objects[3].toString());
        trainingProgram.setCredit(Convert.toFloat(objects[4]));
        trainingProgram.setCore(objects[5].toString().contains("△"));
//        对本单元格匹配中文、数字、英文，包含即为院考
        boolean flag = objects[6] != null && Validator.GENERAL_WITH_CHINESE.matcher(objects[6].toString()).find();
        trainingProgram.setCollegesOrDepartments(flag ? "院考" : "系考");
        trainingProgram.setTimeTheory(Convert.toFloat(objects[8]));
        trainingProgram.setTimeLab(Convert.toFloat(objects[9]));
        trainingProgram.setTimePractical(Convert.toFloat(objects[10]));
        trainingProgram.setTimeComputer(Convert.toFloat(objects[11]));
        trainingProgram.setTimeAll(trainingProgram.getTimeTheory() + trainingProgram.getTimeLab() + trainingProgram.getTimePractical() + trainingProgram.getTimeComputer());
        trainingProgram.setStartSemester(Convert.toInt(objects[12]));
        trainingProgram.setGrade(LocalDate.now().getYear());
        return trainingProgram;
    }

    /**
     * 取合并单元格的值并给每个被合并的cell赋值
     *
     * @param sheet 工作簿
     */
    private void resetMergeValue(Sheet sheet) {
        List<CellRangeAddress> range = sheet.getMergedRegions();
        for (CellRangeAddress cellRangeAddress : range) {
            int rowIndex = cellRangeAddress.getFirstRow();
            int cellIndex = cellRangeAddress.getFirstColumn();
            String value = sheet.getRow(rowIndex).getCell(cellIndex).getStringCellValue();
            for (int j = cellRangeAddress.getFirstRow(); j <= cellRangeAddress.getLastRow(); j++) {
                for (int k = cellRangeAddress.getFirstColumn(); k <= cellRangeAddress.getLastColumn(); k++) {
                    Cell cell = sheet.getRow(j).getCell(k);
                    cell.setCellValue(value);
                }
            }
        }
    }
}
