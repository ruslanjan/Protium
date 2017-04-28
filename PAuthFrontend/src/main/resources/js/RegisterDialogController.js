/*
 * Copyright (C) 2017 - Protium - Ussoltsev Dmitry, Jankurazov Ruslan - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

/*
 In ${PACKAGE_NAME}
 From temporary-protium
 */

//noinspection JSUnusedGlobalSymbols,JSUnusedLocalSymbols
var RegisterDialogController = function ($scope, $mdDialog, $http) {

	$scope.user.repassword = $scope.user.repassword | undefined;

	$scope.reset = function () {
		$scope.user = {};
	};

	$scope.close = function () {
		$mdDialog.hide();
	};

	$scope.validateRepass = function () {
		//noinspection EqualityComparisonWithCoercionJS
		var isValid = ($scope.user.repassword) == ($scope.user.password);

		console.log("'" + ($scope.user.repassword) + "' == '" + ($scope.user.password) + "'");
		console.log("\t-> " + isValid);

		if(!isValid) {
			$scope.user.repassword.$dirty = true;
		}
	};
};
