package api;

import java.math.BigDecimal;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import core.JsonServletBase;
import core.Logger;
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
    private static final Logger LOG = new Logger(AccountController.class);
    private static final String SUCCESS_STATUS = "Success";
    private static final String ACCOUNT_CREATION_FAILES_STATUS = "The account creation failed";
    private static final String REQUIRED_FIELDS_MISSING_STATUS = "Required fields are missing";

    @Override
    protected boolean requireValidSession() {
        LOG.log(Logger.Action.BEGIN);
        LOG.log(Logger.Action.RETURN, "is valid session required");
        return true;
    }

    @Override
    protected Account processGet(HttpServletRequest request, HttpServletResponse response) {
        LOG.setScenarioName("Get Account Transactions");
        LOG.log(Logger.Action.BEGIN, "request", "response");
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
            LOG.error("AccountController.processGetAll(): user was unexpectedly null");
        }

        LOG.log(Logger.Action.RETURN, "account");
        return retval;
    }

    @Override
    protected Collection<Account> processGetAll(HttpServletRequest request, HttpServletResponse response) {
        LOG.setScenarioName("Get All Accounts");
        LOG.log(Logger.Action.BEGIN, "request", "response");
        User user = getUserFromSession(request);
        if (user != null) {
            user = new AccountDaoImpl().loadAccounts(user);
        } else {
            LOG.error("AccountController.processGetAll(): user was unexpectedly null");
        }

        LOG.log(Logger.Action.RETURN, "accounts");
        return user.getAccounts();
    }

    /**
     * Provides the CREATE Account action 
     * @throws ServletException 
     */
    @Override
    protected Account processPost(HttpServletRequest request, HttpServletResponse response, Account account) throws ServletException {
        LOG.setScenarioName("Create Account");
        LOG.log(Logger.Action.BEGIN, "request", "response", "account");

        try {
            User user = getUserFromSession(request);
            if (user == null || account == null) {
                account = new Account();
                account.setStatus(ACCOUNT_CREATION_FAILES_STATUS);
            } else {
                LOG.info(user.toString());
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
        } finally {
            LOG.log(Logger.Action.RETURN, "account");
        }
    }
}
