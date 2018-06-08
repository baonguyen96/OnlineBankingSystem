USE cs6359;

-- select all from each table
SELECT * FROM users;
SELECT * FROM accounts;
SELECT * FROM transactions;

-- select account for a particular user
SELECT *
FROM accounts
WHERE user_id = 'user_id';

-- select top 10 transaction of a particular user
SELECT *
FROM transactions 
WHERE user_id = 1
ORDER BY date DESC 
LIMIT 10;

-- view balance of a particular account
SELECT balance
FROM accounts
WHERE user_id = 1;

-- log in
SELECT *
FROM users
WHERE username = 'username' AND password = 'password';

-- select recover password question
SELECT  recover_password_question
FROM users
WHERE username = 'username';