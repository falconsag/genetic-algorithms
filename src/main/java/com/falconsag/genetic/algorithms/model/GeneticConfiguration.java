package com.falconsag.genetic.algorithms.model;

import java.util.List;

public class GeneticConfiguration {

    private SimulatorConfiguration simulatorConfig;
    private EditorConfiguration editorConfig;
    private List<Integer> genes;

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

    public List<Integer> getGenes() {
        return genes;
    }

    public void setGenes(List<Integer> genes) {
        this.genes = genes;
    }
}
