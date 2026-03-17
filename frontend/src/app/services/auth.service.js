(function () {
  'use strict';

  angular.module('jobTrackerApp').factory('authService', authService);

  function authService($window) {
    const storageKey = 'jobTrackerAuth';

    let auth = load();

    function load() {
      const raw = $window.localStorage.getItem(storageKey);
      if (!raw) {
        return { token: null, username: null, roles: [] };
      }
      try {
        const stored = JSON.parse(raw);
        const token = stored && stored.token;
        const claims = parseJwt(token);
        if (!claims || isExpired(claims)) {
          return { token: null, username: null, roles: [] };
        }
        return {
          token,
          username: claims.sub || null,
          roles: Array.isArray(claims.roles) ? claims.roles : [],
        };
      } catch (e) {
        return { token: null, username: null, roles: [] };
      }
    }

    function save() {
      $window.localStorage.setItem(storageKey, JSON.stringify(auth));
    }

    function parseJwt(token) {
      if (typeof token !== 'string') {
        return null;
      }
      try {
        const payload = token.split('.')[1];
        const decoded = atob(payload.replace(/-/g, '+').replace(/_/g, '/'));
        return JSON.parse(decodeURIComponent(escape(decoded)));
      } catch (e) {
        return null;
      }
    }

    function isExpired(claims) {
      if (!claims || typeof claims.exp !== 'number') {
        return true;
      }
      return claims.exp * 1000 <= Date.now();
    }

    function setToken(token) {
      const claims = parseJwt(token);
      if (!claims || isExpired(claims)) {
        logout();
        return false;
      }

      auth.token = token;
      auth.username = claims.sub || null;
      auth.roles = Array.isArray(claims.roles) ? claims.roles : [];
      save();
      return true;
    }

    function extractToken(payload) {
      if (typeof payload === 'string') {
        return payload;
      }
      if (payload && typeof payload === 'object') {
        return payload.token || payload.accessToken || null;
      }
      return null;
    }

    function login(payload) {
      const token = extractToken(payload);
      if (!token) {
        return false;
      }
      return setToken(token);
    }

    function logout() {
      auth = { token: null, username: null, roles: [] };
      save();
    }

    function isAuthenticated() {
      if (!auth.token) {
        return false;
      }
      const claims = parseJwt(auth.token);
      if (!claims || isExpired(claims)) {
        logout();
        return false;
      }
      return true;
    }

    function isAdmin() {
      return auth.roles && auth.roles.includes('ADMIN');
    }

    function getAuthHeader() {
      return isAuthenticated() ? 'Bearer ' + auth.token : null;
    }

    return {
      login,
      logout,
      isAuthenticated,
      isAdmin,
      getAuthHeader,
      getUsername: () => auth.username,
    };
  }

  authService.$inject = ['$window'];
})();
