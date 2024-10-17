# Contributing to HydraFP

First off, thank you for considering contributing to HydraFP! It's people like you that make HydraFP such a great tool. We welcome contributions from everyone, regardless of their level of experience.

## Table of Contents

1. [Code of Conduct](#code-of-conduct)
2. [Getting Started](#getting-started)
   - [Issues](#issues)
   - [Pull Requests](#pull-requests)
3. [Development Workflow](#development-workflow)
4. [Coding Guidelines](#coding-guidelines)
5. [Commit Message Guidelines](#commit-message-guidelines)
6. [Testing](#testing)
7. [Documentation](#documentation)
8. [Community](#community)

## Code of Conduct

This project and everyone participating in it is governed by the [HydraFP Code of Conduct](CODE_OF_CONDUCT.md). By participating, you are expected to uphold this code. Please report unacceptable behavior by opening an issue or contacting the project team at [mahmoudahmedxyz@gmail.com](mailto:mahmoudahmedxyz@gmail.com).

## Getting Started

### Issues

- Feel free to [submit issues and enhancement requests](https://github.com/mahmoudxyz/hydrafp/issues/new/choose).
- We have issue templates for bug reports and feature requests. Please use them when appropriate.
- Before creating an issue, please check that a similar issue doesn't already exist.
- When you create a new issue, please provide as much relevant information as possible for reproducing and understanding the problem.
- Label issues appropriately to help with organization and prioritization.

### Pull Requests

1. Fork the repo and create your branch from `main`.
2. If you've added code that should be tested, add tests.
3. If you've changed APIs, update the documentation.
4. Ensure the test suite passes.
5. Make sure your code lints.
6. [Create a pull request](https://github.com/mahmoudxyz/hydrafp/compare) to the `main` branch.
7. Reference any relevant issues in your PR description.
8. Wait for the CI checks to pass and for a maintainer to review your PR.

## Development Workflow

1. Clone your fork of the repo: `git clone https://github.com/mahmoudxyz/hydrafp.git`
2. Add the original repo as a remote: `git remote add upstream https://github.com/mahmoudxyz/hydrafp.git`
3. Create a new branch for your feature or bugfix: `git checkout -b feature/your-feature-name`
4. Make your changes and commit them (see Commit Message Guidelines below)
5. Push your changes to your fork: `git push origin feature/your-feature-name`
6. Open a pull request from your fork to the main repo

Remember to keep your fork synced with the upstream repository:

```
git fetch upstream
git checkout main
git merge upstream/main
```

## Coding Guidelines

- Follow the [Java Code Conventions](https://www.oracle.com/java/technologies/javase/codeconventions-contents.html).
- Write code in a functional style, leveraging HydraFP's features.
- Use meaningful variable and method names.
- Keep methods small and focused on a single task.
- Comment your code where necessary, especially for complex logic.
- 
## Commit Message Guidelines

We follow the [Conventional Commits](https://www.conventionalcommits.org/) specification. This leads to more readable messages that are easy to follow when looking through the project history. 

- Use the present tense ("Add feature" not "Added feature")
- Use the imperative mood ("Move cursor to..." not "Moves cursor to...")
- Limit the first line to 72 characters or less
- Reference issues and pull requests liberally after the first line

Example:
```
feat(Option): add flatMap method

This adds the flatMap method to the Option type, allowing for more
flexible chaining of operations.

Closes #123
```

## Testing

- Write unit tests for new code you create.
- Ensure all tests pass before submitting a pull request.
- Aim for high test coverage, especially for core functionality.
- We use [JUnit 5](https://junit.org/junit5/) for testing. Familiarize yourself with it if you haven't used it before.

## Documentation

- Update the documentation with details of changes to interfaces, new features, or important changes in functionality.
- Use clear and concise language in documentation.
- Provide examples where appropriate.
- We use [Javadoc](https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html) for API documentation. Please document all public APIs.

## Community

- Feel free to join our [GitHub Discussions](https://github.com/mahmoudxyz/hydrafp/discussions) to ask questions or discuss HydraFP development.
- Watch the repository to stay updated on new issues and pull requests.
- Star the repository if you find it useful!
- Respect other contributors and maintain a positive and supportive environment.

Remember, contributions to HydraFP don't just mean writing code. You can help out by writing documentation, tests, or even giving feedback about the project. Whatever your level of technical expertise, there's likely an area where you can contribute to HydraFP!

Thank you for your contributions. Together, we're building something amazing!
