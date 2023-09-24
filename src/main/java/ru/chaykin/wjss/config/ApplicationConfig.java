package ru.chaykin.wjss.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import ru.chaykin.wjss.App;

public class ApplicationConfig {
    private static final Pattern EXPRESSION_REG_EXP = Pattern.compile("(\\$\\{(.+?)})");

    private static final Properties APPLICATION_PROP = loadAppProp();

    private ApplicationConfig() {
    }

    public static String get(String name) {
	String value = APPLICATION_PROP.getProperty(name);
	return StringUtils.isBlank(value) ? "" : resolve(value);
    }

    private static String resolve(String expression) {
	StringBuilder result = new StringBuilder();
	Matcher matcher = EXPRESSION_REG_EXP.matcher(expression);
	while (matcher.find()) {
	    String value = getSysProp(matcher.group(2));

	    matcher.appendReplacement(result, Matcher.quoteReplacement(value));
	}
	matcher.appendTail(result);

	return result.toString();
    }

    private static String getSysProp(String propName) {
	String val = System.getProperty(propName);
	if (StringUtils.isBlank(val)) {
	    val = System.getenv(propName);
	    if (StringUtils.isBlank(val)) {
		throw new RuntimeException(String.format("System property %s must be set!", propName));
	    }
	}
	return val;
    }

    private static Properties loadAppProp() {
	try (InputStream in = App.class.getClassLoader().getResourceAsStream("application.properties")) {
	    Properties prop = new Properties();
	    prop.load(in);

	    return prop;
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}
    }
}
