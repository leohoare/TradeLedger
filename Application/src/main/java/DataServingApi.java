import Objects.FilterObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataServingApi {

    private static DataServingClient client = new DataServingClient();
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8500), 0);
        HttpContext eventsContext = server.createContext("/");
        eventsContext.setHandler(DataServingApi::handleRequest);
        server.start();
    }

    private static void handleRequest(HttpExchange exchange) throws IOException {
        URI uri = exchange.getRequestURI();
        String[] pathSections = uri.getPath().split("/");
        if (pathSections.length > 2 && pathSections[2].equals("search")) {
            List<FilterObject> filters = new ArrayList<FilterObject>();
            String queries = uri.getQuery();
            if (queries != null) {
                // Loop through all query strings matching filter
                for (String filterQuery : queries.split("&")) {
                    Matcher matcher = Pattern.compile("^filter=(.*)").matcher(filterQuery);
                    if (matcher.matches()) {
                        try {
                            // Extract matched filter from query string
                            FilterObject filter = objectMapper.readValue(matcher.group(1), FilterObject.class);
                            /*
                                Two types queries :
                                    1. operator = gte/lte and a specified value where range isn't allowed
                                    2. operator = eq and a specified value xor range.
                             */
                            if (filter.attribute != null &&
                                    ((filter.operator.equals("gte") || filter.operator.equals("lte"))
                                        && filter.value != null
                                        && filter.range == null)
                                    || (filter.operator.equals("eq")
                                        && (filter.value != null ^ filter.range != null))) {
                                filters.add(filter);
                            } else {
                                // Filter has validation has failed
                                HandleExchange(exchange, 400, "Incorrect filter validation","text/plain");

                            }
                        } catch (IOException e) {
                            // Filter has failed to pass
                            HandleExchange(exchange, 400, "Incorrect passing of filter","text/plain");
                        }
                    }
                }
            }
            // Passed all validation errors, search database
            HandleExchange(exchange, 200, client.QueryDatabase(pathSections[1], filters), "application/json");
        }
        // Attempting unrecognised path
        HandleExchange(exchange, 404, "Resource not found", "text/plain");
    }

    private static void HandleExchange(HttpExchange exchange, int StatusCode, String response, String contentType) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(StatusCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
        return;
    }
}

