package org.thaliproject.p2p.wifidirectdemo.service.messaging

enum class Message(val data: String) {
    HI("Hi!"),
    HELLO("Hello!"),
    PING("PING"),
    GET_IPS("Give group ip addresses!")
}