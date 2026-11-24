# Multi-Agent Simulation Framework

A Java framework for discrete-event, multi-agent simulations, with four
models built on top of it: flocking boids (prey/predator), cellular
automata (Conway's Game of Life, a segregation/immigration model, and
Schelling's segregation model), and a simple bouncing-balls physics demo.
Each one plugs into the same small event-driven core instead of running
its own ad hoc loop.

## Architecture

- **`gui.Simulable`** (provided): any simulation implements `next()` (advance
  one step) and `restart()` (reset); the GUI calls these from its
  Play/Step/Reset buttons.
- **`control.EventManager`**: a discrete-event scheduler. Events sit in a
  priority queue ordered by date; `next()` advances the clock by one unit
  and runs every event whose date has come due.
- **`control.Event` / `GroupUpdateEvent` / `BoidsUpdateEvent`**: a
  self-rescheduling event is how a simulation keeps running — each one
  re-adds itself to the manager at `date + delay` right after executing,
  so different agent groups can update on different clocks (e.g. prey every
  tick, predators every 3 ticks) without the scheduler knowing anything
  agent-specific.
- **`simulator.GrilleSimulator`** (abstract): shared base for the three
  cellular automata. Owns the grid, drawing, and the `restart()` /
  random-init logic; each subclass only implements `etat_suiv_case()`, the
  per-cell transition rule.
- **`simulator.BoidsSimulator`**: coordinates boid groups, the spatial grid,
  and predation (a predator close enough to a prey kills it) each step.

## The models

- **Boids** (`models.boids`): classic Reynolds flocking — separation,
  alignment, cohesion — plus a predator/prey layer: prey flee nearby
  predators with a force that grows sharply as distance shrinks, predators
  pursue the nearest prey by aiming at its predicted future position rather
  than where it currently is. Neighbor lookups go through a `SpatialGrid`
  that buckets boids into cells sized to the largest perception radius, so
  each boid only checks its own cell and its 8 neighbors instead of every
  other boid (O(N·k) instead of O(N²)).
- **Game of Life**: Conway's rules on a toroidal grid (edges wrap around).
  Computed in two passes per step — mark every cell's next state without
  touching the grid, then flip all marks at once — so a cell's update never
  reads a neighbor that's already been updated this step.
- **Immigration game / Schelling segregation**: same `GrilleSimulator` base,
  different per-cell rule.
- **Balls**: elastic collisions between circles bouncing inside the window.

`Invader` is the small demo shipped with the course's `gui.jar` to show how
`Simulable` works; it isn't part of this project's own code.

## A bug this fixes: the reset button emptied the simulation for good

`BoidsSimulator.restart()` used to just clear the groups and the event
queue. That matches what `Simulable.restart()` is supposed to do, except
nothing ever repopulated them afterwards: the groups were built once in
`main()`, not from a method `restart()` could call again, and the
recurring update events only exist by re-scheduling themselves — once the
queue is cleared, there's nothing left to bring them back. Pressing
"Début" left a permanently blank, frozen simulation, Play and Step included.

The fix: `BoidsSimulator` now keeps its own snapshot of the starting groups
and their update schedule (taken when each group is registered), and
`restart()` rebuilds from that snapshot instead of leaving an empty state
behind.

## Build & run

```bash
make            # compiles everything into bin/
make runBoids   # prey/predator flocking
make runJeuDeLaVie
make runImmigration
make runSchelling
make runBalls
make runBehaviour   # same boids engine, different group presets (see TestBehaviour.java)
```

Javadoc isn't checked in (it's generated output); regenerate it with:

```bash
javadoc -d doc -classpath lib/gui.jar -sourcepath src -subpackages control:models:simulator
```

## Project structure

```
src/
  control/     event scheduling (EventManager, Event, GroupUpdateEvent, BoidsUpdateEvent)
  simulator/   per-family controllers (BoidsSimulator, GrilleSimulator)
  models/
    boids/     AbstractBoids, PreyBoid, PredatorBoid, BoidsGroup, SpatialGrid
    balls/     Balls, BallsSimulator
  Test*.java   entry points, one per simulation
lib/gui.jar    graphics library provided for the course
docs/          design report and user manual (French)
```

## Context

TP Libre (open-ended course project) in object-oriented programming at
Grenoble INP - Ensimag, 2025.
