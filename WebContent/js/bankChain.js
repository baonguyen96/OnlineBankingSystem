var baseServerUrl = "http://localhost:8080/OnlineBankingSystem";
var loggedIn = false;

function resetAll() {
	// set the menu options based on login status
	if (loggedIn) {
		$("[name='loggedOutMenu']").hide();
		$("[name='loggedInMenu']").show();
	} else {
		$("[name='loggedInMenu']").hide();
		$("[name='loggedOutMenu']").show();
	}

	// hide everyting by default
	$("#welcomeScreen").hide();
	$("#loginScreen").hide();
	$("#registerScreen").hide();
	$("#accountsHomeScreen").hide();
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
	
	$(".menuMyAccounts").click(function() {
		resetAll();
		$("#accountsHomeScreen").show();
	});
	
	$("#logoutButton").click(function() {
		console.log("logoutButton");
		processLogoutRequest();
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
		url: baseServerUrl + '/api/status?id=0',
		type: 'GET',
		data: null, //JSON.stringify(paginatedRequest),
		dataType: 'json',
		contentType: "application/json",
		success:function(res){
			console.log(JSON.stringify(res));

			if (res && res.userName) {
				console.log(res.status);
				$("[name='loggedInMenu']").show();
				$("[name='loggedOutMenu']").hide();
				console.log(res);
				loggedIn = true;
				resetAll();
				$("#accountsHomeScreen").show();
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

function processLogoutRequest() {
	username = $("#loginUsername").val();
	password = $("#loginPassword").val();

	loginRequest = {"username":"logout"};

	$.ajax({
		url: baseServerUrl + '/api/login',
		type: 'DELETE',
		data: JSON.stringify(loginRequest),
		dataType: 'json',
		contentType: "application/json",
		success:function(res){
			console.log(JSON.stringify(res));
			loggedIn = false;
			resetAll();
			initialize();
		},
		error:function(res, textStatus) {
			console.log(res.status);
			alert("There was a problem logging you out :(\nPlease try again.");
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
				$("#loginUsername").val("");
				$("#loginPassword").val("");
			
				$("[name='loggedOutMenu']").hide();
				$("[name='loggedInMenu']").show();
				$("#loginAlert").hide();
				loggedIn = true;
				resetAll();
				$("#accountsHomeScreen").show();
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
			console.log(res.status + '; ' + typeof res.status);
			if (res.status === "Success") {
				$("[name='loggedInMenu']").show();
				$("[name='loggedOutMenu']").hide();
				$("#registerAlert").hide();
				loggedIn = true;
				resetAll();
				$("#accountsHomeScreen").show();
			} else { // NOT SUCCESS
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
		error:function(res, textStatus) { // NOT SUCCESS
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