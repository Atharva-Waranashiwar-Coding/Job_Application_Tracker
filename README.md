# Job Application Tracker

A full-stack job application tracking system built with:
- **Backend:** Java Spring Boot (REST API)
- **Frontend:** AngularJS (1.x) SPA
- **Database:** PostgreSQL (with Flyway migrations)
- **DevOps:** Docker + docker-compose, CI/CD pipeline to AWS (ECR + ECS Fargate)

> This repository is built from scratch as a learning project.

## Getting Started

1. Read the detailed developer guide in `README_DEV.md`.
2. Use `docker-compose up --build` for a local environment.

## Architecture Overview

- `backend/` - Spring Boot application
- `frontend/` - AngularJS single-page application
- `docker-compose.yml` - local dev stack (Postgres + backend + frontend)
- `.github/workflows/ci.yml` - CI/CD pipeline with tests, build, and deploy

## Where to change things

For common edits (new field, endpoint, UI page, status list), see the **Where to change things** section in `README_DEV.md`.
