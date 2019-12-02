package com.example.backend.models.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.example.backend.models.entity.Privilegio;

@Repository
public interface IPrivilegioDAO extends MongoRepository<Privilegio, String> {
 
    
    Privilegio findByNombre(String nombre);

}