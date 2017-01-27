package com.example.client;

import com.example.shared.Contact;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

import java.util.List;

public interface ServiceAsync {
    void editContact(Contact contact, AsyncCallback<String> callback);
    void deleteContact(Integer id, AsyncCallback<String> callback);
    void getContacts(AsyncCallback<List<Contact>> callback);

    final class Util {
        private static ServiceAsync instance;

        public static final ServiceAsync getInstance() {
            if (instance == null) {
                instance = GWT.create(Service.class);
                ServiceDefTarget target = (ServiceDefTarget) instance;
                target.setServiceEntryPoint(GWT.getModuleBaseURL() + "Service");
            }
            return instance;
        }

        private Util() {}
    }
}
