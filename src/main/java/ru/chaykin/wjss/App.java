package ru.chaykin.wjss;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import ru.chaykin.wjss.command.CommandFactory;
import ru.chaykin.wjss.option.AppOptions;

@Log4j2
@RequiredArgsConstructor
public class App {

    public static void main(String[] args) {
	try {
	    AppOptions options = AppOptions.getOptions();
	    var commands = CommandFactory.createCommands();

	    var cmdBuilder = JCommander.newBuilder().addObject(options);
	    JCommander jc = commands.values().stream()
			    .reduce(cmdBuilder, JCommander.Builder::addCommand, (b1, b2) -> b1)
			    .build();

	    jc.parse(args);

	    if (options.isHelp()) {
		jc.usage();
	    } else {
		String command = jc.getParsedCommand();
		if (StringUtils.isBlank(command)) {
		    jc.usage();
		    System.exit(ExitCode.INVALID_COMMAND.code);
		}

		log.debug("Execute command: {}", command);
		commands.get(command).execute();
	    }
	} catch (ParameterException e) {
	    System.out.println(e.getMessage());

	    e.usage();
	    System.exit(ExitCode.INVALID_PARAMS.code);
	}
    }
}
