# Flappy Bird in Java

A Java Swing desktop game built for CSCI 282 (Introduction to Object-Oriented Programming), Fall 2025 at McNeese State University.

## Features

- Keyboard-controlled jumping with gravity.
- Randomized pipe obstacles and rectangular collision detection.
- Timer-driven updates and custom rendering.
- Menu, scoring, restart behavior, and a session high score.

## Run locally

Install a Java Development Kit (JDK) and run these commands from this repository's directory:

```sh
javac App.java FlappyBird.java
java App
```

Keep the four PNG files beside the Java files. The game loads images using relative paths. Use the on-screen controls to start and **Space** to jump.

## What I practiced

Connecting input events to state updates and rendering; modeling the bird and pipes; coordinating timers; and resetting game state after a collision.

## Context and credits

Coursework by **Rohit Yadav and Prince Gupta**, developed with AI assistance. Original source credits are retained. The included artwork came with the project materials; it is not claimed as original artwork. This repository is a learning project, not an official Flappy Bird release.

## Current limitations

- High scores last for the current session only.
- Gameplay needs more long-session and edge-case testing.
- A future improvement is separating game logic from rendering and input handling.

[Portfolio](https://rohit-yadav-portfolio.web.app/)
