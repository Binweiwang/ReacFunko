package org.example.services.notificacion;

import org.example.model.Funko;
import org.example.model.Notificacion;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

public class NotificacionFunkoImp implements FunkosNotificacion{
    private static NotificacionFunkoImp instance = new NotificacionFunkoImp();
    private final Flux<Notificacion<Funko>> funkosNotificacionFlux;
    private FluxSink<Notificacion<Funko>> funkosNotificaion;
    private NotificacionFunkoImp(){
        this.funkosNotificacionFlux = Flux.<Notificacion<Funko>>create(emisor -> this.funkosNotificaion = emisor).share();
    }

    public static NotificacionFunkoImp getInstance() {
        if (instance == null){
            instance = new NotificacionFunkoImp();
        }
        return instance;
    }



    @Override
    public Flux<Notificacion<Funko>> getNotificacionFunko() {
        return funkosNotificacionFlux;
    }

    @Override
    public void notify(Notificacion<Funko> notificacion) {
        funkosNotificaion.next(notificacion);
    }
}
