(function () {
  'use strict';

  angular
    .module('jobTrackerApp')
    .factory('apiService', apiService);

  function apiService($http) {
    const base = '/api';

    function listApplications(params) {
      return $http.get(base + '/applications', { params: params || {} });
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

    function moveApplicationStage(id, payload) {
      return $http.patch(base + '/applications/' + id + '/stage', payload);
    }

    function deleteApplication(id) {
      return $http.delete(base + '/applications/' + id);
    }

    function restoreApplication(id) {
      return $http.post(base + '/applications/' + id + '/restore');
    }

    function getStatusCounts() {
      return $http.get(base + '/applications/counts');
    }

    function getStageHistory(applicationId) {
      return $http.get(base + '/applications/' + applicationId + '/stage-history');
    }

    function listStages(includeArchived) {
      return $http.get(base + '/stages', { params: { includeArchived: !!includeArchived } });
    }

    function createStage(payload) {
      return $http.post(base + '/stages', payload);
    }

    function updateStage(id, payload) {
      return $http.put(base + '/stages/' + id, payload);
    }

    function archiveStage(id) {
      return $http.delete(base + '/stages/' + id);
    }

    function listInterviews(applicationId) {
      return $http.get(base + '/applications/' + applicationId + '/interviews');
    }

    function createInterview(applicationId, payload) {
      return $http.post(base + '/applications/' + applicationId + '/interviews', payload);
    }

    function updateInterview(applicationId, interviewId, payload) {
      return $http.put(base + '/applications/' + applicationId + '/interviews/' + interviewId, payload);
    }

    function deleteInterview(applicationId, interviewId) {
      return $http.delete(base + '/applications/' + applicationId + '/interviews/' + interviewId);
    }

    function listReminders() {
      return $http.get(base + '/reminders');
    }

    function listUpcomingReminders() {
      return $http.get(base + '/reminders/upcoming');
    }

    function listOverdueReminders() {
      return $http.get(base + '/reminders/overdue');
    }

    function createReminder(payload) {
      return $http.post(base + '/reminders', payload);
    }

    function updateReminder(id, payload) {
      return $http.put(base + '/reminders/' + id, payload);
    }

    function deleteReminder(id) {
      return $http.delete(base + '/reminders/' + id);
    }

    function getDocuments(applicationId) {
      return $http.get(base + '/applications/' + applicationId + '/documents');
    }

    function updateDocuments(applicationId, payload) {
      return $http.put(base + '/applications/' + applicationId + '/documents', payload);
    }

    function listNotes(applicationId) {
      return $http.get(base + '/applications/' + applicationId + '/notes');
    }

    function upsertNote(applicationId, payload) {
      return $http.put(base + '/applications/' + applicationId + '/notes', payload);
    }

    function listContacts(applicationId) {
      return $http.get(base + '/applications/' + applicationId + '/contacts');
    }

    function createContact(applicationId, payload) {
      return $http.post(base + '/applications/' + applicationId + '/contacts', payload);
    }

    function updateContact(applicationId, contactId, payload) {
      return $http.put(base + '/applications/' + applicationId + '/contacts/' + contactId, payload);
    }

    function deleteContact(applicationId, contactId) {
      return $http.delete(base + '/applications/' + applicationId + '/contacts/' + contactId);
    }

    function listOffers(applicationId) {
      return $http.get(base + '/applications/' + applicationId + '/offers');
    }

    function createOffer(applicationId, payload) {
      return $http.post(base + '/applications/' + applicationId + '/offers', payload);
    }

    function updateOffer(applicationId, offerId, payload) {
      return $http.put(base + '/applications/' + applicationId + '/offers/' + offerId, payload);
    }

    function deleteOffer(applicationId, offerId) {
      return $http.delete(base + '/applications/' + applicationId + '/offers/' + offerId);
    }

    function getDashboardSummary() {
      return $http.get(base + '/dashboard/summary');
    }

    function getDashboardTrends(months) {
      return $http.get(base + '/dashboard/trends', { params: { months: months || 12 } });
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
      moveApplicationStage,
      deleteApplication,
      restoreApplication,
      getStatusCounts,
      getStageHistory,
      listStages,
      createStage,
      updateStage,
      archiveStage,
      listInterviews,
      createInterview,
      updateInterview,
      deleteInterview,
      listReminders,
      listUpcomingReminders,
      listOverdueReminders,
      createReminder,
      updateReminder,
      deleteReminder,
      getDocuments,
      updateDocuments,
      listNotes,
      upsertNote,
      listContacts,
      createContact,
      updateContact,
      deleteContact,
      listOffers,
      createOffer,
      updateOffer,
      deleteOffer,
      getDashboardSummary,
      getDashboardTrends,
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
