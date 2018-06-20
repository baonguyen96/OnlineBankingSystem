var baseServerUrl = "http://localhost:8080/OnlineBankingSystem";
var loggedIn = false;

function resetAll() {
	$("[name='loggedInMenu']").hide();
	$("[name='loggedOutMenu']").show();

	$("#welcomeScreen").hide();
	$("#loginScreen").hide();
	$("#registerScreen").hide();
}

function initialize() {
	checkLoginStatus();

	$('ul.nav a').click(function(e) {
		$('ul.nav li').each(function() {
			$(this).removeClass('active');
		});
		
		$(e.toElement).parent().addClass('active');
	});
}

$(document).ready(function() {

	// initialize the page
	resetAll();
	initialize();

	
	$(".homeLink").click(function() {
		resetAll();
		initialize();
	});
	
	$(".loginButton").click(function() {
		resetAll();
		$("#loginScreen").show();
	});
	
	$("#loginSubmitButton").click(function() {
		console.log("loginSubmitButton");
		processLoginRequest();			
	});

	$("#registerButton").click(function() {
		resetAll();
		$("#registerScreen").show();		
	});

	$("#registerSubmitButton").click(function() {
		console.log("registerSubmitButton");
		processRegistrationRequest();
	});
});

function isValidSSN(value) {
    var re = /^([0-6]\d{2}|7[0-6]\d|77[0-2])([ \-]?)(\d{2})\2(\d{4})$/;
    if (!re.test(value)) {
        return false;
    }
    var temp = value;
    if (value.indexOf("-") != -1) {
        temp = (value.split("-")).join("");
    }
    if (value.indexOf(" ") != -1) {
        temp = (value.split(" ")).join("");
    }
    if (temp.substring(0, 3) == "000") {
        return false;
    }
    if (temp.substring(3, 5) == "00") {
        return false;
    }
    if (temp.substring(5, 9) == "0000") {
        return false;
    }
    return true;
}

function checkLoginStatus() {
	$.ajax({
		url: baseServerUrl + '/api/status',
		type: 'GET',
		data: null, //JSON.stringify(paginatedRequest),
		dataType: 'json',
		contentType: "application/json",
		success:function(res){
			if (res && res.username) {
				console.log(res.status);
				$("[name='loggedInMenu']").show();
				$("[name='loggedOutMenu']").hide();
				console.log(res);
				loggedIn = true;
				resetAll();
				$("#loginScreen").show();
			} else {
				loggedIn = false;	
				resetAll();
				$("#welcomeScreen").show();
			}
		},
		error:function(res, textStatus) {
			console.log(res.status);
			loggedIn = false;
			resetAll();
			$("#welcomeScreen").show();
		}
	});
}

function processLoginRequest() {
	username = $("#loginUsername").val();
	password = $("#loginPassword").val();

	loginRequest = {"username":username,"password":password};

	$.ajax({
		url: baseServerUrl + '/api/login',
		type: 'POST',
		data: JSON.stringify(loginRequest),
		dataType: 'json',
		contentType: "application/json",
		success:function(res){
			console.log(JSON.stringify(res));
			console.log(res.status);
			if (res.status === "Success") {
				$("[name='loggedOutMenu']").hide();
				$("[name='loggedInMenu']").show();
				$("#loginAlert").hide();
				loggedIn = true;
				resetAll();
				$("#welcomeScreen").show();
			} else {
				$("[name='loggedInMenu']").hide();
				$("[name='loggedOutMenu']").show();
				loginAlertDiv = $("#loginAlert");
				loginAlertDiv.empty();
				$('<span>' + res.status + '</span>').appendTo(loginAlertDiv);
				loginAlertDiv.show();
				loggedIn = false;
				resetAll();
				$("#loginScreen").show();
			}
		},
		error:function(res, textStatus) {
			console.log(res.status);
			$("[name='loggedInMenu']").hide();
			$("[name='loggedOutMenu']").show();
			loginAlertDiv = $("#loginAlert");
			loginAlertDiv.empty();
			$('<span>Error processing request, please try again</span>').appendTo(loginAlertDiv);
			loginAlertDiv.show();
			loggedIn = false;
			resetAll();
			$("#loginScreen").show();
	}
	});
}

function processRegistrationRequest() {
	username = $("#registerUsername").val();
	password = $("#registerPassword").val();
	fullname = $("#registerFullName").val();
	question = $("#registerRecoverQuestion").val();
	answer = $("#registerRecoverAnswer").val();
	
	userRequest = {
		"username":username,
		"password":password,
		"name":fullname,
		"recoverPasswordQuestion":question,
		"recoverPasswordAnswer":answer
	};

	$.ajax({
		url: baseServerUrl + '/api/user',
		type: 'POST',
		data: JSON.stringify(userRequest),
		dataType: 'json',
		contentType: "application/json",
		success:function(res){
			console.log(JSON.stringify(res));
			console.log(res.status);
			if (res.status === "Success") {
				$("[name='loggedOutMenu']").hide();
				$("[name='loggedInMenu']").show();
				$("#registerAlert").hide();
				loggedIn = true;
				resetAll();
				$("#welcomeScreen").show();
			} else {
				$("[name='loggedInMenu']").hide();
				$("[name='loggedOutMenu']").show();
				registerAlertDiv = $("#registerAlert");
				registerAlertDiv.empty();
				$('<span>' + res.status + '</span>').appendTo(registerAlertDiv);
				registerAlertDiv.show();
				loggedIn = false;
				resetAll();
				$("#registerScreen").show();
			}
		},
		error:function(res, textStatus) {
			console.log(res.status);
			$("[name='loggedInMenu']").hide();
			$("[name='loggedOutMenu']").show();
			registerAlertDiv = $("#registerAlert");
			registerAlertDiv.empty();
			$('<span>Error processing request, please try again</span>').appendTo(registerAlertDiv);
			registerAlertDiv.show();
			loggedIn = false;
			resetAll();
			$("#registerScreen").show();
	}
	});
}