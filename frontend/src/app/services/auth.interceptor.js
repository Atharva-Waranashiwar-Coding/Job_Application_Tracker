(function () {
  'use strict';

  angular
    .module('jobTrackerApp')
    .factory('authInterceptor', authInterceptor);

  function authInterceptor($q, $location, authService) {
    return {
      request: function (config) {
        const header = authService.getAuthHeader();
        if (header) {
          config.headers.Authorization = header;
        }
        return config;
      },
      responseError: function (rejection) {
        const url = (rejection.config && rejection.config.url) || '';
        const isAuthEndpoint = url.includes('/api/auth/login') || url.includes('/api/auth/register');
        if ((rejection.status === 401 || rejection.status === 403) && !isAuthEndpoint) {
          authService.logout();
          $location.path('/login');
        }
        return $q.reject(rejection);
      },
    };
  }

  authInterceptor.$inject = ['$q', '$location', 'authService'];
})();
