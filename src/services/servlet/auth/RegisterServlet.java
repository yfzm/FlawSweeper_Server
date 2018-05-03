package services.servlet.auth;

import beans.register.RegisterRequest;
import beans.register.RegisterResponse;
import com.google.gson.Gson;
import persistence.UserEntity;
import utils.HibernateUtil;
import utils.InfoAPI;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;

@WebServlet(name = "RegisterServlet", urlPatterns = "/doRegister")
public class RegisterServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();

        String JSONString = request.getParameter("json");
        JSONString = URLDecoder.decode(JSONString, "utf-8");
        if (JSONString == null) {
            System.out.println("No json data!!");
            return;
        }

        HibernateUtil.getSessionFactory().getCurrentSession().beginTransaction();
        registerAndReturn(writer, JSONString);
        HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().commit();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    private void registerAndReturn(PrintWriter writer, String JSONString) {
        Gson gson = new Gson();
        System.out.println(JSONString);
        RegisterRequest registerRequest = gson.fromJson(JSONString, RegisterRequest.class);
        RegisterResponse response = new RegisterResponse();

        int errCode;
        if ((errCode = checkArgsAndReturnError(registerRequest)) != 0) {
            response.setStatus(false);
            response.setErrCode(errCode);
            switch (errCode) {
                case 1:
                    response.setErrMsg("用户名已存在");
                    break;
                case 2:
                    response.setErrMsg("该段IP被限制访问");
                    break;
                default:
                    response.setErrMsg("未知错误");
                    break;
            }
        }

        UserEntity user = new UserEntity();
        String user_id = InfoAPI.getRandomId();
        user.setUserId(user_id);
        user.setUsername(registerRequest.getUsername());
        user.setPassword(registerRequest.getPassword());
        user.setEmail(registerRequest.getEmail());
        user.setPhone(registerRequest.getPhone());
        user.setStatus((byte) 1);
        user.setType(0);

        HibernateUtil.getSessionFactory().getCurrentSession().save(user);

        response.setStatus(true);
        response.setUserId(user_id);
        writer.print(gson.toJson(response, RegisterResponse.class));

    }

    private int checkArgsAndReturnError(RegisterRequest registerRequest) {
        return 0;
    }

}
