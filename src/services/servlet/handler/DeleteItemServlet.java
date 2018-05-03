package services.servlet.handler;

import beans.delete.DeleteResponse;
import com.google.gson.Gson;
import persistence.ItemEntity;
import utils.HibernateUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "DeleteItemServlet", urlPatterns = "/deleteItem")
public class DeleteItemServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter writer = response.getWriter();
        DeleteResponse deleteResponse = new DeleteResponse();
        Gson gson = new Gson();
        String item_id = request.getParameter("id");
        HttpSession session = request.getSession();
        if (item_id == null || session == null || session.getAttribute("userId") == null) {
            deleteResponse.setStatus(false);
            writer.print(gson.toJson(deleteResponse, DeleteResponse.class));
            return;
        }

//        String user_id = session.getAttribute("userId").toString();
        HibernateUtil.getSessionFactory().getCurrentSession().beginTransaction();
        ItemEntity item = HibernateUtil.getSessionFactory().getCurrentSession().get(ItemEntity.class, item_id);
        HibernateUtil.getSessionFactory().getCurrentSession().delete(item);
        HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().commit();

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
