package com.springboot.aldiabackjava.repositories.causalesYObseraciones.causalesYObseraciones;

import com.springboot.aldiabackjava.models.causalesYobservaciones.Causal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ICausal extends JpaRepository<Causal, Long> {}
