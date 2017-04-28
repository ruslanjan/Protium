/*
 * Copyright (C) 2017 - Protium - Ussoltsev Dmitry, Jankurazov Ruslan - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

/*
 In ${PACKAGE_NAME}
 From temporary-protium
 */

var PAuthController = function ($scope, $mdDialog) {
	$scope.showAuthDialog = function ($event) {
		var $parent = angular.element(document.body);
		$mdDialog.show({
			               parent     : $parent,
			               targetEvent: $event,
			               templateUrl: "?static(html/dialogs/auth.html)",
			               controller : AuthDialogController
		               });
	}
};

