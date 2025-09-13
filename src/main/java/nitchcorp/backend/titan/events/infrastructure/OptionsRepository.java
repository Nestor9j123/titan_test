package nitchcorp.backend.titan.events.infrastructure;

import nitchcorp.backend.titan.events.domain.model.Options;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface OptionsRepository extends JpaRepository<Options,Long> {
    Optional<Object> findByTrackingId(UUID trackingId) ;



    @Query(value= """
            SELECT * FROM options ORDER BY  id  DESC
            """ , nativeQuery=true)
    List<Options> getAll() ;



    Optional<Options> getByTrackingId(@Param("trackingIdOption") UUID trackingIdOption) ;

}
