package services.servlet.info;

import beans.list.ListItemInfo;
import beans.list.ListResponse;
import com.google.gson.Gson;
import config.ConfigConstant;
import persistence.ItemEntity;
import persistence.TagEntity;
import persistence.UserEntity;
import utils.HibernateUtil;

import javax.persistence.criteria.CriteriaBuilder;
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

@WebServlet(name = "ListServlet", urlPatterns = "/getList")
public class ListServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();
        String user_id;
//        String adimin_id;
        Boolean is_admin;

        String page = request.getParameter("page");
        String method = request.getParameter("method");

        if (method == null || method.equals("time"))
            method = "createTime";
        else
            method = "viewCount";

        HttpSession session = request.getSession();
        if (session == null) {
            writer.print("{\"status\":false}");
            return;
        }

        if (session.getAttribute("userId") != null) {
            is_admin = false;
            user_id = session.getAttribute("userId").toString();
            if (page == null) {
                writer.print("{\"status\":false}");
                return;
            }
        } else if (session.getAttribute("adminId") != null) {
            is_admin = true;
            user_id = session.getAttribute("adminId").toString();
        } else {
            writer.print("{\"status\":false}");
            return;
        }
//        writer.print("abc");

//        user_id = session.getAttribute("userId").toString();

        HibernateUtil.getSessionFactory().getCurrentSession().beginTransaction();
        queryAndReturnList(writer, user_id, page, method, is_admin);
        HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().commit();

    }

    @SuppressWarnings("unchecked")
    private void queryAndReturnList(PrintWriter writer, String user_id,
                                    String page_str, String method, Boolean is_admin) {
//        String hql = "FROM UserEntity WHERE userId = ?";
//        List<UserEntity> users = HibernateUtil.getSessionFactory().getCurrentSession().createQuery(hql)
//                .setParameter(0, user_id)
//                .list();
//        if (users != null && users.size() == 1) {
//            Set<ItemEntity> items = users.get(0).getItems();
//
//        }
        Gson gson = new Gson();
        ListResponse response = new ListResponse();

        int page = 0;
        if (!is_admin) {
            page = Integer.parseInt(page_str);
            if (page <= 0) {
                response.setStatus(false);
                writer.println(gson.toJson(response, ListResponse.class));
                return;
            }
        }

        String hql;
        if (is_admin) {
            hql = "from ItemEntity where user.userId = ? order by " + method + " desc ";
        } else {
            hql = "from ItemEntity where user.userId = ? or mode = 0 order by " + method + " desc ";
        }
        List<ItemEntity> items = HibernateUtil.getSessionFactory().getCurrentSession().createQuery(hql)
                .setParameter(0, user_id)
                .list();

        response.setStatus(true);

        int begin_num = 0;
        int items_size = items.size();
        if (!is_admin) {
            begin_num = ConfigConstant.PAGE_NUM * (page - 1);  // start at 0
        }


        if (items_size <= begin_num) {
            response.setNum(0);
            writer.print(gson.toJson(response, ListResponse.class));
            return;
        }


        int num = items_size;
        if (!is_admin) {
            num = Math.min(ConfigConstant.PAGE_NUM, items_size - begin_num);
        }

        response.setNum(num);
        ArrayList<ListItemInfo> listItemInfos = new ArrayList<>();
        for (int index = begin_num; index < begin_num + num; index++) {
            ItemEntity item = items.get(index);
            ListItemInfo info = new ListItemInfo();
            info.setId(item.getItemId());
            info.setTitle(item.getTitle());
            // tags
            Set<TagEntity> tag_set = item.getTags();
            List<String> tags = new ArrayList<>();
            for (TagEntity tag : tag_set) {
                tags.add(tag.getTagContent());
                System.out.println(tag.getTagContent());
            }
            info.setqTag(tags);
            // get time stamp (long)
            info.setCreateTime(item.getCreateTime().getTime());
            info.setBySelf(item.getMode() == 1);
            info.setRedoCount(item.getRedoCount());
            info.setViewCount(item.getViewCount());
            listItemInfos.add(info);
        }
        response.setItems(listItemInfos);

        writer.print(gson.toJson(response, ListResponse.class));

//        List<UserEntity> users = HibernateUtil.getSessionFactory().getCurrentSession().createQuery(hql)
//                .setParameter(0, username)
//                .setParameter(1, password)
//                .list();
//        if (users != null && users.size() == 1) {
//            return users.get(0).getUserId();
//        } else {
//            return null;
//        }
    }
}
