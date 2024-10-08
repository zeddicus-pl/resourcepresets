package com.github.zeddicuspl.screen;

import com.github.zeddicuspl.ResourcePresetsPlugin;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class PresetsScreen extends Screen {
    public PresetsScreen(Screen parent) {
        // The parameter is the title of the screen,
        // which will be narrated when you enter the screen.
        super(Text.translatable("Saved resource/shader presets"));
        this.parent = parent;
    }

    @Override
    public void close() {
        if (client != null) {
            client.setScreen(parent);
        }
    }

    protected List<ButtonWidget> presetButtons = new ArrayList<>();
    protected ButtonWidget cancelButton;
    private final Screen parent;
    private String previousPreset;
    private String newPreset;
    private final ResourcePresetsPlugin plugin = ResourcePresetsPlugin.getInstance();

    @Override
    protected void init() {
        previousPreset = plugin.getSelectedPreset();
        newPreset = previousPreset;
        redrawButtons();
    }

    @Override
    public void removed() {
        if (!previousPreset.equals(newPreset)) {
            plugin.setSelectedPreset(newPreset);
        }
    }

    private void redrawButtons() {
        cleanButtons();
        presetButtons.clear();
        int y = 30;
        int x = width / 2 - 150;
        for (String preset: plugin.getPresetNames()) {
            String label = preset;
            if (preset.equals(newPreset)) {
                label = "--> " + label + " <--";
            }
            ButtonWidget button = new ButtonWidget.Builder(Text.of(label), (buttonWidget) -> {
                if (!preset.equals(newPreset)) {
                    newPreset = preset;
                    redrawButtons();
                }
            }).dimensions(x, y, 300, 20).build();
            this.addDrawableChild(button);
            presetButtons.add(button);
            y += 25;
        }
        y+= 15;
        cancelButton = new ButtonWidget.Builder(Text.translatable("Done"), (buttonWidget) -> {
            this.close();
        }).dimensions(x, y, 300, 20).build();
        this.addDrawableChild(cancelButton);
    }

    private void cleanButtons() {
        if (!presetButtons.isEmpty()) {
            presetButtons.forEach(this::remove);
        }
        this.remove(cancelButton);
    }
}
