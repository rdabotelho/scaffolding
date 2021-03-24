package com.m2r.scaffolding.console;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;

public class Console {

	private static final String ENTER_YOUR_CHOICE = "Enter your choice";
	private static final String INVALID_VALUE = "Invalid value %s\n\n";
	private static final String INVALID_BETWEEN_VALUE = "Expected an integer value between %d and %d\n\n";
	private static final String CHOSEN = "Chosen %s\n\n";
	private static final String VALUE_REQUIRED = "Value required\n\n";

	public static Optional<String> read(Prompt prompt) {
		String result = null;
		while (true) {
			if (prompt.getOptions().isEmpty()) {
				result = read(prompt.getTitle());
				if (result.trim().equals("") && prompt.getDefaultValue() != null) {
					result = prompt.getDefaultValue();
				}
				if (result.trim().equals("") && prompt.getDefaultValue() == null) {
					if (prompt.isRequired()) {
						write(VALUE_REQUIRED);
					}
					else {
						return Optional.empty();
					}
				}
				else {
					return Optional.of(result);
				}
			}
			else {
				result = read(prompt.getTitle(), prompt.getOptions());
				if (result.trim().equals("") && prompt.getDefaultValue() != null) {
					int index = -1;
					for (int i=0; i<prompt.getOptions().size(); i++) {
						if (prompt.getOptions().get(i).equals(prompt.getDefaultValue())) {
							index = i;
							break;
						}
					}
					if (index > -1) {
						result = String.format("%d", index);
					}
				}
				if (result.matches("\\d+")) {
					int max = prompt.getOptions().size();
					int value = Integer.parseInt(result);
					if (value < 1 || value > max) {
						write(String.format(INVALID_BETWEEN_VALUE, 1, max));
					} else {
						write(String.format(CHOSEN, prompt.getOptions().get(value - 1)));
						return Optional.of(result);
					}
				}
				else {
					write(String.format(INVALID_VALUE, result));
				}
			}
		}
	}

	public static void writeln(String text) {
		write(text+"\n");
	}

	public static void write(String text) {
		System.out.print(text);
	}

	private static String read(String title, List<String> options) {
		write(String.format("%s\n", title));
		for (int i=0; i< options.size(); i++) {
			write(String.format("%d: %s\n", (i+1), options.get(i)));
		}
		return read(ENTER_YOUR_CHOICE);
	}

	private static String read(String title) {
		if (title != null) {
			write(String.format("%s ", title));
		}
		return read();
	}

    private static String read() {
		try {
	    		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	    		return br.readLine();
		}
		catch (Exception e) {
			return null;
		}
    }

}
