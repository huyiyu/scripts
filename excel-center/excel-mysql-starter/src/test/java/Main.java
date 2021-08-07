import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig.Builder;
import com.baomidou.mybatisplus.generator.config.IDbQuery;
import com.baomidou.mybatisplus.generator.config.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.TemplateConfig;
import com.baomidou.mybatisplus.generator.config.querys.MySqlQuery;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    private static String url="jdbc:mysql://localhost:3306/data_import?useUnicode=true&useSSL=false&characterEncoding=utf8";
    private static String username="data";
    private static String password="data";


    public static void main(String[] args) {



        DataSourceConfig dataSourceConfig = new DataSourceConfig.Builder(url, username, password)
            .schema("")
            .dbQuery(new MySqlQuery())
            .build();
        // 代码生成器
        AutoGenerator mpg = new AutoGenerator(dataSourceConfig);

        String projectPath = System.getProperty("user.dir");

        // 全局配置
        GlobalConfig globalConfig = new GlobalConfig
            .Builder()
            .outputDir(projectPath + "/excel-mysql-starter/src/test/java")
            .author("zyy")
            .openDir(false)
            .build();

        // 包配置
        PackageConfig packageConfig = new PackageConfig
            .Builder()
            .parent("com.huyiyu")
            .moduleName("mysql")
            .build();

        // 配置模板
        TemplateConfig templateConfig = new TemplateConfig
            .Builder()
            .mapperXml(null)
            .build();

        // 策略配置
        StrategyConfig strategyConfig = new StrategyConfig
            .Builder()
            .entityBuilder()
            .enableTableFieldAnnotation()
            .naming(NamingStrategy.underline_to_camel)
            .enableLombok()
            .controllerBuilder()
            .enableRestStyle()
            .build();

        mpg.global(globalConfig);
        mpg.packageInfo(packageConfig);
        mpg.template(templateConfig);
        mpg.strategy(strategyConfig);
        mpg.execute();

    }

}
