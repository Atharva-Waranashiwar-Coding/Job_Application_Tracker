(function () {
  'use strict';

  angular
    .module('jobTrackerApp')
    .directive('kanbanDraggable', kanbanDraggable)
    .directive('kanbanDropzone', kanbanDropzone);

  function kanbanDraggable() {
    return {
      restrict: 'A',
      scope: {
        kanbanDraggable: '<',
        onDragStart: '&',
      },
      link: function (scope, element) {
        element.attr('draggable', true);

        function handleDragStart(event) {
          const payload = angular.copy(scope.kanbanDraggable || {});
          event.dataTransfer.setData('text/plain', JSON.stringify(payload));
          if (scope.onDragStart) {
            scope.$applyAsync(function () {
              scope.onDragStart({ dragData: payload });
            });
          }
        }

        element.on('dragstart', handleDragStart);

        scope.$on('$destroy', function () {
          element.off('dragstart', handleDragStart);
        });
      },
    };
  }

  function kanbanDropzone() {
    return {
      restrict: 'A',
      scope: {
        kanbanDropzone: '<',
        onDropCard: '&',
      },
      link: function (scope, element) {
        function handleDragOver(event) {
          event.preventDefault();
          element.addClass('drop-active');
        }

        function handleDragLeave() {
          element.removeClass('drop-active');
        }

        function handleDrop(event) {
          event.preventDefault();
          element.removeClass('drop-active');

          let dragData = null;
          try {
            dragData = JSON.parse(event.dataTransfer.getData('text/plain'));
          } catch (e) {
            dragData = null;
          }

          scope.$applyAsync(function () {
            scope.onDropCard({
              dragData: dragData,
              toStageId: scope.kanbanDropzone,
            });
          });
        }

        element.on('dragover', handleDragOver);
        element.on('dragleave', handleDragLeave);
        element.on('drop', handleDrop);

        scope.$on('$destroy', function () {
          element.off('dragover', handleDragOver);
          element.off('dragleave', handleDragLeave);
          element.off('drop', handleDrop);
        });
      },
    };
  }
})();
