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
  }));

  it('should filter applications by text', function () {
    $httpBackend.whenGET(/app\/views\/.*/).respond(200, '');

    var deferredList = $q.defer();
    var deferredCounts = $q.defer();
    var apiService = {
      listApplications: function () { return deferredList.promise; },
      getStatusCounts: function () { return deferredCounts.promise; },
      deleteApplication: function () { return $q.resolve(); }
    };

    var vm = $controller('ApplicationsController', {
      $location: {},
      apiService: apiService,
      authService: { logout: function () {} }
    });

    deferredList.resolve({ data: [] });
    deferredCounts.resolve({ data: {} });
    $rootScope.$digest();
    $httpBackend.flush();

    vm.applications = [
      { company: 'Acme', role: 'Engineer', status: 'Applied' },
      { company: 'Beta', role: 'Designer', status: 'Interviewing' }
    ];

    vm.filterText = 'acme';
    expect(vm.matchesFilter(vm.applications[0])).toBe(true);
    expect(vm.matchesFilter(vm.applications[1])).toBe(false);
  });
});
