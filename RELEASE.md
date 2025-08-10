# Release Process

This repository now includes an automated release workflow that can be triggered manually from the GitHub Actions tab.

## How to Create a Release

1. **Ensure Tests Pass**: The latest build-test workflow run on the `main` branch must be successful.

2. **Trigger Release**:
   - Go to the **Actions** tab in GitHub
   - Select the **Release** workflow
   - Click **Run workflow**
   - Enter the desired version number (e.g., `1.0.0`)
   - Click **Run workflow**

## What the Release Process Does

1. **Validation**: Checks that the latest test run on main branch passed
2. **Branch Creation**: Creates a release branch `release/{version}`
3. **Version Update**: Updates `gradle.properties` with the specified version
4. **Git Operations**: Commits changes, pushes branch, and creates a git tag `v{version}`
5. **Docker Build**: Builds and pushes Docker image to `25119801634/finance` with both version and `latest` tags
6. **GitHub Release**: Creates a formal GitHub release with release notes

## Requirements

- The workflow can only be triggered on the `main` branch
- Latest test run on `main` must have passed
- Requires Docker Hub credentials to be configured as repository secrets:
  - `DOCKERHUB_USERNAME`
  - `DOCKERHUB_TOKEN`

## Output

- Release branch: `release/{version}`
- Git tag: `v{version}`
- Docker images: 
  - `25119801634/finance:{version}`
  - `25119801634/finance:latest`
- GitHub release with changelog