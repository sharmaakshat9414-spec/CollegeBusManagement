package com.piet.busmanagement.ui.dialogs;

import com.piet.busmanagement.dao.NoticeDAO;
import com.piet.busmanagement.model.Notice;
import com.piet.busmanagement.ui.components.FormHelper;

import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.Window;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class NoticeFormDialog extends BaseFormDialog {

    private final NoticeDAO dao = new NoticeDAO();
    private final Notice existing;
    private final JTextField title = FormHelper.textField("Notice title", null);
    private final JTextArea description = new JTextArea(5, 20);
    private final JTextField date = FormHelper.textField("yyyy-MM-dd", null);
    private final JComboBox<String> status = FormHelper.combo("ACTIVE", "INACTIVE");

    public NoticeFormDialog(Window owner, Notice existing) {
        super(owner, existing == null ? "Add Notice" : "Edit Notice", "Active notices are visible to students and drivers");
        this.existing = existing;
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        addField("Title *", title);
        addField("Description *", new JScrollPane(description));
        addField("Date *", date);
        addField("Status *", status);
        date.setText(existing == null ? LocalDate.now().toString() : existing.noticeDate());
        if (existing != null) {
            title.setText(existing.title());
            description.setText(existing.description());
            status.setSelectedItem(existing.status());
        }
        finish(owner);
    }

    @Override
    protected boolean onSave() throws Exception {
        if (FormHelper.isBlank(title.getText()) || FormHelper.isBlank(description.getText())) {
            FormHelper.warn(this, "Please fill all required fields.");
            return false;
        }
        try {
            LocalDate.parse(text(date));
        } catch (DateTimeParseException e) {
            FormHelper.warn(this, "Date must be in yyyy-MM-dd format, for example 2025-07-15.");
            return false;
        }
        Notice n = new Notice(existing == null ? 0 : existing.id(), text(title), text(description), text(date),
                (String) status.getSelectedItem());
        if (existing == null) {
            dao.add(n);
            FormHelper.info(this, "Notice added successfully.");
        } else {
            dao.update(n);
            FormHelper.info(this, "Notice updated successfully.");
        }
        return true;
    }
}
