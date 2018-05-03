package services.servlet.info;

import persistence.TagEntity;
import utils.HibernateUtil;
import utils.InfoAPI;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "TagInfoServlet", urlPatterns = "/getAllTags")
public class TagInfoServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();

        // TODO: uncomment codes below
//        HttpSession session = request.getSession();
//        if (session == null || session.getAttribute("userId") == null) {
//            writer.print("{\"status\":false}");
//            return;
//        }

        HibernateUtil.getSessionFactory().getCurrentSession().beginTransaction();
        List<TagEntity> tags = InfoAPI.getAllTags();
        HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().commit();

        for (TagEntity tag: tags) {
            writer.print(tag.getTagContent() + " ");
        }
    }
}
