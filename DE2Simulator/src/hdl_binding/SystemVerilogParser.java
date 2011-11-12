package hdl_binding;

import hardware_model.Assignment;
import hardware_model.CodeBlock;
import hardware_model.Condition;
import hardware_model.HardwareModel;
import hardware_model.ModuleDefinition;
import hardware_model.Variable;
import hardware_model.operation.Operation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SystemVerilogParser implements Parser {

	Set<String> modules;
	HardwareModel model;

	@Override
	public HardwareModel parseHDLCode(Set<String> modules) throws ParsingException{
		this.modules = modules;
		model = new HardwareModel();
		for ( String module : modules ){
			model.defineModule(createModule(module));
		}
		return model;
	}

	@Override
	public ModuleDefinition createModule(String moduleCode) throws ParsingException{

		String moduleName = findModuleName(moduleCode);
		ModuleDefinition module = new ModuleDefinition(moduleName);

		Set<String> inputAndOutputVariables = findInputAndOutputVariables(moduleCode);

		for (String inputOrOutputVariable : inputAndOutputVariables) {

			int openingBracketPosition = 0;
			while(true){
				openingBracketPosition = inputOrOutputVariable.indexOf("[", openingBracketPosition+2);
				if ( openingBracketPosition == -1 ){
					break;
				}
				inputOrOutputVariable =
						inputOrOutputVariable.substring(0, openingBracketPosition)
						+" "+inputOrOutputVariable.substring(openingBracketPosition);
			}

			int closingBracketPosition = 0;
			while(true){
				closingBracketPosition = inputOrOutputVariable.indexOf("]", closingBracketPosition+2);
				if ( closingBracketPosition == -1 ){
					break;
				}
				inputOrOutputVariable =
						inputOrOutputVariable.substring(0, closingBracketPosition+1)
						+" "+inputOrOutputVariable.substring(closingBracketPosition+1);
			}

			String[] elements = inputOrOutputVariable.split(" {1,}+");
			String meaning = elements[0];
			String kind = elements[1];
			int size = 1;
			String name;
			int amount = 1;
			if (elements[2].contains("[")) {
				size = Integer.parseInt(elements[2].replace("[", "")
						.replace("]", "").split(":")[0]) + 1;
				name = elements[3];
				if (elements.length > 4) {
					amount = Integer.parseInt(elements[4].replace("[", "")
							.replace("]", "").split(":")[1]) + 1;
				}
			} else {
				name = elements[2];
				if (elements.length > 3) {
					amount = Integer.parseInt(elements[3].replace("[", "")
							.replace("]", "").split(":")[1]) + 1;
				}
			}
			if (meaning.equals("input")) {
				module.addInputVariable(meaning, kind, size, name, amount);
			} else if (meaning.equals("output")) {
				module.addOutputVariable(meaning, kind, size, name, amount);
			}
		}

		String moduleBodyScope = retrieveModuleBodyScope(moduleCode);
		moduleBodyScope = handleModuleBodyScope(moduleBodyScope);
		String[] moduleBodyScopeLines = moduleBodyScope.split("\n");

		for ( int i=0; i<moduleBodyScopeLines.length; ){

			String line = moduleBodyScopeLines[i];

			line = line.trim();
			if ( line.isEmpty() ){
				i++;
				continue;
			}

			if ( line.indexOf("always_comb") == -1 && line.indexOf("always_ff") == -1 ){

				line = line.replace(";", "");

				int openingBracketPosition = 0;
				while(true){
					openingBracketPosition = line.indexOf("[", openingBracketPosition+2);
					if ( openingBracketPosition == -1 ){
						break;
					}
					line = line.substring(0, openingBracketPosition)+" "+line.substring(openingBracketPosition);
				}

				int closingBracketPosition = 0;
				while(true){
					closingBracketPosition = line.indexOf("]", closingBracketPosition+2);
					if ( closingBracketPosition == -1 ){
						break;
					}
					line = line.substring(0, closingBracketPosition+1)+" "+line.substring(closingBracketPosition+1);
				}

				String[] elements = line.split(" {1,}+");
				String kind = elements[0];
				int size = 1;
				String name;
				int amount = 1;
				if (elements[1].contains("[")) {
					size = Integer.parseInt(elements[1].replace("[", "")
							.replace("]", "").split(":")[0]) + 1;
					name = elements[2];
					if (elements.length > 3) {
						amount = Integer.parseInt(elements[3].replace("[", "")
								.replace("]", "").split(":")[1]) + 1;
					}
				} else {
					name = elements[1];
					if (elements.length > 2) {
						amount = Integer.parseInt(elements[2].replace("[", "")
								.replace("]", "").split(":")[1]) + 1;
					}
				}
				module.addLocalVariable(kind, size, name, amount);
				i++;

			}
			else if ( line.indexOf("always_comb") != -1 ){

				CodeBlock combinationalBlock = new CodeBlock();

				line = line.substring(line.indexOf("always_comb")+11);

				if ( line.indexOf("begin") == -1 ){

					line = line.replace(";", "");
					String[] elements = line.split("<=", 2);
					String assigningVariable = elements[0].trim();
					String assignedOperationOrValue = elements[1].trim();
					Assignment assignment = new Assignment();
					for ( Variable variable : module.getAllVariables() ){
						if ( variable.getName().equals(assigningVariable)) {
							assignment.setAssigningVariable(variable);
							break;
						}
					}
					assignment.setAssignedOperation(new Operation(assignedOperationOrValue));
					combinationalBlock.addInstruction(assignment);
					i++;
				}
				else if ( line.indexOf("begin") != -1 ){
					line = line.substring(line.indexOf("begin") + 5);
					boolean end = false;
					i++;
					while ( !end ){

						do {
							line = moduleBodyScopeLines[++i];
						}
						while ( line.trim().isEmpty() );

						if ( line.contains("end") ){
							end = true;
							i++;
						}
						else if ( !(line.contains("if")	|| line.contains("for") || line.contains("while")) ){

							line = line.replace(";", "");
							String[] elements = line.split("<=", 2);
							String assigningVariable = elements[0].trim();
							String assignedOperationOrValue = elements[1].trim();
							Assignment assignment = new Assignment();
							for ( Variable variable : module.getAllVariables() ){
								if ( variable.getName().equals(assigningVariable)) {
									assignment.setAssigningVariable(variable);
									break;
								}
							}
							assignment.setAssignedOperation(new Operation(assignedOperationOrValue));
							combinationalBlock.addInstruction(assignment);
						}
						else if ( line.contains("if") ){
							int conditionalBlockLinesNum = 
									retrieveConditionalBlock(combinationalBlock, moduleBodyScopeLines, i, 
											module.getAllVariables());
							i += conditionalBlockLinesNum;
						}
						else if ( line.contains("while") ){
							//...
						}
						else if ( line.contains("for") ){
							//...
						}

					}
				}
				module.addCombinationalBlock(combinationalBlock);
			}
		}
		return module;
	}

	private String handleModuleBodyScope(String moduleBodyScope) {

		moduleBodyScope = moduleBodyScope.replace("\n", " ");

		String[] symbols = {"if", "else", "for", "while", "do", "(", ")", "begin", "end"};

		for ( int i=0; i<symbols.length; i++ ){
			int openingSymbolPosition = -2;
			while(true){
				if ( ! symbols[i].equals("end") ){
					openingSymbolPosition = moduleBodyScope.indexOf(symbols[i], openingSymbolPosition+2);
				}
				else{
					openingSymbolPosition = moduleBodyScope.indexOf(symbols[i], openingSymbolPosition+4);
				}
				if ( openingSymbolPosition == -1 ){
					break;
				}
				if ( !(symbols[i].equals("begin") || symbols[i].equals("end")) ){
					moduleBodyScope = moduleBodyScope
							.substring(0, openingSymbolPosition)
							+ " "
							+ moduleBodyScope
							.substring(openingSymbolPosition,	openingSymbolPosition + symbols[i].length())
							+ " "
							+ moduleBodyScope
							.substring(openingSymbolPosition + symbols[i].length());
				}
				else if ( symbols[i].equals("begin") ){
					moduleBodyScope = moduleBodyScope
							.substring(0, openingSymbolPosition)
							+ " "
							+ moduleBodyScope
							.substring(openingSymbolPosition,	openingSymbolPosition + symbols[i].length())
							+ "\n "
							+ moduleBodyScope
							.substring(openingSymbolPosition + symbols[i].length());
				}
				else if ( symbols[i].equals("end") ){
					moduleBodyScope = moduleBodyScope
							.substring(0, openingSymbolPosition)
							+ " \n"
							+ moduleBodyScope
							.substring(openingSymbolPosition,	openingSymbolPosition + symbols[i].length())
							+ "\n "
							+ moduleBodyScope
							.substring(openingSymbolPosition + symbols[i].length());
				}
			}
		}
		for ( int j=0; j<moduleBodyScope.length(); j++ ){
			if ( moduleBodyScope.charAt(j) == ';' ){
				moduleBodyScope = moduleBodyScope.substring(0, j)+";\n"+moduleBodyScope.substring(j+1);
			}
		}
		return moduleBodyScope;
	}

	private int retrieveConditionalBlock(CodeBlock combinationalBlock, String[] moduleBodyScopeLines,
			int lineBegin, Set<Variable> envVars) {

		CodeBlock currentCodeBlock = null;

		String relatedConditions = "";
		StringBuilder condition = new StringBuilder();
		
		int stage = 0;
		//1-2 for if-condition start-finish, 3-4 for else-if-condition start-finish, 5-6 for else condition start-finish

		Set<Variable> IFAssignedVariables = new HashSet<Variable>();
		Set<Variable> ELSEIFAssignedVariables = new HashSet<Variable>();
		Set<Variable> ELSEAssignedVariables = new HashSet<Variable>();
		
		boolean waitingEndStatement = false;
		boolean canExit = false;
		int evaluatedLinesNum = 0;
		
		for (int i = lineBegin; i < moduleBodyScopeLines.length;) {

			if ( stage != 0 && !waitingEndStatement && canExit ){
				checkConsistencyOfConditionalCombinationalConstruct(IFAssignedVariables, ELSEIFAssignedVariables, ELSEAssignedVariables);
				return (evaluatedLinesNum+1) - lineBegin;
			}
			
			if ( moduleBodyScopeLines[i].indexOf("if") != -1 ){
				
				condition = new StringBuilder();
				String[] lineElements = moduleBodyScopeLines[i].trim().split(" {1,}+");
				int j=0;
				while (!lineElements[j].equals(")")) {
					if (!( lineElements[j].equals("(") 
							|| lineElements[j].equals("if") 
							|| lineElements[j].equals("else") )) {
						condition.append(lineElements[j] + " ");
					}
					j++;
				}
				j++;

				if ( moduleBodyScopeLines[i].indexOf("else") == -1 ){
					
					if ( stage == 1 ){
						if ( currentCodeBlock == null );//TODO HANDLE THIS ERROR SITUATION
						int linesSent = retrieveConditionalBlock(currentCodeBlock, moduleBodyScopeLines, i, envVars);
						i += linesSent;
						continue;
					}
					else if ( stage == 2 ){
						canExit = true;
						continue;
					}
					stage = 1;
					currentCodeBlock = new CodeBlock(new Condition(new Operation(new String(condition)), 1));
					combinationalBlock.addInstruction(currentCodeBlock);
					relatedConditions = "";
				}
				else{
					stage = 3;
					currentCodeBlock = new CodeBlock(new Condition(new Operation(
							new String("(!( "+relatedConditions+" )) && ( "+condition+ ")")), 1));
					combinationalBlock.addInstruction(currentCodeBlock);
				}

				if ( relatedConditions.trim().isEmpty() ){
					relatedConditions = new String(condition);
				}
				else{
					relatedConditions = "( "+relatedConditions+" ) || ( "+new String(condition)+" )";
				}

				if (!lineElements[j].equals("begin")) {

					StringBuilder assignments = new StringBuilder();
					while ( true ){
						assignments.append(lineElements[j]+" ");
						if ( lineElements[j].contains(";") ){
							break;
						}
						j++;
					}
					String assigningVariable = new String(assignments).split("<=", 2)[0].trim();
					String assigningOperation = new String(assignments).split("<=", 2)[1].trim().replace(";", "");
					Assignment assignment = new Assignment();
					for ( Variable variable : envVars ){
						if ( variable.getName().equals(assigningVariable) ){
							assignment.setAssigningVariable(variable);
							if ( stage == 1 ) IFAssignedVariables.add(variable);
							else if ( stage == 3 ) ELSEIFAssignedVariables.add(variable);
							break;
						}
					}
					assignment.setAssignedOperation(new Operation(assigningOperation));
					currentCodeBlock.addInstruction(assignment);
					
					stage++;
					i++;
					evaluatedLinesNum = i;
				}
				else{
					waitingEndStatement = true;
					i++;
					evaluatedLinesNum = i;
				}
			}
			else if (moduleBodyScopeLines[i].indexOf("else") != -1){

				if ( stage >= 5 ){
					//TODO HANDLE THIS ERROR SITUATION
				}
				stage = 5;
				condition = new StringBuilder();
				condition.append("(!( "+relatedConditions+"))");

				currentCodeBlock = new CodeBlock(new Condition(new Operation(new String(condition)), 1));
				
				combinationalBlock.addInstruction(currentCodeBlock);
				if ( !(moduleBodyScopeLines[i].contains("begin")) ){

					StringBuilder assignments = new StringBuilder();
					assignments.append(moduleBodyScopeLines[i].replace("else", ""));					
					String assigningVariable = new String(assignments).split("<=", 2)[0].trim();
					String assigningOperation = new String(assignments).split("<=", 2)[1].trim().replace(";", "");
					Assignment assignment = new Assignment();
					for ( Variable variable : envVars ){
						if ( variable.getName().equals(assigningVariable) ){
							assignment.setAssigningVariable(variable);
							if ( stage == 5 ) ELSEAssignedVariables.add(variable);
							break;
						}
					}
					assignment.setAssignedOperation(new Operation(assigningOperation));
					currentCodeBlock.addInstruction(assignment);
					stage++;
					i++;
					evaluatedLinesNum = i;
					canExit = true;
				}
				else{
					waitingEndStatement = true;
					i++;
					evaluatedLinesNum = i;
				}
			}
			else if (moduleBodyScopeLines[i].trim().isEmpty()){
				i++;
				evaluatedLinesNum = i;
			}
			else if (moduleBodyScopeLines[i].trim().equals("end") ){
				if ( stage % 2 == 0 ){
					//TODO HANDLE THIS ERROR SITUATION
				}
				if ( !waitingEndStatement ){
					i++;
				}
				else{
					waitingEndStatement = false;
					stage++;
					i++;
					evaluatedLinesNum = i;
				}
			}
			else { //may expand to include for, while and do constructs

				if ( waitingEndStatement ){

					StringBuilder assignments = new StringBuilder();
					assignments.append(moduleBodyScopeLines[i].trim().replace(";", ""));
					String assigningVariable = new String(assignments).split("<=", 2)[0].trim();
					String assigningOperation = new String(assignments).split("<=", 2)[1].trim();
					Assignment assignment = new Assignment();
					for ( Variable variable : envVars ){
						if ( variable.getName().equals(assigningVariable) ){
							assignment.setAssigningVariable(variable);
							if ( stage == 1 ) IFAssignedVariables.add(variable);
							else if ( stage == 3 ) ELSEIFAssignedVariables.add(variable);
							else if ( stage == 5 ) ELSEAssignedVariables.add(variable);
							break;
						}
					}
					assignment.setAssignedOperation(new Operation(assigningOperation));
					currentCodeBlock.addInstruction(assignment);
					i++;
					evaluatedLinesNum = i;
				}
				else{
					if ( stage % 2 == 0 ){
						canExit = true;
						continue;
					}
					else ;//TODO HANDLE THIS ERROR SITUATION;
				}
			}
		}
		checkConsistencyOfConditionalCombinationalConstruct(IFAssignedVariables, ELSEIFAssignedVariables, ELSEAssignedVariables);
		return (evaluatedLinesNum+1) - lineBegin;//TODO check consistency
	}

	private void checkConsistencyOfConditionalCombinationalConstruct(
			Set<Variable> iFAssignedVariables,
			Set<Variable> eLSEIFAssignedVariables,
			Set<Variable> eLSEAssignedVariables) {

		List<Set<Variable>> relatedVars = new ArrayList<Set<Variable>>();
		relatedVars.add(iFAssignedVariables);
		relatedVars.add(eLSEIFAssignedVariables);
		relatedVars.add(eLSEAssignedVariables);
		
		for ( int index=0; index<3; index++ ){
			for ( Variable var : relatedVars.get(index%3) ){
				if ( !relatedVars.get((index+1)%3).contains(var) ){
					//TODO HANDLE THIS ERROR SITUATION
				}
				if ( !relatedVars.get((index+2)%3).contains(var) ){
					//TODO HANDLE THIS ERROR SITUATION
				}
			}
		}
		
	}

	@Override
	public Set<String> findInputAndOutputVariables(String moduleCode) {

		Set<String> variables = new HashSet<String>();

		String inputAndOutputVariablesScope = retrieveInputAndOutputVariablesScope(moduleCode);
		String[] variableDefinitions = inputAndOutputVariablesScope.split(",");

		for ( String variableDefinition : variableDefinitions ){
			variableDefinition = variableDefinition.trim();
			variables.add(variableDefinition);
		}
		return variables;
	}

	@Override
	public String retrieveInputAndOutputVariablesScope(String moduleCode) {

		int validOpeningParentesis = 0;
		do{
			validOpeningParentesis = moduleCode.indexOf("(", validOpeningParentesis+1);
		}
		while ( moduleCode.charAt(validOpeningParentesis-1) == '#' );

		int validClosingParentesis = moduleCode.indexOf(");");

		return moduleCode.substring(validOpeningParentesis+1, validClosingParentesis);
	}

	@Override
	public String retrieveModuleBodyScope(String moduleCode) {

		int validBeggining = moduleCode.indexOf(");");
		int validEnding = moduleCode.indexOf("endmodule");

		return moduleCode.substring(validBeggining+2, validEnding);
	}

	@Override
	public String findModuleName(String module) throws ParsingException{
		String[] words = module.split(" ");
		for ( int i=0; i<words.length; i++ ){
			if ( words[i].equals("module") ){
				return words[i+1];
			}
		}
		throw new ParsingException("Could not find module name");
	}

	@Override
	public HardwareModel getGeneratedHardwareModel() {
		return model;
	}

}