package com.banreservas.model.outbound.registration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO que contiene toda la información detallada del registro de inscripción.
 * Incluye IDs, fechas, montos, estados y referencias a entidades relacionadas.
 * 
 * @author Roberto Kepp
 * @version 1.0
 * @since 2025-07-01
 */
@RegisterForReflection
public record RegistrationDataDto(
        @JsonProperty("id")
        Integer id,

        @JsonProperty("idTipoAvisoInscripcion")
        Integer idTipoAvisoInscripcion,

        @JsonProperty("tipoConciliacion")
        Integer tipoConciliacion,

        @JsonProperty("numeroRegistro")
        String numeroRegistro,

        @JsonProperty("fechaRegistro")
        Date fechaRegistro,

        @JsonProperty("fechaVencimiento")
        Date fechaVencimiento,

        @JsonProperty("fechaLevantamientoEmbargo")
        Date fechaLevantamientoEmbargo,

        @JsonProperty("comentarios")
        String comentarios,

        @JsonProperty("moneda")
        String moneda,

        @JsonProperty("monto")
        Integer monto,

        @JsonProperty("sucursalLey")
        String sucursalLey,

        @JsonProperty("otro")
        String otro,

        @JsonProperty("tipoDeGarantiaMobiliario")
        Integer tipoDeGarantiaMobiliario,

        @JsonProperty("idUsuario")
        Integer idUsuario,

        @JsonProperty("idOrganizacion")
        Integer idOrganizacion,

        @JsonProperty("idSucursal")
        Integer idSucursal,

        @JsonProperty("idEstado")
        Integer idEstado,

        @JsonProperty("idTipoEmbargo")
        Integer idTipoEmbargo,

        @JsonProperty("motivoLevantamientoEmbargo")
        String motivoLevantamientoEmbargo,

        @JsonProperty("descripcionEstatus")
        String descripcionEstatus,

        @JsonProperty("estadoEmbargoNoDispAdmjud")
        String estadoEmbargoNoDispAdmjud,

        @JsonProperty("ejecucionDescripcionObligacionGarantizada")
        String ejecucionDescripcionObligacionGarantizada,

        @JsonProperty("ejecucionDescripcionIncumplimientoDeudor")
        String ejecucionDescripcionIncumplimientoDeudor,

        @JsonProperty("ejecucionDescripcionPruebaIncumplimiento")
        String ejecucionDescripcionPruebaIncumplimiento,

        @JsonProperty("ejecucionDescripcionMontoSaldo")
        Integer ejecucionDescripcionMontoSaldo,

        @JsonProperty("ejecucionDescripcionMontofijado")
        Integer ejecucionDescripcionMontofijado,

        @JsonProperty("ejecucionCostaProcesales")
        Integer ejecucionCostaProcesales,

        @JsonProperty("numeroSentenciaPrivilegio")
        String numeroSentenciaPrivilegio,

        @JsonProperty("tipoAvisosInscripcion")
        String tipoAvisosInscripcion,

        @JsonProperty("idSucursalNavigation")
        Object idSucursalNavigation,

        @JsonProperty("idUsuarioNavigation")
        Object idUsuarioNavigation,

        @JsonProperty("idOrganizacionNavigation")
        Object idOrganizacionNavigation,

        @JsonProperty("acreedores")
        Object acreedores,

        @JsonProperty("bienes")
        Object bienes,

        @JsonProperty("deudores")
        Object deudores
) implements Serializable {
}