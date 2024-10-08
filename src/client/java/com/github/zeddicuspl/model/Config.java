package com.github.zeddicuspl.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {
    private final Map<String, List<String>> presets = new HashMap<>();
    private String selectedPreset = "";

    public Map<String, List<String>> getPresets() {
        return presets;
    }
    public String getSelectedPreset() {
        return selectedPreset;
    }

    public void setSelectedPreset(String selectedPreset) {
        this.selectedPreset = selectedPreset;
    }
}
