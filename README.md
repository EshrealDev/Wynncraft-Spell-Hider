# WynncraftSpellHider

A Fabric client-side mod for Minecraft 1.21.1 that gives you full control over how Wynncraft spell effects are rendered.
---

## Features

### Spell Customization
Each spell effect is broken down into groups that can be individually configured:
- **Hide / Show** — remove specific spell effects entirely from your screen
- **Scale** — make effects larger or smaller
- **Offset** — shift effects along the X, Y, and Z axes relative to the models orientation 
- **Transparency** — set a per-group alpha value from fully visible to fully invisible. This works with shaders

### Particle Hider
Hide individual particle effects

### Config Manager
Save and load multiple named profiles so you can switch between different configurations instantly. Set a default profile that is applied automatically when you launch the game.

### Commands
| Command | Description |
|---|---|
| `/wynncraftspellhider menu` | Opens the mod GUI |
| `/wynncraftspellhider globalTransparency <0.0 - 1.0>` | Sets the transparency of all spell groups at once |

---

## Usage

Open the mod menu with `/wynncraftspellhider menu` or press n on your keyboard.
