#!/bin/sh
# Wait until backend sockets exist
while [ ! -S /sockets/api1.sock ] || [ ! -S /sockets/api2.sock ]; do
    echo "Waiting for backend sockets..."
    sleep 1
done

# Start HAProxy
exec haproxy -f /usr/local/etc/haproxy/haproxy.cfg