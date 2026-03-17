(function () {
  'use strict';

  angular.module('jobTrackerApp').controller('LoginController', LoginController);

  function LoginController($location, apiService, authService) {
    const vm = this;
    vm.username = '';
    vm.password = '';
    vm.error = null;

    vm.login = function () {
      if (!vm.username || !vm.password) {
        vm.error = 'Username and password are required.';
        return;
      }

      apiService
        .login({ username: vm.username, password: vm.password })
        .then(function (res) {
          if (!authService.login(res.data)) {
            vm.error = 'Login failed: invalid token received.';
            return;
          }
          $location.path('/applications');
        })
        .catch(function (err) {
          vm.error = (err.data && (err.data.error || err.data.message)) || 'Invalid username or password.';
        });
    };
  }

  LoginController.$inject = ['$location', 'apiService', 'authService'];
})();
