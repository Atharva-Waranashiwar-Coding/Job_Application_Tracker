(function () {
  'use strict';

  angular.module('jobTrackerApp').controller('ApplicationsController', ApplicationsController);

  function ApplicationsController($location, apiService, authService, toastService) {
    const vm = this;

    vm.items = [];
    vm.stages = [];
    vm.counts = {};
    vm.loading = false;

    vm.page = 0;
    vm.size = 12;
    vm.total = 0;
    vm.hasNext = false;

    vm.includeDeleted = false;
    vm.query = {
      q: '',
      stageId: '',
      location: '',
      workMode: '',
      sponsorship: '',
      source: '',
      sortBy: 'STAGE_UPDATED',
      sortDir: 'DESC',
    };

    vm.load = function () {
      vm.loading = true;

      const params = {
        page: vm.page,
        size: vm.size,
        q: vm.query.q || undefined,
        stageId: vm.query.stageId || undefined,
        location: vm.query.location || undefined,
        workMode: vm.query.workMode || undefined,
        sponsorship: vm.query.sponsorship === '' ? undefined : vm.query.sponsorship,
        source: vm.query.source || undefined,
        sortBy: vm.query.sortBy,
        sortDir: vm.query.sortDir,
        includeDeleted: vm.includeDeleted,
      };

      apiService
        .listApplications(params)
        .then(function (res) {
          vm.items = res.data.items || [];
          vm.total = res.data.total || 0;
          vm.page = res.data.page || 0;
          vm.size = res.data.size || vm.size;
          vm.hasNext = !!res.data.hasNext;
          return apiService.getStatusCounts();
        })
        .then(function (res) {
          vm.counts = res.data || {};
        })
        .catch(function (err) {
          toastService.error(extractError(err) || 'Failed to load applications.');
        })
        .finally(function () {
          vm.loading = false;
        });
    };

    vm.loadStages = function () {
      apiService
        .listStages(false)
        .then(function (res) {
          vm.stages = res.data || [];
        })
        .catch(function () {
          vm.stages = [];
        });
    };

    vm.search = function () {
      vm.page = 0;
      vm.load();
    };

    vm.resetFilters = function () {
      vm.query = {
        q: '',
        stageId: '',
        location: '',
        workMode: '',
        sponsorship: '',
        source: '',
        sortBy: 'STAGE_UPDATED',
        sortDir: 'DESC',
      };
      vm.includeDeleted = false;
      vm.page = 0;
      vm.load();
    };

    vm.nextPage = function () {
      if (!vm.hasNext) {
        return;
      }
      vm.page += 1;
      vm.load();
    };

    vm.prevPage = function () {
      if (vm.page === 0) {
        return;
      }
      vm.page -= 1;
      vm.load();
    };

    vm.open = function (app) {
      $location.path('/applications/' + app.id);
    };

    vm.edit = function (app) {
      $location.path('/applications/' + app.id + '/edit');
    };

    vm.delete = function (app) {
      if (!confirm('Move "' + app.company + '" to deleted items?')) {
        return;
      }
      apiService
        .deleteApplication(app.id)
        .then(function () {
          toastService.success('Application moved to deleted items.');
          vm.load();
        })
        .catch(function (err) {
          toastService.error(extractError(err) || 'Failed to delete application.');
        });
    };

    vm.restore = function (app) {
      apiService
        .restoreApplication(app.id)
        .then(function () {
          toastService.success('Application restored.');
          vm.load();
        })
        .catch(function (err) {
          toastService.error(extractError(err) || 'Failed to restore application.');
        });
    };

    vm.stageClass = function (app) {
      const normalized = ((app.stage && app.stage.name) || '').toLowerCase();
      if (normalized === 'applied') return 'status-applied';
      if (normalized === 'interviewing') return 'status-interviewing';
      if (normalized === 'offer') return 'status-offer';
      if (normalized === 'rejected') return 'status-rejected';
      if (normalized === 'hired') return 'status-hired';
      return 'status-default';
    };

    vm.priorityClass = function (priority) {
      const value = (priority || '').toUpperCase();
      if (value === 'HIGH') return 'priority-high';
      if (value === 'MEDIUM') return 'priority-medium';
      return 'priority-low';
    };

    vm.logout = function () {
      authService.logout();
      $location.path('/login');
    };

    function extractError(err) {
      if (!err || !err.data) {
        return '';
      }
      return err.data.error || '';
    }

    vm.loadStages();
    vm.load();
  }

  ApplicationsController.$inject = ['$location', 'apiService', 'authService', 'toastService'];
})();
