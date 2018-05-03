package services.servlet.handler;

import beans.list.ListResponse;
import beans.modify.ModifyRequest;
import beans.modify.ModifyResponse;
import com.google.gson.Gson;
import persistence.ItemEntity;
import persistence.TagEntity;
import utils.HibernateUtil;
import utils.InfoAPI;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sound.sampled.Line;
import javax.swing.text.html.parser.Entity;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@WebServlet(name = "ModifyItemServlet", urlPatterns = "/editItem")
public class ModifyItemServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();

        String JSONString = request.getParameter("json");
        JSONString = URLDecoder.decode(JSONString, "utf-8");
        if (JSONString == null) {
            System.out.println("No json data!!");
            return;
        }

        HttpSession session = request.getSession();
        if (session == null || session.getAttribute("userId") == null) {
            writer.print("{\"status\":false}");
            return;
        }
        String user_id = session.getAttribute("userId").toString();

        HibernateUtil.getSessionFactory().getCurrentSession().beginTransaction();
        modifyAndReturnResult(writer, JSONString, user_id);
        HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().commit();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    private void modifyAndReturnResult(PrintWriter writer, String JSONString, String user_id) {
        Gson gson = new Gson();
        System.out.println(JSONString);
        ModifyRequest modifyRequest = gson.fromJson(JSONString, ModifyRequest.class);
        ModifyResponse response = new ModifyResponse();
//        System.out.println(JSONString);

        if (modifyRequest.getId() != null) {
            System.out.println(modifyRequest.getId());
        }
        ItemEntity item = InfoAPI.getItemViaPK(modifyRequest.getId());
        if (item == null || modifyRequest.getCreateTime() == 0) {
            response.setStatus(false);
            writer.print(gson.toJson(response, ModifyResponse.class));
            return;
        }
        item.setCreateTime(new Timestamp(modifyRequest.getCreateTime()));
        if (modifyRequest.getTitle() != null) item.setTitle(modifyRequest.getTitle());
        if (modifyRequest.getqText() != null) item.setContent(modifyRequest.getqText());
        if (modifyRequest.getcAnswer() != null) item.setAnswer(modifyRequest.getcAnswer());
        if (modifyRequest.getReason() != null) item.setReason(modifyRequest.getReason());
        item.setEditCount(item.getEditCount() + 1);

        System.out.println(modifyRequest.getqTag());

        if (modifyRequest.getqTag() != null) item.setTags(InfoAPI.getTagEntities(modifyRequest.getqTag()));
//        HibernateUtil.getSessionFactory().getCurrentSession().update(item);
        response.setStatus(true);
        writer.print(gson.toJson(response, ModifyResponse.class));
    }


}
