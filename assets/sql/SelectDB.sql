USE cs6359;

-- select all from each table
SELECT * FROM `user`;
SELECT * FROM `account`;
SELECT * FROM `transaction`;

-- select top 10 transaction of a particular user
SELECT *
FROM `transaction` 
WHERE id = 1
ORDER BY date DESC 
LIMIT 10;

-- view balance of a particular account
SELECT balance
FROM `user`
WHERE id = 1;

-- log in
SELECT *
FROM `user`
WHERE username = 'username' AND password = 'password';

-- select recover password question
SELECT  recover_password_question
FROM `user`
WHERE username = 'username';