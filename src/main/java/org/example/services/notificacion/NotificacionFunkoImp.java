package org.example.services.notificacion;

import org.example.model.Funko;
import org.example.model.Notificacion;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

public class NotificacionFunkoImp implements FunkosNotificacion{
    // Atributos
    private static NotificacionFunkoImp instance = new NotificacionFunkoImp();
    private final Flux<Notificacion<Funko>> funkosNotificacionFlux;
    private FluxSink<Notificacion<Funko>> funkosNotificaion;
    // Constructor privado
    private NotificacionFunkoImp(){
        this.funkosNotificacionFlux = Flux.<Notificacion<Funko>>create(emisor -> this.funkosNotificaion = emisor).share();
    }

    /**
     * Singleton
     * @return instancia
     */
    public static NotificacionFunkoImp getInstance() {
        if (instance == null){
            instance = new NotificacionFunkoImp();
        }
        return instance;
    }


    /**
     * Devuelve las notificaciones
     * @return notificaciones
     */
    @Override
    public Flux<Notificacion<Funko>> getNotificacionFunko() {
        return funkosNotificacionFlux;
    }

    /**
     * Notifica una notificacion
     * @param notificacion notificacion
     */
    @Override
    public void notify(Notificacion<Funko> notificacion) {
        funkosNotificaion.next(notificacion);
    }
}
