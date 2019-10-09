//package xyz.kingsword.course.controller;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
//import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//import xyz.kingsword.course.controller.interceptor.UserInterceptor;
//
//@Configuration
//public class WebConfigurer implements WebMvcConfigurer {
//    private final UserInterceptor userInterceptor;
//    private String[] excludePathPatterns = {"/", "/static/**", "/login", "/logout", "/test"};
//
//    public WebConfigurer(UserInterceptor userInterceptor) {
//        this.userInterceptor = userInterceptor;
//    }
//
//    /**
//     * 这个方法是用来配置静态资源的，比如html，js，css，等等
//     *
//     * @param registry 静态资源路由注册器
//     */
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
//        registry.addResourceHandler("/html/**").addResourceLocations("classpath:/templates/");
//        registry.addResourceHandler("/*.html").addResourceLocations("classpath:/templates/");
//    }
//
//    /**
//     * 添加视图导航
//     *
//     * @param registry 视图注册机
//     */
//    @Override
//    public void addViewControllers(ViewControllerRegistry registry) {
//        registry.addViewController("/").setViewName("login");
//        registry.addViewController("/index").setViewName("index");
////        registry.addViewController("/test").setViewName("role/student.html");
//        registry.addViewController("/calendar/teachingContentInsert").setViewName("teacher/calendarFullInContent");
//        registry.addViewController("/calendar/toFullInCalendarTime").setViewName("teacher/calendarFullInTime");
//        registry.addViewController("/calendar/toUpdateInCalendarTime").setViewName("teacher/updateInCalendarTime");
//        registry.addViewController("/calendar/toCalendarUpdateContent").setViewName("teacher/calendarUpdateContent");
//    }
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(userInterceptor).addPathPatterns("/**").excludePathPatterns(excludePathPatterns);
//    }
//
//}
