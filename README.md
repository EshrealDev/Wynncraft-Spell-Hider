# Wynncraft Spell Hider

[![GitHub](https://img.shields.io/github/downloads/EshrealDev/Wynncraft-Spell-Hider/total?style=flat&logo=github&label=GitHub&color=4c1)](https://github.com/EshrealDev/Wynncraft-Spell-Hider/releases)
[![Modrinth](https://img.shields.io/modrinth/dt/wynncraft-spell-hider?style=flat&logo=modrinth&label=Modrinth&color=1bd96a)](https://modrinth.com/mod/wynncraft-spell-hider)
[![License: CC0-1.0](https://img.shields.io/badge/License-CC0%201.0-lightgrey?style=flat&logo=creativecommons)](https://github.com/EshrealDev/Wynncraft-Spell-Hider/blob/main/LICENSE)

<div align="center">

<img src="https://raw.githubusercontent.com/EshrealDev/Static-Storage/main/logo.png" width=25% alt="Wynncraft Spell Hider Logo">

</div>

A mod that gives you full control over how Wynncraft spell effects are rendered.
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
| `/wynncraftspellhider version` | Displays the current installed version |
| `/wynncraftspellhider update` | Checks if a newer version is available and provides a download link if so |

---

## Usage

Open the mod menu with `/wynncraftspellhider menu` or press N on your keyboard.