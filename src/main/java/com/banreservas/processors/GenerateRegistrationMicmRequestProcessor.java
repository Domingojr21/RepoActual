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

        if (requestOrqDto.operations() == null) {
            throw new IllegalArgumentException("La sección 'operaciones' es requerida");
        }
        
        if (requestOrqDto.assets() == null || requestOrqDto.assets().isEmpty()) {
            throw new IllegalArgumentException("La sección 'bienes' es requerida y no puede estar vacía");
        }
        
        if (requestOrqDto.debtors() == null || requestOrqDto.debtors().isEmpty()) {
            throw new IllegalArgumentException("La sección 'deudores' es requerida y no puede estar vacía");
        }
        
        if (requestOrqDto.creditors() == null || requestOrqDto.creditors().isEmpty()) {
            throw new IllegalArgumentException("La sección 'acreedores' es requerida y no puede estar vacía");
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
            operations.comments(),
            operations.currency(),
            operations.amount(),
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
        
        for (DebtorOrqDto debtor : debtors) {
            result.add(new DebtorDto(
                debtor.rncCedula(),
                parseInteger(debtor.debtorTypeId()),
                debtor.debtorName(),
                debtor.municipalityId(),
                debtor.address(),
                debtor.email(),
                debtor.phone(),
                debtor.national()
            ));
        }
        return result;
    }

    private List<AssetDto> mapAssets(List<AssetOrqDto> assets) {
        List<AssetDto> result = new ArrayList<>();
        Date currentDate = generateCurrentDate();
        
        for (AssetOrqDto asset : assets) {
            result.add(new AssetDto(
                asset.propertyTypeId(), // Solo enviar si tiene valor, null si no
                asset.assetTypeId(),    // Solo enviar si tiene valor, null si no
                asset.serialNumber(),
                asset.assetDescription(),
                asset.realEstateIncorporation(),
                asset.realEstateIncorporationDescription(),
                "string", // Estos campos siempre van con valores por defecto según el JSON que funciona
                "string", 
                "string",
                currentDate, // exclusionDate siempre con fecha actual
                asset.registrationRecord(),
                asset.propertyLocation()
            ));
        }
        return result;
    }

    private List<CreditorDto> mapCreditors(List<CreditorOrqDto> creditors) {
        List<CreditorDto> result = new ArrayList<>();
        Date currentDate = generateCurrentDate();
        
        for (CreditorOrqDto creditor : creditors) {
            result.add(new CreditorDto(
                creditor.rncCedula(),
                creditor.creditorName(),
                creditor.municipalityId(),
                creditor.address(),
                creditor.email(),
                creditor.phone(),
                currentDate,
                creditor.national()
            ));
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