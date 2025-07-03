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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ApplicationScoped
public class GenerateRegistrationMicmRequestProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(GenerateRegistrationMicmRequestProcessor.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    @Override
    public void process(Exchange exchange) throws Exception {
        logger.info("Generating request for Master Registration Inscription MICM");

        RequestRegistrationOrqDto requestOrqDto = (RequestRegistrationOrqDto) exchange.getProperty("InitialRequest");
        
        if (requestOrqDto == null) {
            logger.error("RequestRegistrationOrqDto is null - Request body is required");
            throw new IllegalArgumentException("Request body is required");
        }

        String micmToken = (String) exchange.getProperty("micmToken");
        
        if (micmToken == null || micmToken.isEmpty()) {
            logger.error("MICM token not found in exchange");
            throw new IllegalArgumentException("Authentication token not available");
        }

        logger.info("MICM token obtained successfully");

        // Map operations
        OperationsDto operations = mapOperations(requestOrqDto.operations());

        // Map debtors
        List<DebtorDto> debtors = mapDebtors(requestOrqDto.debtors());

        // Map assets
        List<AssetDto> assets = mapAssets(requestOrqDto.assets());

        // Map creditors
        List<CreditorDto> creditors = mapCreditors(requestOrqDto.creditors());

        // Create security token
        TokenRegistrationDto token = new TokenRegistrationDto(micmToken);
        SecurityRegistrationDto security = new SecurityRegistrationDto(token);

        RequestRegistrationDto backendRequest = new RequestRegistrationDto(
            operations, debtors, assets, creditors, security);

        exchange.getIn().setBody(backendRequest);
        logger.info("MICM registration request generated successfully");
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
            logger.warn("Error parsing date: {}", dateString);
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
            logger.warn("Error parsing integer: {}", value);
            return null;
        }
    }
}