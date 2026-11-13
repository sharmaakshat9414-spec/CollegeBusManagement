package com.piet.busmanagement.ui.components;

/** Item for combo boxes: shows a label but carries a database id. */
public record ComboItem(int id, String label) {
    @Override
    public String toString() {
        return label;
    }
}
