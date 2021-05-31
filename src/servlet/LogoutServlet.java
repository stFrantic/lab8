package servlet;

import java.io.IOException;
import java.util.Calendar;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import entity.ChatMessage;
import entity.ChatUser;


@WebServlet(name = "LogoutServlet")
public class LogoutServlet extends ChatServlet {
    private static final long serialVersionUID=1L;
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String name = (String) request.getSession().getAttribute("name");
        // Если в сессии имеется имя пользователя...
        if(name!=null) {
            // Получить объект, описывающий пользователя с таким именем
            ChatUser aUser = activeUsers.get(name);
            // Если идентификатор сессии пользователя, вошедшего под
            // этим именем, совпадает с идентификатором сессии
            // пользователя, пытающегося выйти из чата// (т.е. выходит тот же, кто и входил)
            if(aUser.getSessionId().equals((String) request.getSession().getId())) {

                synchronized(activeUsers) {
                    activeUsers.remove(name);
                }

                // Сбросить имяпользователя в сессии
                request.getSession().setAttribute("name", null);
                // СброситьID сессии в cookie
                response.addCookie(new Cookie("sessionId", null));
                // Перенаправить наглавную страницу
                String systemMessage =  " Пользователь " + aUser.getName() + " вышел из чата " ;
                response.sendRedirect(response.encodeRedirectURL("/lab8/"));
                synchronized (messages) {
                    ChatMessage str = new ChatMessage(systemMessage, aUser, Calendar.getInstance().getTimeInMillis(),"red");
                    messages.add(str);
                }
            }
            else{
                // Пользователь пытается аннулировать чужую сессию –// неделатьничего
                response.sendRedirect(response.encodeRedirectURL("/lab8/view.jsp"));
            }
        }
        else{
            // Перенаправить пользователя на главное окно чата
            response.sendRedirect(response.encodeRedirectURL("/lab8/view.jsp"));
        }
    }
}