(function () {
  'use strict';

  angular.module('jobTrackerApp').controller('ForgotPasswordController', ForgotPasswordController);

  function ForgotPasswordController($location, apiService) {
    const vm = this;
    vm.username = '';
    vm.message = null;
    vm.error = null;

    vm.sendReset = function () {
      vm.error = null;
      vm.message = null;

      if (!vm.username) {
        vm.error = 'Username is required.';
        return;
      }

      apiService
        .forgotPassword({ username: vm.username })
        .then(function (res) {
          vm.message = 'Password reset token generated. (In production this would be emailed.)';
          vm.token = res.data;
        })
        .catch(function (err) {
          vm.error = (err.data && (err.data.error || err.data.message)) || 'Unable to generate reset token.';
        });
    };

    vm.resetPassword = function () {
      vm.error = null;
      vm.message = null;

      if (!vm.token || !vm.newPassword) {
        vm.error = 'Token and new password are required.';
        return;
      }

      apiService
        .resetPassword({ token: vm.token, newPassword: vm.newPassword })
        .then(function () {
          vm.message = 'Password has been reset. You can now log in.';
          $location.path('/login');
        })
        .catch(function (err) {
          vm.error = (err.data && (err.data.error || err.data.message)) || 'Unable to reset password.';
        });
    };
  }

  ForgotPasswordController.$inject = ['$location', 'apiService'];
})();
