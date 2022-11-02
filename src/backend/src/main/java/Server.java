package backend.src.main.java;

import backend.src.main.java.entities.database.Configuration;
import backend.src.main.java.exceptions.InvalidCalciteSchemaException;
import backend.src.main.java.services.database.ConfigurationService;
import backend.src.main.java.services.database.ConfigurationServiceImpl;
import com.google.gson.Gson;
import de.tu_berlin.cqp.driver.Demo;

import javax.servlet.MultipartConfigElement;
import java.text.SimpleDateFormat;
import java.util.Date;

import static spark.Spark.*;

public class Server {

   public static void main(String[] args) {

        Server server = new Server();

        // Setup error handling
        server.errorHandling();

        // Setup exception handling
        server.exceptionHandling();

        // Set listening port
        port(8080);

        // Set CORS header
        server.enableCors();

        // Register routes
        server.registerRoutes();
    }

    private  void registerRoutes() {
        after((req, res) -> res.type("application/json"));

        path("/api", () -> {
            before("/*", (req, res) -> {
                logInfo("Received API call: " + req.pathInfo());

                MultipartConfigElement multipartConfigElement = new MultipartConfigElement(System.getProperty("java.io.tmpdir"));
                req.raw().setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement);
            });

            path("/databases", () -> {
                path("/configuration", () -> {
                    final ConfigurationService configurationService = new ConfigurationServiceImpl();

                    options("/:id", (req, res) -> new Gson().toJson(new StandardResponse(
                        StatusResponse.SUCCESS,
                        configurationService.configurationExist(req.params(":id")) ?
                            "Configuration exists" :
                            "Configuration does not exist"
                    )));
                    post("/create", (req, res) -> {

                        String dbConfigFileId = configurationService.createConfiguration(req);

                        return new Gson().toJson(new StandardResponse(
                            StatusResponse.SUCCESS,
                            new Gson().toJsonTree(dbConfigFileId)
                        ));
                    }
                    );


                });

                get("/schemas", (req, res) -> {
                    final ConfigurationService configurationService = new ConfigurationServiceImpl();
                    Configuration configuration = configurationService.getConfiguration(req.queryParams("configurationID"));
                    //String calciteConfigurationFilePath = configuration.getFilePath();
                    String dbConfigFile = configuration.getFileName();

                    Demo demo = DemoManager.getInstance(dbConfigFile);
                    String schemas = demo.getSchemas();

                    return """
                            {
                            "status": "SUCCESS",
                            "data": """ + schemas + """
                        }""";

                });

                get("/relations", (req, res) -> {
                    final ConfigurationService configurationService = new ConfigurationServiceImpl();
                    Configuration configuration = configurationService.getConfiguration(req.queryParams("configurationID"));
                    //String calciteConfigurationFilePath = configuration.getFilePath();
                    String dbConfigFile = configuration.getFileName();
                    String schemaID = req.queryParams("schemaID");

                    Demo demo = DemoManager.getInstance(dbConfigFile);
                    String relations = demo.getRelations(schemaID);

                    return """
                        {
                        "status": "SUCCESS",
                        "data": """ + relations + """
                        }
                        """;


                });

                get("/constraints", (req, res) -> {
                    final ConfigurationService configurationService = new ConfigurationServiceImpl();
                    Configuration configuration = configurationService.getConfiguration(req.queryParams("configurationID"));
                    //String calciteConfigurationFilePath = configuration.getFilePath();
                    String dbConfigFile = configuration.getFileName();
                    String schemaID = req.queryParams("schemaID");

                    Demo demo = DemoManager.getInstance(dbConfigFile);
                    String expressions = demo.getExpressions(schemaID);

                    return """
                        {
                            "status": "SUCCESS",
                            "data": {
                                "constraints": """ + expressions + """
                            }
                        }""";

                });

                path("/constraint", () -> {
                    post("/add", (req, res) -> {
                        final ConfigurationService configurationService = new ConfigurationServiceImpl();
                        Configuration configuration = configurationService.getConfiguration(req.queryParams("configurationID"));
                        //String calciteConfigurationFilePath = configuration.getFilePath();
                        String dbConfigFile = configuration.getFileName();
                        String schemaID = req.queryParams("schemaID");
                        String description = req.queryParams("description");
                        String constraint = req.queryParams("constraint");

                        Demo demo = DemoManager.getInstance(dbConfigFile);
                        String policy = demo.addExpression(schemaID, description, constraint);

                        return """
                            {
                                "status": "SUCCESS",
                                "data": {
                                    "constraint": """ + policy + """
                                }
                            }""";

                    });

                    patch("/update", (req, res) -> {
                        final ConfigurationService configurationService = new ConfigurationServiceImpl();
                        Configuration configuration = configurationService.getConfiguration(req.queryParams("configurationID"));
                        //String calciteConfigurationFilePath = configuration.getFilePath();
                        String dbConfigFile = configuration.getFileName();
                        String schemaID = req.queryParams("schemaID");
                        String constraintID = req.queryParams("constraintID");
                        String description = req.queryParams("description");
                        String constraint = req.queryParams("constraint");
                        String status = req.queryParams("status");

                        Demo demo = DemoManager.getInstance(dbConfigFile);
                        String policy = demo.updateExpression(schemaID, constraintID, description, constraint,
                        status);

                        return """
                            {
                                "status": "SUCCESS",
                                "data": {
                                    "constraint": """ + policy + """
                                }
                            }""";

                    });

                    patch("/status", (req, res) -> {
                        final ConfigurationService configurationService = new ConfigurationServiceImpl();
                        Configuration configuration = configurationService.getConfiguration(req.queryParams("configurationID"));
                        //String calciteConfigurationFilePath = configuration.getFilePath();
                        String dbConfigFile = configuration.getFileName();
                        String schemaID = req.queryParams("schemaID");
                        String constraintID = req.queryParams("constraintID");
                        String status = req.queryParams("status");

                        Demo demo = DemoManager.getInstance(dbConfigFile);
                        String policy = demo.toggleExpression(schemaID, constraintID, status);

                        return """
                            {
                                "status": "SUCCESS",
                                "data": {
                                    "constraint": """ + policy + """
                                }
                            }""";

                    });

                    delete("/delete", (req, res) -> {
                        final ConfigurationService configurationService = new ConfigurationServiceImpl();
                        Configuration configuration = configurationService.getConfiguration(req.queryParams("configurationID"));
                        //String calciteConfigurationFilePath = configuration.getFilePath();
                        String dbConfigFile = configuration.getFileName();
                        String schemaID = req.queryParams("schemaID");
                        String constraintID = req.queryParams("constraintID");

                        Demo demo = DemoManager.getInstance(dbConfigFile);
                        boolean success = demo.deleteExpression(schemaID, constraintID);


                        return """
                            {
                                "status": "SUCCESS",
                                "data": {
                                    "constraint": {
                                        "id": """ + constraintID + """
                                    }
                                }
                            }""";

                    });
                });

                post("/query/plan", (req, res) -> {
                    final ConfigurationService configurationService = new ConfigurationServiceImpl();
                    Configuration configuration = configurationService.getConfiguration(req.queryParams("configurationID"));
                    //String calciteConfigurationFilePath = configuration.getFilePath();
                    String dbConfigFile = configuration.getFileName();
                    String query = req.queryParams("query");
                    String optimizationType = req.queryParams("optimizationType");

                    Demo demo = DemoManager.getInstance(dbConfigFile);
                    String plan = demo.getPlan(query, optimizationType);

                    return """
                        {
                            "status" : "SUCCESS",
                            "data" : {
                                "query" : {
                                    "plan" : """
                                    + plan + """
                                }
                            }
                        }""";

                });

                post("/query/execute", (req, res) -> {
                    final ConfigurationService configurationService = new ConfigurationServiceImpl();
                    Configuration configuration = configurationService.getConfiguration(req.queryParams("configurationID"));
                    String dbConfigFile = configuration.getFileName();
                    String query = req.queryParams("query");
                    String optimizationType = req.queryParams("optimizationType");

                    Demo demo = DemoManager.getInstance(dbConfigFile);
                    String result = demo.exectuteQuery(query, optimizationType);

                    return """
                        {
                        "status": "SUCCESS",
                            "data": {
                                "query": {
                                    "result": """ + result + """
                        }}}""";

                });
            });
        });
    }

    private  void enableCors() {
        options("/*", (req, res) -> {
            String accessControlRequestHeaders = req.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                res.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }

            String accessControlRequestMethod = req.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                res.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }

            return "OK";
        });

        before((req, res) -> {
            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Methods", "GET,HEAD,OPTIONS,PUT,PATCH,POST,DELETE");
        });
    }

    private  void exceptionHandling() {
        exception(InvalidCalciteSchemaException.class, (exception, req, res) -> {
            res.type("application/json");
            //res.header("Access-Control-Allow-Origin", "*");
            res.status(422);
            res.body(new Gson().toJson(new StandardResponse(StatusResponse.ERROR, exception.getMessage())));
        });
    }

    private  void errorHandling() {
        // 404
        notFound((req, res) -> {
            res.type("application/json");
            //res.header("Access-Control-Allow-Origin", "*");

            return new Gson().toJson(new StandardResponse(StatusResponse.ERROR, "Not Found (404)"));
        });

        // 500
        internalServerError((req, res) -> {
            res.type("application/json");
            res.header("Access-Control-Allow-Origin", "*");

            return new Gson().toJson(new StandardResponse(StatusResponse.ERROR, "Server Error (500)"));
        });
        //DebugScreen.enableDebugScreen();
    }

    private  void logInfo(String msg) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String timeNow = formatter.format(new Date());

        System.out.println("[LOG] [" + timeNow + "] " + msg);
    }
}
