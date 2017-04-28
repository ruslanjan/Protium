/*
 * Copyright (C) 2017 - Protium - Ussoltsev Dmitry, Jankurazov Ruslan - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

/*
 In ${PACKAGE_NAME}
 From temporary-protium
 */

var AuthDialogController = function ($scope, $mdDialog, $http) {
	$scope.reset = function () {
		$scope.user = {};
	};

	$scope.commit = function () {
		var data = {
			login   : $scope.user.login,
			password: $scope.user.password
		};

		$http
			.post("http://localhost:8081/api/pauth/auth/?", data)
			.success = function (data) {
			console.log(data);
		}
	};

	$scope.close = function () {
		$mdDialog.hide();
	}
};