package api;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import core.JsonServletBase;
import core.Logger;
import dao.AccountDaoImpl;
import dao.TransactionDaoImpl;
import domain.Account;
import domain.Transaction;
import domain.TransactionType;
import domain.User;

/**
 * Servlet API implementation class TransactionController
 */
@WebServlet({ // 
        "/api/users/{userId}/accounts/{accountId}/transactions/{transactionId}", // 
        "/api/users/{userId}/accounts/{accountId}/{transferToAccountId}/transactions/{transactionId}" //
})
public class TransactionController extends JsonServletBase<Transaction> {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = new Logger(TransactionController.class);

    private static final String SUCCESS_STATUS = "Success";
    private static final String TRANSACTION_CREATION_FAILED_STATUS = "The transaction failed";
    private static final String TRANSACTION_OVERDRAWN_FAILED_STATUS = "DUDE! That's more money than you have!";
    private static final String REQUIRED_FIELDS_MISSING_STATUS = "Required fields are missing";

    @Override
    protected boolean requireValidSession() {
        LOG.log(Logger.Action.BEGIN);
        LOG.log(Logger.Action.RETURN, "is valid session required");
        return true;
    }

    /**
     * Provides the CREATE Transaction action 
     */
    @Override
    protected Transaction processPost(HttpServletRequest request, HttpServletResponse response, Transaction transaction) throws ServletException {
        LOG.setScenarioName("Process Transaction");
        LOG.log(Logger.Action.BEGIN, "request", "response", "transaction");

        try {
            Integer accountId = getUriPathVariableAsInteger(request, "accountId");
            Integer transferToAccountId = getUriPathVariableAsInteger(request, "transferToAccountId");

            if (accountId == null || transaction.getAmount() == null) transaction.setStatus(REQUIRED_FIELDS_MISSING_STATUS);
            else {
                User user = getUserFromSession(request);

                if (user != null) {
                    user = new AccountDaoImpl().loadAccounts(user);

                    Account targetAccount = user.getAccountByHashCode(accountId);
                    Account transferToAccount = user.getAccountByHashCode(transferToAccountId);

                    transaction.setAccount(targetAccount);
                    if (TransactionType.TransferOut == transaction.getType()) {
                        transaction.setTransferFromAccount(targetAccount);
                        transaction.setTransferToAccount(transferToAccount);
                    }

                    if (targetAccount == null || !transaction.isValid()) {
                        LOG.info("transaction was null or failed validation, " + (targetAccount == null) + ", " + (!transaction.isValid()));
                        transaction.setStatus(TRANSACTION_CREATION_FAILED_STATUS);
                    }
                    if ((TransactionType.Withdraw == transaction.getType() || TransactionType.TransferOut == transaction.getType()) //
                            && targetAccount.getBalance().doubleValue() + transaction.getAmount().doubleValue() < 0) {
                        LOG.info("transaction was withdraw or transfer out exceeded balance");
                        transaction.setStatus(TRANSACTION_OVERDRAWN_FAILED_STATUS);
                    } else {
                        try {
                            transaction = new TransactionDaoImpl().createTransaction(transaction);
                            transaction.setStatus(SUCCESS_STATUS);

                            if (transaction != null && TransactionType.TransferOut == transaction.getType()) {
                                Transaction toTrans = transaction.cloneForTransfer();
                                LOG.info(toTrans.toString());
                                new TransactionDaoImpl().createTransaction(toTrans);
                            }
                        } catch (Exception e) {
                            throw new ServletException(e);
                        }
                    }
                } else {
                    LOG.error("AccountController.processGetAll(): user was unexpectedly null");
                    transaction.setStatus(TRANSACTION_CREATION_FAILED_STATUS);
                }
            }
        } finally {
            LOG.log(Logger.Action.RETURN, "transaction");
        }
        return transaction;
    }
}
