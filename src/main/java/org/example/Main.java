package org.example;

import org.example.database.DatabaseManager;
import org.example.model.Funko;
import org.example.repositories.funko.FunkoReposotoryImp;
import org.example.services.funko.FunkoServiceImp;
import org.example.services.notificacion.NotificacionFunkoImp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        var funkosService = FunkoServiceImp.getInstance(FunkoReposotoryImp.getInstance(DatabaseManager.getInstance()));
        var funkosNotification = NotificacionFunkoImp.getInstance();
//
//        System.out.println("Sistema de obtención de notificaciones en Tiempo Real");
//        funkosNotification.getNotificacionFunko().subscribe(
//                notificacion -> {
//                    switch (notificacion.tipo()) {
//                        case NEW:
//                            System.out.println("🟢 Funko insertado: " + notificacion.contenido());
//                            break;
//                        case UPDATED:
//                            System.out.println("🟠 Funko actualizado: " + notificacion.contenido());
//                            break;
//                        case DELETED:
//                            System.out.println("🔴 Funko eliminado: " + notificacion.contenido());
//                            break;
//                    }
//                },
//                error -> System.err.println("Se ha producido un error: " + error),
//                () -> System.out.println("Completado")
//        );
//        System.out.println("Importando los funkos");
//        funkosService.importarCSV().subscribe(
//                funkos -> System.out.println("Funkos: " + funkos),
//                error -> System.err.println("Error al obtener todos los funkos: " + error.getMessage()),
//                () -> System.out.println("Obtención de funkos completada")
//        );


//        System.out.println("Obtenemos todos los funkos");
//        funkosService.findAll().collectList().subscribe(
//                funkos -> System.out.println("Funkos: " + funkos),
//                error -> System.err.println("Error al obtener todos los funkos: " + error.getMessage()),
//                () -> System.out.println("Obtención de funkos completada")
//        );
//        System.out.println("Insertarmos 3 funkos");
//        var funko = Funko.builder().id(0).cod(UUID.randomUUID()).nombre("Carolina").myId(0L).fechaLanzamiento(LocalDate.now()).precio(9.75).created_at(LocalDateTime.now()).updated_at(LocalDateTime.now()).modelo("DISNEY").build();
//        funkosService.save(funko).subscribe(
//                funkoInsertado -> System.out.println("Funko insertado: " + funkoInsertado),
//                error -> System.err.println("Error al insertar: " + error.getMessage()),
//                () -> System.out.println("Inserción completada")
//        );
//        var funko1 = Funko.builder().id(1).cod(UUID.randomUUID()).nombre("Carolina").myId(1L).fechaLanzamiento(LocalDate.now()).precio(10).created_at(LocalDateTime.now()).updated_at(LocalDateTime.now()).modelo("DISNEY").build();
//        funkosService.save(funko1).subscribe(
//                funkoInsertado -> System.out.println("Funko insertado: " + funkoInsertado),
//                error -> System.err.println("Error al insertar: " + error.getMessage()),
//                () -> System.out.println("Inserción completada")
//        );
//        var funko2 = Funko.builder().id(2).cod(UUID.randomUUID()).nombre("Carolina").myId(2L).fechaLanzamiento(LocalDate.now()).precio(8.59).created_at(LocalDateTime.now()).updated_at(LocalDateTime.now()).modelo("DISNEY").build();
//        funkosService.save(funko2).subscribe(
//                funkoInsertado -> System.out.println("Funko insertado: " + funkoInsertado),
//                error -> System.err.println("Error al insertar: " + error.getMessage()),
//                () -> System.out.println("Inserción completada")
//        );
//        System.out.println("Obtenemos el funko con id 1");
//        funkosService.findById(1).subscribe(
//                funko -> System.out.println("Funko encontrado: " + funko),
//                error -> System.out.println("Error al obtener el funko: " + error.getMessage()),
//                () -> System.out.println("Obetención de funko completada")
//        );
//        var funko = funkosService.findById(1L).block();
//        System.out.println("Obtenemos el funko con uuid: " + funko.getCod());
//        funkosService.findByUuid(funko.getCod()).subscribe(
//                funkoEncontrado -> System.out.println("Funko encontrado: " + funkoEncontrado),
//                error -> System.out.println("Error al obtener el funko: " + error.getMessage()),
//                () -> System.out.println("Obtención de funko completada")
//        );
//        System.out.println("Obtenemos los funkos con nombre Carolina");
//        funkosService.findAllByNombre("Carolina").subscribe(
//                funko -> System.out.println("Funkos de nombre Calorina: " + funko),
//                error -> System.out.println("Error al obtener los funkos: " + error.getMessage()),
//                () -> System.out.println("Obtención completada")
//        );
//        var funko = Funko.builder().id(1).cod(UUID.randomUUID()).nombre("CarolinaActualizado").myId(1L).fechaLanzamiento(LocalDate.now()).precio(9.75).created_at(LocalDateTime.now()).updated_at(LocalDateTime.now()).modelo("DISNEY").build();
//        System.out.println("Actualizamos el funko con id 1");
//        funkosService.update(funko).subscribe(
//                funkoActualizado -> System.out.println("Funko actualizado: " + funkoActualizado),
//                error -> System.out.println("Error al actualizar: " + error.getMessage()),
//                () -> System.out.println("Actualización completada")
//        );
//        System.out.println("Obtenemos todos los funkos");
//        funkosService.findAll().subscribe(
//                funkos -> System.out.println("Funkos: " + funkos),
//                error -> System.err.println("Error al obtener todos los funkos: " + error.getMessage()),
//                () -> System.out.println("Obtención de funkos completada")
//        );

//        System.out.println("Borramos el funko con id 1");
//        funkosService.deleteById(1L).subscribe(
//                funko -> System.out.println("Funko eliminado: " + funko),
//                error -> System.out.println("Error al eliminar: " + error.getMessage()),
//                () -> System.out.println("Eliminación completada")
//        );

//        System.out.println("Borramos el funko con id 1000");
//        funkosService.deleteById(1000L).subscribe(
//                funko -> System.out.println("Funko eliminado: " + funko),
//                error -> System.out.println("Error al eliminar: " + error.getMessage()),
//                () -> System.out.println("Eliminación completada")
//        );
//        System.out.println("Borramos todos los funkos");
//        funkosService.deleteAll().subscribe(
//                funko -> System.out.println("Funko eliminado: " + funko),
//                error -> System.out.println("Error al eliminar: " + error.getMessage()),
//                () -> System.out.println("Eliminación completada")
//        );
        System.out.println("Funko mas caro");
        funkosService.funkoMasCaro().subscribe(
                funko -> System.out.println("Funko mas caro: " + funko),
                error -> System.out.println("Error al obtener el funko mas caro: " + error.getMessage()),
                () -> System.out.println("Obtención completada")
        );

        System.exit(0);
    }

}