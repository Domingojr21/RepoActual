package com.banreservas.model.outbound.orq;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.Serializable;
import java.util.Collections;

/**
 * DTO que contiene la estructura completa de datos del registro para la respuesta del orquestador.
 * Incluye todos los campos del servicio original, aunque estén vacíos.
 * 
 * @author Roberto Kepp
 * @version 1.0
 * @since 2025-07-01
 */
@RegisterForReflection
public class CompleteRegistrationDataOrqDto implements Serializable {

    @JsonProperty("id")
    private String id;

    @JsonProperty("idTipoAvisoInscripcion")
    private Object idTipoAvisoInscripcion;

    @JsonProperty("tipoConciliacion")
    private Object tipoConciliacion;

    @JsonProperty("numeroRegistro")
    private Object numeroRegistro;

    @JsonProperty("fechaRegistro")
    private Object fechaRegistro;

    @JsonProperty("fechaVencimiento")
    private Object fechaVencimiento;

    @JsonProperty("fechaLevantamientoEmbargo")
    private Object fechaLevantamientoEmbargo;

    @JsonProperty("comentarios")
    private Object comentarios;

    @JsonProperty("moneda")
    private Object moneda;

    @JsonProperty("monto")
    private Object monto;

    @JsonProperty("sucursalLey")
    private Object sucursalLey;

    @JsonProperty("otro")
    private Object otro;

    @JsonProperty("tipoDeGarantiaMobiliario")
    private Object tipoDeGarantiaMobiliario;

    @JsonProperty("idUsuario")
    private Object idUsuario;

    @JsonProperty("idOrganizacion")
    private Object idOrganizacion;

    @JsonProperty("idSucusal")
    private Object idSucusal;

    @JsonProperty("idEstado")
    private Object idEstado;

    @JsonProperty("idTipoEmbargo")
    private Object idTipoEmbargo;

    @JsonProperty("motivoLevantamientoEmbargo")
    private Object motivoLevantamientoEmbargo;

    @JsonProperty("descripcionEstatus")
    private Object descripcionEstatus;

    @JsonProperty("estadoEmbargoNoDispAdmjud")
    private Object estadoEmbargoNoDispAdmjud;

    @JsonProperty("ejecucionDescripcionObligaciongarantizada")
    private Object ejecucionDescripcionObligaciongarantizada;

    @JsonProperty("ejecucionDescripcionIncumplimientoDeudor")
    private Object ejecucionDescripcionIncumplimientoDeudor;

    @JsonProperty("ejecucionDescripcionPruebaIncumplimiento")
    private Object ejecucionDescripcionPruebaIncumplimiento;

    @JsonProperty("ejecucionDescripcionMontoSaldo")
    private Object ejecucionDescripcionMontoSaldo;

    @JsonProperty("ejecucionDescripcionMontofijado")
    private Object ejecucionDescripcionMontofijado;

    @JsonProperty("ejecucionCostaProcesales")
    private Object ejecucionCostaProcesales;

    @JsonProperty("numeroSentenciaPrivilegio")
    private Object numeroSentenciaPrivilegio;

    @JsonProperty("tipoAvisosInscripcion")
    private Object tipoAvisosInscripcion;

    @JsonProperty("idSucusalNavigation")
    private Object idSucusalNavigation;

    @JsonProperty("idUsuarioNavigation")
    private Object idUsuarioNavigation;

    @JsonProperty("idOrganizacionNavigation")
    private Object idOrganizacionNavigation;

    @JsonProperty("acreedores")
    private Object acreedores;

    @JsonProperty("bienes")
    private Object bienes;

    public CompleteRegistrationDataOrqDto() {
        // Constructor vacío
    }

    public static CompleteRegistrationDataOrqDto fromId(String id) {
        CompleteRegistrationDataOrqDto dto = new CompleteRegistrationDataOrqDto();
        dto.id = id;
        
        // Inicializar todos los campos como objetos vacíos
        Object empty = Collections.emptyMap();
        dto.idTipoAvisoInscripcion = empty;
        dto.tipoConciliacion = empty;
        dto.numeroRegistro = empty;
        dto.fechaRegistro = empty;
        dto.fechaVencimiento = empty;
        dto.fechaLevantamientoEmbargo = empty;
        dto.comentarios = empty;
        dto.moneda = empty;
        dto.monto = empty;
        dto.sucursalLey = empty;
        dto.otro = empty;
        dto.tipoDeGarantiaMobiliario = empty;
        dto.idUsuario = empty;
        dto.idOrganizacion = empty;
        dto.idSucusal = empty;
        dto.idEstado = empty;
        dto.idTipoEmbargo = empty;
        dto.motivoLevantamientoEmbargo = empty;
        dto.descripcionEstatus = empty;
        dto.estadoEmbargoNoDispAdmjud = empty;
        dto.ejecucionDescripcionObligaciongarantizada = empty;
        dto.ejecucionDescripcionIncumplimientoDeudor = empty;
        dto.ejecucionDescripcionPruebaIncumplimiento = empty;
        dto.ejecucionDescripcionMontoSaldo = empty;
        dto.ejecucionDescripcionMontofijado = empty;
        dto.ejecucionCostaProcesales = empty;
        dto.numeroSentenciaPrivilegio = empty;
        dto.tipoAvisosInscripcion = empty;
        dto.idSucusalNavigation = empty;
        dto.idUsuarioNavigation = empty;
        dto.idOrganizacionNavigation = empty;
        dto.acreedores = empty;
        dto.bienes = empty;
        
        return dto;
    }

    // Getters
    public String getId() { return id; }
    public Object getIdTipoAvisoInscripcion() { return idTipoAvisoInscripcion; }
    public Object getTipoConciliacion() { return tipoConciliacion; }
    public Object getNumeroRegistro() { return numeroRegistro; }
    public Object getFechaRegistro() { return fechaRegistro; }
    public Object getFechaVencimiento() { return fechaVencimiento; }
    public Object getFechaLevantamientoEmbargo() { return fechaLevantamientoEmbargo; }
    public Object getComentarios() { return comentarios; }
    public Object getMoneda() { return moneda; }
    public Object getMonto() { return monto; }
    public Object getSucursalLey() { return sucursalLey; }
    public Object getOtro() { return otro; }
    public Object getTipoDeGarantiaMobiliario() { return tipoDeGarantiaMobiliario; }
    public Object getIdUsuario() { return idUsuario; }
    public Object getIdOrganizacion() { return idOrganizacion; }
    public Object getIdSucusal() { return idSucusal; }
    public Object getIdEstado() { return idEstado; }
    public Object getIdTipoEmbargo() { return idTipoEmbargo; }
    public Object getMotivoLevantamientoEmbargo() { return motivoLevantamientoEmbargo; }
    public Object getDescripcionEstatus() { return descripcionEstatus; }
    public Object getEstadoEmbargoNoDispAdmjud() { return estadoEmbargoNoDispAdmjud; }
    public Object getEjecucionDescripcionObligaciongarantizada() { return ejecucionDescripcionObligaciongarantizada; }
    public Object getEjecucionDescripcionIncumplimientoDeudor() { return ejecucionDescripcionIncumplimientoDeudor; }
    public Object getEjecucionDescripcionPruebaIncumplimiento() { return ejecucionDescripcionPruebaIncumplimiento; }
    public Object getEjecucionDescripcionMontoSaldo() { return ejecucionDescripcionMontoSaldo; }
    public Object getEjecucionDescripcionMontofijado() { return ejecucionDescripcionMontofijado; }
    public Object getEjecucionCostaProcesales() { return ejecucionCostaProcesales; }
    public Object getNumeroSentenciaPrivilegio() { return numeroSentenciaPrivilegio; }
    public Object getTipoAvisosInscripcion() { return tipoAvisosInscripcion; }
    public Object getIdSucusalNavigation() { return idSucusalNavigation; }
    public Object getIdUsuarioNavigation() { return idUsuarioNavigation; }
    public Object getIdOrganizacionNavigation() { return idOrganizacionNavigation; }
    public Object getAcreedores() { return acreedores; }
    public Object getBienes() { return bienes; }
}