package api;

import java.io.IOException;
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
import domain.Transaction;
import domain.TransactionType;
import domain.User;

/**
 * Servlet implementation class Login
 */
@WebServlet("/api/transaction")
public class TransactionController extends JsonServletBase<Transaction> {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(TransactionController.class.getName());
    private static final String SUCCESS_STATUS = "Success";
    private static final String TRANSACTION_CREATION_FAILED_STATUS = "The transaction failed";
    private static final String REQUIRED_FIELDS_MISSING_STATUS = "Required fields are missing";

    public TransactionController() {
    }

    @Override
    protected boolean requireValidSession() {
        return true;
    }

    @Override
    protected Transaction processGet(HttpServletRequest request, HttpServletResponse response, Long id) throws ServletException, IOException {
        return null;
    }

    /**
     * Use AccountController GET to load an account with Transactions
     */
    @Override
    protected Collection<Transaction> processGetAll(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        return null;
    }

    /**
     * Provides the CREATE Transaction action 
     */
    @Override
    protected Transaction processPost(HttpServletRequest request, HttpServletResponse response, Transaction transaction) throws ServletException, IOException {
        if (transaction.getAccountId() == null || transaction.getAmount() == null) transaction.setStatus(REQUIRED_FIELDS_MISSING_STATUS);
        else {
            User user = getUserFromSession(request);

            if (user != null) {
                user = new AccountDaoImpl().loadAccounts(user);

                Account targetAccount = null;
                for (Account account : user.getAccounts()) {
                    if (account.getId() != null && account.getId().equals(transaction.getAccountId())) {
                        targetAccount = account;
                        break;
                    }
                }

                if (targetAccount == null //
                        || !TransactionType.Deposit.equals(transaction.getType()) //
                        || transaction.getAmount().doubleValue() <= 0.0) {
                    transaction.setStatus(TRANSACTION_CREATION_FAILED_STATUS);
                } else {
                    transaction = new TransactionDaoImpl().createTransaction(transaction);
                    transaction.setStatus(SUCCESS_STATUS);
                }
            } else {
                LOG.log(Level.SEVERE, "AccountController.processGetAll(): user was unexpectedly null");
                transaction.setStatus(TRANSACTION_CREATION_FAILED_STATUS);
            }
        }
        return transaction;
    }

    @Override
    protected Transaction processPut(HttpServletRequest request, HttpServletResponse response, Transaction putObject) throws ServletException, IOException {
        return null;
    }

    @Override
    protected void processDelete(HttpServletRequest request, HttpServletResponse response, Transaction deleteObject) throws ServletException, IOException {
    }

}
