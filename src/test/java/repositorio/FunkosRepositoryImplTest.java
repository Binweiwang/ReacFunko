package repositorio;

import org.example.database.DatabaseManager;
import org.example.model.Funko;
import org.example.repositories.funko.FunkoRepository;
import org.example.repositories.funko.FunkoRepositoryImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class FunkosRepositoryImplTest {

    private FunkoRepository funkoReposotory;

    @BeforeEach
    void setUp() throws SQLException {
        funkoReposotory = FunkoRepositoryImp.getInstance(DatabaseManager.getInstance());
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
    @Test
    void findFunkoByIdNoExiste() {
        Optional<Funko> foundFunko = funkoReposotory.findById(99L).blockOptional();
        assertAll(() -> assertFalse(foundFunko.isPresent())
        );
    }
    @Test
    void findAllAlumnos() {
        var funko1 = getFunko();
        var funko2 = getFunko();
        var funko3 = getFunko();
        funkoReposotory.save(funko1).block();
        funkoReposotory.save(funko2).block();
        funkoReposotory.save(funko3).block();

        List<Funko> foundFunkos = funkoReposotory.findAll().collectList().block();
        assertEquals(3, foundFunkos.size());
    }
    @Test
    void findFunkoByNombre() {
        var funko1 = getFunko();
        var funko2 = getFunko();
        funkoReposotory.save(funko1).block();
        funkoReposotory.save(funko2).block();
        List<Funko> funkofound = funkoReposotory.findByNombre("Test").collectList().block();
        System.out.println(funkofound);
        assertAll(() -> assertNotNull(funkofound),
                () -> assertEquals(2, funkofound.size()),
                () -> assertEquals(funkofound.get(0).getNombre(), funko1.getNombre()),
                () -> assertEquals(funkofound.get(0).getPrecio(), funko1.getPrecio()),
                () -> assertEquals(funkofound.get(1).getNombre(), funko2.getNombre()),
                () -> assertEquals(funkofound.get(1).getPrecio(), funko2.getPrecio())
        );
    }
    @Test
    void updateFunko() {
        var funko = getFunko();
        Funko savedAlumno = funkoReposotory.save(funko).block();
        savedAlumno.setNombre("Updated");
        savedAlumno.setPrecio(8.5);
        funkoReposotory.update(savedAlumno).block();
        Optional<Funko> foundFunko = funkoReposotory.findById(savedAlumno.getId()).blockOptional();
        assertAll(() -> assertTrue(foundFunko.isPresent()),
                () -> assertEquals(savedAlumno.getNombre(), foundFunko.get().getNombre()),
                () -> assertEquals(savedAlumno.getPrecio(), foundFunko.get().getPrecio())
        );
    }
    @Test
    void deleteFunko() {
        var funko = getFunko();

        Funko savedFunko = funkoReposotory.save(funko).block();
        funkoReposotory.deleteById(savedFunko.getId()).block();
        Optional<Funko> foundFunko = funkoReposotory.findById(savedFunko.getId()).blockOptional();
        assertAll(() -> assertFalse(foundFunko.isPresent())
        );
    }
    @Test
    void deleteAllAlumnos() {
        var funko1 = getFunko();
        var funko2 = getFunko();
        funkoReposotory.save(funko1).block();
        funkoReposotory.save(funko2).block();
        funkoReposotory.deleteAll().block();
        List<Funko> foundFunko = funkoReposotory.findAll().collectList().block();
        assertEquals(0, foundFunko.size());
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
}