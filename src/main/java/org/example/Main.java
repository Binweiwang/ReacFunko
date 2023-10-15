package org.example;

import org.example.database.DatabaseManager;
import org.example.repositories.funko.FunkoRepositoryImp;
import org.example.services.funko.FunkoServiceImp;
import org.example.services.notificacion.NotificacionFunkoImp;

public class Main {
    public static void main(String[] args) {
        var funkosService = FunkoServiceImp.getInstance(FunkoRepositoryImp.getInstance(DatabaseManager.getInstance()));
        var funkosNotification = NotificacionFunkoImp.getInstance();

        System.out.println("Sistema de obtención de notificaciones en Tiempo Real");
        funkosNotification.getNotificacionFunko().subscribe(
                notificacion -> {
                    switch (notificacion.tipo()) {
                        case NEW:
                            System.out.println("🟢 Funko insertado: " + notificacion.contenido());
                            break;
                        case UPDATED:
                            System.out.println("🟠 Funko actualizado: " + notificacion.contenido());
                            break;
                        case DELETED:
                            System.out.println("🔴 Funko eliminado: " + notificacion.contenido());
                            break;
                    }
                },
                error -> System.err.println("Se ha producido un error: " + error),
                () -> System.out.println("Completado")
        );
        funkosService.importarCSV().subscribe(
                funkos -> System.out.println("Funkos importados: " + funkos),
                error -> System.out.println("Error al importar los funkos: " + error.getMessage()),
                () -> System.out.println("Importación completada")
        );
        System.out.println("Funko mas caro");
        funkosService.funkoMasCaro().subscribe(
                funkoMasCaro -> System.out.println("Funko mas caro: " + funkoMasCaro),
                error -> System.out.println("Error al obtener el funko mas caro: " + error.getMessage()),
                () -> System.out.println("Obtención completada")
        );

        System.out.println("Media de los precios de los funkos");
        funkosService.mediaFunkos().subscribe(
                mediaFunkos -> System.out.println("Media de los precios de los funkos: " + mediaFunkos.toString().substring(0, 5) + "€"),
                error -> System.out.println("Error al obtener la media de los precios de los funkos: " + error.getMessage()),
                () -> System.out.println("Obtención completada")
        );

        System.out.println("Funkos por modelo");
        funkosService.funkoPorModelo().subscribe(
                funkoPorModelo -> System.out.println("Funkos por modelo: " + funkoPorModelo),
                error -> System.out.println("Error al obtener los funkos por modelo: " + error.getMessage()),
                () -> System.out.println("Obtención completada")
        );

        System.out.println("Numero de funkos por modelo");
        funkosService.numeroFunkosPorModelo().subscribe(
                numeroFunkosPorModelo -> System.out.println("Numero de funkos por modelo: " + numeroFunkosPorModelo),
                error -> System.out.println("Error al obtener el numero de funkos por modelo: " + error.getMessage()),
                () -> System.out.println("Obtención completada")
        );

        System.out.println("Funkos lanzados en 2023");
        funkosService.funkosLanzadosEn2023().subscribe(
                funkos2023 -> System.out.println("Funkos lanzados en 2023: " + funkos2023),
                error -> System.out.println("Error al obtener los funkos lanzados en 2023: " + error.getMessage()),
                () -> System.out.println("Obtención completada")
        );
        System.out.println("Numero de funkos de stitch");
        funkosService.numeroFunkosStitch().subscribe(
                numeroFunkos -> System.out.println("Numero de funkos de stitch: " + numeroFunkos),
                error -> System.out.println("Error al obtener el numero de funkos de stitch: " + error.getMessage()),
                () -> System.out.println("Obtención completada")
        );
        System.out.println("Funkos de stitch");
        funkosService.funkosStitch().subscribe(
                funkosStitch -> System.out.println("Funkos de stitch: " + funkosStitch),
                error -> System.out.println("Error al obtener los funkos de stitch: " + error.getMessage()),
                () -> System.out.println("Obtención completada")
        );
        System.exit(0);
    }

}