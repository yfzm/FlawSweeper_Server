package services.servlet.handler;

import beans.delete.DeleteResponse;
import com.google.gson.Gson;
import persistence.ItemEntity;
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

@WebServlet(name = "DeleteUserServlet", urlPatterns = "/deleteUser")
public class DeleteUserServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter writer = response.getWriter();
        DeleteResponse deleteResponse = new DeleteResponse();
        Gson gson = new Gson();
        String user_id = request.getParameter("id");
        HttpSession session = request.getSession();
        if (user_id == null || session == null || session.getAttribute("adminId") == null) {
            deleteResponse.setStatus(false);
            writer.print(gson.toJson(deleteResponse, DeleteResponse.class));
            return;
        }

//        String user_id = session.getAttribute("userId").toString();
        HibernateUtil.getSessionFactory().getCurrentSession().beginTransaction();
        UserEntity user = HibernateUtil.getSessionFactory().getCurrentSession().get(UserEntity.class, user_id);
        HibernateUtil.getSessionFactory().getCurrentSession().delete(user);
        HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().commit();

        deleteResponse.setStatus(true);
        writer.print(gson.toJson(deleteResponse, DeleteResponse.class));
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
