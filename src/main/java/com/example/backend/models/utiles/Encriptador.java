package com.example.backend.models.utiles;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.validation.Valid;

import java.util.logging.Logger;
import java.util.logging.Level;

import com.example.backend.models.entity.Cita;
import com.example.backend.models.entity.Medico;
import com.example.backend.models.entity.Usuario;

public class Encriptador {

	private SecretKey key;
	private Cipher cipher;
	private String algoritmo;
	private int keysize;
	private String clave;
	Logger logger = Logger.getGlobal();

	public Encriptador(SecretKey key, Cipher cipher, String algoritmo, int keysize, String clave) {
		super();
		this.key = key;
		this.cipher = cipher;
		this.algoritmo = algoritmo;
		this.keysize = keysize;
		this.clave = clave;
	}

	/**
	 * Crea la Llave para encriptar/desencriptar
	 * 
	 * @param String value
	 */
	public void addKey(String value) {
		byte[] valuebytes = value.getBytes();
		key = new SecretKeySpec(Arrays.copyOf(valuebytes, keysize), algoritmo);
	}

	public String encriptar(String texto) throws UnsupportedEncodingException {
		String value = "";
		try {
			cipher = Cipher.getInstance(algoritmo);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] textobytes = texto.getBytes("utf-8");
			byte[] cipherbytes = cipher.doFinal(textobytes);
			value = Base64.getEncoder().encodeToString(cipherbytes);
		} catch (NoSuchAlgorithmException ex) {
			logger.log(Level.SEVERE, ex.getMessage());
		} catch (NoSuchPaddingException ex) {
			logger.log(Level.SEVERE,ex.getMessage());
		} catch (InvalidKeyException ex) {
			logger.log(Level.SEVERE,ex.getMessage());
		} catch (IllegalBlockSizeException ex) {
			logger.log(Level.SEVERE,ex.getMessage());
		} catch (BadPaddingException ex) {
			logger.log(Level.SEVERE,ex.getMessage());
		}
		return value;
	}

	/**
	 * Metodo para desencriptar un texto
	 * 
	 * @param texto Texto encriptado
	 * @return String texto desencriptado
	 * @throws UnsupportedEncodingException
	 */
	public String desencriptar(String texto) throws UnsupportedEncodingException {
		String str = "";
		try {
			byte[] value = Base64.getDecoder().decode(texto.getBytes("utf-8"));
			cipher = Cipher.getInstance(algoritmo);
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte[] cipherbytes = cipher.doFinal(value);
			str = new String(cipherbytes);
		} catch (InvalidKeyException ex) {
			logger.log(Level.SEVERE,ex.getMessage());
		} catch (IllegalBlockSizeException ex) {
			logger.log(Level.SEVERE,ex.getMessage());
		} catch (BadPaddingException ex) {
			logger.log(Level.SEVERE,ex.getMessage());
		} catch (NoSuchAlgorithmException ex) {
			logger.log(Level.SEVERE,ex.getMessage());
		} catch (NoSuchPaddingException ex) {
			logger.log(Level.SEVERE,ex.getMessage());
		}
		return str;
	}

	public Usuario encriptarUsuario(Usuario usuario) throws UnsupportedEncodingException {
		addKey(clave);
		usuario.setDni(encriptar(usuario.getDni()));
		usuario.setNombre(encriptar(usuario.getNombre()));
		usuario.setApellidos(encriptar(usuario.getApellidos()));
		usuario.setTelefono(encriptar(usuario.getTelefono()));
		usuario.setEmail(encriptar(usuario.getEmail()));
		usuario.setDireccion(encriptar(usuario.getDireccion()));
		usuario.setTipo(encriptar(usuario.getTipo()));
		usuario.setPassword(encriptar(usuario.getPassword()));
		usuario.setSexo(encriptar(usuario.getSexo()));
		usuario.setMedico(encriptar(usuario.getMedico()));
		usuario.setLocalidad(encriptar(usuario.getLocalidad()));
		usuario.setCentroMedico(encriptar(usuario.getCentroMedico()));
		return usuario;

	}


	public Usuario desencriptarUsuario(Usuario usuario) throws UnsupportedEncodingException {
		addKey(clave);
		usuario.setDni(desencriptar(usuario.getDni()));
		usuario.setNombre(desencriptar(usuario.getNombre()));
		usuario.setApellidos(desencriptar(usuario.getApellidos()));
		usuario.setTelefono(desencriptar(usuario.getTelefono()));
		usuario.setEmail(desencriptar(usuario.getEmail()));
		usuario.setDireccion(desencriptar(usuario.getDireccion()));
		usuario.setTipo(desencriptar(usuario.getTipo()));
		usuario.setPassword(desencriptar(usuario.getPassword()));
		usuario.setSexo(desencriptar(usuario.getSexo()));
		usuario.setMedico(desencriptar(usuario.getMedico()));
		usuario.setLocalidad(desencriptar(usuario.getLocalidad()));
		usuario.setCentroMedico(desencriptar(usuario.getCentroMedico()));
		return usuario;

	}


	public String encriptarDni(String dni) throws UnsupportedEncodingException {
		addKey(clave);
		dni = encriptar(dni);
		return dni;
	}

	public String encriptarPassword(String password) throws UnsupportedEncodingException {
		addKey(clave);
		password = encriptar(password);
		return password;
	}

	public Cita encriptarCita(Cita cita) throws UnsupportedEncodingException {
		addKey(clave);
		cita.setDniPaciente(encriptar(cita.getDniPaciente()));
		cita.setDniMedico(encriptar(cita.getDniMedico()));
		// Falta fecha
		cita.setConsulta(encriptar(cita.getConsulta()));
		cita.setEspecialidad(encriptar(cita.getEspecialidad()));
		return cita;
	}
	
	public Cita desencriptarCita(Cita cita) throws UnsupportedEncodingException {
		addKey(clave);
		cita.setDniPaciente(desencriptar(cita.getDniPaciente()));
		cita.setDniMedico(desencriptar(cita.getDniMedico()));
		// Falta fecha
		cita.setConsulta(desencriptar(cita.getConsulta()));
		cita.setEspecialidad(desencriptar(cita.getEspecialidad()));
		return cita;
	}

	public @Valid Medico encriptarMedico(@Valid Medico medico) throws UnsupportedEncodingException {
		addKey(clave);
		medico.setDni(encriptar(medico.getDni()));
		medico.setNombre(encriptar(medico.getNombre()));
		medico.setApellidos(encriptar(medico.getApellidos()));
		medico.setTelefono(encriptar(medico.getTelefono()));
		medico.setEmail(encriptar(medico.getEmail()));
		medico.setDireccion(encriptar(medico.getDireccion()));
		medico.setTipo(encriptar(medico.getTipo()));
		medico.setPassword(encriptar(medico.getPassword()));
		medico.setSexo(encriptar(medico.getSexo()));
		medico.setMedico(encriptar(medico.getMedico()));
		medico.setLocalidad(encriptar(medico.getLocalidad()));
		medico.setEspecialidad(encriptar(medico.getEspecialidad()));
		medico.setCentroMedico(encriptar(medico.getCentroMedico()));
		return medico;

	}

	public Medico desencriptarMedico(@Valid Medico medico) throws UnsupportedEncodingException {
		addKey(clave);
		medico.setDni(desencriptar(medico.getDni()));
		medico.setNombre(desencriptar(medico.getNombre()));
		medico.setApellidos(desencriptar(medico.getApellidos()));
		medico.setTelefono(desencriptar(medico.getTelefono()));
		medico.setEmail(desencriptar(medico.getEmail()));
		medico.setDireccion(desencriptar(medico.getDireccion()));
		medico.setTipo(desencriptar(medico.getTipo()));
		medico.setPassword(desencriptar(medico.getPassword()));
		medico.setSexo(desencriptar(medico.getSexo()));
		medico.setMedico(desencriptar(medico.getMedico()));
		medico.setLocalidad(desencriptar(medico.getLocalidad()));
		medico.setEspecialidad(desencriptar(medico.getEspecialidad()));
		medico.setCentroMedico(desencriptar(medico.getCentroMedico()));
		return medico;
		
	}
}
