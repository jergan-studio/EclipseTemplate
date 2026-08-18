# Eclipse

Eclipse is a simple scripting language designed to be fun and easy to learn, with syntax inspired by Lua.

Eclipse source files use the `.ecs` extension.

`.ecs` = Eclipse Source


## What is Eclipse?

Eclipse lets you write simple `.ecs` code that the Eclipse compiler converts into Java code and runs.

Eclipse (.ecs)
    |
    v
Eclipse Compiler
    |
    v
Java (.java)
    |
    v
javac
    |
    v
Program

The goal is to make programming simple like Lua while still allowing Eclipse to work with Java, C, Lua, HTML, CSS, and JavaScript.


## Features

Eclipse is planned to support:

- Variables
- Functions
- Conditions
- Loops
- Drawing
- Objects
- Collision
- Movement
- HTML
- CSS
- JavaScript
- Java
- C
- Lua
- File creation
- App launching
- Game development
- Single-file projects


## Eclipse Project Template

The official starter template is:

https://github.com/jergan-studio/EclipseTemplate

The template is NOT the compiler. It is simply a starting project.

A template project can look like:

EclipseProject/
├── main.ecs
├── Main.java
├── index.html
├── script.js
├── Assets/
│   └── Eclipse.ico
└── assets/
    ├── images/
    ├── sounds/
    └── fonts/


## Using the Template

You need Git installed.

Clone the template:

git clone https://github.com/jergan-studio/EclipseTemplate.git

Enter the project:

cd EclipseTemplate

Or create a new project directly from the template:

git clone https://github.com/jergan-studio/EclipseTemplate.git MyEclipseProject

Then:

cd MyEclipseProject

You can now edit main.ecs.


## Eclipse Source Files

Eclipse uses the `.ecs` extension.

Example:

console.log("Hello Eclipse!")

x = 3

if [x] = 3 then
    console.log("x is 3!")
end

repeat 3
    console.log("Eclipse!")
end


## Running an Eclipse Program

The Eclipse compiler is written in Java.

Compile the compiler:

javac Eclipse.java

Run an Eclipse file:

java Eclipse main.ecs

The compiler reads the `.ecs` file and generates Java.

main.ecs
   |
   v
Eclipse.java
   |
   v
EclipseProgram.java
   |
   v
javac
   |
   v
EclipseProgram

Generated files are stored in:

.eclipse-build/


## Eclipse Commands

### Console

console.log("Hello Eclipse!")

This can generate Java similar to:

System.out.println("Hello Eclipse!");


### Variables

x = 10
name = "Eclipse"

Variables can be used in conditions and other Eclipse commands.


### Conditions

if [x] = 10 then
    console.log("x is 10")
end

Eclipse uses:

if
then
end

to keep conditional code simple.


### Functions

func hello()
    console.log("Hello!")
end

Functions are planned to compile into Java methods.


### Loops

Example:

repeat 5
    console.log("Hello!")
end

This is intended to become a Java loop.


## Files

Eclipse can create files:

create file("index.html")

Future versions will support commands for writing and injecting code into files.


## Open Applications

Eclipse includes the planned `openapp` command:

openapp file("game.java")

The command is intended to allow Eclipse to launch supported files and applications.

Eclipse can eventually work with:

- .ecs
- .java
- .js
- .html


## HTML, CSS & JavaScript

Eclipse can be used alongside web technologies.

A project can contain:

main.ecs
index.html
script.js

Eclipse can eventually generate and modify these files.

For example:

create file("index.html")

And:

show("index.html" inject ("script.js") to ("index.html"))


## Java

Java is the initial target for the Eclipse compiler.

For example:

console.log("Hello!")

can become:

System.out.println("Hello!");

This lets Eclipse use Java's runtime while keeping Eclipse's syntax simple.


## C & Lua

Eclipse is also planned to use C and Lua as part of its compiler/runtime ecosystem.

The general idea is:

             Eclipse
                |
        +-------+-------+
        |       |       |
        v       v       v
       Java     C      Lua
        |       |       |
        +-------+-------+
                |
                v
          Eclipse Runtime

Java is the first compiler target, while C and Lua can be added as the project develops.


## Game Development

Eclipse is designed to eventually make simple game development possible.

Planned commands/features include:

- Drawing
- Objects
- Movement
- Collision
- Sprites
- Input
- Game loops
- Sounds

For example, a future Eclipse program could look like:

obj player

move player x 10
move player y 5

if collision(player, enemy) then
    console.log("Hit!")
end


## Drawing

Planned drawing commands could look like:

draw rectangle(100, 100, 50, 50)
draw circle(200, 100, 25)
draw text("Hello Eclipse!", 50, 50)

The exact drawing API will be developed as the compiler/runtime is built.


## Collision

Eclipse is planned to support simple collision detection:

if collision(player, enemy) then
    console.log("Collision!")
end


## Movement

Objects can eventually be moved with commands such as:

move player x 10
move player y 5


## CSS

Eclipse can eventually generate and modify CSS:

css "button"
    color = "white"
    background = "black"
end


## Eclipse Icon

Eclipse source files use the Eclipse icon:

Assets/Eclipse.ico

The icon represents a 2D eclipse with a planet far away.

The icon is intended to become the recognizable icon for `.ecs` files.


## Eclipse, eC & ECMAScript

Eclipse is its own language.

Eclipse       -> .ecs -> Eclipse language
eC            -> existing eC language
ECMAScript    -> JavaScript specification
JavaScript    -> ECMAScript-based language

`.ecs` specifically identifies an Eclipse Source file.


## Compiler Architecture

The Eclipse compiler is designed as a translator rather than simply interpreting every command.

          main.ecs
              |
              v
      Eclipse Compiler
              |
              v
     Generated Java Code
              |
              v
             javac
              |
              v
       Java Application

For example:

console.log("Hello!")

becomes approximately:

System.out.println("Hello!");


## Creating a New Eclipse Project

Clone the template:

git clone https://github.com/jergan-studio/EclipseTemplate.git MyEclipseProject

Enter it:

cd MyEclipseProject

Your new project is now ready for Eclipse development.


## Development

Eclipse is currently under development.

The first compiler versions focus on:

.ecs
  |
  v
Java
  |
  v
javac
  |
  v
Program

More Eclipse commands will be added as the language develops.


## Goal

The main goal of Eclipse is:

Programming should be fun and easy.

Eclipse aims to combine the simplicity of Lua-like scripting with the ability to create games, applications, and web projects.


## License

License information will be added when the Eclipse project reaches its first official release.
