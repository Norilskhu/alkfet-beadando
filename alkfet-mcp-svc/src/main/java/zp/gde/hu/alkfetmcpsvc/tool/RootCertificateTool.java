package zp.gde.hu.alkfetmcpsvc.tool;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import zp.gde.hu.alkfetmcpsvc.dto.PageDto;
import zp.gde.hu.alkfetmcpsvc.dto.RootCertificateDto;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RootCertificateTool {

    private final RestClient dbSvcRestClient;

    @Tool(description = """
            Listázza az összes gyökér (root) X.509 tanúsítványt az adatbázisból.
            Visszaadja a tanúsítványok nevét, szervezetét, érvényességét és sorozatszámát.
            Használd, ha a felhasználó rákérdez a meglévő gyökér tanúsítványokra.
            """)
    public List<RootCertificateDto> listRootCertificates(
            @ToolParam(description = "Oldalszám, 0-tól kezdve. Alapértelmezett: 0") int page,
            @ToolParam(description = "Oldalméret. Alapértelmezett: 10") int size) {
        PageDto<RootCertificateDto> result = dbSvcRestClient.get()
                .uri("/internal/root-certificates?page={page}&size={size}", page, size)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        return result != null ? result.getContent() : List.of();
    }

    @Tool(description = """
            Lekérdez egy adott gyökér tanúsítványt az azonosítója (ID) alapján.
            Visszaadja a tanúsítvány részletes adatait.
            Használd, ha a felhasználó egy konkrét gyökér tanúsítványra kíváncsi.
            """)
    public RootCertificateDto getRootCertificateById(
            @ToolParam(description = "A gyökér tanúsítvány azonosítója (MongoDB ObjectId)") String id) {
        return dbSvcRestClient.get()
                .uri("/internal/root-certificates/{id}", id)
                .retrieve()
                .body(RootCertificateDto.class);
    }

    @Tool(description = """
            Megszámolja, hány gyökér tanúsítvány található az adatbázisban.
            Használd, ha a felhasználó rákérdez a gyökér tanúsítványok számára.
            """)
    public long countRootCertificates() {
        PageDto<RootCertificateDto> result = dbSvcRestClient.get()
                .uri("/internal/root-certificates?page=0&size=1")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        return result != null ? result.getTotalElements() : 0;
    }
}

