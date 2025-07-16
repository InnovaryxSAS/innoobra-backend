package com.lambdas.repository;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;
import software.amazon.awssdk.services.ssm.model.ParameterNotFoundException;

public class ParameterStoreUtil {

    private static final String DB_HOST_PARAMETER = "/innobra/dev/db/host";

    public static String getDbHost() {
        try (SsmClient ssmClient = SsmClient.builder()
                .region(Region.US_EAST_1)
                .build()) {

            GetParameterRequest request = GetParameterRequest.builder()
                    .name(DB_HOST_PARAMETER)
                    .withDecryption(false) 
                    .build();

            GetParameterResponse response = ssmClient.getParameter(request);
            String host = response.parameter().value();
            
            if (host == null || host.trim().isEmpty()) {
                throw new RuntimeException("DB host parameter is empty or null");
            }
            
            return host.trim();

        } catch (ParameterNotFoundException e) {
            throw new RuntimeException("Parameter " + DB_HOST_PARAMETER + " not found in Parameter Store", e);
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving DB host from Parameter Store", e);
        }
    }
}