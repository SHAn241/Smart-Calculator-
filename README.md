# Smart Calculator

A simple calculator app built with Android (Java) for the App Development Round 2 task.

## Features
- Addition, subtraction, multiplication and division
- Clear (C) button and backspace (⌫)
- Display shows the current number, the pending operation and the result
- Decimal numbers and chained operations
- Extras: percentage (%) button and dark mode toggle
- Handles divide by zero without crashing

## Tech
Java, XML layouts, Material Components

## Project structure
- `MainActivity.java`: calculator logic and display
- `activity_main.xml`: screen layout
- `styles_calc.xml`: button styles
- `colors_calc.xml` and `values-night/colors_calc.xml`: light and dark colors

## How it works
The app keeps a few variables: `current` (the number being typed), `firstValue`, `operator` and `justCalculated`.
- Pressing an operator saves the typed number and remembers the operator.
- Pressing `=` calculates the result and shows it.
- `%` divides the number by 100, or takes a percentage of the first number if an operator is pending.
- Dark mode uses `AppCompatDelegate.setDefaultNightMode()` with a `values-night` colors file.

## How to run
1. Open the project in Android Studio (or Code on the Go).
2. Let Gradle sync.
3. Run on an emulator or a real device.
