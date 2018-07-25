package api;

import java.math.BigDecimal;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import core.JsonServletBase;
import core.Logger;
import dao.AccountDao;
import dao.AccountDaoImpl;
import dao.TransactionDao;
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

    private AccountDao accountDao;
    private TransactionDao transactionDao;

    protected AccountDao getAccountDao() {
        if (accountDao == null) accountDao = new AccountDaoImpl();
        return accountDao;
    }

    void setAccountDao(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    protected TransactionDao getTransactionDao() {
        if (transactionDao == null) transactionDao = new TransactionDaoImpl();
        return transactionDao;
    }

    void setTransactionDao(TransactionDao transactionDao) {
        this.transactionDao = transactionDao;
    }

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
        LOG.log(Logger.Action.ALT_START, "user != null");
        if (user != null) {
            LOG.info("AccountController.processGet(): Loaded User: " + user.getUsername());

            user = getAccountDao().loadAccounts(user);
            Account account = user.getAccountByHashCode(accountId);

            LOG.log(Logger.Action.ALT_START, "account != null");
            if (account != null) {
                LOG.info("AccountController.processGet(): Account id matches, loading transactions");
                retval = getTransactionDao().loadTransactions(account);
                retval.setStatus(SUCCESS_STATUS);
            }
            LOG.log(Logger.Action.ALT_END);
        }
        LOG.log(Logger.Action.ALT_END);

        LOG.log(Logger.Action.RETURN, "account");
        return retval;
    }

    @Override
    protected Collection<Account> processGetAll(HttpServletRequest request, HttpServletResponse response) {
        LOG.setScenarioName("Get All Accounts");
        LOG.log(Logger.Action.BEGIN, "request", "response");
        User user = getUserFromSession(request);
        LOG.log(Logger.Action.ALT_START, "user != null");
        if (user != null) {
            user = new AccountDaoImpl().loadAccounts(user);
        }
        LOG.log(Logger.Action.ALT_END);

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

        account.setStatus(REQUIRED_FIELDS_MISSING_STATUS);

        try {
            User user = getUserFromSession(request);
            LOG.log(Logger.Action.ALT_START, "user != null && account != null");
            if (user != null && account != null) {
                LOG.log(Logger.Action.ALT_START, "account.getName() != null");
                if (account.getName() != null) {
                    account.setBalance(new BigDecimal(0));
                    account.setUser(user);
                    try {
                        account = new AccountDaoImpl().createAccount(account);
                    } catch (Exception e) {
                        throw new ServletException(e);
                    }

                    if (account != null) account.setStatus(SUCCESS_STATUS);
                }
                LOG.log(Logger.Action.ALT_END);
            } else {
                account = new Account();
                account.setStatus(ACCOUNT_CREATION_FAILES_STATUS);
            }
            LOG.log(Logger.Action.ALT_END);

            return account;
        } finally {
            LOG.log(Logger.Action.RETURN, "account");
        }
    }
}
