package com.springboot.aldiabackjava.controller.webSocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketMessageController {
    @MessageMapping("/notificar")
    @SendTo("/topic/notificaciones")
    public String enviarMensaje(String mensaje) {
        return mensaje;
    }
}
