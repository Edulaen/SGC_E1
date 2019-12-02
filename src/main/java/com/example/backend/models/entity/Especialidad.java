package com.example.backend.models.entity;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(value = "especialidades")
public class Especialidad {

    @Id
    private ObjectId _id;
    private String nombre;
    private int duracionCita;
    private String[] listaMedicos;

    public String get_id() {
	return _id.toHexString();
    }

    public void set_id(ObjectId _id) {
	this._id = _id;
    }

    public String get_nombre() {
        return nombre;
    }

    public void set_nombre(String nombre) {
        this.nombre = nombre;
    }

    public int get_duracionCita() {
        return duracionCita;
    }

    public void set_duracionCita(int duracionCita) {
        this.duracionCita = duracionCita;
    }

    public String[] get_listaMedicos() {
        return listaMedicos;
    }

    public void set_listaMedicos(String[] listaMedicos) {
        this.listaMedicos = listaMedicos;
    }


}
