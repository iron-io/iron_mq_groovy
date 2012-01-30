package io.iron.ironmq;

import groovy.json.JsonBuilder;

/**
 * The Queue class represents a specific IronMQ queue bound to a client.
 */
public class Queue {
    final private Client client;
    final private String name;

    Queue(Client client, String name) {
        this.client = client;
        this.name = name;
    }

    /**
    * Retrieves a Message from the queue. If there are no items on the queue, an
    * HTTPException is thrown.
    *
    * @throws HTTPException If the IronMQ service returns a status other than 200 OK.
    * @throws IOException If there is an error accessing the IronMQ server.
    */
    public Message get() throws IOException {
        Map jsonObj = client.doGet("queues/" + name + "/messages");
        Message msg = new Message();
        msg.setId((String)jsonObj.get("id"));
        msg.setBody((String)jsonObj.get("body"));
        if (jsonObj.get("timeout") != null) {
            msg.setTimeout((Long)jsonObj.get("timeout"));
        }
        return msg;
    }

    /**
    * Deletes a Message from the queue.
    *
    * @param id The ID of the message to delete.
    *
    * @throws HTTPException If the IronMQ service returns a status other than 200 OK.
    * @throws IOException If there is an error accessing the IronMQ server.
    */
    public void deleteMessage(String id) throws IOException {
        client.doDelete("queues/" + name + "/messages/" + id);
    }

    /**
    * Deletes a Message from the queue.
    *
    * @param msg The message to delete.
    *
    * @throws HTTPException If the IronMQ service returns a status other than 200 OK.
    * @throws IOException If there is an error accessing the IronMQ server.
    */
    public void deleteMessage(Message msg) throws IOException {
        deleteMessage(msg.getId());
    }

    /**
    * Pushes a message onto the queue.
    *
    * @param msg The body of the message to push.
    *
    * @throws HTTPException If the IronMQ service returns a status other than 200 OK.
    * @throws IOException If there is an error accessing the IronMQ server.
    */
    public void push(String msg) throws IOException {
        push(msg, 0);
    }

    /**
    * Pushes a message onto the queue.
    *
    * @param msg The body of the message to push.
    * @param timeout The timeout of the message to push.
    *
    * @throws HTTPException If the IronMQ service returns a status other than 200 OK.
    * @throws IOException If there is an error accessing the IronMQ server.
    */
    public void push(String msg, long timeout) throws IOException {
        JsonBuilder builder = new JsonBuilder();
        builder body: msg, timeout: timeout;
        String jsonStr = builder.toString();
        client.doPost("queues/" + name + "/messages", jsonStr);
    }
}
