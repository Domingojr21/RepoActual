package com.banreservas.processors;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class DebugJsonProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(DebugJsonProcessor.class);
    
    // JSON que sabemos que funciona
    private static final String WORKING_JSON = """
    {
      "operations": {
        "noticeRegistrationTypeId": 1,
        "reconciliationType": 1,
        "expirationDate": "2025-02-19T15:42:09.378Z",
        "comments": "string",
        "currency": "string",
        "amount": 0,
        "movableGuaranteeType": 1,
        "seizureTypeId": 0,
        "seizureStateNotAvailableAdminJud": "string",
        "executionGuaranteedObligationDescription": "string",
        "executionDebtorDefaultDescription": "string",
        "executionDefaultEvidenceDescription": "string",
        "executionBalanceAmountDescription": 0,
        "executionLegalCosts": 0
      },
      "debtors": [
        {
          "rncCedula": "string",
          "debtorTypeId": 0,
          "debtorName": "string",
          "municipalityId": "string",
          "address": "string",
          "email": "string",
          "phone": "string",
          "national": true
        }
      ],
      "assets": [
        {
          "propertyTypeId": 1,
          "assetTypeId": 1,
          "serialNumber": "string",
          "assetDescription": "string",
          "realEstateIncorporation": true,
          "realEstateIncorporationDescription": "string",
          "realEstateIncorporationRegistration": "string",
          "realEstateCadastralDistrict": "string",
          "realEstateParcelNumber": "string",
          "exclusionDate": "2025-02-19T15:42:09.378Z",
          "registrationRecord": "string",
          "propertyLocation": "string"
        }
      ],
      "creditors": [
        {
          "rncCedula": "string",
          "creditorName": "string",
          "municipalityId": "string",
          "address": "string",
          "email": "string",
          "phone": "string",
          "exclusionDate": "2025-02-19T15:42:09.378Z",
          "national": true
        }
      ],
      "security": {
        "token": {
          "numero": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJpbG96YWRhQEJhbnJlc2VydmFzLmNvbSIsImp0aSI6ImQ2MDJiOTk4LTI5OGMtNGU2Zi1iMzNkLTEyM2ZkYWZkODk5ZCIsIkVtYWlsIjoiaWxvemFkYUBCYW5yZXNlcnZhcy5jb20iLCJ1aWQiOiIxMTk2IiwiaXAiOiIxMC43MC4zMi4yNSIsIm5hbWUiOiJJZGFtaXMgTWFnZGFsZW5hIiwiSW5zdGl0dWNpb25JZCI6IjEwNzYiLCJJZFN1Y3Vyc2FsIjoiMTE4MiIsIklkVXN1YXJpbyI6IjExOTYiLCJPcmdhbml6YWNpb24iOiJCYW5jbyBkZSBSZXNlcnZhcyBkZSBsYSBSZXDDumJsaWNhIERvbWluaWNhbmEuIiwiTm9tYnJlUm9sIjoiQVBJIiwiUm9sSWQiOiIxMDA5IiwiZXhwIjoxNzUxNTkxMjI1LCJpc3MiOiJzZGZzZGY0M3R5Z2g2ZXQ1aGdkaGRnaDFkZ2RmZ2RmZ3NlIyQjJGVySXN1ZXIiLCJhdWQiOiJoZ2RoZGhkZ2hnZGgifQ.X2e71IQcDkg-4CcYG3WsjG5aQCDYCZlyjB5TaEA54X8"
        }
      }
    }
    """;

    @Override
    public void process(Exchange exchange) throws Exception {
        String currentJson = exchange.getIn().getBody(String.class);
        
        logger.error("=== COMPARACION DE JSONS ===");
        logger.error("JSON que FUNCIONA:");
        logger.error(WORKING_JSON);
        logger.error("");
        logger.error("JSON que estamos ENVIANDO:");
        logger.error(currentJson);
        logger.error("");
        
        // Comparar byte por byte los primeros 200 caracteres
        logger.error("Comparación caracteres:");
        for (int i = 0; i < Math.min(200, Math.min(WORKING_JSON.length(), currentJson.length())); i++) {
            char c1 = WORKING_JSON.charAt(i);
            char c2 = currentJson.charAt(i);
            if (c1 != c2) {
                logger.error("DIFERENCIA en posición {}: esperado='{}' obtenido='{}'", i, c1, c2);
                break;
            }
        }
        logger.error("=============================");
    }
}