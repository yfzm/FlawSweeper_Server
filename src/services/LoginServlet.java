package services;

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

@WebServlet(name = "LoginServlet",  urlPatterns = "/loginInfo")
public class LoginServlet extends HttpServlet {

    public LoginServlet() {
        super();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HibernateUtil.getSessionFactory().getCurrentSession().beginTransaction();

        PrintWriter writer = response.getWriter();

        List<UserEntity> users = HibernateUtil.getSessionFactory().getCurrentSession().createQuery("from UserEntity ").list();

        for (UserEntity user : users) {
            writer.println(user.getUserId() + "\t" + user.getUsername() + "\t" + user.getPassword() + "\t" + user.getType());

        }

        writer.flush();
        writer.close();

        HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().commit();

    }
}
