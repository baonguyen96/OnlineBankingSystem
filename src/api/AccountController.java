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
import dao.TransactionDaoImpl;
import domain.Account;
import domain.User;

/**
 * Servlet API implementation class AccountController
 */
@WebServlet("/api/users/{userId}/accounts/{accountId}")
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
    protected Account processGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LOG.info("AccountController.processGet(): BEGIN");
        Integer accountId = getUriPathVariableAsInteger(request, "accountId");

        Account retval = null;
        User user = getUserFromSession(request);
        if (user != null) {
            LOG.info("AccountController.processGet(): Loaded User: " + user.getUsername());
            user = new AccountDaoImpl().loadAccounts(user);

            for (Account account : user.getAccounts()) {
                LOG.info("AccountController.processGet(): Checking account: " + account.getName() + " to see if it's id = " + accountId);

                if (account.hashCode() == accountId) {
                    LOG.info("AccountController.processGet(): Account id matches, loading transactions");
                    retval = new TransactionDaoImpl().loadTransactions(account);
                    retval.setStatus(SUCCESS_STATUS);
                    break;
                }
            }

        } else {
            LOG.log(Level.SEVERE, "AccountController.processGetAll(): user was unexpectedly null");
        }

        LOG.info("AccountController.processGet(): END");
        return retval;
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
                account.setUser(user);
                try {
                    account = new AccountDaoImpl().createAccount(account);
                } catch (Exception e) {
                    throw new ServletException(e);
                }

                if (account != null) {
                    account.setStatus(SUCCESS_STATUS);
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
