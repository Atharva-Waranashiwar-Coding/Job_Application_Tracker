(function () {
  'use strict';

  angular
    .module('jobTrackerApp')
    .factory('toastService', toastService);

  function toastService($rootScope, $timeout) {
    const toasts = [];

    function sync() {
      $rootScope.appToasts = toasts.slice();
    }

    function show(type, message, durationMs) {
      const toast = {
        id: Date.now() + Math.floor(Math.random() * 1000),
        type: type || 'info',
        message: message || '',
      };
      toasts.push(toast);
      sync();

      const duration = durationMs || 3200;
      $timeout(function () {
        remove(toast.id);
      }, duration);
    }

    function remove(id) {
      const index = toasts.findIndex(function (item) {
        return item.id === id;
      });
      if (index >= 0) {
        toasts.splice(index, 1);
        sync();
      }
    }

    function success(message, durationMs) {
      show('success', message, durationMs);
    }

    function error(message, durationMs) {
      show('error', message, durationMs || 5000);
    }

    function info(message, durationMs) {
      show('info', message, durationMs);
    }

    sync();

    return {
      show: show,
      success: success,
      error: error,
      info: info,
      remove: remove,
    };
  }

  toastService.$inject = ['$rootScope', '$timeout'];
})();
