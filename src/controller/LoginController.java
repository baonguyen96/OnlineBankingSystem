package controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.CustomerDao;
import entity.reg.Customer;
import entity.staging.Login;
import service.CustomerDaoImpl;


@WebServlet("/")
public class LoginController extends HttpServlet {

    private static final long serialVersionUID = 1L;


    public LoginController() {
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
	    throws ServletException, IOException {

	CustomerDao customerDao = new CustomerDaoImpl();

	String username = request.getParameter("username");
	String pass = request.getParameter("password");
	String submitType = request.getParameter("submit");
	Login login = new Login(username, pass);
	Customer c = customerDao.validateCustomer(login);

	if (submitType.equals("login") && c != null && c.getName() != null) {
	    request.setAttribute("message", "Hello " + c.getName());
	    request.getRequestDispatcher("welcome.jsp").forward(request, response);
	}
	else if (submitType.equals("register")) {
	    c.setName(request.getParameter("name"));
	    c.setUsername(request.getParameter("username"));
	    c.setPassword(request.getParameter("password"));
	    c.setRecoverPasswordQuestion(request.getParameter("recover-password-question"));
	    c.setRecoverPasswordAnswer(request.getParameter("recover-password-answer"));
	    customerDao.register(c);
	    request.setAttribute("successMessage", "Registration done, please login!");
	    request.getRequestDispatcher("login.jsp").forward(request, response);
	}
	else {
	    request.setAttribute("message", "Data Not Found! Please register!");
	    request.getRequestDispatcher("register.jsp").forward(request, response);
	}

    }

}
