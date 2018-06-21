USE cs6359;

-- add new user
INSERT INTO `user` (username, password, full_name, recover_password_question, recover_password_answer, balance) value ('?', '?', '?', '', '', 0);

-- add new transaction
INSERT INTO `transaction` (user_id, type, amount) value (2, 'Deposit', 50.00);

-- update account balance
UPDATE `user`
SET balance = balance + 100
WHERE id = 2;

-- recover forgotten password
UPDATE `user`
SET password = 'new password'
WHERE username = 'username' AND recover_password_answer = 'answer';

-- update user's password
UPDATE `user`
SET password = 'new password'
WHERE username = 'username3' and password = 'pass3';