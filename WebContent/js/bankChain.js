var baseServerUrl = "http://localhost:8080/OnlineBankingSystem";
var loggedIn = false;
var currentUserId = "";

function resetAll() {
	// set the menu options based on login status
	if (loggedIn) {
		$("[name='loggedOutMenu']").hide();
		$("[name='loggedInMenu']").show();
	} else {
		console.log('resetAll(): not logged in')
		currentUserId = "";
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
		loadAndShowAccounts();
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

	$("#createNewAccountButton").click(function() {
		$("#createAccountModal").modal();
	});

	$("#createAccountSubmitButton").click(function() {
		processCreateAccount();
	});

	$("#depositSubmitButton").click(function() {
		processDeposit();		
	});

	$("#withdrawSubmitButton").click(function() {
		processWithdraw();		
	});

	$("#transferSubmitButton").click(function() {
		processTransfer();		
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
		data: null,
		dataType: 'json',
		contentType: "application/json",
		success:function(res){
			console.log(JSON.stringify(res));

			if (res && res.username && res.id) {
				console.log(res.status);
				$("[name='loggedInMenu']").show();
				$("[name='loggedOutMenu']").hide();
				console.log(res);
				loggedIn = true;
				currentUserId = res.id;
				loadAndShowAccounts();
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
			if (res.status == "Success") {
				console.log("Success");
				loggedIn = true;
				currentUserId = res.id;
				$("#loginUsername").val("");
				$("#loginPassword").val("");
				$("[name='loggedOutMenu']").hide();
				$("[name='loggedInMenu']").show();
				$("#loginAlert").hide();
				resetAll();
				loadAndShowAccounts();
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
		url: baseServerUrl + '/api/register',
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
				currentUserId = res.id;
				loadAndShowAccounts();
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

function loadAndShowAccounts() {
	console.log('loadAndShowAccounts(): currentUserId=' + currentUserId);
	$.ajax({
		url: baseServerUrl + '/api/users/' + currentUserId + '/accounts',
		type: 'GET',
		data: null, 
		dataType: 'json',
		contentType: "application/json",
		success:function(accounts){
			console.log(JSON.stringify(accounts));
			accountsList = $("#accountsList");
        	accountsList.empty();
			
			var html = "<div class='row alert-info'>";
			html += "<div class='col-md-2'><strong>Name</strong></div>";
			html += "<div class='col-md-2'><strong>Last Updated</strong></div>";
			html += "<div class='col-md-1'><strong>Balance</strong></div>";
			html += "</div>";
			$(html).appendTo(accountsList);
			
			var numOfAccounts = $(accounts).length;
			$(accounts).each(function() {
				var row = $("<div class='row'></div>");
				$("<div class='col-md-2'>" + this.name + "</div>").appendTo(row);
				$("<div class='col-md-2'>" + new Date(this.updatedOn).toLocaleString() + "</div>").appendTo(row);
				$("<div class='col-md-1'>" + this.balance + "</div>").appendTo(row);
				buttonDiv = $("<div class='col-md-2'></div>")
				
				var transactionsButton = $("<button accountId='" + this.id + "' accountName='" + this.name + "' class='btn btn-primary btn-link btn-sm'>View Transactions</button>")
				$(transactionsButton).click(function() {
					transactionsModalTitle = $("#transactionsModalTitle");
					transactionsModalTitle.html("Transactions for " + $(this).attr('accountName'));
					transactionsModalTitle.attr("accountId", $(this).attr('accountId'));
					$("#transactionsModal").modal();
					loadAndShowTransactions();
				})
				$(transactionsButton).appendTo(buttonDiv);
				buttonDiv.appendTo(row);

				buttonDiv = $("<div class='col-md-1'></div>")
				var depositButton = $("<button accountId='" + this.id + "' accountName='" + this.name + "' class='btn btn-primary btn-link btn-sm'>Deposit</button>")
				$(depositButton).click(function() {
					depositModalTitle = $("#depositModalTitle");
					depositModalTitle.html("Deposit into " + $(this).attr('accountName'));
					depositModalTitle.attr("accountId", $(this).attr('accountId'));
					$("#depositModal").modal();
				})
				$(depositButton).appendTo(buttonDiv);
				buttonDiv.appendTo(row);

				console.log('numOfAccounts = ' + numOfAccounts);

				if (parseFloat(this.balance) > 0) {
					buttonDiv = $("<div class='col-md-1'></div>")
					var withdrawButton = $("<button accountId='" + this.id + "' accountName='" + this.name + "' class='btn btn-primary btn-link btn-sm'>Withdraw</button>")
					$(withdrawButton).click(function() {
						withdrawModalTitle = $("#withdrawModalTitle");
						withdrawModalTitle.html("Withdraw from " + $(this).attr('accountName'));
						withdrawModalTitle.attr("accountId", $(this).attr('accountId'));
						$("#withdrawModal").modal();
					})
					$(withdrawButton).appendTo(buttonDiv);
					buttonDiv.appendTo(row);

					if (numOfAccounts > 1) {
						var currentAccountId = this.id;

						buttonDiv = $("<div class='col-md-1'></div>")
						var transferButton = $("<button accountId='" + this.id + "' accountName='" + this.name + "' class='btn btn-primary btn-link btn-sm'>Transfer Out</button>")
						$(transferButton).click(function() {
							transferModalTitle = $("#transferModalTitle");
							transferModalTitle.html("Transfer from " + $(this).attr('accountName'));
							transferModalTitle.attr("accountId", $(this).attr('accountId'));

							$.ajax({
								url: baseServerUrl + '/api/users/' + currentUserId + '/accounts',
								type: 'GET',
								data: null, 
								dataType: 'json',
								contentType: "application/json",
								success:function(accounts){
									toSelect = $('#transferToAccount');
									toSelect.empty();

									$(accounts).each(function() {
										if (this.id != currentAccountId) {
											var opt = $("<option accountToId='" + this.id + "'>" + this.name + "</option>");
											opt.appendTo(toSelect);
										}
									});
								}
							});
							$("#transferModal").modal();
						})
						$(transferButton).appendTo(buttonDiv);
						buttonDiv.appendTo(row);
					}
				}

				$(row).appendTo(accountsList);
			});
		},
		error:function(res, textStatus) {
			console.log(JSON.stringify(res));
			console.log(textStatus);
		}
	});

	resetAll();
	$("#accountsHomeScreen").show();
}

function processCreateAccount() {
	name = $("#createAccountName").val();

	accountRequest = {
		"name":name
	};

	$.ajax({
		url: baseServerUrl + '/api/users/' + currentUserId + '/accounts',
		type: 'POST',
		data: JSON.stringify(accountRequest),
		dataType: 'json',
		contentType: "application/json",
		success:function(res){
			console.log(JSON.stringify(res));
			console.log(res.status + '; ' + typeof res.status);
			if (res.status === "Success") {
				$('#createAccountModal').modal('toggle');
				loadAndShowAccounts();
			} else { // NOT SUCCESS
				console.log(JSON.stringify(res));
			}
		},
		error:function(res, textStatus) { // NOT SUCCESS
			console.log(JSON.stringify(res));
			console.log(textStatus);
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

	$("#createAccountName").val('');
}

function processDeposit() {
	accountId = $("#depositModalTitle").attr("accountid");
	amount = $("#depositModalAmount").val();

	transactionRequest = {
		"type":"Deposit",
		"amount":amount
	};

	$.ajax({
		url: baseServerUrl + '/api/users/' + currentUserId + '/accounts/' + accountId + '/transactions',
		type: 'POST',
		data: JSON.stringify(transactionRequest),
		dataType: 'json',
		contentType: "application/json",
		success:function(res){
			console.log(JSON.stringify(res));
			console.log(res.status + '; ' + typeof res.status);
			if (res.status === "Success") {
				$('#depositModal').modal('toggle');
				depositAlertDiv = $("#depositTransactionAlert");
				depositAlertDiv.hide();
				loadAndShowAccounts();
			} else { // NOT SUCCESS
				console.log(JSON.stringify(res));
				depositAlertDiv = $("#depositTransactionAlert");
				depositAlertDiv.empty();
				$('<span>' + res.status + '</span>').appendTo(depositAlertDiv);
				depositAlertDiv.show();				
			}
		},
		error:function(res, textStatus) { // NOT SUCCESS
			console.log(JSON.stringify(res));
			console.log(textStatus);
			depositAlertDiv = $("#depositTransactionAlert");
			depositAlertDiv.empty();
			$('<span>' + res.status + '</span>').appendTo(depositAlertDiv);
			depositAlertDiv.show();
		}
	});

	$("#depositModalAmount").val('');		
}

function processWithdraw() {
	accountId = $("#withdrawModalTitle").attr("accountid");
	amount = $("#withdrawModalAmount").val();

	transactionRequest = {
		"type":"Withdraw",
		"amount":parseFloat(amount) * -1
	};

	$.ajax({
		url: baseServerUrl + '/api/users/' + currentUserId + '/accounts/' + accountId + '/transactions',
		type: 'POST',
		data: JSON.stringify(transactionRequest),
		dataType: 'json',
		contentType: "application/json",
		success:function(res){
			console.log(JSON.stringify(res));
			console.log(res.status + '; ' + typeof res.status);
			if (res.status === "Success") {
				$('#withdrawModal').modal('toggle');
				withdrawAlertDiv = $("#withdrawTransactionAlert");
				withdrawAlertDiv.hide();
				loadAndShowAccounts();
			} else { // NOT SUCCESS
				console.log(JSON.stringify(res));
				withdrawAlertDiv = $("#withdrawTransactionAlert");
				withdrawAlertDiv.empty();
				$('<span>' + res.status + '</span>').appendTo(withdrawAlertDiv);
				withdrawAlertDiv.show();
			}
		},
		error:function(res, textStatus) { // NOT SUCCESS
			console.log(JSON.stringify(res));
			console.log(textStatus);
			withdrawAlertDiv = $("#withdrawTransactionAlert");
			withdrawAlertDiv.empty();
			$('<span>' + res.status + '</span>').appendTo(withdrawAlertDiv);
			withdrawAlertDiv.show();
		// 
		}
	});

	$("#withdrawModalAmount").val('');		
}

function processTransfer() {
	accountId = $("#transferModalTitle").attr("accountid");
	amount = $("#transferModalAmount").val();

	accountToId = $('#transferToAccount').find(":selected").attr('accountToId');
	console.log('accountToId=' + accountToId);

	transactionRequest = {
		"type":"TransferOut",
		"amount":parseFloat(amount) * -1
	};

	$.ajax({
		url: baseServerUrl + '/api/users/' + currentUserId + '/accounts/' + accountId + '/' + accountToId + '/transactions',
		type: 'POST',
		data: JSON.stringify(transactionRequest),
		dataType: 'json',
		contentType: "application/json",
		success:function(res){
			console.log(JSON.stringify(res));
			console.log(res.status + '; ' + typeof res.status);
			if (res.status === "Success") {
				$('#transferModal').modal('toggle');
				transferAlertDiv = $("#transferTransactionAlert");
				transferAlertDiv.hide();
				loadAndShowAccounts();
			} else { // NOT SUCCESS
				console.log(JSON.stringify(res));
				transferAlertDiv = $("#transferTransactionAlert");
				transferAlertDiv.empty();
				$('<span>' + res.status + '</span>').appendTo(transferAlertDiv);
				transferAlertDiv.show();
				}
		},
		error:function(res, textStatus) { // NOT SUCCESS
			console.log(JSON.stringify(res));
			console.log(textStatus);
			transferAlertDiv = $("#transferTransactionAlert");
			transferAlertDiv.empty();
			$('<span>' + res.status + '</span>').appendTo(transferAlertDiv);
			transferAlertDiv.show();
		}
	});

	$("#transferModalAmount").val('');		
}

function loadAndShowTransactions() {
	transactionsModalTitle = $("#transactionsModalTitle");
	accountId = transactionsModalTitle.attr("accountId");
	console.log(accountId);

	$.ajax({
		url: baseServerUrl + '/api/users/' + currentUserId + '/accounts/' + accountId,
		type: 'GET',
		data: { "id": accountId }, 
		dataType: 'json',
		success:function(account){
			console.log(JSON.stringify(account));
			transactionsList = $("#transactionsList");
        	transactionsList.empty();
			
			var html = "<div class='row alert-info'>";
			html += "<div class='col-md-3'><strong>Post Date</strong></div>";
			html += "<div class='col-md-2'><strong>Type</strong></div>";
			html += "<div class='col-md-1'><strong>Amount</strong></div>";
			html += "</div>";
			$(html).appendTo(transactionsList);
			
			$(account.transactions).each(function() {
				var row = $("<div class='row'></div>");
				$("<div class='col-md-3'>" + new Date(this.createdOn).toLocaleString() + "</div>").appendTo(row);
				$("<div class='col-md-2'>" + this.type + "</div>").appendTo(row);
				$("<div class='col-md-1'>" + this.amount + "</div>").appendTo(row);
				$(row).appendTo(transactionsList);
			});
		},
		error:function(res, textStatus) {
			console.log(JSON.stringify(res));
			console.log(textStatus);
		}
	});
}