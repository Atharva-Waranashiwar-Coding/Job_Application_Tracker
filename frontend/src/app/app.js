(function () {
  'use strict';

  angular
    .module('jobTrackerApp', ['ngRoute'])
    .config(configure)
    .run(runBlock);

  function configure($routeProvider, $httpProvider, $locationProvider) {
    // Remove the default "!" hash prefix so routes look like #/login instead of #!/login.
    $locationProvider.hashPrefix('');

    $routeProvider
      .when('/login', {
        templateUrl: 'app/views/login.html',
        controller: 'LoginController',
        controllerAs: 'vm',
      })
      .when('/signup', {
        templateUrl: 'app/views/signup.html',
        controller: 'SignupController',
        controllerAs: 'vm',
      })
      .when('/forgot-password', {
        templateUrl: 'app/views/forgot-password.html',
        controller: 'ForgotPasswordController',
        controllerAs: 'vm',
      })
      .when('/applications', {
        templateUrl: 'app/views/applications.html',
        controller: 'ApplicationsController',
        controllerAs: 'vm',
      })
      .when('/applications/new', {
        templateUrl: 'app/views/application-form.html',
        controller: 'ApplicationFormController',
        controllerAs: 'vm',
      })
      .when('/applications/:id/edit', {
        templateUrl: 'app/views/application-form.html',
        controller: 'ApplicationFormController',
        controllerAs: 'vm',
      })
      .when('/kanban', {
        templateUrl: 'app/views/kanban.html',
        controller: 'KanbanController',
        controllerAs: 'vm',
      })
      .when('/activity', {
        templateUrl: 'app/views/activity.html',
        controller: 'ActivityController',
        controllerAs: 'vm',
      })
      .when('/admin/users', {
        templateUrl: 'app/views/admin.html',
        controller: 'AdminController',
        controllerAs: 'vm',
      })
      .otherwise({ redirectTo: '/applications' });

    $httpProvider.interceptors.push('authInterceptor');
  }

  function runBlock($rootScope, $location, authService) {
    $rootScope.$on('$routeChangeStart', function (event, next) {
      const openRoutes = ['/login', '/signup', '/forgot-password'];
      if (next.$$route && !openRoutes.includes(next.$$route.originalPath) && !authService.isAuthenticated()) {
        event.preventDefault();
        $location.path('/login');
      }
    });
  }

  configure.$inject = ['$routeProvider', '$httpProvider', '$locationProvider'];
  runBlock.$inject = ['$rootScope', '$location', 'authService'];
})();
