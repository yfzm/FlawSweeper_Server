package services.servlet.info;

import beans.user.UserInfo;
import beans.user.UserResponse;
import com.google.gson.Gson;
import persistence.UserEntity;
import utils.HibernateUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "UserInfoServlet", urlPatterns = "/getUsers")
public class UserInfoServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();
        String admin_id;

        HttpSession session = request.getSession();
        if (session == null || session.getAttribute("adminId") == null) {
            writer.print("{\"status\":false}");
            return;
        }

        admin_id = session.getAttribute("adminId").toString();

        HibernateUtil.getSessionFactory().getCurrentSession().beginTransaction();
        queryAndReturnUserList(writer);
        HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().commit();

    }

    @SuppressWarnings("unchecked")
    private void queryAndReturnUserList(PrintWriter writer) {
        Gson gson = new Gson();
        UserResponse response = new UserResponse();

        String hql= "from UserEntity where type = 0";

        List<UserEntity> users = HibernateUtil.getSessionFactory().getCurrentSession().createQuery(hql).list();

        response.setStatus(true);
        if (users == null) {
            response.setNum(0);
            writer.print(gson.toJson(response, UserResponse.class));
            return;
        }

        response.setNum(users.size());
        ArrayList<UserInfo> listUserInfoList = new ArrayList<>();
        for (UserEntity user: users) {
            UserInfo userInfo = new UserInfo();
            userInfo.setId(user.getUserId());
            userInfo.setUsername(user.getUsername());
            userInfo.setValid(user.getStatus() == 1);
            userInfo.setNum(user.getItems().size());
            userInfo.setPhone(user.getPhone());
            userInfo.setEmail(user.getEmail());
            listUserInfoList.add(userInfo);
        }
        response.setUsers(listUserInfoList);

        writer.print(gson.toJson(response, UserResponse.class));
    }
}
