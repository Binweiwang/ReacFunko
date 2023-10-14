package repositorio;

import org.example.database.DatabaseManager;
import org.example.model.Funko;
import org.example.repositories.funko.FunkoReposotory;
import org.example.repositories.funko.FunkoReposotoryImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
class FunkosRepositoryImplTest {

    private FunkoReposotory funkoReposotory;

    @BeforeEach
    void setUp() throws SQLException {
        funkoReposotory = FunkoReposotoryImp.getInstance(DatabaseManager.getInstance());
        DatabaseManager.getInstance().initTables();

    }


    @Test
    void saveFunko() {
        var funko = getFunko();
        Funko savedFunko = funkoReposotory.save(funko).block();
        assertAll(() -> assertNotNull(savedFunko),
                () -> assertNotNull(savedFunko.getId()),
                () -> assertEquals(funko.getNombre(), savedFunko.getNombre()),
                () -> assertEquals(funko.getPrecio(), savedFunko.getPrecio()),
                () -> assertNotNull(savedFunko.getCod()),
                () -> assertNotNull(savedFunko.getUpdated_at()),
                () -> assertNotNull(savedFunko.getCreated_at())
        );
    }

    private static Funko getFunko() {
        var funko = Funko.builder()
                .id(1L)
                .cod(UUID.randomUUID())
                .nombre("Test")
                .myId(1L)
                .modelo("DISNEY")
                .precio(10.0)
                .fechaLanzamiento(LocalDate.now())
                .created_at(LocalDateTime.now())
                .updated_at(LocalDateTime.now())
                .build();
        return funko;
    }

    @Test
    void findFunkoById() {
        var funko = getFunko();
        Funko savedFunko = funkoReposotory.save(funko).block();
        Optional<Funko> foundFunko = funkoReposotory.findById(savedFunko.getId()).blockOptional();
        assertAll(
                () -> assertEquals(funko.getNombre(), foundFunko.get().getNombre()),
                () -> assertEquals(funko.getPrecio(), foundFunko.get().getPrecio()),
                () -> assertNotNull(foundFunko.get().getCod()),
                () -> assertNotNull(foundFunko.get().getUpdated_at()),
                () -> assertNotNull(foundFunko.get().getCreated_at())
        );
    }
//
//    @Test
//    void findAlumnoByIdNoExiste() {
//        Optional<Alumno> foundAlumno = funkoReposotory.findById(1L).blockOptional();
//        assertAll(() -> assertFalse(foundAlumno.isPresent())
//        );
//    }
//
//    @Test
//    void findAllAlumnos() {
//        Alumno alumno1 = Alumno.builder()
//                .nombre("Test-1")
//                .calificacion(9.5)
//                .build();
//        Alumno alumno2 = Alumno.builder()
//                .nombre("Test-2")
//                .calificacion(8.5)
//                .build();
//        funkoReposotory.save(alumno1).block();
//        funkoReposotory.save(alumno2).block();
//        List<Alumno> foundAlumnos = funkoReposotory.findAll().collectList().block();
//        assertEquals(2, foundAlumnos.size());
//    }
//
//    @Test
//    void findAlumnosByNombre() {
//        Alumno alumno1 = Alumno.builder()
//                .nombre("Test-1")
//                .calificacion(9.5)
//                .build();
//        Alumno alumno2 = Alumno.builder()
//                .nombre("Test-2")
//                .calificacion(8.5)
//                .build();
//        funkoReposotory.save(alumno1).block();
//        funkoReposotory.save(alumno2).block();
//        List<Alumno> foundAlumnos = funkoReposotory.findByNombre("Test").collectList().block();
//        System.out.println(foundAlumnos);
//        assertAll(() -> assertNotNull(foundAlumnos),
//                () -> assertEquals(2, foundAlumnos.size()),
//                () -> assertEquals(foundAlumnos.get(0).getNombre(), alumno1.getNombre()),
//                () -> assertEquals(foundAlumnos.get(0).getCalificacion(), alumno1.getCalificacion()),
//                () -> assertEquals(foundAlumnos.get(1).getNombre(), alumno2.getNombre()),
//                () -> assertEquals(foundAlumnos.get(1).getCalificacion(), alumno2.getCalificacion())
//        );
//    }
//
//    @Test
//    void updateAlumno() {
//        Alumno alumno = Alumno.builder()
//                .id(1L)
//                .nombre("Test")
//                .calificacion(9.5)
//                .build();
//        Alumno savedAlumno = funkoReposotory.save(alumno).block();
//        savedAlumno.setNombre("Updated");
//        savedAlumno.setCalificacion(8.5);
//        funkoReposotory.update(savedAlumno).block();
//        Optional<Alumno> foundAlumno = funkoReposotory.findById(savedAlumno.getId()).blockOptional();
//        assertAll(() -> assertTrue(foundAlumno.isPresent()),
//                () -> assertEquals(savedAlumno.getNombre(), foundAlumno.get().getNombre()),
//                () -> assertEquals(savedAlumno.getCalificacion(), foundAlumno.get().getCalificacion())
//        );
//    }
//
//    @Test
//    void deleteAlumno() {
//        Alumno alumno = Alumno.builder()
//                .id(1L)
//                .nombre("Test")
//                .calificacion(9.5)
//                .build();
//        Alumno savedAlumno = funkoReposotory.save(alumno).block();
//        funkoReposotory.deleteById(savedAlumno.getId()).block();
//        Optional<Alumno> foundAlumno = funkoReposotory.findById(savedAlumno.getId()).blockOptional();
//        assertAll(() -> assertFalse(foundAlumno.isPresent())
//        );
//    }
//
//
//    @Test
//    void deleteAllAlumnos() {
//        Alumno alumno1 = Alumno.builder()
//                .nombre("Test-1")
//                .calificacion(9.5)
//                .build();
//        Alumno alumno2 = Alumno.builder()
//                .nombre("Test-2")
//                .calificacion(8.5)
//                .build();
//        funkoReposotory.save(alumno1).block();
//        funkoReposotory.save(alumno2).block();
//        funkoReposotory.deleteAll().block();
//        List<Alumno> foundAlumnos = funkoReposotory.findAll().collectList().block();
//        assertEquals(0, foundAlumnos.size());
//    }
//
    // OJO, los id no existen ya no los comparamos aquí porque han salido al servicio

}