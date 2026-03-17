(function () {
  'use strict';

  angular.module('jobTrackerApp').controller('ApplicationsController', ApplicationsController);

  function ApplicationsController($location, apiService, authService) {
    const vm = this;
    vm.applications = [];
    vm.filterText = '';
    vm.counts = {};
    vm.loading = false;

    vm.reload = function () {
      vm.loading = true;
      apiService
        .listApplications()
        .then(function (res) {
          vm.applications = res.data;
          return apiService.getStatusCounts();
        })
        .then(function (res) {
          vm.counts = res.data;
        })
        .finally(function () {
          vm.loading = false;
        });
    };

    vm.edit = function (app) {
      $location.path('/applications/' + app.id + '/edit');
    };

    vm.delete = function (app) {
      if (!confirm('Delete "' + app.company + '"?')) {
        return;
      }
      apiService.deleteApplication(app.id).then(vm.reload);
    };

    vm.logout = function () {
      authService.logout();
      $location.path('/login');
    };

    vm.matchesFilter = function (app) {
      const text = vm.filterText.toLowerCase();
      if (!text) {
        return true;
      }
      return (
        (app.company || '').toLowerCase().includes(text) ||
        (app.role || '').toLowerCase().includes(text) ||
        (app.status || '').toLowerCase().includes(text)
      );
    };

    vm.statusClass = function (status) {
      const normalized = (status || '').toLowerCase();
      if (normalized === 'applied') return 'status-applied';
      if (normalized === 'interviewing') return 'status-interviewing';
      if (normalized === 'offer') return 'status-offer';
      if (normalized === 'rejected') return 'status-rejected';
      if (normalized === 'hired') return 'status-hired';
      return 'status-default';
    };

    vm.reload();
  }

  ApplicationsController.$inject = ['$location', 'apiService', 'authService'];
})();
