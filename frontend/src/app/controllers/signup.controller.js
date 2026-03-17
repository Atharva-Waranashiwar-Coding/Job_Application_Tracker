(function () {
  'use strict';

  angular.module('jobTrackerApp').controller('SignupController', SignupController);

  function SignupController($location, apiService, authService) {
    const vm = this;
    vm.username = '';
    vm.password = '';
    vm.confirmPassword = '';
    vm.displayName = '';
    vm.error = null;

    vm.signup = function () {
      vm.error = null;

      if (!vm.username || !vm.password || !vm.displayName) {
        vm.error = 'All fields are required.';
        return;
      }

      if (vm.password !== vm.confirmPassword) {
        vm.error = 'Passwords do not match.';
        return;
      }

      apiService
        .registerUser({
          username: vm.username,
          password: vm.password,
          displayName: vm.displayName,
        })
        .then(function () {
          // Auto-login after sign up
          return apiService.login({ username: vm.username, password: vm.password });
        })
        .then(function (res) {
          if (!authService.login(res.data)) {
            vm.error = 'Account created, but auto-login failed due to an invalid token response.';
            return;
          }
          $location.path('/applications');
        })
        .catch(function (err) {
          vm.error = (err.data && (err.data.error || err.data.message)) || 'Unable to register. Please try again.';
        });
    };
  }

  SignupController.$inject = ['$location', 'apiService', 'authService'];
})();
