package com.example.client;

import com.example.shared.Contact;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.ListDataProvider;

import java.util.Comparator;
import java.util.List;

public class MyGwtApp implements EntryPoint {
    private static final String BAD_PARAMETERS = "Bad parameters";
    private static final String MAIN_TABLE = "tableContainer";
    private final ServiceAsync service = GWT.create(Service.class);

    public void onModuleLoad() {
        final Button sendButton = new Button("Create/Update");
        final Button removeButton = new Button("Remove");
        final IntegerBox idField = new IntegerBox();
        final TextBox nameField = new TextBox();
        final TextBox surnameField = new TextBox();
        final Label errorField = new Label();
        final IntegerBox phoneField = new IntegerBox();

        RootPanel.get("idFieldContainer").add(idField);
        RootPanel.get("nameFieldContainer").add(nameField);
        RootPanel.get("surnameFieldContainer").add(surnameField);
        RootPanel.get("phoneFieldContainer").add(phoneField);
        RootPanel.get("errorContainer").add(errorField);
        RootPanel.get("sendButtonContainer").add(sendButton);
        RootPanel.get("removeButtonContainer").add(removeButton);

        idField.setStyleName("form-control");
        nameField.setStyleName("form-control");
        surnameField.setStyleName("form-control");
        phoneField.setStyleName("form-control");
        sendButton.setStyleName("btn btn-info");
        removeButton.setStyleName("btn btn-warning");
        errorField.setStyleName("alert alert-danger text-center");
        errorField.setVisible(false);

        CellTable<Contact> table = createTable();
        RootPanel.get("tableContainer").add(table);

        class EditHandler implements ClickHandler, KeyUpHandler {
            public void onClick(ClickEvent event) {
                send();
            }

            public void onKeyUp(KeyUpEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    send();
                }
            }

            private void send() {
                if (validateFail()) {
                    return;
                }
                Contact contact = new Contact(idField.getValue(), nameField.getValue(),
                        surnameField.getValue(), phoneField.getValue());
                service.editContact(contact, new AsyncCallback<String>() {
                    public void onFailure(Throwable caught) {
                        errorField.setVisible(true);
                        errorField.setText(BAD_PARAMETERS);
                    }

                    public void onSuccess(String result) {
                        flushFields();
                        CellTable<Contact> table = createTable();
                        RootPanel.get(MAIN_TABLE).clear();
                        RootPanel.get(MAIN_TABLE).add(table);
                    }
                });
            }

            private boolean validateFail() {
                if (nameField.getValue().isEmpty() || surnameField.getValue().isEmpty()
                        || phoneField.getValue() == null) {
                    errorField.setVisible(true);
                    errorField.setText(BAD_PARAMETERS);
                    return true;
                }
                errorField.setVisible(false);
                return false;
            }

            private void flushFields(){
                idField.setText("");
                nameField.setText("");
                surnameField.setText("");
                errorField.setText("");
                phoneField.setText("");
            }
        }

        class DeleteHandler implements ClickHandler {
            public void onClick(ClickEvent event) {
                delete();
            }

            private void delete() {
                service.deleteContact(idField.getValue(), new AsyncCallback<String>() {
                    public void onFailure(Throwable caught) {
                        errorField.setVisible(true);
                        errorField.setText(BAD_PARAMETERS);
                    }

                    public void onSuccess(String result) {
                        flushFields();
                        CellTable<Contact> table = createTable();
                        RootPanel.get(MAIN_TABLE).clear();
                        RootPanel.get(MAIN_TABLE).add(table);
                    }
                });
            }

            private void flushFields(){
                idField.setText("");
                nameField.setText("");
                surnameField.setText("");
                errorField.setText("");
                phoneField.setText("");
            }
        }

        EditHandler editHandler = new EditHandler();
        sendButton.addClickHandler(editHandler);
        nameField.addKeyUpHandler(editHandler);
        DeleteHandler deleteHandler = new DeleteHandler();
        removeButton.addClickHandler(deleteHandler);

    }

    private CellTable<Contact> createTable() {
        CellTable<Contact> table = new CellTable<Contact>();

        TextColumn<Contact> idColumn = new TextColumn<Contact>() {
            @Override
            public String getValue(Contact contact) {
                return contact.getId().toString();
            }
        };
        idColumn.setSortable(true);

        TextColumn<Contact> nameColumn = new TextColumn<Contact>() {
            @Override
            public String getValue(Contact contact) {
                return contact.getName();
            }
        };
        nameColumn.setSortable(true);

        TextColumn<Contact> surnameColumn = new TextColumn<Contact>() {
            @Override
            public String getValue(Contact contact) {
                return contact.getSurname();
            }
        };

        TextColumn<Contact> phoneColumn = new TextColumn<Contact>() {
            @Override
            public String getValue(Contact contact) {
                return contact.getPhoneNumber().toString();
            }
        };

        table.addColumn(idColumn, "ID");
        table.addColumn(nameColumn, "Name");
        table.addColumn(surnameColumn, "Surname");
        table.addColumn(phoneColumn, "Phone number");

        ListDataProvider<Contact> dataProvider = new ListDataProvider<Contact>();
        table.setStyleName("table table-bordered table-hover");
        dataProvider.addDataDisplay(table);

        final List<Contact> list = dataProvider.getList();

        service.getContacts(new AsyncCallback<List<Contact>>() {
            public void onFailure(Throwable caught) {
            }

            public void onSuccess(List<Contact> result) {
                list.addAll(result);
            }
        });

        ColumnSortEvent.ListHandler<Contact> nameColumnSortHandler = new ColumnSortEvent.ListHandler<Contact>(list);
        ColumnSortEvent.ListHandler<Contact> idColumnSortHandler = new ColumnSortEvent.ListHandler<Contact>(list);
        nameColumnSortHandler.setComparator(nameColumn, new Comparator<Contact>() {
                    public int compare(Contact o1, Contact o2) {
                        return (o2 != null) ? o1.getName().compareTo(o2.getName()) : 1;
                    }
                });
        idColumnSortHandler.setComparator(idColumn, new Comparator<Contact>() {
                    public int compare(Contact o1, Contact o2) {
                        return (o2 != null) ? o1.getId().compareTo(o2.getId()) : 1;
                    }
                });
        table.addColumnSortHandler(idColumnSortHandler);
        table.addColumnSortHandler(nameColumnSortHandler);
        table.getColumnSortList().push(nameColumn);
        return table;
    }
}
