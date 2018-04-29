package services;

import beans.login.LoginResponse;
import com.google.gson.Gson;
import persistence.UserEntity;
import utils.HibernateUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "LoginServlet", urlPatterns = "/loginAuth")
public class LoginServlet extends HttpServlet {

    public LoginServlet() {
        super();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Gson gson = new Gson();

            PrintWriter out = response.getWriter();
            response.setContentType("application/json");

            String username = request.getParameter("username");
            String password = request.getParameter("password");

            System.out.println(username + " " + password);
            HibernateUtil.getSessionFactory().getCurrentSession().beginTransaction();

            String loginId = getLoginId(username, password);
            LoginResponse loginResponse = new LoginResponse();

            if (loginId != null) {
                request.getSession().setAttribute("userId", loginId);
                loginResponse.setStatus(true);
            } else {
                loginResponse.setStatus(false);
            }

            out.println(gson.toJson(loginResponse, LoginResponse.class));
            out.flush();
            out.close();

            HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().commit();
        } catch (Exception ex) {
            if (ServletException.class.isInstance(ex)) {
                throw (ServletException) ex;
            } else {
                throw new ServletException(ex);
            }
        }
    }

    private String getLoginId(String username, String password) {
        String hql = "FROM UserEntity WHERE username = ? AND password = ? AND type = 0";
        List<UserEntity> users = HibernateUtil.getSessionFactory().getCurrentSession().createQuery(hql)
                .setParameter(0, username)
                .setParameter(1, password)
                .list();
        if (users != null && users.size() == 1) {
            return users.get(0).getUserId();
        } else {
            return null;
        }
//        return username.equals("yfzm") && password.equals("123456");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HibernateUtil.getSessionFactory().getCurrentSession().beginTransaction();

        PrintWriter writer = response.getWriter();

        List<UserEntity> users = HibernateUtil.getSessionFactory().getCurrentSession().createQuery("from UserEntity ").list();

        for (UserEntity user : users) {
            writer.println(user.getUserId() + "\t" + user.getUsername() + "\t" + user.getPassword() + "\t" + user.getType());
//            writer.println();
        }

        writer.flush();
        writer.close();

        HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().commit();

    }
}
