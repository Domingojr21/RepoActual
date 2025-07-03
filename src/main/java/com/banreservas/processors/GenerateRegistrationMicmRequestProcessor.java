package com.banreservas.processors;

import com.banreservas.model.inbound.orq.AssetOrqDto;
import com.banreservas.model.inbound.orq.CreditorOrqDto;
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
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ApplicationScoped
public class GenerateRegistrationMicmRequestProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(GenerateRegistrationMicmRequestProcessor.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private static final DateTimeFormatter ISO_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    @Override
    public void process(Exchange exchange) throws Exception {
        logger.info("Generando request para Master Registration Inscription MICM");

        RequestRegistrationOrqDto requestOrqDto = (RequestRegistrationOrqDto) exchange.getProperty("InitialRequest");
        
        if (requestOrqDto == null) {
            logger.error("RequestRegistrationOrqDto es nulo - Request body es requerido");
            throw new IllegalArgumentException("Request body es requerido");
        }

        String micmToken = (String) exchange.getProperty("micmToken");
        
        if (micmToken == null || micmToken.isEmpty()) {
            logger.error("Token MICM no encontrado en exchange");
            throw new IllegalArgumentException("Token de autenticación no disponible");
        }

        logger.info("Token MICM obtenido exitosamente");

        OperationsDto operations = mapOperations(requestOrqDto.operations());
        List<DebtorDto> debtors = mapDebtors(requestOrqDto.debtors());
        List<AssetDto> assets = mapAssets(requestOrqDto.assets());
        List<CreditorDto> creditors = mapCreditors(requestOrqDto.creditors());

        TokenRegistrationDto token = new TokenRegistrationDto(micmToken);
        SecurityRegistrationDto security = new SecurityRegistrationDto(token);

        RequestRegistrationDto backendRequest = new RequestRegistrationDto(
            operations, debtors, assets, creditors, security);

        exchange.getIn().setBody(backendRequest);
        logger.info("Request para registro MICM generado exitosamente");
    }

    private OperationsDto mapOperations(OperationsOrqDto operations) {
        if (operations == null) {
            throw new IllegalArgumentException("Las operaciones son requeridas");
        }

        String currentDate = generateCurrentDateString();

        return new OperationsDto(
            operations.noticeRegistrationTypeId(),
            operations.reconciliationType(),
            currentDate,
            operations.comments() != null ? operations.comments() : "string",
            operations.currency() != null ? operations.currency() : "string",
            operations.amount() != null ? operations.amount() : 0,
            operations.movableGuaranteeType(),
            0,
            "string",
            "string",
            "string",
            "string",
            0,
            0
        );
    }

    private List<DebtorDto> mapDebtors(List<DebtorOrqDto> debtors) {
        List<DebtorDto> result = new ArrayList<>();
        if (debtors != null && !debtors.isEmpty()) {
            DebtorOrqDto firstDebtor = debtors.get(0);
            result.add(new DebtorDto(
                firstDebtor.rncCedula() != null ? firstDebtor.rncCedula() : "string",
                0,
                firstDebtor.debtorName() != null ? firstDebtor.debtorName() : "string",
                firstDebtor.municipalityId() != null ? firstDebtor.municipalityId() : "string",
                firstDebtor.address() != null ? firstDebtor.address() : "string",
                firstDebtor.email() != null ? firstDebtor.email() : "string",
                firstDebtor.phone() != null ? firstDebtor.phone() : "string",
                firstDebtor.national() != null ? firstDebtor.national() : true
            ));
        } else {
            result.add(new DebtorDto("string", 0, "string", "string", "string", "string", "string", true));
        }
        return result;
    }

    private List<AssetDto> mapAssets(List<AssetOrqDto> assets) {
        List<AssetDto> result = new ArrayList<>();
        Date currentDate = generateCurrentDate();
        
        if (assets != null && !assets.isEmpty()) {
            AssetOrqDto firstAsset = assets.get(0);
            result.add(new AssetDto(
                firstAsset.propertyTypeId() != null ? firstAsset.propertyTypeId() : 1,
                firstAsset.assetTypeId() != null ? firstAsset.assetTypeId() : 1,
                firstAsset.serialNumber() != null ? firstAsset.serialNumber() : "string",
                firstAsset.assetDescription() != null ? firstAsset.assetDescription() : "string",
                firstAsset.realEstateIncorporation() != null ? firstAsset.realEstateIncorporation() : true,
                firstAsset.realEstateIncorporationDescription() != null ? firstAsset.realEstateIncorporationDescription() : "string",
                "string",
                "string", 
                "string",
                currentDate,
                firstAsset.registrationRecord() != null ? firstAsset.registrationRecord() : "string",
                firstAsset.propertyLocation() != null ? firstAsset.propertyLocation() : "string"
            ));
        } else {
            result.add(new AssetDto(1, 1, "string", "string", true, "string", "string", "string", "string", currentDate, "string", "string"));
        }
        return result;
    }

    private List<CreditorDto> mapCreditors(List<CreditorOrqDto> creditors) {
        List<CreditorDto> result = new ArrayList<>();
        Date currentDate = generateCurrentDate();
        
        if (creditors != null && !creditors.isEmpty()) {
            CreditorOrqDto firstCreditor = creditors.get(0);
            result.add(new CreditorDto(
                firstCreditor.rncCedula() != null ? firstCreditor.rncCedula() : "string",
                firstCreditor.creditorName() != null ? firstCreditor.creditorName() : "string",
                firstCreditor.municipalityId() != null ? firstCreditor.municipalityId() : "string",
                firstCreditor.address() != null ? firstCreditor.address() : "string",
                firstCreditor.email() != null ? firstCreditor.email() : "string",
                firstCreditor.phone() != null ? firstCreditor.phone() : "string",
                currentDate,
                firstCreditor.national() != null ? firstCreditor.national() : true
            ));
        } else {
            result.add(new CreditorDto("string", "string", "string", "string", "string", "string", currentDate, true));
        }
        return result;
    }

    private String generateCurrentDateString() {
        return OffsetDateTime.now().format(ISO_DATE_FORMAT);
    }

    private Date generateCurrentDate() {
        try {
            String dateString = generateCurrentDateString();
            return DATE_FORMAT.parse(dateString);
        } catch (Exception e) {
            logger.warn("Error generando fecha actual, usando fecha por defecto");
            return new Date();
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