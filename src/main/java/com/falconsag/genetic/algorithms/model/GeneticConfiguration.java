package com.falconsag.genetic.algorithms.model;

public class GeneticConfiguration {

    private SimulatorConfiguration simulatorConfig;
    private EditorConfiguration editorConfig;

    public SimulatorConfiguration getSimulatorConfig() {
        return simulatorConfig;
    }

    public void setSimulatorConfig(SimulatorConfiguration simulatorConfig) {
        this.simulatorConfig = simulatorConfig;
    }

    public EditorConfiguration getEditorConfig() {
        return editorConfig;
    }

    public void setEditorConfig(EditorConfiguration editorConfig) {
        this.editorConfig = editorConfig;
    }
}
