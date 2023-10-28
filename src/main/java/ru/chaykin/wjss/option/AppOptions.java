package ru.chaykin.wjss.option;

import com.beust.jcommander.Parameter;
import ru.chaykin.wjss.conflict.ResolveType;

public class AppOptions {
    private static final AppOptions INSTANCE = new AppOptions();

    public static AppOptions getOptions() {
	return INSTANCE;
    }

    private AppOptions() {
    }

    @Parameter(
		    names = { "-f", "--force" }, order = 10,
		    description = "Force conflict resolution with one of strategies: (p)ostpone, (m)ine, (t)heirs",
		    validateWith = ResolveTypeValidator.class,
		    converter = ResolveTypeConverter.class)
    private ResolveType forceResolveType;

    @Parameter(names = "--always-cert-trust", order = 15, description = "Trust all server certificates")
    private boolean alwaysCertTrust;

    @Parameter(names = { "-h", "--help" }, help = true, description = "Display help information", order = 20)
    private boolean help;

    public ResolveType getForceResolveType() {
	return forceResolveType;
    }

    public boolean isAlwaysCertTrust() {
	return alwaysCertTrust;
    }

    public boolean isHelp() {
	return help;
    }
}
