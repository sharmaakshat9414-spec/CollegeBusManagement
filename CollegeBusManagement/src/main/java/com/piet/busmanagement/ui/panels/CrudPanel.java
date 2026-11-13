package com.piet.busmanagement.ui.panels;

import com.piet.busmanagement.config.UITheme;
import com.piet.busmanagement.ui.components.FormHelper;
import com.piet.busmanagement.ui.components.IconFactory;
import com.piet.busmanagement.ui.components.ModernButton;
import com.piet.busmanagement.ui.components.ModernTable;
import com.piet.busmanagement.ui.components.Refreshable;
import com.piet.busmanagement.ui.components.RoundedPanel;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Base class of all management screens: title, search/filter toolbar, table and Add/Edit/Delete buttons.
 * Subclasses only describe their columns and how to load/add/edit/delete records.
 */
public abstract class CrudPanel extends JPanel implements Refreshable {

    protected final ModernTable table;
    private final JTextField searchField = FormHelper.textField("Search...", IconFactory.Type.SEARCH);
    private final JComboBox<String> filterBox;
    private final JLabel countLabel = FormHelper.muted(" ");
    private final JPanel actionBar = FormHelper.panel(new FlowLayout(FlowLayout.RIGHT, 8, 0));

    protected CrudPanel(String title, String subtitle, String[] columns, String filterLabel, String[] filterOptions) {
        setLayout(new BorderLayout(0, 16));
        setOpaque(false);
        setBorder(new EmptyBorder(24, 28, 24, 28));
        table = new ModernTable(columns);
        filterBox = filterOptions == null ? null : new JComboBox<>(prepend("All", filterOptions));

        // ----- header: title on the left, Add/Edit/Delete on the right -----
        JPanel titles = FormHelper.panel(new BorderLayout(0, 2));
        titles.add(FormHelper.heading(title, 24f), BorderLayout.CENTER);
        titles.add(FormHelper.muted(subtitle), BorderLayout.SOUTH);

        ModernButton add = new ModernButton("Add", ModernButton.Style.SUCCESS, IconFactory.Type.ADD);
        ModernButton edit = new ModernButton("Edit", ModernButton.Style.PRIMARY, IconFactory.Type.EDIT);
        ModernButton delete = new ModernButton("Delete", ModernButton.Style.DANGER, IconFactory.Type.DELETE);
        add.addActionListener(e -> addRecord());
        edit.addActionListener(e -> onEditClicked());
        delete.addActionListener(e -> onDeleteClicked());
        actionBar.add(add);
        actionBar.add(edit);
        actionBar.add(delete);

        JPanel header = FormHelper.panel(new BorderLayout());
        header.add(titles, BorderLayout.WEST);
        header.add(actionBar, BorderLayout.EAST);

        // ----- toolbar: search / clear / filter / refresh -----
        searchField.setColumns(20);
        ModernButton search = new ModernButton("Find", ModernButton.Style.PRIMARY, IconFactory.Type.SEARCH);
        ModernButton clear = new ModernButton("Clear", ModernButton.Style.SECONDARY, IconFactory.Type.CLEAR);
        ModernButton refresh = new ModernButton("Refresh", ModernButton.Style.SECONDARY, IconFactory.Type.REFRESH);
        search.addActionListener(e -> refresh());
        searchField.addActionListener(e -> refresh());
        clear.addActionListener(e -> {
            searchField.setText("");
            if (filterBox != null) {
                filterBox.setSelectedIndex(0);
            }
            refresh();
        });
        refresh.addActionListener(e -> refresh());

        JPanel tools = FormHelper.panel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        tools.add(searchField);
        tools.add(search);
        tools.add(clear);
        if (filterOptions != null) {
            tools.add(FormHelper.muted("  " + filterLabel + ":"));
            filterBox.addActionListener(e -> refresh());
            tools.add(filterBox);
        }
        tools.add(refresh);

        RoundedPanel toolbar = new RoundedPanel(new BorderLayout(), 8);
        toolbar.add(tools, BorderLayout.WEST);

        // ----- table card -----
        RoundedPanel tableCard = new RoundedPanel(new BorderLayout(0, 6), 6);
        tableCard.add(ModernTable.scroll(table), BorderLayout.CENTER);
        countLabel.setFont(UITheme.font(Font.PLAIN, 12f));
        countLabel.setBorder(new EmptyBorder(0, 8, 2, 0));
        tableCard.add(countLabel, BorderLayout.SOUTH);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() >= 0) {
                    onEditClicked();
                }
            }
        });

        JPanel top = FormHelper.panel(new BorderLayout(0, 14));
        top.add(header, BorderLayout.NORTH);
        top.add(toolbar, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);
        add(tableCard, BorderLayout.CENTER);
    }

    private static String[] prepend(String first, String[] rest) {
        String[] all = new String[rest.length + 1];
        all[0] = first;
        System.arraycopy(rest, 0, all, 1, rest.length);
        return all;
    }

    // ---------------- hooks for subclasses ----------------

    protected abstract List<Object[]> fetchRows(String keyword, String filter) throws Exception;

    protected abstract void addRecord();

    protected abstract void editRecord(int id) throws Exception;

    protected abstract void deleteRecord(int id) throws Exception;

    protected void addActionButton(ModernButton button) {
        actionBar.add(button);
    }

    protected Window owner() {
        return SwingUtilities.getWindowAncestor(this);
    }

    /** The id of the selected row, or -1 after telling the user to select one. */
    protected int requireSelection() {
        int id = table.getSelectedId();
        if (id < 0) {
            FormHelper.warn(this, "Please select a record first.");
        }
        return id;
    }

    // ---------------- behaviour ----------------

    @Override
    public void refresh() {
        try {
            String filter = filterBox == null ? "All" : (String) filterBox.getSelectedItem();
            List<Object[]> rows = fetchRows(searchField.getText().trim(), filter);
            table.setRows(rows);
            countLabel.setText(rows.size() + " record(s)");
        } catch (Exception ex) {
            FormHelper.handleException(this, ex);
        }
    }

    private void onEditClicked() {
        int id = requireSelection();
        if (id < 0) {
            return;
        }
        try {
            editRecord(id);
        } catch (Exception ex) {
            FormHelper.handleException(this, ex);
        }
    }

    private void onDeleteClicked() {
        int id = requireSelection();
        if (id < 0 || !FormHelper.confirmDelete(this)) {
            return;
        }
        try {
            deleteRecord(id);
            FormHelper.info(this, "Record deleted successfully.");
            refresh();
        } catch (Exception ex) {
            FormHelper.handleException(this, ex);
        }
    }
}
