package services.servlet.handler;

import beans.modify.ModifyRequest;
import beans.modify.ModifyResponse;
import beans.redo.RedoRequest;
import beans.redo.RedoResponse;
import com.google.gson.Gson;
import persistence.ItemEntity;
import persistence.RedoEntity;
import persistence.UserEntity;
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
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Random;

@WebServlet(name = "RedoServlet", urlPatterns = "/redo")
public class RedoServlet extends HttpServlet {
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
        addRedoAndReturnResult(writer, JSONString, user_id);
        HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().commit();

    }

    private void addRedoAndReturnResult(PrintWriter writer, String JSONString, String user_id) {
        Gson gson = new Gson();
//        System.out.println(JSONString);
        RedoRequest redoRequest = gson.fromJson(JSONString, RedoRequest.class);
        RedoResponse response = new RedoResponse();
//        System.out.println(JSONString);

//        if (redoRequest.getId() != null) {
//            System.out.println(redoRequest.getId());
//        }
        ItemEntity item = InfoAPI.getItemViaPK(redoRequest.getId());
        UserEntity user = InfoAPI.getUserViaPK(user_id);
        if (item == null) {
            response.setStatus(false);
            writer.print(gson.toJson(response, ModifyResponse.class));
            return;
        }

        item.setRedoCount(item.getRedoCount() + 1);
        HibernateUtil.getSessionFactory().getCurrentSession().update(item);

        // TODO: generate auto increment id
//        String redo_id =
        int redo_id = (int) (new Date()).getTime();
        RedoEntity redoEntity = new RedoEntity();
        redoEntity.setRedoId(redo_id);
        redoEntity.setAnswer(redoRequest.getrAnswer());
        redoEntity.setRedoTime(new Timestamp(redoRequest.getrTime()));
        redoEntity.setUser(user);
        redoEntity.setItem(item);

        HibernateUtil.getSessionFactory().getCurrentSession().persist(redoEntity);

        response.setStatus(true);
        response.setRedoId(redo_id);
        writer.print(gson.toJson(response, RedoResponse.class));

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
