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
public class RootCertificateService {

    private final RestClient dbSvcRestClient;

    public PageResponse<RootCertificateResponse> findAll(int page, int size) {
        SpringPage<RootCertificateResponse> result = dbSvcRestClient.get()
                .uri("/internal/root-certificates?page={page}&size={size}", page, size)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        return result != null ? result.toPageResponse() : PageResponse.<RootCertificateResponse>builder().build();
    }

    public RootCertificateResponse findById(String id) {
        return dbSvcRestClient.get()
                .uri("/internal/root-certificates/{id}", id)
                .retrieve()
                .body(RootCertificateResponse.class);
    }

    public RootCertificateResponse create(CreateRootCertificateRequest request) {
        RootCertificateResponse body = RootCertificateResponse.builder()
                .commonName(request.getCommonName())
                .organization(request.getOrganization())
                .organizationalUnit(request.getOrganizationalUnit())
                .country(request.getCountry())
                .state(request.getState())
                .locality(request.getLocality())
                .validFrom(LocalDateTime.now())
                .validTo(LocalDateTime.now().plusDays(request.getValidityDays()))
                .createdAt(LocalDateTime.now())
                .build();

        return dbSvcRestClient.post()
                .uri("/internal/root-certificates")
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(RootCertificateResponse.class);
    }

    public DeleteCertificateResponse delete(String id) {
        dbSvcRestClient.delete()
                .uri("/internal/root-certificates/{id}", id)
                .retrieve()
                .toBodilessEntity();
        return DeleteCertificateResponse.builder()
                .id(id)
                .success(true)
                .message("Gyökér tanúsítvány sikeresen törölve.")
                .build();
    }
}

