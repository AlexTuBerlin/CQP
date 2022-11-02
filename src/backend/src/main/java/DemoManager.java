package backend.src.main.java;

import com.google.gson.Gson;
import de.tu_berlin.cqp.dictionary.DBCatalog;
import de.tu_berlin.cqp.driver.Cqp;
import de.tu_berlin.cqp.driver.Demo;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DemoManager {

    private static Map<String, Demo> demoInstances = new HashMap<>();

    private DemoManager() {}

    public static Demo getInstance(String calciteModelFile) throws IOException, SQLException {
        if(demoInstances.get(calciteModelFile) == null) {
            Demo demo = new Demo(calciteModelFile);
            demoInstances.put(calciteModelFile, demo);
            return demo;
        }
        return demoInstances.get(calciteModelFile);
    }


/** Quick and dirty testing **/	
    public static void main(String[] args) throws IOException, SQLException {
        Demo demo = DemoManager.getInstance("modelFile");

        System.out.println(demo.getSchemas());

        //print some schemas
        System.out.println(demo.getRelations("tpch-america"));
        System.out.println(demo.getRelations("tpch-europe"));


        //print some constraints
        System.out.println(demo.getExpressions("tpch-america"));
        System.out.println(demo.getExpressions("tpch-europe"));

        //TEst adding constraints
        demo.addExpression("tpch-america", "test",
            "ship * from \"tpch-america\".\"lineitem\" TO EUROPE where \"l_linenumber\" > 10");

        System.out.println(demo.getExpressions("tpch-america"));
        System.out.println(demo.getExpressions("tpch-europe"));

        demo.getPlan("select \"o_orderkey\", \"l_linenumber\" \n" +
            "from \"tpch-europe\".\"orders\", \"tpch-america\".\"lineitem\"\n" +
            " where \"l_orderkey\" = \"o_orderkey\" and \"l_linenumber\" > 25",
            "compliant"
            );
        demo.getPlan("select \"o_orderkey\", \"l_linenumber\" \n" +
                "from \"tpch-europe\".\"orders\", \"tpch-america\".\"lineitem\"\n" +
                " where \"l_orderkey\" = \"o_orderkey\" and \"l_linenumber\" > 25",
            "traditional"
        );

        demo.deleteExpression("tpch-america", "1");
        demo.addExpression("tpch-europe", "test", "ship * from \"tpch-europe\".\"orders\" TO America");

        System.out.println(demo.getExpressions("tpch-america"));
        System.out.println(demo.getExpressions("tpch-europe"));

        demo.getPlan("select \"o_orderkey\", \"l_linenumber\" \n" +
                "from \"tpch-europe\".\"orders\", \"tpch-america\".\"lineitem\"\n" +
                " where \"l_orderkey\" = \"o_orderkey\" and \"l_linenumber\" > 25",
            "compliant"
        );

        demo.getPlan("select \"o_orderkey\", \"l_linenumber\" \n" +
                "from \"tpch-europe\".\"orders\", \"tpch-america\".\"lineitem\"\n" +
                " where \"l_orderkey\" = \"o_orderkey\" and \"l_linenumber\" > 25",
            "traditional"
        );

        /**demo.getPlan("select \"o_orderkey\", \"l_linenumber\" \n" +
                "from \"tpch-europe\".\"orders\", \"tpch-america\".\"lineitem\"\n" +
                " where \"l_orderkey\" = \"o_orderkey\" and \"l_linenumber\" > 25",
            "none"
        );*/




    }

}
