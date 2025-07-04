package com.banreservas.model.outbound.registration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO que contiene toda la información detallada del registro de inscripción.
 * Incluye IDs, fechas, montos, estados y referencias a entidades relacionadas.
 * 
 * @author Domingo Ruiz c-djruiz@banreservas.com
 * @version 1.0
 * @since 2025-07-01
 */
@RegisterForReflection
public record RegistrationDataDto(
        @JsonProperty("id")
        Integer id,

        @JsonProperty("idTipoAvisoInscripcion")
        Integer noticeRegistrationTypeId,

        @JsonProperty("tipoConciliacion")
        Integer reconciliationType,

        @JsonProperty("numeroRegistro")
        String registrationNumber,

        @JsonProperty("fechaRegistro")
        Date registrationDate,

        @JsonProperty("fechaVencimiento")
        Date expirationDate,

        @JsonProperty("fechaLevantamientoEmbargo")
        Date seizureReleaseDate,

        @JsonProperty("comentarios")
        String comments,

        @JsonProperty("moneda")
        String currency,

        @JsonProperty("monto")
        Integer amount,

        @JsonProperty("sucursalLey")
        String branchLaw,

        @JsonProperty("otro")
        String other,

        @JsonProperty("tipoDeGarantiaMobiliario")
        Integer movableGuaranteeType,

        @JsonProperty("idUsuario")
        Integer userId,

        @JsonProperty("idOrganizacion")
        Integer organizationId,

        @JsonProperty("idSucursal")
        Integer branchId,

        @JsonProperty("idEstado")
        Integer statusId,

        @JsonProperty("idTipoEmbargo")
        Integer seizureTypeId,

        @JsonProperty("motivoLevantamientoEmbargo")
        String seizureReleaseReason,

        @JsonProperty("descripcionEstatus")
        String statusDescription,

        @JsonProperty("estadoEmbargoNoDispAdmjud")
        String seizureStateNotAvailableAdminJud,

        @JsonProperty("ejecucionDescripcionObligacionGarantizada")
        String executionGuaranteedObligationDescription,

        @JsonProperty("ejecucionDescripcionIncumplimientoDeudor")
        String executionDebtorDefaultDescription,

        @JsonProperty("ejecucionDescripcionPruebaIncumplimiento")
        String executionDefaultEvidenceDescription,

        @JsonProperty("ejecucionDescripcionMontoSaldo")
        Integer executionBalanceAmountDescription,

        @JsonProperty("ejecucionDescripcionMontofijado")
        Integer executionFixedAmountDescription,

        @JsonProperty("ejecucionCostaProcesales")
        Integer executionLegalCosts,

        @JsonProperty("numeroSentenciaPrivilegio")
        String privilegeSentenceNumber,

        @JsonProperty("tipoAvisosInscripcion")
        String noticeRegistrationType,

        @JsonProperty("idSucursalNavigation")
        Object branchNavigation,

        @JsonProperty("idUsuarioNavigation")
        Object userNavigation,

        @JsonProperty("idOrganizacionNavigation")
        Object organizationNavigation,

        @JsonProperty("acreedores")
        Object creditors,

        @JsonProperty("bienes")
        Object assets,

        @JsonProperty("deudores")
        Object debtors
) implements Serializable {
}
