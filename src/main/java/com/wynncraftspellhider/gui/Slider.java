package com.wynncraftspellhider.gui;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import java.util.function.Consumer;

public class Slider extends AbstractSliderButton {

    private final float min;
    private final float max;
    private final String label;
    private final Consumer<Float> onChanged;

    // Scale constructor (0 to 3)
    public Slider(int x, int y, int width, String label, float initialValue, Consumer<Float> onChanged) {
        this(x, y, width, label, initialValue, 0.0f, 3.0f, onChanged);
    }

    // Full constructor with custom range
    public Slider(int x, int y, int width, String label, float initialValue, float min, float max, Consumer<Float> onChanged) {
        super(x, y, width, 20, Component.literal(label + ": " + String.format("%.2f", initialValue)), toSlider(initialValue, min, max));
        this.label = label;
        this.min = min;
        this.max = max;
        this.onChanged = onChanged;
    }

    private static double toSlider(float value, float min, float max) {
        return (value - min) / (max - min);
    }

    private float fromSlider() {
        return min + (float) value * (max - min);
    }

    public void forceValue(float newValue) {
        this.value = toSlider(newValue, min, max);
        updateMessage();
    }

    @Override
    protected void updateMessage() {
        setMessage(Component.literal(label + ": " + String.format("%.2f", fromSlider())));
    }

    @Override
    protected void applyValue() {
        onChanged.accept(fromSlider());
    }
}