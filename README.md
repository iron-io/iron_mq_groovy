IronMQ Java Client
----------------

Getting Started
===============

**Import** IronMQ client package:

    import io.iron.ironmq.*;

**Initialize** a client and get a queue object:

    client = new Client("my project", "my token");
    queue = client.queue("my_queue");

**Push** a message on the queue:

    queue.Push("Hello, world!");

**Pop** a message off the queue:

    msg = queue.get();

When you pop/get a message from the queue, it will *not* be deleted. It will
eventually go back onto the queue after a timeout if you don't delete it. (The
default timeout is 10 minutes.)

**Delete** a message from the queue:

    queue.deleteMessage(msg);
