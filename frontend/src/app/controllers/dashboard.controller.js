(function () {
  'use strict';

  angular.module('jobTrackerApp').controller('DashboardController', DashboardController);

  function DashboardController(apiService, toastService, $q) {
    const vm = this;
    vm.loading = false;
    vm.summary = null;
    vm.trends = { monthly: [], rejectionsByStage: [] };
    vm.upcoming = [];
    vm.overdue = [];

    vm.load = function () {
      vm.loading = true;

      $q.all([
        apiService.getDashboardSummary(),
        apiService.getDashboardTrends(12),
        apiService.listUpcomingReminders(),
        apiService.listOverdueReminders(),
      ])
        .then(function (responses) {
          vm.summary = responses[0].data;
          vm.trends = responses[1].data || { monthly: [], rejectionsByStage: [] };
          vm.upcoming = responses[2].data || [];
          vm.overdue = responses[3].data || [];
        })
        .catch(function (err) {
          toastService.error(extractError(err) || 'Failed to load dashboard.');
        })
        .finally(function () {
          vm.loading = false;
        });
    };

    vm.maxMonthlyApplications = function () {
      if (!vm.trends.monthly || vm.trends.monthly.length === 0) {
        return 1;
      }
      return Math.max.apply(
        null,
        vm.trends.monthly.map(function (point) {
          return point.applications;
        })
      ) || 1;
    };

    vm.maxRejectionsByStage = function () {
      if (!vm.trends.rejectionsByStage || vm.trends.rejectionsByStage.length === 0) {
        return 1;
      }
      return Math.max.apply(
        null,
        vm.trends.rejectionsByStage.map(function (point) {
          return point.count;
        })
      ) || 1;
    };

    function extractError(err) {
      return err && err.data && err.data.error ? err.data.error : '';
    }

    vm.load();
  }

  DashboardController.$inject = ['apiService', 'toastService', '$q'];
})();
