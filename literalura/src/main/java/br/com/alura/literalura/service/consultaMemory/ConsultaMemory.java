package br.com.alura.literalura.service.consultaMemory;

import br.com.alura.literalura.service.ConsumoApi;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class ConsultaMemory {

    private final ConsumoApi consumoApi;
    private final ObjectMapper mapper = new ObjectMapper();

    public ConsultaMemory(ConsumoApi consumoApi) {
        this.consumoApi = consumoApi;
    }

    public String obterTraducao(String text, String targetLang) {
        try {
            String textoCodificado = URLEncoder.encode(text, StandardCharsets.UTF_8);
            String langpair = URLEncoder.encode("en|" + targetLang, StandardCharsets.UTF_8);

            String url = "https://api.mymemory.translated.net/get?q=" + textoCodificado + "&langpair=" + langpair;

            String json = consumoApi.obterDados(url);

            if (json == null || json.isEmpty()) return text;

            DadosTraducao traducao = mapper.readValue(json, DadosTraducao.class);

            if (traducao.dadosResposta() != null && traducao.dadosResposta().textoTraduzido() != null) {
                return traducao.dadosResposta().textoTraduzido();
            } else {
                return text;
            }
        } catch (Exception e) {
            System.err.println("Erro ao obter tradução para '" + text + "': " + e.getMessage());
            return text;
        }
    }
}
