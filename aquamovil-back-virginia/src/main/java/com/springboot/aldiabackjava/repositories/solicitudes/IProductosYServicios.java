package com.springboot.aldiabackjava.repositories.solicitudes;

import com.springboot.aldiabackjava.models.solicitudes.ProductosYServicios;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IProductosYServicios extends JpaRepository<ProductosYServicios, Long> {
}
