package com.example.backend.models.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.backend.models.entity.Rol;

@Repository
public interface IRolDAO extends MongoRepository<Rol, String> {
    
    Rol findByNombre(String nombre);

}