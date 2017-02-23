package application.persistance.repositories;

import application.persistance.entities.Mensajes;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by santiago.barandiaran on 7/12/2016.
 */
@Repository
public interface MensajesRepository extends CrudRepository<Mensajes, Integer> {
    @Query("select max(m.id) from Mensajes m")
    Optional<Integer> findMaxMessageId();

    @Query("select m from Mensajes m where m.estado = :estado and m.idSentido = :idSentido")
    List<Mensajes> findMessagesByDirectionAndState(@Param("estado") Integer estado, @Param("idSentido") Integer idSentido);

    @Query("select COUNT(m) as contador, m.estado as status from Mensajes m group by m.estado")
    List<Map<Integer, Integer>> findStatus();
}
