package com.example.backend.models.entity;

public class Medico extends Usuario {

    private String especialidad;
    //calendario
    
    public Medico( String especialidad) {
	super();
	this.especialidad = especialidad;
    }

	public Medico() {
		// TODO Auto-generated constructor stub
	}


	public String getEspecialidad() {
	return especialidad;
    }

    public void setEspecialidad(String especialidad) {
	this.especialidad = especialidad;
    }

   
}
