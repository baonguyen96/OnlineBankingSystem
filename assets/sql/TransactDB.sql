USE cs6359;

-- add new user
INSERT INTO users (username, password, full_name, recover_password_question, recover_password_answer) value ('username3', 'pass3', 'Customer Three', '', '');

-- add new transaction
INSERT INTO transactions (user_id, type, amount) value (2, 'Deposit', 50.00);

-- add new account
INSERT INTO accounts (user_id, balance) value (2, 200.00);

-- update account balance
UPDATE accounts
SET balance = balance + 100
WHERE user_id = 2;

-- recover forgotten password
UPDATE users
SET password = 'new password'
WHERE username = 'username' AND recover_password_answer = 'answer';

-- update user's password
UPDATE users
SET password = 'new password'
WHERE username = 'username3' and password = 'pass3';