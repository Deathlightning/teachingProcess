//package xyz.kingsword.course.controller.interceptor;
//
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.HandlerInterceptor;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//import java.io.IOException;
//
//@Component
//public class UserInterceptor implements HandlerInterceptor {
//    /**
//     * 要求登录后才能进行操作
//     *
//     * @throws IOException IOException
//     */
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
//        HttpSession session = request.getSession();
////        测试用的
//        String header = request.getHeader("test");
//        if ("6f2c54d6".equals(header)) {
//            System.out.print("测试");
//            return true;
//        }
////        未登录访问页面时先检测cookie，存在就进行自动登录
//        if (session.getAttribute("user") == null) {
////            Cookie cookie = ServletUtil.getCookie(request, "autoLogin");
////            if (cookie == null) {
////                response.sendRedirect(request.getContextPath() + "/");
////                return false;
////            } else {
////                HttpRequest httpRequest = HttpUtil.createPost(request.getContextPath() + "/login").body("username=" + cookie.getValue());
////                httpRequest.execute();
////            }
//            response.sendRedirect(request.getContextPath() + "/");
//            return false;
//        }
//        return true;
//    }
//}
