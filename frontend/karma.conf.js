module.exports = function(config) {
  config.set({
    basePath: '',
    frameworks: ['jasmine'],
    files: [
      'node_modules/angular/angular.js',
      'node_modules/angular-route/angular-route.js',
      'node_modules/angular-mocks/angular-mocks.js',
      'src/app/**/!(*.spec).js',
      'src/app/**/*.spec.js'
    ],
    exclude: [],
    preprocessors: {
      'src/app/**/!(*.spec).js': ['coverage']
    },
    reporters: ['progress', 'coverage'],
    port: 9876,
    colors: true,
    logLevel: config.LOG_INFO,
    autoWatch: false,
    browsers: ['ChromeHeadless'],
    singleRun: true,
    concurrency: Infinity,
    coverageReporter: {
      type: 'lcov',
      dir: 'coverage/'
    }
  });
};
