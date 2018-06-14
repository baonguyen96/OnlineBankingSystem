package controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.UserDao;
import entity.reg.UserAccount;
import entity.staging.Login;
import service.UserAccountImpl;


@WebServlet("/")
public class LoginController extends HttpServlet {

    private static final long serialVersionUID = 1L;


    public LoginController() {
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
	    throws ServletException, IOException {

	UserDao userDao = new UserAccountImpl();

	String username = request.getParameter("username");
	String pass = request.getParameter("password");
	String submitType = request.getParameter("submit");
	Login login = new Login(username, pass);
	UserAccount user = userDao.validateCustomer(login);

	if (submitType.equals("login") && user != null && user.getName() != null) {
	    request.setAttribute("message", "Hello " + user.getName());
	    request.getRequestDispatcher("welcome.jsp").forward(request, response);
	}
	else if (submitType.equals("register")) {
	    user.setName(request.getParameter("name"));
	    user.setUsername(request.getParameter("username"));
	    user.setPassword(request.getParameter("password"));
	    user.setRecoverPasswordQuestion(request.getParameter("recover-password-question"));
	    user.setRecoverPasswordAnswer(request.getParameter("recover-password-answer"));
	    userDao.register(user);
	    request.setAttribute("successMessage", "Registration done, please login!");
	    request.getRequestDispatcher("login.jsp").forward(request, response);
	}
	else {
	    request.setAttribute("message", "Data Not Found! Please register!");
	    request.getRequestDispatcher("register.jsp").forward(request, response);
	}

    }

}
