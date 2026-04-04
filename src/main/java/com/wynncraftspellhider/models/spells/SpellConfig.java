package com.wynncraftspellhider.models.spells;

import java.util.List;

public class SpellConfig {
    public final String name;
    public final List<SpellGroup> groups;
    public final String description; // null = no info button

    public SpellConfig(String name, List<SpellGroup> groups) {
        this.name = name;
        this.groups = groups;
        this.description = null;
    }

    public SpellConfig(String name, String description, List<SpellGroup> groups) {
        this.name = name;
        this.groups = groups;
        this.description = description;
    }
}