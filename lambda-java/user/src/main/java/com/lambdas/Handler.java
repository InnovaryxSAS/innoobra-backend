package com.lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

import java.util.Map;
import java.util.HashMap;

public class Handler implements RequestHandler<Map<String, Object>, Map<String, Object>> {
    
    @Override
    public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
        LambdaLogger logger = context.getLogger();
        
        // Log de entrada
        logger.log("Función Lambda iniciada con input: " + input);
        logger.log("AWS Request ID: " + context.getAwsRequestId());
        logger.log("Function Name: " + context.getFunctionName());
        
        // Crear respuesta
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", 200);
        
        // Procesar el input
        String mensaje = "Hola desde Lambda en Java";
        
        if (input != null && input.containsKey("nombre")) {
            String nombre = (String) input.get("nombre");
            mensaje = "Hola " + nombre + " desde Lambda en Java";
        }
        
        // Agregar información adicional
        Map<String, Object> body = new HashMap<>();
        body.put("message", mensaje);
        body.put("timestamp", java.time.Instant.now().toString());
        body.put("requestId", context.getAwsRequestId());
        body.put("input", input);
        
        response.put("body", body);
        
        // Log de salida
        logger.log("Respuesta generada: " + response);
        
        return response;
    }
}
/*
public class Handler implements RequestHandler<Object, String> {
    @Override
    public String handleRequest(Object input, Context context) {
        return "Hola desde Lambda en Java";
    }
}*/
