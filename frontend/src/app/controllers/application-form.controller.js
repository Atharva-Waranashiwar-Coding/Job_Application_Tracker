(function () {
  'use strict';

  angular
    .module('jobTrackerApp')
    .controller('ApplicationFormController', ApplicationFormController);

  function ApplicationFormController($routeParams, $location, apiService) {
    const vm = this;
    vm.id = $routeParams.id;
    vm.form = {
      company: '',
      role: '',
      status: 'Applied',
      appliedAt: '',
      sourceUrl: '',
      notes: '',
    };

    vm.statuses = ['Applied', 'Interviewing', 'Offer', 'Rejected', 'Hired'];

    vm.save = function () {
      const payload = angular.copy(vm.form);
      if (vm.id) {
        apiService.updateApplication(vm.id, payload).then(() => $location.path('/applications'));
      } else {
        apiService.createApplication(payload).then(() => $location.path('/applications'));
      }
    };

    vm.cancel = function () {
      $location.path('/applications');
    };

    if (vm.id) {
      apiService.getApplication(vm.id).then(function (res) {
        vm.form = res.data;
      });
    }
  }

  ApplicationFormController.$inject = ['$routeParams', '$location', 'apiService'];
})();
