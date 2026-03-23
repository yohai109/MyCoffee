# Contributing to MyCoffee

Thank you for your interest in contributing to MyCoffee! We welcome contributions from the community.

## Getting Started

1. **Fork the repository** on GitHub
2. **Clone your fork** locally:
   ```bash
   git clone https://github.com/YOUR_USERNAME/MyCoffee.git
   cd MyCoffee
   ```
3. **Create a new branch** from master for your feature or bugfix:
   ```bash
   git checkout master
   git pull origin master
   git checkout -b feature/your-feature-name
   ```

## Development Setup

### Prerequisites

- JDK 11 or higher
- Android Studio (for Android development)
- Xcode (for iOS development, macOS only)
- Kotlin Multiplatform plugin

### Building the Project

The project uses Gradle for building. You can build different targets:

**Android:**
```bash
./gradlew :composeApp:assembleDebug
```

**Desktop (JVM):**
```bash
./gradlew :composeApp:run
```

**Server:**
```bash
./gradlew :server:run
```

**iOS:**
Open the `iosApp` directory in Xcode or use the run configuration in your IDE.

## Running Tests

Before submitting your contribution, make sure all tests pass:

### Run All Tests
```bash
./gradlew test
```

### Run Tests for Specific Modules

- **Common Tests:** `./gradlew :composeApp:allTests`
- **Android Tests:** `./gradlew :composeApp:testDebugUnitTest`
- **iOS Tests:** `./gradlew :composeApp:iosSimulatorArm64Test`
- **Desktop Tests:** `./gradlew :composeApp:jvmTest`
- **Server Tests:** `./gradlew :server:test`
- **Shared Module Tests:** `./gradlew :shared:allTests`

## Making Changes

### Code Style

- Follow Kotlin coding conventions
- Use meaningful variable and function names
- Add comments for complex logic
- Keep functions small and focused

### Commit Guidelines

- Write clear, concise commit messages
- Use the present tense ("Add feature" not "Added feature")
- Use the imperative mood ("Move cursor to..." not "Moves cursor to...")
- Reference issues and pull requests when applicable

Example:
```
Add brew timer feature

- Implement timer functionality in BrewScreen
- Add tests for timer logic
- Update documentation

Fixes #123
```

### Pull Request Process

1. **Update your branch** with the latest changes from master:
   ```bash
   git fetch origin
   git merge origin/master
   ```

2. **Ensure all tests pass** and the code builds successfully

3. **Push your changes** to your fork:
   ```bash
   git push origin feature/your-feature-name
   ```

4. **Create a Pull Request** on GitHub with:
   - A clear title describing the change
   - A detailed description of what changes were made and why
   - Reference to any related issues
   - Screenshots (for UI changes)

5. **Address review feedback** if requested

6. **Merge your PR** - use "Merge pull request" button on GitHub or:
   ```bash
   git checkout master
   git pull origin master
   ```

**IMPORTANT:** Never commit directly to the `master` branch. Always use feature branches and submit PRs.

## Reporting Bugs

When reporting bugs, please include:

- A clear and descriptive title
- Steps to reproduce the issue
- Expected behavior vs actual behavior
- Screenshots or logs if applicable
- Your environment (OS, platform, versions)

## Suggesting Enhancements

We welcome feature suggestions! Please:

- Check if the feature has already been suggested
- Clearly describe the feature and its benefits
- Provide examples of how it would be used

## Questions?

If you have questions about contributing, feel free to open an issue with the "question" label.

## Code of Conduct

- Be respectful and inclusive
- Welcome newcomers and help them get started
- Focus on constructive feedback
- Respect differing viewpoints and experiences

Thank you for contributing to MyCoffee! ☕
