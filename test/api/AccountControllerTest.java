package api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import core.JsonServletBase;
import core.SessionConstants;
import dao.AccountDao;
import dao.TransactionDao;
import domain.Account;
import domain.Transaction;
import domain.TransactionType;
import domain.User;

class AccountControllerTest {
    private AccountController controllerUnderTest;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private AccountDao accountDao;
    private TransactionDao transactionDao;
    private ByteArrayOutputStream outputStream;
    private PrintWriter printWriter;

    private Date transDate = new Date();
    private User sessionUser = createUser("testUsername", "testName");

    @BeforeEach
    public void beforeEach() throws IOException {
        controllerUnderTest = new AccountController();

        outputStream = new ByteArrayOutputStream();
        printWriter = new PrintWriter(new OutputStreamWriter(outputStream));

        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(printWriter);

        session = Mockito.mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(request.getSession(true)).thenReturn(session);

        accountDao = Mockito.mock(AccountDao.class);
        transactionDao = Mockito.mock(TransactionDao.class);

        controllerUnderTest.setAccountDao(accountDao);
        controllerUnderTest.setTransactionDao(transactionDao);
    }

    @Test
    void processGetTestSuccess() throws Exception {
        User userWithAccounts = createUser("testUsername", "testName");
        Account accountNoTransactions = createAccount(userWithAccounts, "testAccount", true);
        Account accountWithTransactions = createAccount(userWithAccounts, "testAccount", false);
        Transaction trans = createTestTransaction(accountWithTransactions, 1.00, TransactionType.Deposit, transDate);
        addTransToAccount(accountWithTransactions, trans);

        setHttpMethod(HttpMethod.GET);
        mockActiveSession(sessionUser);
        mockUriPathVariable("accountId", accountNoTransactions.hashCode());

        when(accountDao.loadAccounts(sessionUser)).thenReturn(userWithAccounts);
        when(transactionDao.loadTransactions(Mockito.any())).thenReturn(accountWithTransactions);

        String output = runController();
        assertNotNull(output);

        Account result = jsonToObject(Account.class, output);
        assertAccount(accountWithTransactions, result);

    }

    @Test
    void processGetTestSuccessNoTransactions() throws Exception {
        User userWithAccounts = createUser("testUsername", "testName");
        Account accountNoTransactions = createAccount(userWithAccounts, "testAccount", true);

        setHttpMethod(HttpMethod.GET);
        mockActiveSession(sessionUser);
        mockUriPathVariable("accountId", accountNoTransactions.hashCode());

        when(accountDao.loadAccounts(sessionUser)).thenReturn(userWithAccounts);
        when(transactionDao.loadTransactions(Mockito.any())).thenReturn(accountNoTransactions);

        String output = runController();
        assertNotNull(output);

        Account result = jsonToObject(Account.class, output);
        assertAccount(accountNoTransactions, result);

    }

    // Security Test, if you request a specific account but the user doesn't have that account return nothing
    @Test
    void processGetTestNoMatch() throws Exception {
        User userWithAccounts = createUser("testUsername", "testName");
        Account accountWithTransactions = createAccount(userWithAccounts, "testAccount", false);
        Transaction trans = createTestTransaction(accountWithTransactions, 1.00, TransactionType.Deposit, transDate);
        addTransToAccount(accountWithTransactions, trans);

        setHttpMethod(HttpMethod.GET);
        mockActiveSession(sessionUser);
        mockUriPathVariable("accountId", 123);

        when(accountDao.loadAccounts(sessionUser)).thenReturn(userWithAccounts);
        when(transactionDao.loadTransactions(Mockito.any())).thenReturn(accountWithTransactions);

        String output = runController();
        assertEquals("null", output);
    }

    // Security Test, if you request a specific account but the user doesn't have any accounts return nothing
    @Test
    void processGetTestNoAccounts() throws Exception {
        User userWithAccounts = createUser("testUsername", "testName");
        Account accountNoTransactions = createAccount(userWithAccounts, "testAccount", true);

        setHttpMethod(HttpMethod.GET);
        mockActiveSession(sessionUser);
        mockUriPathVariable("accountId", accountNoTransactions.hashCode());

        when(accountDao.loadAccounts(sessionUser)).thenReturn(sessionUser);

        String output = runController();
        assertEquals("null", output);
    }

    // Security Test, if not logged in, validate nothing gets returned
    @Test
    void processGetTestNoInvalidSession() throws Exception {
        setHttpMethod(HttpMethod.GET);
        mockUriPathVariable("accountId", 123);
        String output = runController();
        assertEquals(JsonServletBase.NOT_AUTHORIZED_MESSAGE, output);
    }

    private void assertAccount(Account expected, Account actual) {
        if (expected != null) assertNotNull(actual);
        assertEquals(expected.getBalance(), actual.getBalance());
        assertEquals(expected.getCreatedOn(), actual.getCreatedOn());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getStatus(), actual.getStatus());
        if (expected.getTransactions() == null) {
            assertNull(actual.getTransactions());
        } else {
            assertNotNull(actual.getTransactions());
            assertEquals(expected.getTransactions().size(), actual.getTransactions().size());
            for (int i = 0; i < expected.getTransactions().size(); i++) {
                assertTransaction(expected.getTransactions().get(i), actual.getTransactions().get(i));
            }
        }
        assertEquals(expected.getUpdatedOn(), actual.getUpdatedOn());
    }

    private void assertTransaction(Transaction expected, Transaction actual) {
        assertEquals(expected.getAmount(), actual.getAmount());
        assertEquals(expected.getCreatedOn(), actual.getCreatedOn());
        assertEquals(expected.getStatus(), actual.getStatus());
        assertEquals(expected.getTransferFromAccount(), actual.getTransferFromAccount());
        assertEquals(expected.getTransferToAccount(), actual.getTransferToAccount());
        assertEquals(expected.getType(), actual.getType());
        assertEquals(expected.getUpdatedOn(), actual.getUpdatedOn());
    }

    private String runController() throws ServletException, IOException {
        controllerUnderTest.service(request, response);
        return outputStream.toString();
    }

    private void addTransToAccount(Account account, Transaction trans) {
        List<Transaction> transactions = account.getTransactions();
        if (transactions == null) {
            transactions = new ArrayList<>();
            account.setTransactions(transactions);
        }
        transactions.add(trans);
        double balance = account.getTransactions().stream().map(Transaction::getAmount).mapToDouble(BigDecimal::doubleValue).sum();
        account.setBalance(new BigDecimal(balance));
    }

    private User createUser(String username, String name) {
        User user = new User();
        user.setUsername(username);
        user.setName(name);
        return user;
    }

    private Account createAccount(User user, String name, boolean addAcountToUser) throws Exception {
        Account account = new Account();
        account.setName(name);
        if (user != null) {
            account.setUser(user);
            if (addAcountToUser) user.addAccount(account);
        }
        return account;
    }

    private void setHttpMethod(HttpMethod method) {
        when(request.getMethod()).thenReturn(method.name());
    }

    private void mockUriPathVariable(String name, int value) {
        mockUriPathVariable(name, "" + value);
    }

    private void mockUriPathVariable(String name, String value) {
        when(request.getAttribute(name)).thenReturn(value);
    }

    private void mockActiveSession(User user) {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(SessionConstants.USER)).thenReturn(user);
    }

    private Transaction createTestTransaction(Account account, double amount, TransactionType type, Date transDate) {
        Transaction trans = new Transaction();
        trans.setAccount(account);
        trans.setAmount(new BigDecimal(amount));
        trans.setCreatedOn(transDate);
        trans.setType(type);
        trans.setUpdatedOn(transDate);
        return trans;
    }

    private <T extends Object> T jsonToObject(Class<T> clazz, String json) throws IOException {
        T retval;
        ObjectMapper objectMapper = new ObjectMapper() //
                .configure(MapperFeature.USE_ANNOTATIONS, false) //
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ObjectReader objectReader = objectMapper.readerFor(clazz);
        retval = objectReader.readValue(json);
        return retval;
    }

    enum HttpMethod {
        GET, POST, PUT, DELETE
    };
}
