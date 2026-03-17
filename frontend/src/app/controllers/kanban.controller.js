(function () {
  'use strict';

  angular.module('jobTrackerApp').controller('KanbanController', KanbanController);

  function KanbanController(apiService, $location) {
    const vm = this;
    vm.loading = false;
    vm.columns = [
      { name: 'Applied', items: [] },
      { name: 'Interviewing', items: [] },
      { name: 'Offer', items: [] },
      { name: 'Rejected', items: [] },
      { name: 'Hired', items: [] },
    ];

    vm.columnClass = function (name) {
      const normalized = (name || '').toLowerCase();
      if (normalized === 'applied') return 'column-applied';
      if (normalized === 'interviewing') return 'column-interviewing';
      if (normalized === 'offer') return 'column-offer';
      if (normalized === 'rejected') return 'column-rejected';
      if (normalized === 'hired') return 'column-hired';
      return 'column-default';
    };

    vm.load = function () {
      vm.loading = true;
      apiService
        .listApplications()
        .then(function (res) {
          const apps = res.data;
          vm.columns.forEach((col) => (col.items = []));
          apps.forEach((app) => {
            const col = vm.columns.find((c) => c.name === app.status);
            if (col) {
              col.items.push(app);
            }
          });
        })
        .finally(function () {
          vm.loading = false;
        });
    };

    vm.edit = function (app) {
      $location.path('/applications/' + app.id + '/edit');
    };

    vm.load();
  }

  KanbanController.$inject = ['apiService', '$location'];
})();
