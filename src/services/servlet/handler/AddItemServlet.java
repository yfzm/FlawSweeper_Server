package services.servlet.handler;

import beans.add.AddRequest;
import beans.add.AddResponse;
import beans.modify.ModifyRequest;
import beans.modify.ModifyResponse;
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
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

import static utils.InfoAPI.getRandomId;
import static utils.InfoAPI.getTagEntities;

@WebServlet(name = "AddItemServlet", urlPatterns = "/addItem")
public class AddItemServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();

        HttpSession session = request.getSession();
        if (session == null ||
                (session.getAttribute("userId") == null && session.getAttribute("adminId") == null)) {
            writer.print("{\"status\":false}");
            return;
        }

        Boolean is_admin = (session.getAttribute("adminId") != null);
        String user_id;
        if (is_admin) {
            user_id = session.getAttribute("adminId").toString();
        } else {
            user_id = session.getAttribute("userId").toString();
        }

        String JSONString = request.getParameter("json");
        JSONString = URLDecoder.decode(JSONString, "utf-8");
        if (JSONString == null) {
            System.out.println("No json data!!");
            return;
        }

        HibernateUtil.getSessionFactory().getCurrentSession().beginTransaction();
        addItemAndReturnId(writer, JSONString, user_id, is_admin);
        HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().commit();

    }

    private void addItemAndReturnId(PrintWriter writer, String JSONString, String user_id, Boolean is_admin) {
        Gson gson = new Gson();
//        System.out.println(JSONString);
        AddRequest addRequest = gson.fromJson(JSONString, AddRequest.class);
        AddResponse response = new AddResponse();

        UserEntity user = HibernateUtil.getSessionFactory().getCurrentSession().get(UserEntity.class, user_id);
        if (user == null
                || addRequest.getTitle() == null
                || addRequest.getcAnswer() == null
                || addRequest.getCreateTime() == 0
                || addRequest.getqText() == null) {
            response.setId("");
            response.setStatus(false);
            writer.print(gson.toJson(response, AddResponse.class));
            return;
        }

        ItemEntity item = new ItemEntity();
        String item_id = getRandomId();
        item.setItemId(item_id);
        item.setTitle(addRequest.getTitle());
        item.setContent(addRequest.getqText());
        item.setAnswer(addRequest.getcAnswer());
        item.setCreateTime(new Timestamp(addRequest.getCreateTime()));
        item.setViewCount(0);
        item.setEditCount(0);
        item.setRedoCount(0);
        if (is_admin) {
            item.setMode((byte) 0);
        } else {
            item.setMode((byte) 1);
        }
        item.setReason(addRequest.getReason());

        item.setUser(user);

        if (addRequest.getqTag() != null) {
            item.setTags(getTagEntities(addRequest.getqTag()));
        }

        HibernateUtil.getSessionFactory().getCurrentSession().save(item);

        response.setStatus(true);
        response.setId(item_id);
        writer.print(gson.toJson(response, AddResponse.class));

    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
