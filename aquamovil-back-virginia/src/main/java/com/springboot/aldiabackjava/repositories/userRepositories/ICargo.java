package com.springboot.aldiabackjava.repositories.userRepositories;

import com.springboot.aldiabackjava.models.userModels.Cargo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICargo extends JpaRepository<Cargo, Long> {
}
