(function () {
  'use strict';

  angular.module('jobTrackerApp').controller('AdminController', AdminController);

  function AdminController(apiService) {
    const vm = this;
    vm.users = [];
    vm.loading = false;

    vm.loadUsers = function () {
      vm.loading = true;
      apiService
        .listUsers()
        .then(function (res) {
          vm.users = res.data;
        })
        .finally(function () {
          vm.loading = false;
        });
    };

    vm.loadUsers();
  }

  AdminController.$inject = ['apiService'];
})();
