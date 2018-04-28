package services;

import utils.HibernateUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "ListServlet", urlPatterns = "/getList")
public class ListServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter writer = response.getWriter();

        HttpSession session = request.getSession();
        if (session == null || session.getAttribute("userId") == null) {
            writer.print("{\"status\":false}");
            return;
        }

        writer.print("{\"status\":true, \"user_id\":\"" + session.getAttribute("userId") + "\"}");
    }
}
