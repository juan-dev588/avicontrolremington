
package com.avicontrol;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MortalidadRepository extends JpaRepository<Mortalidad, Long> {

    List<Mortalidad> findByLoteId(Long loteId);

}

