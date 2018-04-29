package services;

import beans.list.ListItemInfo;
import beans.list.ListResponse;
import com.google.gson.Gson;
import config.ConfigConstant;
import persistence.ItemEntity;
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
        PrintWriter writer = response.getWriter();
        String page = request.getParameter("page");

        if (page != null)
            System.out.println(page);
        else {
            System.out.println("No page!!");
            return;
        }

        HttpSession session = request.getSession();
        if (session == null || session.getAttribute("userId") == null) {
            writer.print("{\"status\":false}");
            return;
        }
//        writer.print("abc");

        String user_id = session.getAttribute("userId").toString();

        HibernateUtil.getSessionFactory().getCurrentSession().beginTransaction();
        queryAndReturnList(writer, user_id, page);
        HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().commit();

    }

    @SuppressWarnings("unchecked")
    private void queryAndReturnList(PrintWriter writer, String user_id, String page_str) {
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
        int page = Integer.parseInt(page_str);
        if (page <= 0) {
            response.setStatus(false);
            writer.println(gson.toJson(response, ListResponse.class));
            return;
        }

        String hql = "from ItemEntity where user.userId = ? or mode = 0 order by createTime desc ";
        List<ItemEntity> items = HibernateUtil.getSessionFactory().getCurrentSession().createQuery(hql)
                .setParameter(0, user_id)
                .list();

        response.setStatus(true);

        int begin_num = ConfigConstant.PAGE_NUM * (page - 1);  // start at 0
        int items_size = items.size();

        if (items_size <= begin_num) {
            response.setNum(0);
        } else {

            int num = Math.min(ConfigConstant.PAGE_NUM, items_size - begin_num);
            response.setNum(num);
            ArrayList<ListItemInfo> listItemInfos = new ArrayList<>();
            for (int index = begin_num; index < begin_num + num; index++) {
                ItemEntity item = items.get(index);
                ListItemInfo info = new ListItemInfo();
                info.setId(item.getItemId());
                info.setTitle(item.getTitle());
                // TODO: query form tag table
                info.setqTag(null);
                info.setCreateTime(item.getCreateTime());
                info.setBySelf(item.getMode() == 1);
                info.setRedoCount(item.getRedoCount());
                info.setViewCount(item.getViewCount());
                listItemInfos.add(info);
            }
            response.setItems(listItemInfos);
        }
        writer.println(gson.toJson(response, ListResponse.class));

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
