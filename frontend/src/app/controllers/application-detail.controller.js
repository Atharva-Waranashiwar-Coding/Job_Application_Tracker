(function () {
  'use strict';

  angular.module('jobTrackerApp').controller('ApplicationDetailController', ApplicationDetailController);

  function ApplicationDetailController($routeParams, $location, $q, apiService, toastService) {
    const vm = this;

    vm.applicationId = Number($routeParams.id);
    vm.loading = false;
    vm.tab = 'interviews';

    vm.application = null;
    vm.stages = [];
    vm.stageHistory = [];

    vm.interviews = [];
    vm.reminders = [];
    vm.documents = {};
    vm.notes = [];
    vm.contacts = [];
    vm.offers = [];

    vm.noteCategories = [
      'COMPANY_RESEARCH',
      'RECRUITER_NOTES',
      'INTERVIEW_PREP',
      'BEHAVIORAL_STORIES',
      'WHY_THIS_COMPANY',
      'MISCELLANEOUS',
    ];

    vm.stageMove = { toStageId: '', reason: '' };

    vm.interviewForm = defaultInterviewForm();
    vm.reminderForm = defaultReminderForm();
    vm.noteForm = { category: 'COMPANY_RESEARCH', content: '' };
    vm.contactForm = defaultContactForm();
    vm.offerForm = defaultOfferForm();

    vm.setTab = function (tab) {
      vm.tab = tab;
    };

    vm.back = function () {
      $location.path('/applications');
    };

    vm.refresh = function () {
      vm.loading = true;
      $q
        .all([
          apiService.getApplication(vm.applicationId),
          apiService.getStageHistory(vm.applicationId),
          apiService.listStages(false),
          apiService.listInterviews(vm.applicationId),
          apiService.listReminders(),
          apiService.getDocuments(vm.applicationId),
          apiService.listNotes(vm.applicationId),
          apiService.listContacts(vm.applicationId),
          apiService.listOffers(vm.applicationId),
        ])
        .then(function (responses) {
          vm.application = responses[0].data;
          vm.stageHistory = responses[1].data || [];
          vm.stages = responses[2].data || [];
          vm.interviews = responses[3].data || [];
          vm.reminders = (responses[4].data || []).filter(function (item) {
            return item.applicationId === vm.applicationId;
          });
          vm.documents = responses[5].data || {};
          vm.notes = responses[6].data || [];
          vm.contacts = responses[7].data || [];
          vm.offers = responses[8].data || [];

          vm.stageMove.toStageId = vm.application && vm.application.stage ? String(vm.application.stage.id) : '';
        })
        .catch(function (err) {
          toastService.error(extractError(err) || 'Unable to load application workspace.');
          $location.path('/applications');
        })
        .finally(function () {
          vm.loading = false;
        });
    };

    vm.moveStage = function () {
      if (!vm.stageMove.toStageId) {
        return;
      }

      apiService
        .moveApplicationStage(vm.applicationId, {
          toStageId: Number(vm.stageMove.toStageId),
          reason: vm.stageMove.reason || 'Moved from details workspace',
        })
        .then(function (res) {
          vm.application = res.data;
          vm.stageMove.reason = '';
          toastService.success('Stage updated.');
          return apiService.getStageHistory(vm.applicationId);
        })
        .then(function (res) {
          vm.stageHistory = res.data || [];
        })
        .catch(function (err) {
          toastService.error(extractError(err) || 'Unable to update stage.');
        });
    };

    vm.saveInterview = function () {
      const payload = angular.copy(vm.interviewForm);
      payload.scheduledAt = toInstant(payload.scheduledAt);
      const request = payload.id
        ? apiService.updateInterview(vm.applicationId, payload.id, payload)
        : apiService.createInterview(vm.applicationId, payload);

      request
        .then(function () {
          toastService.success(payload.id ? 'Interview updated.' : 'Interview added.');
          vm.interviewForm = defaultInterviewForm();
          return apiService.listInterviews(vm.applicationId);
        })
        .then(function (res) {
          vm.interviews = res.data || [];
        })
        .catch(function (err) {
          toastService.error(extractError(err) || 'Unable to save interview.');
        });
    };

    vm.editInterview = function (item) {
      vm.interviewForm = {
        id: item.id,
        roundName: item.roundName,
        roundType: item.roundType,
        scheduledAt: toLocalDateTime(item.scheduledAt),
        interviewerName: item.interviewerName,
        notes: item.notes,
        feedback: item.feedback,
        resultStatus: item.resultStatus,
      };
      vm.tab = 'interviews';
    };

    vm.deleteInterview = function (item) {
      if (!confirm('Delete this interview round?')) {
        return;
      }
      apiService
        .deleteInterview(vm.applicationId, item.id)
        .then(function () {
          toastService.success('Interview deleted.');
          vm.interviews = vm.interviews.filter(function (row) {
            return row.id !== item.id;
          });
        })
        .catch(function (err) {
          toastService.error(extractError(err) || 'Unable to delete interview.');
        });
    };

    vm.saveReminder = function () {
      const payload = angular.copy(vm.reminderForm);
      payload.applicationId = vm.applicationId;
      payload.dueAt = toInstant(payload.dueAt);

      const request = payload.id
        ? apiService.updateReminder(payload.id, payload)
        : apiService.createReminder(payload);

      request
        .then(function () {
          toastService.success(payload.id ? 'Reminder updated.' : 'Reminder created.');
          vm.reminderForm = defaultReminderForm();
          return apiService.listReminders();
        })
        .then(function (res) {
          vm.reminders = (res.data || []).filter(function (item) {
            return item.applicationId === vm.applicationId;
          });
        })
        .catch(function (err) {
          toastService.error(extractError(err) || 'Unable to save reminder.');
        });
    };

    vm.editReminder = function (item) {
      vm.reminderForm = {
        id: item.id,
        title: item.title,
        description: item.description,
        dueAt: toLocalDateTime(item.dueAt),
        status: item.status,
        reminderType: item.reminderType,
      };
      vm.tab = 'reminders';
    };

    vm.deleteReminder = function (item) {
      if (!confirm('Delete this reminder?')) {
        return;
      }
      apiService
        .deleteReminder(item.id)
        .then(function () {
          toastService.success('Reminder deleted.');
          vm.reminders = vm.reminders.filter(function (row) {
            return row.id !== item.id;
          });
        })
        .catch(function (err) {
          toastService.error(extractError(err) || 'Unable to delete reminder.');
        });
    };

    vm.saveDocuments = function () {
      apiService
        .updateDocuments(vm.applicationId, vm.documents)
        .then(function (res) {
          vm.documents = res.data || {};
          toastService.success('Documents saved.');
        })
        .catch(function (err) {
          toastService.error(extractError(err) || 'Unable to save document references.');
        });
    };

    vm.editNote = function (item) {
      vm.noteForm = {
        category: item.category,
        content: item.content || '',
      };
      vm.tab = 'notes';
    };

    vm.saveNote = function () {
      apiService
        .upsertNote(vm.applicationId, vm.noteForm)
        .then(function () {
          toastService.success('Note saved.');
          return apiService.listNotes(vm.applicationId);
        })
        .then(function (res) {
          vm.notes = res.data || [];
        })
        .catch(function (err) {
          toastService.error(extractError(err) || 'Unable to save note.');
        });
    };

    vm.saveContact = function () {
      const payload = angular.copy(vm.contactForm);
      const request = payload.id
        ? apiService.updateContact(vm.applicationId, payload.id, payload)
        : apiService.createContact(vm.applicationId, payload);

      request
        .then(function () {
          toastService.success(payload.id ? 'Contact updated.' : 'Contact created.');
          vm.contactForm = defaultContactForm();
          return apiService.listContacts(vm.applicationId);
        })
        .then(function (res) {
          vm.contacts = res.data || [];
        })
        .catch(function (err) {
          toastService.error(extractError(err) || 'Unable to save contact.');
        });
    };

    vm.editContact = function (item) {
      vm.contactForm = angular.copy(item);
      vm.tab = 'contacts';
    };

    vm.deleteContact = function (item) {
      if (!confirm('Delete this contact?')) {
        return;
      }
      apiService
        .deleteContact(vm.applicationId, item.id)
        .then(function () {
          toastService.success('Contact deleted.');
          vm.contacts = vm.contacts.filter(function (row) {
            return row.id !== item.id;
          });
        })
        .catch(function (err) {
          toastService.error(extractError(err) || 'Unable to delete contact.');
        });
    };

    vm.saveOffer = function () {
      const payload = angular.copy(vm.offerForm);
      payload.baseSalary = normalizeNumber(payload.baseSalary);
      payload.bonus = normalizeNumber(payload.bonus);
      payload.equity = normalizeNumber(payload.equity);
      const request = payload.id
        ? apiService.updateOffer(vm.applicationId, payload.id, payload)
        : apiService.createOffer(vm.applicationId, payload);

      request
        .then(function () {
          toastService.success(payload.id ? 'Offer updated.' : 'Offer created.');
          vm.offerForm = defaultOfferForm();
          return apiService.listOffers(vm.applicationId);
        })
        .then(function (res) {
          vm.offers = res.data || [];
        })
        .catch(function (err) {
          toastService.error(extractError(err) || 'Unable to save offer.');
        });
    };

    vm.editOffer = function (item) {
      vm.offerForm = angular.copy(item);
      vm.tab = 'offers';
    };

    vm.deleteOffer = function (item) {
      if (!confirm('Delete this offer?')) {
        return;
      }
      apiService
        .deleteOffer(vm.applicationId, item.id)
        .then(function () {
          toastService.success('Offer deleted.');
          vm.offers = vm.offers.filter(function (row) {
            return row.id !== item.id;
          });
        })
        .catch(function (err) {
          toastService.error(extractError(err) || 'Unable to delete offer.');
        });
    };

    function defaultInterviewForm() {
      return {
        roundName: '',
        roundType: 'TECHNICAL',
        scheduledAt: '',
        interviewerName: '',
        notes: '',
        feedback: '',
        resultStatus: 'SCHEDULED',
      };
    }

    function defaultReminderForm() {
      return {
        title: '',
        description: '',
        dueAt: '',
        status: 'PENDING',
        reminderType: 'CUSTOM',
      };
    }

    function defaultContactForm() {
      return {
        recruiterName: '',
        recruiterEmail: '',
        referralContactName: '',
        referralContactEmail: '',
        outreachDate: '',
        referralRequested: false,
        referralReceived: false,
        followUpNotes: '',
      };
    }

    function defaultOfferForm() {
      return {
        baseSalary: '',
        bonus: '',
        equity: '',
        currency: 'USD',
        location: '',
        offerDate: '',
        responseDeadline: '',
        decisionStatus: 'PENDING',
        notes: '',
      };
    }

    function extractError(err) {
      return err && err.data && err.data.error ? err.data.error : '';
    }

    function normalizeNumber(value) {
      if (value === null || value === undefined || value === '') {
        return null;
      }
      return Number(value);
    }

    function toLocalDateTime(value) {
      if (!value) {
        return '';
      }
      const date = new Date(value);
      if (Number.isNaN(date.getTime())) {
        return '';
      }
      const local = new Date(date.getTime() - date.getTimezoneOffset() * 60000);
      return local.toISOString().slice(0, 16);
    }

    function toInstant(value) {
      if (!value) {
        return null;
      }
      const date = new Date(value);
      if (Number.isNaN(date.getTime())) {
        return null;
      }
      return date.toISOString();
    }

    vm.refresh();
  }

  ApplicationDetailController.$inject = ['$routeParams', '$location', '$q', 'apiService', 'toastService'];
})();
