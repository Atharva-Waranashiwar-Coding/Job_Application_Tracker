(function () {
  'use strict';

  angular
    .module('jobTrackerApp')
    .controller('ApplicationFormController', ApplicationFormController);

  function ApplicationFormController($routeParams, $location, apiService, toastService) {
    const vm = this;
    vm.id = $routeParams.id;
    vm.loading = false;
    vm.stages = [];

    vm.form = {
      company: '',
      role: '',
      stageId: '',
      priority: 'MEDIUM',
      appliedAt: '',
      sourceUrl: '',
      notes: '',
      location: '',
      workMode: '',
      employmentType: '',
      salaryRange: '',
      sponsorshipAvailable: null,
      applicationSource: '',
      applicationDeadline: '',
      postingUrl: '',
      jobDescription: '',
    };

    vm.workModes = ['REMOTE', 'HYBRID', 'ONSITE'];
    vm.employmentTypes = ['FULL_TIME', 'PART_TIME', 'CONTRACT', 'INTERN', 'TEMPORARY'];
    vm.priorities = ['LOW', 'MEDIUM', 'HIGH'];

    vm.save = function () {
      const payload = angular.copy(vm.form);
      payload.stageId = payload.stageId ? Number(payload.stageId) : null;
      payload.sponsorshipAvailable = payload.sponsorshipAvailable === null || payload.sponsorshipAvailable === ''
        ? null
        : payload.sponsorshipAvailable;

      const action = vm.id
        ? apiService.updateApplication(vm.id, payload)
        : apiService.createApplication(payload);

      action
        .then(function () {
          toastService.success(vm.id ? 'Application updated.' : 'Application created.');
          $location.path('/applications');
        })
        .catch(function (err) {
          toastService.error(extractError(err) || 'Unable to save application.');
        });
    };

    vm.cancel = function () {
      $location.path('/applications');
    };

    vm.loadStages = function () {
      apiService.listStages(false).then(function (res) {
        vm.stages = res.data || [];
        if (!vm.id && vm.stages.length > 0) {
          vm.form.stageId = String(vm.stages[0].id);
        }
      });
    };

    function extractError(err) {
      return err && err.data && err.data.error ? err.data.error : '';
    }

    vm.loadStages();

    if (vm.id) {
      vm.loading = true;
      apiService
        .getApplication(vm.id)
        .then(function (res) {
          const app = res.data;
          vm.form.company = app.company || '';
          vm.form.role = app.role || '';
          vm.form.stageId = app.stage ? String(app.stage.id) : '';
          vm.form.priority = app.priority || 'MEDIUM';
          vm.form.appliedAt = app.appliedAt || '';
          vm.form.sourceUrl = app.sourceUrl || '';
          vm.form.notes = app.notes || '';
          vm.form.location = app.location || '';
          vm.form.workMode = app.workMode || '';
          vm.form.employmentType = app.employmentType || '';
          vm.form.salaryRange = app.salaryRange || '';
          vm.form.sponsorshipAvailable =
            app.sponsorshipAvailable === null || app.sponsorshipAvailable === undefined
              ? null
              : app.sponsorshipAvailable;
          vm.form.applicationSource = app.applicationSource || '';
          vm.form.applicationDeadline = app.applicationDeadline || '';
          vm.form.postingUrl = app.postingUrl || '';
          vm.form.jobDescription = app.jobDescription || '';
        })
        .catch(function (err) {
          toastService.error(extractError(err) || 'Unable to load application.');
          $location.path('/applications');
        })
        .finally(function () {
          vm.loading = false;
        });
    }
  }

  ApplicationFormController.$inject = ['$routeParams', '$location', 'apiService', 'toastService'];
})();
