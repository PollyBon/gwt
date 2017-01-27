package com.example.client;

import com.example.shared.Contact;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import java.util.List;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("main")
public interface Service extends RemoteService {
  String editContact(Contact contact);
  String deleteContact(Integer id);
  List getContacts();
}
