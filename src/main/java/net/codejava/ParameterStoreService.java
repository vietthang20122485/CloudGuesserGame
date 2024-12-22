package net.codejava;

import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;
import org.springframework.stereotype.Service;

@Service
public class ParameterStoreService {

    private final SsmClient ssmClient;

    public ParameterStoreService() {
        this.ssmClient = SsmClient.builder().build();
    }

    public String getParameter(String parameterName) {
        GetParameterRequest request = GetParameterRequest.builder()
                .name(parameterName)
                .withDecryption(true)
                .build();

        GetParameterResponse response = ssmClient.getParameter(request);
        return response.parameter().value();
    }
}

