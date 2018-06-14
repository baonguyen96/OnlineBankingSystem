USE cs6359;

-- select all from each table
SELECT * FROM user_accountss;
SELECT * FROM transactions;

-- select top 10 transaction of a particular user
SELECT *
FROM transactions 
WHERE user_id = 1
ORDER BY date DESC 
LIMIT 10;

-- view balance of a particular account
SELECT balance
FROM user_accounts
WHERE user_id = 1;

-- log in
SELECT *
FROM user_accounts
WHERE username = 'username' AND password = 'password';

-- select recover password question
SELECT  recover_password_question
FROM user_accounts
WHERE username = 'username';