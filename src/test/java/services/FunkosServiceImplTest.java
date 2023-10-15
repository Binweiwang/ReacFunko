package services;

import org.example.exceptions.FunkoNoEncontradoException;
import org.example.model.Funko;
import org.example.repositories.funko.FunkoRepository;
import org.example.services.funko.FunkoServiceImp;
import org.example.services.notificacion.FunkosNotificacion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FunkosServiceImplTest {

    @Mock
    FunkoRepository repository;
    @Mock
    FunkosNotificacion notification;
    @InjectMocks
    FunkoServiceImp service;
@Test
void findAll(){
    var funko = List.of(
            getFunko(1L,95),
            getFunko(2L,95)
    );

    when(repository.findAll()).thenReturn(Flux.fromIterable(funko));

    var result = service.findAll().collectList().block();

    assertAll("findAll",
            () -> assertEquals(result.size(), 2, "No se han recuperado dos funkos"),
            () -> assertEquals(result.get(0).getNombre(), "Test", "El primer funko no es el esperado"),
            () -> assertEquals(result.get(1).getNombre(), "Test", "El segundo funko no es el esperado"),
            () -> assertEquals(result.get(0).getPrecio(), 95.0, "El precio del primer funko no es el esperado"),
            () -> assertEquals(result.get(1).getPrecio(), 95.0, "El precio del segundo funko no es el esperado")
    );

}
        @Test
    void findAllByNombre() throws SQLException, ExecutionException, InterruptedException {
        // Arrange
        var alumnos = List.of(getFunko(1L,95));

        // Cuando se llame al método al repositorio simulamos...
        when(repository.findByNombre("Test")).thenReturn(Flux.fromIterable(alumnos));

        // Act
        var result = service.findAllByNombre("Test").collectList().block();


        // Assert
        assertAll("findAllByNombre",
                () -> assertEquals(result.size(), 1, "No se ha recuperado un Funko"),
                () -> assertEquals(result.get(0).getNombre(), "Test", "El alumno no es el esperado"),
                () -> assertEquals(result.get(0).getPrecio(), 95.0, "La calificación del alumno no es la esperada")
        );

    }

    @Test
    void findById() throws SQLException, ExecutionException, InterruptedException {
        // Arrange
        var funko =getFunko(1L,95);

        // Cuando se llame al método al repositorio simulamos...
        when(repository.findById(1L)).thenReturn(Mono.just(funko));

        // Act
        var result = service.findById(1L).blockOptional();


        // Assert
        assertAll("findById",
                () -> assertEquals(result.get().getNombre(), "Test", "El funko no es el esperado"),
                () -> assertEquals(result.get().getPrecio(), 95.0, "La calificación del funko no es la esperada")
        );


    }

    @Test
    void findByIdNoExiste() throws SQLException, ExecutionException, InterruptedException {
        // Arrange
        var funko = getFunko(1L,95);

        // Cuando se llame al método al repositorio simulamos...
        when(repository.findById(1L)).thenReturn(Mono.empty());

        var res = assertThrows(Exception.class, () -> service.findById(1L).blockOptional());
        assertTrue(res.getMessage().contains("Funko con id 1 no encontrado"));

    }

    @Test
    void save() throws SQLException, ExecutionException, InterruptedException {
        // Arrange
        var funko = getFunko(1L,95);

        // Cuando se llame al método al repositorio simulamos...
        when(repository.findByUuid(funko.getCod())).thenReturn(Mono.just(funko));
        when(repository.save(funko)).thenReturn(Mono.just(funko));

        // Act
        var result = service.save(funko).block();


        // Assert
        assertAll("save",
                () -> assertEquals(result.getNombre(), "Test", "El alumno no es el esperado"),
                () -> assertEquals(result.getPrecio(), 95.0, "La calificación del alumno no es la esperada")
        );

    }

    @Test
    void update() throws SQLException, FunkoNoEncontradoException, ExecutionException, InterruptedException {
        // Arrange
        var funko = getFunko(1L,95);

        // Cuando se llame al método al repositorio simulamos...
        when(repository.findById(1L)).thenReturn(Mono.just(funko));
        when(repository.update(funko)).thenReturn(Mono.just(funko));

        // Act
        // No lo vamos a poer testear bien por la notificación, no te preocupes
        var result = service.update(funko).block();


        // Assert
        assertAll("update",
                () -> assertEquals(result.getNombre(), "Test", "El funko no es el esperado"),
                () -> assertEquals(result.getPrecio(), 95.0, "La calificación del funko no es la esperada")
        );
    }

    @Test
    void updateNoExiste() throws SQLException, FunkoNoEncontradoException, ExecutionException, InterruptedException {
        // Arrange
        var funko = getFunko(1L,95);
        when(repository.findById(1L)).thenReturn(Mono.empty());

        // Act
        var res = assertThrows(Exception.class, () -> service.deleteById(1L).blockOptional());
        assertTrue(res.getMessage().contains("Funko con id 1 no encontrado"));


    }

    @Test
    void deleteById() throws SQLException, ExecutionException, InterruptedException {
        // Arrange
        var funko = getFunko(1l,95);
        when(repository.findById(1L)).thenReturn(Mono.just(funko));
        when(repository.deleteById(1L)).thenReturn(Mono.just(true));

        // Act
        var result = service.deleteById(1L).block();

        // Assert
        assertEquals(result, funko, "El funko no es el esperado");

    }

    @Test
    void deleteByIdNoExiste() throws SQLException, ExecutionException, InterruptedException {
        // Arrange
        var funko = getFunko(1L,95);
        when(repository.findById(1L)).thenReturn(Mono.empty());

        // Act
        var res = assertThrows(Exception.class, () -> service.deleteById(1L).blockOptional());
        assertTrue(res.getMessage().contains("Funko con id 1 no encontrado"));

        // Comprobamos que se ha llamado al método del repositorio
        verify(repository, times(1)).findById(1L);

    }

    @Test
    void deleteAll() throws SQLException, ExecutionException, InterruptedException {
        // Arrange
        var funko = getFunko(1l,95);

        // Cuando se llame al método al repositorio simulamos...
        when(repository.deleteAll()).thenReturn(Mono.empty());

        // Act
        service.deleteAll().block();

        // Comprobamos que se ha llamado al método del repositorio
        verify(repository, times(1)).deleteAll();
    }

    private static Funko getFunko(Long id,double precio) {
        var funko = Funko.builder()
                .id(id)
                .cod(UUID.randomUUID())
                .nombre("Test")
                .myId(id)
                .modelo("DISNEY")
                .precio(precio)
                .fechaLanzamiento(LocalDate.now())
                .created_at(LocalDateTime.now())
                .updated_at(LocalDateTime.now())
                .build();
        return funko;
    }
    @Test
    void funkoMascaro(){
        var funkos = List.of(
                getFunko(1L,95),
                getFunko(2L,1110)
        );

        when(repository.findAll()).thenReturn(Flux.fromIterable(funkos));

        var result = service.funkoMasCaro().block();
        assertAll("funkoMasCaro",
                () -> assertEquals(result.getNombre(), "Test", "El funko no es el esperado"),
                () -> assertEquals(result.getPrecio(), 1110.0, "El precio del funko no es el esperado"));
    }
    @Test
    void mediaFunkos(){
        var funkos = List.of(
                getFunko(1L,95),
                getFunko(2L,1110)
        );

        when(repository.findAll()).thenReturn(Flux.fromIterable(funkos));

        var result = service.mediaFunkos().block();
        assertAll("mediaFunkos",
                () -> assertEquals(result, 602.5, "La media no es la esperada"));
    }
    @Test
    void funkoPorModelo(){
        var funkos = List.of(
                getFunko(1L,95),
                getFunko(2L,1110)
        );
        when(repository.findAll()).thenReturn(Flux.fromIterable(funkos));
        Map<String, List<Funko>> result = service.funkoPorModelo().block();
        assertAll("funkoPorModelo",
                () -> assertEquals(result.get("DISNEY").size(), 2, "El número de funkos no es el esperado"),
                () -> assertEquals(result.size(),1,"El número de modelos no es el esperado")
        );
    }
    @Test
    void funkosLanzadosEn2023(){
        var funkos = List.of(
                getFunko(1L,95),
                getFunko(2L,1110)
        );
        when(repository.findAll()).thenReturn(Flux.fromIterable(funkos));
        var result = service.funkosLanzadosEn2023().block();
        assertAll("funkosLanzadosEn2023",
                () -> assertEquals(result.size(), 2, "El número de funkos no es el esperado")
        );
    }
    @Test
    void numeroFunkosStitch(){
        var funkos = List.of(
                getFunko(1L,95),
                getFunko(2L,1110)
        );
        when(repository.findAll()).thenReturn(Flux.fromIterable(funkos));
        var result = service.numeroFunkosStitch().block();
        assertAll("numeroFunkosStitch",
                () -> assertEquals(result, 0, "El número de funkos no es el esperado")
        );
    }
    @Test
    void funkoStitch(){
        var funkos = List.of(
                getFunko(1L,95),
                getFunko(2L,1110)
        );
        when(repository.findAll()).thenReturn(Flux.fromIterable(funkos));
        var result = service.funkosStitch().block();
        assertAll("funkosStitch",
                () -> assertEquals(result.size(), 0, "El número de funkos no es el esperado")
        );
    }
}
