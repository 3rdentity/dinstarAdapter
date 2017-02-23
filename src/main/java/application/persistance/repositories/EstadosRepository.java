package application.persistance.repositories;

import application.persistance.entities.Estados;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by santiago.barandiaran on 7/12/2016.
 */
@Repository
public interface EstadosRepository extends CrudRepository<Estados, Integer> {

    @Query("select e from Estados e")
    List<Estados> findAllStates();
}
