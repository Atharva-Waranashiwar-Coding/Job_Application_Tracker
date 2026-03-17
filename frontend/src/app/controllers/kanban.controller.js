(function () {
  'use strict';

  angular.module('jobTrackerApp').controller('KanbanController', KanbanController);

  function KanbanController(apiService, $location, $q, toastService) {
    const vm = this;
    vm.loading = false;
    vm.columns = [];
    vm.dragData = null;

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

      $q
        .all([
          apiService.listStages(false),
          apiService.listApplications({ page: 0, size: 500, sortBy: 'STAGE_UPDATED', sortDir: 'DESC' }),
        ])
        .then(function (responses) {
          const stages = responses[0].data || [];
          const apps = (responses[1].data && responses[1].data.items) || [];

          vm.columns = stages.map(function (stage) {
            return {
              stage: stage,
              items: [],
            };
          });

          apps.forEach(function (app) {
            const column = vm.columns.find(function (col) {
              return col.stage.id === (app.stage && app.stage.id);
            });
            if (column) {
              column.items.push(app);
            }
          });
        })
        .catch(function (err) {
          toastService.error(extractError(err) || 'Failed to load kanban board.');
        })
        .finally(function () {
          vm.loading = false;
        });
    };

    vm.handleDragStart = function (dragData) {
      vm.dragData = dragData;
    };

    vm.handleDrop = function (dragData, toStageId) {
      const data = dragData || vm.dragData;
      if (!data || !data.appId || !data.fromStageId || !toStageId) {
        return;
      }

      if (Number(data.fromStageId) === Number(toStageId)) {
        return;
      }

      const move = moveLocally(data.appId, data.fromStageId, toStageId);
      if (!move) {
        return;
      }

      apiService
        .moveApplicationStage(data.appId, {
          toStageId: Number(toStageId),
          reason: 'Moved in kanban board',
        })
        .then(function (res) {
          const updated = res.data;
          move.item.stage = updated.stage;
          move.item.stageEnteredAt = updated.stageEnteredAt;
          move.item.daysInCurrentStage = updated.daysInCurrentStage;
          move.item.stalled = updated.stalled;
          toastService.success('Stage updated.');
        })
        .catch(function (err) {
          toastService.error(extractError(err) || 'Unable to move card.');
          vm.load();
        });
    };

    vm.open = function (app) {
      $location.path('/applications/' + app.id);
    };

    vm.edit = function (app) {
      $location.path('/applications/' + app.id + '/edit');
    };

    function moveLocally(appId, fromStageId, toStageId) {
      const fromColumn = vm.columns.find(function (col) {
        return col.stage.id === Number(fromStageId);
      });
      const toColumn = vm.columns.find(function (col) {
        return col.stage.id === Number(toStageId);
      });
      if (!fromColumn || !toColumn) {
        return null;
      }

      const index = fromColumn.items.findIndex(function (item) {
        return item.id === Number(appId);
      });
      if (index < 0) {
        return null;
      }

      const item = fromColumn.items.splice(index, 1)[0];
      item.stage = toColumn.stage;
      toColumn.items.unshift(item);

      return { item: item };
    }

    function extractError(err) {
      return err && err.data && err.data.error ? err.data.error : '';
    }

    vm.load();
  }

  KanbanController.$inject = ['apiService', '$location', '$q', 'toastService'];
})();
