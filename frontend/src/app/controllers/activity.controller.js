(function () {
  'use strict';

  angular.module('jobTrackerApp').controller('ActivityController', ActivityController);

  function ActivityController(apiService) {
    const vm = this;
    vm.logs = [];
    vm.loading = false;

    vm.loading = true;
    apiService
      .getActivityLogs()
      .then(function (res) {
        vm.logs = res.data;
      })
      .finally(function () {
        vm.loading = false;
      });
  }

  ActivityController.$inject = ['apiService'];
})();
