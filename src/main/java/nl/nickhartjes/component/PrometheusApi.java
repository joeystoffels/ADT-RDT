package nl.nickhartjes.component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import nl.nickhartjes.persistence.PersistenceAdapter;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;

@Slf4j
public class PrometheusApi {

    private final ObjectMapper objectMapper;
    private final HttpClient client;

    private static final String BASE_URL = "http://prometheus-prometheus-server.exe-ops.svc.cluster.local/api/v1/query?query=";
    private static final String MEM_CATEGORY = "container_memory_usage_bytes";
    private static final String CPU_CATEGORY = "container_cpu_usage_seconds_total";


    public PrometheusApi() {
        client = HttpClient.newHttpClient();
        objectMapper = new ObjectMapper();
    }

    public long getCurrentMemUsage(PersistenceAdapter adapter) {
        String stringNow = Long.toString(Instant.now().toEpochMilli()).substring(0, 10);

        String url = BASE_URL + MEM_CATEGORY + "%7Bcontainer_name%3D%22" + adapter.getContainerName() + "%22%2C%20pod_name%3D%22" + adapter.getPodName() + "%22%7D&time=" + stringNow;

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonNode parent = objectMapper.readTree(response.body());
            return Long.valueOf(parent.path("data").path("result").get(0).path("value").get(1).asText());
        } catch (Exception e) {
            log.error("Error occurred in PrometheusAPI" + e);
            return 0;
        }
    }

    public double getTotalCpuUsage(PersistenceAdapter adapter) {
        String stringNow = Long.toString(Instant.now().toEpochMilli()).substring(0, 10);

        String url = BASE_URL + CPU_CATEGORY + "%7Bcontainer_name%3D%22" + adapter.getContainerName() + "%22%2C%20pod_name%3D%22" + adapter.getPodName() + "%22%7D&time=" + stringNow;

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonNode parent = objectMapper.readTree(response.body());
            return Double.valueOf(parent.path("data").path("result").get(0).path("value").get(1).asText());
        } catch (Exception e) {
            log.error("Error occurred in PrometheusAPI" + e);
            return 0;
        }
    }

}
