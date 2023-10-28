package ru.chaykin.wjss.option;

import com.beust.jcommander.Parameter;
import ru.chaykin.wjss.conflict.ResolveType;

public class AppOptions {

    @Parameter(
		    names = { "-f", "--force" }, order = 10,
		    description = "Force conflict resolution with one of strategies: (p)ostpone, (m)ine, (t)heirs",
		    validateWith = ResolveTypeValidator.class,
		    converter = ResolveTypeConverter.class)
    private ResolveType forceResolveType;

    @Parameter(names = { "-h", "--help" }, help = true, description = "Display help information", order = 20)
    private boolean help;

    public ResolveType getForceResolveType() {
	return forceResolveType;
    }

    public boolean isHelp() {
	return help;
    }
}
