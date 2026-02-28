package zp.gde.hu.alkfetmcpsvc.tool;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import zp.gde.hu.alkfetmcpsvc.dto.PageDto;
import zp.gde.hu.alkfetmcpsvc.dto.UserCertificateDto;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserCertificateTool {

    private final RestClient dbSvcRestClient;

    @Tool(description = """
            Listázza az összes felhasználói X.509 tanúsítványt az adatbázisból.
            Visszaadja a tanúsítványok nevét, státuszát, érvényességét és sorozatszámát.
            Használd, ha a felhasználó rákérdez a meglévő felhasználói tanúsítványokra.
            """)
    public List<UserCertificateDto> listUserCertificates(
            @ToolParam(description = "Oldalszám, 0-tól kezdve. Alapértelmezett: 0") int page,
            @ToolParam(description = "Oldalméret. Alapértelmezett: 10") int size) {
        PageDto<UserCertificateDto> result = dbSvcRestClient.get()
                .uri("/internal/user-certificates?page={page}&size={size}", page, size)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        return result != null ? result.getContent() : List.of();
    }

    @Tool(description = """
            Listázza egy adott gyökér tanúsítványhoz tartozó felhasználói tanúsítványokat.
            Használd, ha a felhasználó egy konkrét gyökér tanúsítvány aláírt certjeit kérdezi.
            """)
    public List<UserCertificateDto> listUserCertificatesByRootCert(
            @ToolParam(description = "A gyökér tanúsítvány azonosítója (MongoDB ObjectId)") String rootCertificateId,
            @ToolParam(description = "Oldalszám, 0-tól kezdve. Alapértelmezett: 0") int page,
            @ToolParam(description = "Oldalméret. Alapértelmezett: 10") int size) {
        PageDto<UserCertificateDto> result = dbSvcRestClient.get()
                .uri("/internal/user-certificates?page={page}&size={size}&rootCertificateId={rootId}",
                        page, size, rootCertificateId)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        return result != null ? result.getContent() : List.of();
    }

    @Tool(description = """
            Lekérdez egy adott felhasználói tanúsítványt az azonosítója (ID) alapján.
            Visszaadja a tanúsítvány részletes adatait, státuszát.
            """)
    public UserCertificateDto getUserCertificateById(
            @ToolParam(description = "A felhasználói tanúsítvány azonosítója (MongoDB ObjectId)") String id) {
        return dbSvcRestClient.get()
                .uri("/internal/user-certificates/{id}", id)
                .retrieve()
                .body(UserCertificateDto.class);
    }

    @Tool(description = """
            Megszámolja az összes felhasználói tanúsítványt, opcionálisan státusz szerint szűrve.
            Státusz lehetséges értékei: ACTIVE, REVOKED, EXPIRED.
            Használd, ha a felhasználó rákérdez a tanúsítványok számára vagy azok státuszára.
            """)
    public long countUserCertificates() {
        PageDto<UserCertificateDto> result = dbSvcRestClient.get()
                .uri("/internal/user-certificates?page=0&size=1")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        return result != null ? result.getTotalElements() : 0;
    }
}

