function regValidate() {

	var username = document.forms["regform"]["username"].value;
	var password = document.forms["regform"]["password"].value;
	var rpassword = document.forms["regform"]["retry-password"].value;
	var question = document.forms["regform"]["recover-password-question"].value;
	var answer = document.forms["regform"]["recover-password-answer"].value;

	if (username === "") {
		alert("username must be filled out");
		document.forms["regform"]["username"].focus();
		return false;
	}
	else if (password === "") {
		alert("password must be filled out");
		document.forms["regform"]["password"].focus();
		return false;
	}
	else if (rpassword === "") {
		alert("retry-password must be filled out");
		document.forms["regform"]["retry-password"].focus();
		return false;
	}
	else if (password !== rpassword) {
		alert("passwords do not match");
		document.forms["regform"]["password"].focus();
		return false;
	}
	else if (question === "") {
		alert("recover password question must be filled out");
		document.forms["regform"]["recover-password-question"].focus();
		return false;
	}
	else if (answer === "") {
		alert("recover password answer must be filled out");
		document.forms["regform"]["recover-password-answer"].focus();
		return false;
	}
}




function loginValidate() {
	var username = document.forms["loginform"]["username"].value;
	var password = document.forms["loginform"]["password"].value;

	if (username === "") {
		alert("username must be filled out");
		document.forms["loginform"]["username"].focus();
		return false;
	}
	else if (password === "") {
		alert("password must be filled out");
		document.forms["loginform"]["password"].focus();
		return false;
	}
}
