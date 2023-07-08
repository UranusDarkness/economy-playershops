package uranus.economyplayershop.config;

public class ConfigData {
    public String sqliteDatabaseLocation = "economyplayershop-sqlite.db";
    public String databaseName = "";

    public ConfigData(String sqliteDatabaseLocation, String databaseName) {
        this.sqliteDatabaseLocation = sqliteDatabaseLocation;
        this.databaseName = databaseName;
    }

    public ConfigData() {
        this("economyplayershop-sqlite.db", "shops");
    }

    public DbConfig getDatabaseConfig() {
        var dbConfig = new DbConfig();
        dbConfig.database = envOrVal("DATABASE", databaseName);
        return dbConfig;
    }

    private String envOrVal(String env, String val) {
        return System.getenv().getOrDefault(env, val);
    }
}
