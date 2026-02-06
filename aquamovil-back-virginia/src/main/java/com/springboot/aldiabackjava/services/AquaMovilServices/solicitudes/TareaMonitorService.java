package com.springboot.aldiabackjava.services.AquaMovilServices.solicitudes;

import com.springboot.aldiabackjava.config.Socket.WebSocketSessionTracker;
import com.springboot.aldiabackjava.models.DTO.NotificacionDTO;
import com.springboot.aldiabackjava.models.Notificaciones;
import com.springboot.aldiabackjava.models.solicitudes.autorizacionesTrabajo.AutorizacionTrabajo;
import com.springboot.aldiabackjava.repositories.INotificaciones;
import com.springboot.aldiabackjava.repositories.solicitudes.autorizacionesTrabajo.IAutorizacionTrabajo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class TareaMonitorService {
    private final SimpMessagingTemplate messagingTemplate;
    private final IAutorizacionTrabajo iAutorizacionTrabajo;
    private final WebSocketSessionTracker sessionTracker;
    private final INotificaciones iNotificaciones;

    @Scheduled(fixedDelay = 100000)
    public void verificarTareasAtrasadas() {
        Date ahora = new Date();

        List<AutorizacionTrabajo> tareasAtrasadas = iAutorizacionTrabajo.findByCompletadoFalseOrNull().stream()
                .filter(autorizacion -> {
                    long diasAtraso = diasEntre(autorizacion.getFechaDeAsignacion(), ahora);
                    return diasAtraso > 2 &&
                            (autorizacion.getFechaUltimaNotificacion() == null ||
                                    !esMismoDia(autorizacion.getFechaUltimaNotificacion(), ahora));
                })
                .collect(Collectors.toList());

        List<AutorizacionTrabajo> notificacionesEnviadas = new ArrayList<>();
        List<Notificaciones> notificacionesAGuardar = new ArrayList<>();
        for (AutorizacionTrabajo tarea : tareasAtrasadas) {
            if (tarea.getUser() != null && sessionTracker.isUserConnected(tarea.getUser().getUsername())) {
                long diasAtraso = diasEntre(tarea.getFechaDeAsignacion(), ahora);

                String mensaje = String.format(
                        "Orden de trabajo %d atrasada, se asignó el %s y lleva %d día(s) de atraso",
                        tarea.getIdOt(), tarea.getFechaDeAsignacion(), diasAtraso
                );

                messagingTemplate.convertAndSendToUser(
                        tarea.getUser().getUsername(),
                        "/queue/notificaciones",
                        new NotificacionDTO("1", mensaje, tarea.getIdOt())
                );
                Notificaciones notificacion = Notificaciones.builder()
                        .notificacion(mensaje)
                        .fechaNotificacion(ahora)
                        .build();
                tarea.setFechaUltimaNotificacion(ahora);
                notificacionesAGuardar.add(notificacion);
                notificacionesEnviadas.add(tarea);
            }

            // Admin siempre recibe
            messagingTemplate.convertAndSend(
                    "/topic/admin/notificaciones",
                    new NotificacionDTO("ATRASO_ADMIN",
                            "Orden atrasada: " + tarea.getIdOt(), tarea.getIdOt())
            );
        }

        iAutorizacionTrabajo.saveAll(notificacionesEnviadas);
        iNotificaciones.saveAll(notificacionesAGuardar);
    }

    private long diasEntre(Date fechaInicio, Date fechaFin) {
        long diffMillis = fechaFin.getTime() - fechaInicio.getTime();
        return TimeUnit.DAYS.convert(diffMillis, TimeUnit.MILLISECONDS);
    }

    private boolean esMismoDia(Date fecha1, Date fecha2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(fecha1);
        cal2.setTime(fecha2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }
}
