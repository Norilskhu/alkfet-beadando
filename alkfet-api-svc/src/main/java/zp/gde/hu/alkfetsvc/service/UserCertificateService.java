package zp.gde.hu.alkfetsvc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import zp.gde.hu.alkfetsvc.dto.*;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserCertificateService {

    private final RestClient dbSvcRestClient;

    public PageResponse<UserCertificateResponse> findAll(int page, int size, String rootCertificateId) {
        String uri = rootCertificateId != null
                ? "/internal/user-certificates?page={page}&size={size}&rootCertificateId={rootId}"
                : "/internal/user-certificates?page={page}&size={size}";

        SpringPage<UserCertificateResponse> result = rootCertificateId != null
                ? dbSvcRestClient.get()
                        .uri(uri, page, size, rootCertificateId)
                        .retrieve()
                        .body(new ParameterizedTypeReference<>() {})
                : dbSvcRestClient.get()
                        .uri(uri, page, size)
                        .retrieve()
                        .body(new ParameterizedTypeReference<>() {});

        return result != null ? result.toPageResponse() : PageResponse.<UserCertificateResponse>builder().build();
    }

    public UserCertificateResponse findById(String id) {
        return dbSvcRestClient.get()
                .uri("/internal/user-certificates/{id}", id)
                .retrieve()
                .body(UserCertificateResponse.class);
    }

    public UserCertificateResponse signAndStore(SignUserCertificateRequest request) {
        UserCertificateResponse body = UserCertificateResponse.builder()
                .rootCertificateId(request.getRootCertificateId())
                .csrPem(request.getCsrPem())
                .validFrom(LocalDateTime.now())
                .validTo(LocalDateTime.now().plusDays(request.getValidityDays()))
                .status("ACTIVE")
                .storedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();

        return dbSvcRestClient.post()
                .uri("/internal/user-certificates")
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(UserCertificateResponse.class);
    }

    public DeleteCertificateResponse delete(String id) {
        dbSvcRestClient.delete()
                .uri("/internal/user-certificates/{id}", id)
                .retrieve()
                .toBodilessEntity();
        return DeleteCertificateResponse.builder()
                .id(id)
                .success(true)
                .message("Felhasználói tanúsítvány sikeresen törölve.")
                .build();
    }
}

