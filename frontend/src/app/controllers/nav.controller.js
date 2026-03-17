(function () {
  'use strict';

  angular.module('jobTrackerApp').controller('NavController', NavController);

  function NavController($location, authService) {
    const vm = this;

    vm.isAuthenticated = authService.isAuthenticated;
    vm.isAdmin = authService.isAdmin;
    vm.username = authService.getUsername;

    vm.logout = function () {
      authService.logout();
      $location.path('/login');
    };
  }

  NavController.$inject = ['$location', 'authService'];
})();
