import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Scanner;

public class OSLab1 {

	private static PrintWriter writer;
	private static String outputNum;

	private static Map<String, Integer> definitions = new LinkedHashMap<String, Integer>();
	private static Map<String, Integer> definitionModule = new LinkedHashMap<String, Integer>();
	private static Map<String, Integer> useModule = new LinkedHashMap<String, Integer>();
	private static Map<String, Integer> defErrors = new LinkedHashMap<String, Integer>();
	private static Map<String, Integer> error5 = new LinkedHashMap<String, Integer>();
	private static Map<String, Integer> error6 = new LinkedHashMap<String, Integer>();
	private static Map<String, ArrayList<Integer>> uses = new LinkedHashMap<String, ArrayList<Integer>>();
	private static ArrayList<ArrayList<Object>> texts = new ArrayList<ArrayList<Object>>();

	public static void main(String[] args) throws IOException {

		if (args.length == 0) {
			System.out.println("Please print a file name ex: \"input-1.txt\"");
		} else if (args.length > 1) {
			System.out.println("Please only print one file name");
		} else if (!args[0].contains("input-") && !args[0].contains(".txt")) {
			System.out.println("Please format your input name properly ex: \"input-1.txt\"");
		} else {
		outputNum= args[0].replaceAll("[^0-9]", "");

    	Scanner reader = new Scanner(new BufferedReader(new FileReader(args[0])));
		try {
			boolean typeNumberDefined = false;
			int countType = 0;
			int module = 1;
			int relativeToAbsoluteCount = 0;
			int modCount = 0;
			int error8Count = 0;

			int countArg = 0;
			String name = "";

			boolean use = false;

			while (reader.hasNext()) {
				String readNext = reader.next();

				switch(countType) {
					case 1:

						if (typeNumberDefined == false) {
							countArg = Integer.parseInt(readNext) * 2;
							typeNumberDefined = true;
						} else {
							if (countArg > 0) {
								if (countArg % 2 == 0) name = readNext;
								else {
									//CHECK ERROR #1
									if (definitions.containsKey(name)) defErrors.put(name, 0);
									else definitions.put(name, texts.size() + Integer.parseInt(readNext));
									definitionModule.put(name, module);
								}
								countArg --;
							}
						}
						if (countArg == 0) { countType ++; typeNumberDefined = false; }
						break;

					case 2:
						if (typeNumberDefined == false) {
							countArg = Integer.parseInt(readNext);
							typeNumberDefined = true;
						} else {
							if (countArg > 0) {
								if (use == false) { name = readNext; use = true; useModule.put(name, module);}
								else if (Integer.parseInt(readNext) == -1) { use = false; countArg--; }
								else {
									if (!uses.containsKey(name)) uses.put(name, new ArrayList<Integer>());
									uses.get(name).add(texts.size() + Integer.parseInt(readNext));
								}
							}
						}
						if (countArg == 0) { countType++; typeNumberDefined = false; use = false; }
						break;

					case 3:
						if (typeNumberDefined == false) {
							int next = Integer.parseInt(readNext);
							modCount = next;
							countArg = next * 2;

							//CHECK ERROR 5
							for (Map.Entry<String, Integer> entry : definitionModule.entrySet()) {
								if (Integer.parseInt(entry.getValue().toString()) == module) {
									if (definitions.get(entry.getKey()) >= (texts.size() + next)) {
										error5.put(entry.getKey(), 5);
										definitions.put(entry.getKey(), relativeToAbsoluteCount);
									}
								}
							}

							//CHECK ERROR 6
							for (Map.Entry<String, Integer> entry : useModule.entrySet()) {
								if (Integer.parseInt(entry.getValue().toString()) == module) {
									for (int i = 0; i < uses.get(entry.getKey()).size(); i ++ ) {
										int useMod = uses.get(entry.getKey()).get(i);
										if (useMod >= (texts.size() + next)) {
											uses.get(entry.getKey()).remove(i);
											error6.put(entry.getKey(), module);
										}
									}
								}
							}
							error8Count += modCount;
							typeNumberDefined = true;
						} else {
							if (countArg > 0) {
								if (countArg % 2 == 0) {
									ArrayList<Object> text = new ArrayList<Object>();
									text.add(readNext);
									texts.add(text);
								}
								else {
									if (texts.get(texts.size() - 1).get(0).toString().equals("R")) {
										int originalText = Integer.parseInt(readNext);
										int relativeAddress = originalText + relativeToAbsoluteCount;
										texts.get(texts.size() - 1).add(relativeAddress);
										//CHECK ERROR #8
										int zeroAddress = ((originalText/1000) * 1000);
										int errorNum = originalText - zeroAddress;
										if (errorNum >= error8Count) {
											texts.get(texts.size() - 1).add(returnError("",0,7));
											texts.get(texts.size() - 1).set(1, zeroAddress);
										}
									} else texts.get(texts.size() - 1).add(readNext);
								}
								countArg -= 1;
							}
						}
						if (countArg == 0) { countType = 1; typeNumberDefined = false; module++;relativeToAbsoluteCount += modCount; modCount = 0;}//
						break;
					default:
						countType ++;
				}
			}
		} finally { if (reader != null) reader.close(); }

		secondRun();
		}
	}

	public static void secondRun() throws FileNotFoundException, UnsupportedEncodingException {
		ArrayList<Boolean> instructionUsed = new ArrayList<Boolean>();
		for (int i = 0; i < texts.size(); i ++) {
			instructionUsed.add(false);
		}

		File file = new File("./output/output-" + outputNum + ".txt");
		writer = new PrintWriter(file);

		writer.println("Symbol Table");
		for (Map.Entry<String, Integer> entry : definitions.entrySet()) {
			String symbolPrint = new String();

			if (defErrors.containsKey(entry.getKey())) {
				if (defErrors.get(entry.getKey()) == 0) {
					symbolPrint = entry.getKey() + "=" + entry.getValue() + " " + returnError("",0,0); }
			} else {
				symbolPrint = entry.getKey() + "=" + entry.getValue() + " " + "";
			}

			//CHECK ERROR #3
			if (!uses.containsKey(entry.getKey())) defErrors.put(entry.getKey(), 2);
			//GET ERROR #5
			if (error5.containsKey(entry.getKey())) symbolPrint += returnError("",0,4) + " ";

			writer.println(symbolPrint);
		}

		writer.println("\nMemory Map");
		for (Map.Entry<String, ArrayList<Integer>> entry : uses.entrySet()) {

			//CHECK ERROR #2
			int symbolValue = 0;
			boolean error = false;
			if (definitions.containsKey(entry.getKey())) symbolValue = definitions.get(entry.getKey());
			else error = true;

			for (int use : entry.getValue()) {
				if (error) {
					int zeroValue = (Integer.parseInt(texts.get(use).get(1).toString()) / 1000) * 1000;
					texts.get(use).set(1, zeroValue);
					texts.get(use).add(returnError(entry.getKey(), 0, 1));
				} else {
					ArrayList<Object> text = texts.get(use);
					int textValue = Integer.parseInt(text.get(1).toString());
					switch((String) text.get(0)){
						//CHECK ERROR 4
						case "E":
							if (!instructionUsed.get(use)) {
								int newValue = ((textValue / 1000) * 1000) + symbolValue;
								texts.get(use).set(1, newValue);
							} else texts.get(use).add(returnError("",0,3));
							break;
						default:
							if (!instructionUsed.get(use)) texts.get(use).set(1, textValue + symbolValue);
							else texts.get(use).add(returnError("",0,3));
							break;
					}
				}
				instructionUsed.set(use, true);
			}
		}

		//PRINT TEXT
		int count = 0;
		for (ArrayList<Object> entry : texts) {
			String countText = count + ": ";
			switch (entry.get(0).toString()) {
				case "I":
					writer.print(countText);
					for (int i = 1; i < entry.size(); i++) writer.print(entry.get(i).toString() + " ");
					writer.println();
					break;
				case "R":
					writer.print(countText);
					for (int i = 1; i < entry.size(); i++) writer.print(entry.get(i).toString() + " ");
					writer.println();
					break;
				case "E":
					writer.print(countText);
					for (int i = 1; i < entry.size(); i++) writer.print(entry.get(i).toString() + " ");
					writer.println();
					break;
				case "A":
					writer.print(countText);
					int absolute = (Integer.parseInt(entry.get(1).toString()) / 1000) * 1000;
					//CHECK ERROR 7
					if (Integer.parseInt(entry.get(1).toString()) - absolute > 200) {
						entry.add(returnError("",0,6));
						entry.set(1, absolute);
					}
					for (int i = 1; i < entry.size(); i++) writer.print(entry.get(i).toString() + " ");
					writer.println();
					break;
				default:
			}
			count ++;
		}

		//PRINT ERROR 3
		writer.println();
		for (Map.Entry<String, Integer> entry : defErrors.entrySet()) {
			if (entry.getValue() == 2) {
				String errorMessage = returnError(entry.getKey(), definitionModule.get(entry.getKey()), 2);
				writer.println(errorMessage);
			}
		}

		//PRINT ERROR 6
		for (Map.Entry<String, Integer> entry: error6.entrySet()) {
			String errorMessage = returnError(entry.getKey(), entry.getValue(), 5);
			writer.println(errorMessage);
		}

		writer.close();
	}

	public static String returnError(String symbol, int module, int index){
		String[] listOfErrors = {
				"Error: This variable is multiply defined; first value used.",
				"Error: " + symbol + " is not defined; zero used.",
				"Warning: " + symbol + " was defined in module " + module + " but never used.",
				"Error: Multiple variables used in instruction; all but first ignored.",
				"Error: Definition exceeds module size; first word in module used.",
				"Error: Use of " + symbol + " in module " + module + " exceeds module size; use ignored.",
				"Error: Absolute address exceeds machine size; zero used.",
				"Error: Relative address exceeds module size; zero used."
		};
		return listOfErrors[index];
	}
}
