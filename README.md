# NightPass – A Survival Card Game Simulation

NightPass is a card-based survival game simulation which focuses on algorithmic decision-making, data structure design, and performance under large-scale input constraints.

---

## Overview

The game models a turn-based duel between two entities: **The Survivor** and **The Stranger**.  
Each turn requires selecting exactly one card from the deck according to strict priority rules.  
The correctness and efficiency of this selection directly affect scoring and long-term gameplay.

The project is designed to handle large input sizes efficiently while strictly following the rules defined in the project specification.

---

## Game Concept

- The Survivor owns a deck of cards.
- Each card has attack and health attributes.
- Cards participate in battles against The Stranger.
- Cards may be defeated, revived, or permanently modified based on battle outcomes.

Every decision must be made according to predefined priority rules, making the project heavily focused on algorithmic correctness.

---

## Core Mechanics

### Card Management
- Active cards are stored in a **Deck**.
- Defeated cards are moved to a **Discard Pile**.
- Cards in the discard pile may be revived using healing points.

### Battle System
Card selection follows a strict priority order:
1. Cards that survive and defeat the opponent
2. Cards that survive and deal maximum damage
3. Cards that defeat the opponent even if they do not survive
4. Cards that deal maximum possible damage
5. Tie-breaking based on insertion order

Battles apply **simultaneous damage**, and card statistics are updated after each encounter.

### Revival Mechanics
- Healing points are distributed according to priority rules.
- Partial and full revivals apply permanent attack penalties.
- Revived cards re-enter the deck with updated baseline statistics.

---

## Algorithmic Focus

- Priority-based decision logic
- Efficient processing of large command sequences
- Clear separation of temporary and permanent state changes
- Designed to scale to hundreds of thousands of operations

Special care is taken to avoid unnecessary computations and ensure predictable time complexity.

---
## Repository Structure

```bash
nightpass/
├─ src/
├─ testcase_inputs/
├─ testcase_outputs/
├─ test_runner.py

```
---

## Compilation and Execution

Compile the project:
```bash
javac *.java
```

Run with input and output files:
```bash
java Main <input_file> <output_file>
```

Run automated tests:
```bash
python3 test_runner.py
```

---

## Performance Considerations

The implementation is designed to:
- Pass small, medium, and large test cases
- Meet strict time and memory constraints
- Handle high card counts and long command sequences efficiently

Care is taken to ensure predictable time complexity and avoid unnecessary recomputation.

---

## Notes

This repository contains the **full implementation** of the project.  
It is made public for **educational and demonstration purposes**.

---

## Usage and License

This project is provided for educational and demonstration purposes only.  
Reuse, modification, or submission for academic credit is **not permitted**.

---

