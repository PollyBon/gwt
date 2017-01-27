package com.example.server;

import com.example.client.Service;
import com.example.shared.Contact;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.hibernate.Session;

import java.util.List;

public class ServiceImpl extends RemoteServiceServlet implements Service {

    public String editContact(Contact contact) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.saveOrUpdate(contact);
        session.getTransaction().commit();

        return "ok";
    }

    @Override
    public String deleteContact(Integer id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Contact contact = (Contact) session.load(Contact.class, id);
        session.delete(contact);
        session.getTransaction().commit();

        return "ok";
    }

    public List<Contact> getContacts() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        return (List<Contact>) session.createQuery("from Contact").list();
    }
}
