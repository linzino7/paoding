package net.paoding.analysis.analyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import net.paoding.analysis.knife.PaodingMaker;

public class TryPaodingAnalyzer {
	
	public static void main(String[] args) {
		String input = null;
		String file = null;
		String charset = null;
		String mode = null;
		String properties = PaodingMaker.DEFAULT_PROPERTIES_PATH;
		int inInput = 0;
		for (int i = 0; i < args.length; i++) {
			if (args[i] == null || (args[i] = args[i].trim()).length() == 0) {
				continue;
			}
			if (args[i].equals("--file") || args[i].equals("-f")) {
				file = args[++i];
			} else if (args[i].equals("--charset") || args[i].equals("-c")) {
				charset = args[++i];
			} else if (args[i].equals("--mode") || args[i].equals("-m")) {
				mode = args[++i];
			} else if (args[i].equals("--properties") || args[i].equals("-p")) {
				properties = args[++i];
			} else if (args[i].equals("--input") || args[i].equals("-i")) {
				inInput++;
			} else if (args[i].equals("--help") || args[i].equals("-h")
					|| args[i].equals("?")) {
				String app = System.getProperty("paoding.try.app",
						TryPaodingAnalyzer.class.getSimpleName());
				String cmd = System.getProperty("paoding.try.cmd", "java "
						+ TryPaodingAnalyzer.class.getName());
				System.out.println(app + "的用法:");
				System.out.println("\t" + cmd + " 中华人民共和国");
				System.out.println("OR:");
				System.out.println("\t" + cmd + " [--help|-h|? ][--file|-f file ][--charset|-c charset ][--properties|-p path-of-properties ][--mode|-m mode ][--input|-i ][中华人民共和国]");
				System.out.println("\n选项说明:");
				System.out.println("\t--file, -f:\n\t\t文章以文件的形式输入，在前缀加上\"classpath:\"表示从类路径中寻找该文件。");
				System.out.println("\t--charset, -c:\n\t\t文章的字符集编码，比如gbk,utf-8等。如果没有设置该选项，则使用Java环境默认的字符集编码。");
				System.out.println("\t--properties, -p:\n\t\t不读取默认的类路径下的庖丁分词属性文件，而使用指定的文件，在前缀加上\"classpath:\"表示从类路径中寻找该文件。");
				System.out.println("\t--mode, -m:\n\t\t强制使用给定的mode的分词器；可以设定为default,max或指定类名的其他mode(指定类名的，需要加前缀\"class:\")。");
				System.out.println("\t--input, -i:\n\t\t要被分词的文章内容；当没有通过-f或--file指定文章输入文件时可选择这个选项指定要被分词的内容。");
				System.out.println("\n示例:");
				System.out.println("\t" + cmd + " -h");
				System.out.println("\t" + cmd + " 中华人民共和国");
				System.out.println("\t" + cmd + " -m max 中华人民共和国");
				System.out.println("\t" + cmd + " -f e:/content.txt -c gbk");
				System.out.println("\t" + cmd + " -f e:/content.txt -c gbk -m max");
				return;
			} else {
				// 非选项的参数数组视为input
				if (!args[i].startsWith("-")
						&& (i == 0 || args[i - 1].equals("-i") || args[i - 1].equals("--input") || !args[i - 1].startsWith("-"))) {
					if (inInput == 0) {
						input = args[i];// !!没有++i
					} else {
						input = input + ' ' + args[i];// !!没有++i
					}
					inInput++;
				}
			}
		}
		if (file != null) {
			try {
				input = Estimate.Helper.readText(file, charset);
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
		PaodingAnalyzer analyzer = new PaodingAnalyzer(properties);
		if (mode != null) {
			analyzer.setMode(mode);
		}
		Estimate estimate = new Estimate(analyzer);
		boolean readInputFromConsle = false;
		while (true) {
			if (input == null || input.length() == 0 || readInputFromConsle) {
				try {
					input = getInputFromConsole();
					readInputFromConsle = true;
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
			if (input == null || input.length() == 0) {
				System.out.println("Warn: none charactors you input!!");
				continue;
			}
			else {
				estimate.test(input);
				System.out.println("--------------------------------------------------");
			}
			if (false == readInputFromConsle) {
				System.exit(0);
			}
		}
	}

	public static String getInputFromConsole() throws IOException {
		String input = null;
		System.out.println();
		System.out.println("Type the content to be analyzed below, end by \";\", exit by \"exit\" or \"quit\":");
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				System.in));
		String line;
		do {
			System.out.print("> ");
			line = reader.readLine();
			if (line == null || line.length() == 0) {
				continue;
			}
			if (line.equals("clear") || line.equals("c")) {
				input = null;
				System.out.println("> Input Cleared");
				return getInputFromConsole();
			}
			else if (line.equals("exit") || line.equals("quit") ) {
				System.out.println("Bye!");
				System.exit(0);
			}
			else {
				if (line.endsWith(";")) {
					if (line.length() > ";".length()) {
						input = line.substring(0, line.length() - ";".length());
					}
					break;
				}
				else {
					if (input == null) {
						input = line;
					} else {
						input = input + "\n" + line;
					}
				}
			}
		} while (true);
		return input == null ? null : input.trim();
	}
}
