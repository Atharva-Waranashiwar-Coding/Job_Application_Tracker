describe('ApplicationsController', function () {
  beforeEach(module('jobTrackerApp'));

  var $controller;
  var $q;
  var $rootScope;
  var $httpBackend;

  beforeEach(inject(function (_$controller_, _$q_, _$rootScope_, _$httpBackend_) {
    $controller = _$controller_;
    $q = _$q_;
    $rootScope = _$rootScope_;
    $httpBackend = _$httpBackend_;
    $httpBackend.whenGET(/app\/views\/.*/).respond(200, '');
  }));

  it('loads paged applications and status counts', function () {
    var deferredList = $q.defer();
    var deferredCounts = $q.defer();
    var deferredStages = $q.defer();

    var apiService = {
      listApplications: function () {
        return deferredList.promise;
      },
      getStatusCounts: function () {
        return deferredCounts.promise;
      },
      listStages: function () {
        return deferredStages.promise;
      },
      deleteApplication: function () {
        return $q.resolve();
      },
      restoreApplication: function () {
        return $q.resolve();
      },
    };

    var vm = $controller('ApplicationsController', {
      $location: { path: function () {} },
      apiService: apiService,
      authService: { logout: function () {} },
      toastService: { success: function () {}, error: function () {} },
    });

    deferredStages.resolve({ data: [{ id: 1, name: 'Applied' }] });
    deferredList.resolve({
      data: {
        items: [{ id: 10, company: 'Acme', role: 'Engineer', stage: { id: 1, name: 'Applied' }, priority: 'MEDIUM' }],
        total: 1,
        page: 0,
        size: 12,
        hasNext: false,
      },
    });
    deferredCounts.resolve({ data: { Applied: 1 } });
    $rootScope.$digest();
    $httpBackend.flush();

    expect(vm.items.length).toBe(1);
    expect(vm.total).toBe(1);
    expect(vm.counts.Applied).toBe(1);
    expect(vm.stageClass(vm.items[0])).toBe('status-applied');
  });

  afterEach(function () {
    $httpBackend.verifyNoOutstandingExpectation();
    $httpBackend.verifyNoOutstandingRequest();
  });
});
