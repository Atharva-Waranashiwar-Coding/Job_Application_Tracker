(function () {
  'use strict';

  angular
    .module('jobTrackerApp')
    .factory('apiService', apiService);

  function apiService($http) {
    const base = '/api';

    function listApplications() {
      return $http.get(base + '/applications');
    }

    function getApplication(id) {
      return $http.get(base + '/applications/' + id);
    }

    function createApplication(payload) {
      return $http.post(base + '/applications', payload);
    }

    function updateApplication(id, payload) {
      return $http.put(base + '/applications/' + id, payload);
    }

    function deleteApplication(id) {
      return $http.delete(base + '/applications/' + id);
    }

    function getStatusCounts() {
      return $http.get(base + '/applications/counts');
    }

    function getActivityLogs() {
      return $http.get(base + '/activity');
    }

    function registerUser(payload) {
      return $http.post('/api/auth/register', payload);
    }

    function login(payload) {
      return $http.post('/api/auth/login', payload);
    }

    function forgotPassword(payload) {
      return $http.post('/api/auth/forgot-password', payload);
    }

    function resetPassword(payload) {
      return $http.post('/api/auth/reset-password', payload);
    }

    function listUsers() {
      return $http.get('/api/users');
    }

    return {
      listApplications,
      getApplication,
      createApplication,
      updateApplication,
      deleteApplication,
      getStatusCounts,
      getActivityLogs,
      registerUser,
      login,
      forgotPassword,
      resetPassword,
      listUsers,
    };
  }

  apiService.$inject = ['$http'];
})();
