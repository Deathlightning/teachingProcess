package xyz.kingsword.course;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.ResourceUtils;

import java.io.File;

@EnableTransactionManagement//启用事务
@MapperScan("xyz.kingsword.course.dao")
@SpringBootApplication
public class CourseApplication {

    public static void main(String[] args) throws Exception{
        SpringApplication.run(CourseApplication.class, args);
    }

}
