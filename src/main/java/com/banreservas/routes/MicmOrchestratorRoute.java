package com.banreservas.routes;

import com.banreservas.model.inbound.orq.RequestRegistrationOrqDto;
import com.banreservas.processors.ErrorResponseProcessor;
import com.banreservas.processors.MapRegistrationBackendResponseProcessor;
import com.banreservas.util.Constants;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class MicmOrchestratorRoute extends RouteBuilder {

	private static final Logger logger = LoggerFactory.getLogger(MicmOrchestratorRoute.class);

	@Override
	public void configure() throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);

		// Manejo de errores de validación
		onException(IllegalArgumentException.class)
				.handled(true)
				.log(LoggingLevel.WARN, logger, "Error de validación en orquestador: ${exception.message}")
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
				.setProperty(Constants.MESSAGE_PROPERTIE, simple("${exception.message}"))
				.process(new ErrorResponseProcessor())
				.marshal().json(JsonLibrary.Jackson)
				.end();

		// Manejo genérico para otras excepciones
		onException(Exception.class)
				.handled(true)
				.log(LoggingLevel.ERROR, logger, "Error inesperado en orquestador: ${exception.message}")
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
				.setProperty(Constants.MESSAGE_PROPERTIE, simple("${exception.message}"))
				.process(new ErrorResponseProcessor())
				.marshal().json(JsonLibrary.Jackson)
				.end();

		restConfiguration()
				.component("platform-http")
				.contextPath("change/status/operation/micm")
				.bindingMode(RestBindingMode.json)
				.apiProperty("api.title", "Orquestador cambio estado operacion MICM")
				.apiProperty("api.version", "1.0.0")
				.apiProperty("cors", "true")
				.apiProperty("prettyPrint", "true");

		rest("/api/v1")
				.put("ChangeStatusOperation")
				.type(RequestRegistrationOrqDto.class)
				.to("direct:CambiaEstadoOperacionEndpoint");

		// Ruta principal: JSON > Login > Cambio Estado > Respuesta JSON
		from("direct:CambiaEstadoOperacionEndpoint")
				.routeId("change-status-operacion-operation-orchestrator")
				.log(LoggingLevel.INFO, logger, "Orquestación iniciada - Request ID: ${body.id}, Status: ${body.status}")
				
				// Validar que el body no esté vacío
				.choice()
				.when(body().isNull())
					.log(LoggingLevel.WARN, logger, "Request body vacío recibido")
					.setProperty(Constants.MESSAGE_PROPERTIE, constant("Request body es requerido"))
					.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
					.process(new ErrorResponseProcessor())
					.marshal().json(JsonLibrary.Jackson)
					.stop()
				.end()
				
				// Extraer y guardar headers del request como propiedades
				.setProperty("canalRq", header("Canal"))
				.setProperty("usuarioRq", header("Usuario"))
				.setProperty("terminalRq", header("Terminal"))
				.setProperty("fechaHoraRq", header("FechaHora"))
				.setProperty("versionRq", header("Version"))
				.setProperty("servicioRq", header("Servicio"))

				.setProperty("InitialRequest", body())

				// Paso 1: Login MICM
				.to("direct:login-micm")

				// Paso 2: Cambio estado operación MICM
				.to("direct:change-status-operation")

				// Procesar respuesta exitosa o manejar errores ya procesados
				.choice()
				.when(exchangeProperty("Tipo").isEqualTo("0"))
					.log(LoggingLevel.INFO, logger, "Procesando respuesta exitosa del backend")
					.process(new MapRegistrationBackendResponseProcessor())
				.otherwise()
					.log(LoggingLevel.INFO, logger, "Error ya procesado en servicios anteriores")
				.end()

				.removeHeader(Constants.HEADER_MSG_TYPE)
				.removeHeader(Constants.HEADER_CODE_RESPONSE)
				.removeHeader(Constants.HEADER_MESSAGE_RESPONSE)

				.log(LoggingLevel.INFO, logger, "Orquestación completada - Request ID: ${exchangeProperty.InitialRequest.id}")
				.end();

		// Ruta de error centralizada usando el ErrorResponseProcessor
		from(Constants.ROUTE_ERROR_500)
			.routeId("micm-error-centralized-response")
			.log(LoggingLevel.INFO, logger, "Procesando error centralizado")
			.process(new ErrorResponseProcessor())
			.marshal().json(JsonLibrary.Jackson)
			.removeHeaders("*", "CamelHttpResponseCode")
			.end();
	}
}