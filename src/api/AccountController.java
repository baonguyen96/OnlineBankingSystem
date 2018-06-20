package api;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import core.JsonServletBase;
import dao.AccountDaoImpl;
import domain.Account;
import domain.User;

/**
 * Servlet implementation class Login
 */
@WebServlet("/api/account")
public class AccountController extends JsonServletBase<Account> {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(AccountController.class.getName());
    private static final String SUCCESS_STATUS = "Success";
    private static final String ACCOUNT_CREATION_FAILES_STATUS = "The account creation failed";
    private static final String REQUIRED_FIELDS_MISSING_STATUS = "Required fields are missing";

    public AccountController() {
    }

    @Override
    protected boolean requireValidSession() {
        return true;
    }

    @Override
    protected Account processGet(HttpServletRequest request, HttpServletResponse response, Long id) throws ServletException, IOException {
        return null;
    }

    @Override
    protected Collection<Account> processGetAll(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LOG.info("AccountController.processGetAll(): BEGIN");
        User user = getUserFromSession(request);
        if (user != null) {
            user = new AccountDaoImpl().loadAccounts(user);
        } else {
            LOG.log(Level.SEVERE, "AccountController.processGetAll(): user was unexpectedly null");
        }

        LOG.info("AccountController.processGetAll(): END");
        return user.getAccounts();
    }

    /**
     * Provides the CREATE Account action 
     */
    @Override
    protected Account processPost(HttpServletRequest request, HttpServletResponse response, Account account) throws ServletException, IOException {
        User user = getUserFromSession(request);
        if (user == null || account == null) {
            account = new Account();
            account.setStatus(ACCOUNT_CREATION_FAILES_STATUS);
        } else {
            LOG.log(Level.INFO, user.toString());
            if (account.getName() == null) account.setStatus(REQUIRED_FIELDS_MISSING_STATUS);
            else {
                account.setBalance(new BigDecimal(0));
                account.setId(null);
                account.setUserId(user.getId());
                account = new AccountDaoImpl().createAccount(account);

                if (account != null) {
                    account.setStatus(SUCCESS_STATUS);
                } else {
                    account.setStatus(REQUIRED_FIELDS_MISSING_STATUS);
                }
            }
        }
        return account;
    }

    @Override
    protected Account processPut(HttpServletRequest request, HttpServletResponse response, Account putObject) throws ServletException, IOException {
        return null;
    }

    @Override
    protected void processDelete(HttpServletRequest request, HttpServletResponse response, Account deleteObject) throws ServletException, IOException {
    }

}
