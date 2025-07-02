package com.banreservas.processors;

import com.banreservas.model.inbound.orq.CreditorOrqDto;
import com.banreservas.model.inbound.orq.CreditorOrqDto;
import com.banreservas.model.inbound.orq.CreditorOrqDto;
import com.banreservas.model.inbound.orq.AssetOrqDto;
import com.banreservas.model.inbound.orq.DebtorOrqDto;
import com.banreservas.model.inbound.orq.OperationsOrqDto;
import com.banreservas.model.inbound.orq.RequestRegistrationOrqDto;
import com.banreservas.model.inbound.registration.*;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ApplicationScoped
public class GenerateRegistrationMicmRequestProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(GenerateRegistrationMicmRequestProcessor.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    @Override
    public void process(Exchange exchange) throws Exception {
        logger.info("Generando request para Master Registro Inscripcion MICM");

        RequestRegistrationOrqDto requestOrqDto = (RequestRegistrationOrqDto) exchange.getProperty("InitialRequest");
        
        if (requestOrqDto == null) {
            logger.error("RequestRegistrationOrqDto es null - Request body es requerido");
            throw new IllegalArgumentException("Request body es requerido");
        }

        String tokenMicm = (String) exchange.getProperty("micmToken");
        
        if (tokenMicm == null || tokenMicm.isEmpty()) {
            logger.error("Token MICM no encontrado en el exchange");
            throw new IllegalArgumentException("Token de autenticación no disponible");
        }

        logger.info("Token MICM obtenido exitosamente");

        // Mapear operaciones
        OperationsDto operations = mapOperations(requestOrqDto.operations());

        // Mapear deudores
        List<DebtorDto> debtors = mapDebtors(requestOrqDto.debtors());

        // Mapear activos/bienes
        List<AssetDto> assets = mapAssets(requestOrqDto.assets());

        // Mapear acreedores
        List<CreditorDto> creditors = mapCreditors(requestOrqDto.creditors());

        // Crear token de seguridad
        TokenRegistrationDto token = new TokenRegistrationDto(tokenMicm);
        SecurityRegistrationDto security = new SecurityRegistrationDto(token);

        RequestRegistrationDto requestBackend = new RequestRegistrationDto(
            operations, debtors, assets, creditors, security);

        exchange.getIn().setBody(requestBackend);
        logger.info("Request de registro MICM generado exitosamente");
    }

    private OperationsDto mapOperations(OperationsOrqDto operations) {
        if (operations == null) {
            throw new IllegalArgumentException("Operations are required");
        }

        return new OperationsDto(
            operations.noticeRegistrationTypeId(),
            operations.reconciliationType(),
            parseDate(operations.expirationDate()),
            operations.comments(),
            operations.currency(),
            operations.amount(),
            operations.movableGuaranteeType(),
            operations.seizureTypeId(),
            operations.seizureStateNotAvailableAdminJud(),
            operations.executionGuaranteedObligationDescription(),
            operations.executionDebtorDefaultDescription(),
            operations.executionDefaultEvidenceDescription(),
            operations.executionBalanceAmountDescription(),
            operations.executionLegalCosts()
        );
    }

    private List<DebtorDto> mapDebtors(List<DebtorOrqDto> debtors) {
        List<DebtorDto> result = new ArrayList<>();
        if (debtors != null) {
            debtors.forEach(debtor -> 
                result.add(new DebtorDto(
                    debtor.rncCedula(),
                    parseInteger(debtor.debtorTypeId()),
                    debtor.debtorName(),
                    debtor.municipalityId(),
                    debtor.address(),
                    debtor.email(),
                    debtor.phone(),
                    debtor.national()
                ))
            );
        }
        return result;
    }

    private List<AssetDto> mapAssets(List<AssetOrqDto> assets) {
        List<AssetDto> result = new ArrayList<>();
        if (assets != null) {
            assets.forEach(asset -> 
                result.add(new AssetDto(
                    asset.propertyTypeId(),
                    asset.assetTypeId(),
                    asset.serialNumber(),
                    asset.assetDescription(),
                    asset.realEstateIncorporation(),
                    asset.realEstateIncorporationDescription(),
                    asset.realEstateIncorporationRegistration(),
                    asset.realEstateCadastralDistrict(),
                    asset.realEstateParcelNumber(),
                    parseDate(asset.exclusionDate()),
                    asset.registrationRecord(),
                    asset.propertyLocation()
                ))
            );
        }
        return result;
    }

    private List<CreditorDto> mapCreditors(List<CreditorOrqDto> creditors) {
        List<CreditorDto> result = new ArrayList<>();
        if (creditors != null) {
            creditors.forEach(creditor -> 
                result.add(new CreditorDto(
                    creditor.rncCedula(),
                    creditor.creditorName(),
                    creditor.municipalityId(),
                    creditor.address(),
                    creditor.email(),
                    creditor.phone(),
                    parseDate(creditor.exclusionDate()),
                    creditor.national()
                ))
            );
        }
        return result;
    }

    private Date parseDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return null;
        }
        try {
            return DATE_FORMAT.parse(dateString);
        } catch (Exception e) {
            logger.warn("Error parseando fecha: {}", dateString);
            return null;
        }
    }

    private Integer parseInteger(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            logger.warn("Error parseando entero: {}", value);
            return null;
        }
    }
}