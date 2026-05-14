# Contributing Guide

[貢獻指南 (Chinese Version)](CONTRIBUTING.zh-Hant.md)

---

## Submitting an Issue

Before you submit a new issue, please search existing issues to avoid duplicates.

When submitting an issue, please provide the following information:

- **Title:** A brief and descriptive title for your issue.
- **Steps to Reproduce:** If it's a bug report, please provide detailed steps to reproduce the issue. This helps us fix it faster.
- **Expected vs. Actual Result:** Describe what you expected to happen and what actually occurred.
- **Environment Information:** Include details about your environment, such as your browser, operating system, and version numbers.

---

## Code Contribution Guide

### 1. Branching Convention

Please create your feature branch from the `main` branch. We recommend the following naming convention for your branches:

- `feat/issue-number-short-description` (for new features)
- `fix/issue-number-short-description` (for bug fixes)
- `docs/issue-number-short-description` (for documentation changes, like README or other guides)
- `chore/issue-number-short-description` (for maintenance, like CI/CD configurations or build process changes)

**Examples**:

- `feat/issue-123-add-login-button`
- `docs/issue-456-update-contributing-guide`
- `chore/issue-789-update-build-config`

### 2. Commit Message Convention

We follow the **[Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/)** specification. This helps us automatically generate release notes and version numbers.

#### Structure
Each commit message must adhere to the following structure:

```text
<type>(<scope>): <subject>

Why:
- Explain the motivation or the "reason why" behind the change.

What:
- List the key technical changes in bullet points.

[optional footer(s)]
```

- **`type`:** Required. Describes the main purpose of the commit (e.g., `feat`, `fix`, `docs`, `chore`, `refactor`).
- **`scope`:** Optional. Describes the part of the codebase affected.
- **`subject`:** Required. A short, concise description of the change.
- **`Why` section:** Required. Explains the "reason why" behind the change.
- **`What` section:** Required. Lists technical changes in bullet points.

#### Examples

**Standard Commit:**
```text
feat(core): implement robust error handling for Ktor calls

Why:
- To provide more descriptive error messages to SDK consumers when network requests fail.
- To unify result mapping across different data sources.

What:
- Created NetworkException class hierarchy.
- Refactored KtorClientFactory to include a global response validator.
- Updated UnsplashRepository to map Ktor exceptions to Domain Results.
```

**Breaking Change:**
Add a `!` after the `<type>` or `<scope>` and include `BREAKING CHANGE` in the footer.
```text
refactor(ui)!: rename OAuthWebView to LoginWebView

Why:
- To better reflect the component's purpose and align with the new authentication flow.

What:
- Renamed OAuthWebView.kt to LoginWebView.kt.
- Updated all internal references in the :omnifeed-ui module.

BREAKING CHANGE: OAuthWebView has been renamed to LoginWebView. Consumers must update their imports.
```

### 3. Pull Request (PR) Guidelines

When your feature is complete and ready to be merged, create a pull request.

The PR title must follow the **Conventional Commits** specification so that automated tools can parse it correctly. The title format is:

`<type>(<scope>): <subject> (#<issue number>)`

- `<type>`: Indicates the nature of the change, for example:
  - `feat`: A new feature
  - `fix`: A bug fix
  - `docs`: Documentation changes
  - `style`: Code style changes (does not affect logic)
  - `refactor`: Code refactoring
  - `perf`: Performance optimization

- `<scope>` (optional): Indicates the module or scope affected by the change.
- `<subject>`: Briefly describes the content of the change.
- `(#<issue number>)`: Clearly links to the relevant Issue.

**Example:** `feat(auth): add user login feature (#123)`

In the PR description, please detail:

- **Relevant Issue Number:** Link to the related issue, e.g., `Closes #123`.
- **Summary of Changes:** A brief summary of what was changed.
- **Testing Information:** A description of how you tested your changes.

At least one reviewer must approve your pull request before it can be merged.

---
