package xyz.kingsword.course;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableScheduling
@EnableTransactionManagement//启用事务
@MapperScan("xyz.kingsword.course.dao")
@SpringBootApplication
public class CourseApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(CourseApplication.class, args);
    }

}
