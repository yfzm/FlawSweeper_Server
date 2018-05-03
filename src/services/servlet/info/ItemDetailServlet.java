package services.servlet.info;

import beans.detail.DetailResponse;
import beans.detail.RedoInfo;
import com.google.gson.Gson;
import persistence.ItemEntity;
import persistence.RedoEntity;
import persistence.TagEntity;
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
import java.util.Set;

@WebServlet(name = "ItemDetailServlet", urlPatterns = "/getDetail")
public class ItemDetailServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();
        String id = request.getParameter("id");

        if (id != null)
            System.out.println(id);
        else {
            System.out.println("No Id!!");
            return;
        }

        HttpSession session = request.getSession();
        if (session == null || session.getAttribute("userId") == null) {
            writer.print("{\"status\":false}");
            return;
        }
        String user_id = session.getAttribute("userId").toString();

        HibernateUtil.getSessionFactory().getCurrentSession().beginTransaction();
        queryAndReturnDetail(writer, id, user_id);
        HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().commit();
    }

    @SuppressWarnings("unchecked")
    private void queryAndReturnDetail(PrintWriter writer, String id, String user_id) {
        Gson gson = new Gson();
        DetailResponse response = new DetailResponse();

        String hql = "FROM ItemEntity WHERE itemId = ?";
        List<ItemEntity> items = HibernateUtil.getSessionFactory().getCurrentSession().createQuery(hql)
                .setParameter(0, id)
                .list();
        if (items != null && items.size() == 1) {
            ItemEntity item = items.get(0);
            response.setStatus(true);
            response.setId(item.getItemId());
            response.setTitle(item.getTitle());
            List<String> tags = new ArrayList<>();
            Set<TagEntity> tag_set = item.getTags();
            for (TagEntity tag : tag_set) {
                tags.add(tag.getTagContent());
            }
            response.setqTag(tags);
            response.setReason(item.getReason());
            response.setqText(item.getContent());
            response.setcAnswer(item.getAnswer());
            response.setCreateTime(item.getCreateTime().getTime());
            response.setBySelf(item.getMode() == 1);
            response.setViewCount(item.getViewCount());
            response.setEditCount(item.getEditCount());
            response.setRedoCount(item.getRedoCount());
            List<RedoInfo> redoInfos = new ArrayList<>();
            Set<RedoEntity> redo_set = item.getRedoSet();
            for (RedoEntity redo : redo_set) {
                if (redo.getUser().getUserId().equals(user_id)) {
                    RedoInfo r = new RedoInfo();
                    r.setrAnswer(redo.getAnswer());
                    r.setrTime(redo.getRedoTime().getTime());
                    redoInfos.add(r);
                }
            }
            response.setRedo(redoInfos);
        } else {
            response.setStatus(false);
        }
        writer.print(gson.toJson(response, DetailResponse.class));

    }
}
