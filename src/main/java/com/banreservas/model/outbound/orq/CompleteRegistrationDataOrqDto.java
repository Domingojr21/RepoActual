package com.banreservas.model.outbound.orq;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.Serializable;
import java.util.Collections;

/**
 * DTO que contiene la estructura completa de datos del registro para la respuesta del orquestador.
 * Incluye todos los campos del servicio original, aunque estén vacíos.
 * 
 * @author Domingo Ruiz c-djruiz@banreservas.com
 * @version 1.0
 * @since 2025-07-01
 */
@RegisterForReflection
public class CompleteRegistrationDataOrqDto implements Serializable {

    @JsonProperty("id")
    private String id;

    @JsonProperty("idTipoAvisoInscripcion")
    private Object noticeRegistrationTypeId;

    @JsonProperty("tipoConciliacion")
    private Object reconciliationType;

    @JsonProperty("numeroRegistro")
    private Object registrationNumber;

    @JsonProperty("fechaRegistro")
    private Object registrationDate;

    @JsonProperty("fechaVencimiento")
    private Object expirationDate;

    @JsonProperty("fechaLevantamientoEmbargo")
    private Object seizureReleaseDate;

    @JsonProperty("comentarios")
    private Object comments;

    @JsonProperty("moneda")
    private Object currency;

    @JsonProperty("monto")
    private Object amount;

    @JsonProperty("sucursalLey")
    private Object branchLaw;

    @JsonProperty("otro")
    private Object other;

    @JsonProperty("tipoDeGarantiaMobiliario")
    private Object movableGuaranteeType;

    @JsonProperty("idUsuario")
    private Object userId;

    @JsonProperty("idOrganizacion")
    private Object organizationId;

    @JsonProperty("idSucusal")
    private Object branchId;

    @JsonProperty("idEstado")
    private Object statusId;

    @JsonProperty("idTipoEmbargo")
    private Object seizureTypeId;

    @JsonProperty("motivoLevantamientoEmbargo")
    private Object seizureReleaseReason;

    @JsonProperty("descripcionEstatus")
    private Object statusDescription;

    @JsonProperty("estadoEmbargoNoDispAdmjud")
    private Object seizureStateNotAvailableAdminJud;

    @JsonProperty("ejecucionDescripcionObligaciongarantizada")
    private Object executionGuaranteedObligationDescription;

    @JsonProperty("ejecucionDescripcionIncumplimientoDeudor")
    private Object executionDebtorDefaultDescription;

    @JsonProperty("ejecucionDescripcionPruebaIncumplimiento")
    private Object executionDefaultEvidenceDescription;

    @JsonProperty("ejecucionDescripcionMontoSaldo")
    private Object executionBalanceAmountDescription;

    @JsonProperty("ejecucionDescripcionMontofijado")
    private Object executionFixedAmountDescription;

    @JsonProperty("ejecucionCostaProcesales")
    private Object executionLegalCosts;

    @JsonProperty("numeroSentenciaPrivilegio")
    private Object privilegeSentenceNumber;

    @JsonProperty("tipoAvisosInscripcion")
    private Object noticeRegistrationType;

    @JsonProperty("idSucusalNavigation")
    private Object branchNavigation;

    @JsonProperty("idUsuarioNavigation")
    private Object userNavigation;

    @JsonProperty("idOrganizacionNavigation")
    private Object organizationNavigation;

    @JsonProperty("acreedores")
    private Object creditors;

    @JsonProperty("bienes")
    private Object assets;

    public CompleteRegistrationDataOrqDto() {
    }

    public static CompleteRegistrationDataOrqDto fromId(String id) {
        CompleteRegistrationDataOrqDto dto = new CompleteRegistrationDataOrqDto();
        dto.id = id;
        
        Object empty = Collections.emptyMap();
        dto.noticeRegistrationTypeId = empty;
        dto.reconciliationType = empty;
        dto.registrationNumber = empty;
        dto.registrationDate = empty;
        dto.expirationDate = empty;
        dto.seizureReleaseDate = empty;
        dto.comments = empty;
        dto.currency = empty;
        dto.amount = empty;
        dto.branchLaw = empty;
        dto.other = empty;
        dto.movableGuaranteeType = empty;
        dto.userId = empty;
        dto.organizationId = empty;
        dto.branchId = empty;
        dto.statusId = empty;
        dto.seizureTypeId = empty;
        dto.seizureReleaseReason = empty;
        dto.statusDescription = empty;
        dto.seizureStateNotAvailableAdminJud = empty;
        dto.executionGuaranteedObligationDescription = empty;
        dto.executionDebtorDefaultDescription = empty;
        dto.executionDefaultEvidenceDescription = empty;
        dto.executionBalanceAmountDescription = empty;
        dto.executionFixedAmountDescription = empty;
        dto.executionLegalCosts = empty;
        dto.privilegeSentenceNumber = empty;
        dto.noticeRegistrationType = empty;
        dto.branchNavigation = empty;
        dto.userNavigation = empty;
        dto.organizationNavigation = empty;
        dto.creditors = empty;
        dto.assets = empty;
        
        return dto;
    }

    // Getters
    public String getId() { return id; }
    public Object getNoticeRegistrationTypeId() { return noticeRegistrationTypeId; }
    public Object getReconciliationType() { return reconciliationType; }
    public Object getRegistrationNumber() { return registrationNumber; }
    public Object getRegistrationDate() { return registrationDate; }
    public Object getExpirationDate() { return expirationDate; }
    public Object getSeizureReleaseDate() { return seizureReleaseDate; }
    public Object getComments() { return comments; }
    public Object getCurrency() { return currency; }
    public Object getAmount() { return amount; }
    public Object getBranchLaw() { return branchLaw; }
    public Object getOther() { return other; }
    public Object getMovableGuaranteeType() { return movableGuaranteeType; }
    public Object getUserId() { return userId; }
    public Object getOrganizationId() { return organizationId; }
    public Object getBranchId() { return branchId; }
    public Object getStatusId() { return statusId; }
    public Object getSeizureTypeId() { return seizureTypeId; }
    public Object getSeizureReleaseReason() { return seizureReleaseReason; }
    public Object getStatusDescription() { return statusDescription; }
    public Object getSeizureStateNotAvailableAdminJud() { return seizureStateNotAvailableAdminJud; }
    public Object getExecutionGuaranteedObligationDescription() { return executionGuaranteedObligationDescription; }
    public Object getExecutionDebtorDefaultDescription() { return executionDebtorDefaultDescription; }
    public Object getExecutionDefaultEvidenceDescription() { return executionDefaultEvidenceDescription; }
    public Object getExecutionBalanceAmountDescription() { return executionBalanceAmountDescription; }
    public Object getExecutionFixedAmountDescription() { return executionFixedAmountDescription; }
    public Object getExecutionLegalCosts() { return executionLegalCosts; }
    public Object getPrivilegeSentenceNumber() { return privilegeSentenceNumber; }
    public Object getNoticeRegistrationType() { return noticeRegistrationType; }
    public Object getBranchNavigation() { return branchNavigation; }
    public Object getUserNavigation() { return userNavigation; }
    public Object getOrganizationNavigation() { return organizationNavigation; }
    public Object getCreditors() { return creditors; }
    public Object getAssets() { return assets; }
}