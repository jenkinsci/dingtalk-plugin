# Repository Guidelines

## Project Structure & Module Organization
- `src/main/java` and `src/main/resources` contain the Jenkins plugin source and resources.
- `src/test/java` holds JUnit 5 tests.
- `docs/` contains the VitePress documentation site.
- `bin/` includes helper scripts such as `docs-release.sh`.

## Key Classes
- `io.jenkins.plugins.DingTalkRunListener`: listens to build lifecycle events and triggers notifications.
- `io.jenkins.plugins.DingTalkStep`: Pipeline step entry point (`dingtalk`) for scripted usage.
- `io.jenkins.plugins.DingTalkGlobalConfig`: global settings and robot registry.
- `io.jenkins.plugins.service.DingTalkService`: sender cache and message dispatch.

## Architecture Overview
- Jenkins global configuration lives in `DingTalkGlobalConfig`, with per-job settings in `DingTalkJobProperty`.
- Build/run events are handled by `DingTalkRunListener`, while pipeline usage is exposed via `DingTalkStep`.
- Message delivery flows through `DingTalkService` to `sdk/DingTalkSender`, which formats and posts to the DingTalk robot webhook.
- UI and permissions are wired through descriptors and `DingTalkPermissions` in `src/main/java/io/jenkins/plugins`.
- UI forms and help text live in Jelly/HTML under `src/main/resources/io/jenkins/plugins/**/config.jelly` and `help-*.html`.
- Localization is handled with `Messages.properties` and `Messages_zh_CN.properties` in `src/main/resources/io/jenkins/plugins`.

## Build, Test, and Development Commands
- `mvn hpi:run` runs the plugin in a local Jenkins for development (use the Maven tool window in IntelliJ as noted in `CONTRIBUTING.md`).
- `mvn test` runs the JUnit 5 test suite.
- `yarn` installs documentation dependencies for the `docs/` site.
- `yarn docs:dev` starts the VitePress docs dev server.
- `yarn docs:build` builds the static docs site.
- `yarn docs:serve` serves the built docs locally on port 5173.

## Coding Style & Naming Conventions
- Java code follows Google Java Style; use your IDE formatter with the Google style XML.
- Run the Alibaba Java Coding Guidelines inspections in IntelliJ.
- Use 4-space indentation for Java and keep class names in `UpperCamelCase`.
- The `spotless-maven-plugin` enforces sorted POM structure; keep `pom.xml` consistent.

## Repo-Specific Conventions
- Use IntelliJ with the Maven tool window to add `hpi:run` as a run configuration (see `CONTRIBUTING.md`).
- Prefer updating both global (`DingTalkGlobalConfig`) and job-level (`DingTalkJobProperty`) defaults when adding new config fields.
- Keep user-facing strings in `Messages.properties` and access them via `Messages.*` helpers (for example, `Messages.DingTalkPermissions_GroupTitle()`).
- When adding a new config field, update the matching `config.jelly`, `help-*.html`, and defaults (for example, `DingTalkJobPropertyDescriptor.getDefaultNotifierConfigs()`).
- UI resources follow the plugin class path, e.g., `DingTalkRobotConfig` -> `src/main/resources/io/jenkins/plugins/DingTalkRobotConfig/config.jelly`.

## Testing Guidelines
- Tests use JUnit 5 (`org.junit.jupiter`).
- Place tests in `src/test/java` and mirror the production package structure.
- Name tests with a `*Test` suffix (for example, `RobotConfigModelTest`).

## Commit & Pull Request Guidelines
- Use Conventional Commits (for example, `feat: add user settings`).
- Always work on a feature branch; do not commit directly to `main`.
- Create or switch to a feature branch before making changes and confirm the current branch before committing (for example, `git checkout -b feature/my-change`).
- New features target the `feature` branch; other changes target `main`.
- PRs should include a clear description, link related issues, and note any UI changes with screenshots.
- Commit with signed-off and signed commits: `git commit -s -S -m "feat: ..."` .

## Git Permissions & Operations
- Do not modify `.git` directly; use git commands for branch switches, staging, and commits.

## CI Expectations
- Jenkins CI builds with the Jenkins Infra pipeline library in `Jenkinsfile`.
- The matrix runs on Linux (JDK 21) and Windows (JDK 17); keep changes compatible with both.
- There are no custom Maven profiles documented; locally you can approximate CI with `mvn -ntp test` or `mvn -ntp verify`.

## Documentation & Releases
- Documentation is built with VitePress in `docs/`.
- Documentation releases are triggered via GitHub Actions; do not run local release scripts.

## Docs Flow
- Edit content under `docs/` and preview with `yarn docs:dev`.
- Build the static site with `yarn docs:build` and verify locally using `yarn docs:serve`.
- Use the GitHub Actions workflow to publish docs when changes are ready.
