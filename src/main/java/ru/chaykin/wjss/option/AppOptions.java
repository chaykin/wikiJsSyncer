package ru.chaykin.wjss.option;

import com.beust.jcommander.Parameter;
import lombok.Getter;

@Getter
public class AppOptions {
    private static final AppOptions INSTANCE = new AppOptions();

    public static AppOptions getOptions() {
	return INSTANCE;
    }

    private AppOptions() {
    }

    @Parameter(names = "--always-cert-trust", order = 15, description = "Trust all server certificates")
    private boolean alwaysCertTrust;

    @Parameter(names = { "-h", "--help" }, help = true, description = "Display help information", order = 20)
    private boolean help;
}
