package hdl_binding;

import hardware_model.Assignment;
import hardware_model.CodeBlock;
import hardware_model.Condition;
import hardware_model.HardwareModel;
import hardware_model.ModuleDefinition;
import hardware_model.Variable;
import hardware_model.operation.Operation;

import java.util.HashSet;
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
					String[] elements = line.split("<=");
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
							String[] elements = line.split("<=");
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

		CodeBlock root = new CodeBlock();
		CodeBlock currentCodeBlock = null;

		String relatedConditions = "";
		StringBuilder condition = new StringBuilder();
		
		for (int i = lineBegin; i < moduleBodyScopeLines.length;) {

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
				if (!lineElements[j].equals("begin")) {
					
					if ( moduleBodyScopeLines[i].indexOf("else") == -1 ){
						currentCodeBlock = new CodeBlock(new Condition(new Operation(new String(condition)), 1));
						root.addInstruction(currentCodeBlock);
						relatedConditions = "";
					}
					else{
						currentCodeBlock = new CodeBlock(new Condition(new Operation(
								new String("(!( "+relatedConditions+" )) || ( "+condition+ ")")), 1));
						root.addInstruction(currentCodeBlock);
					}
					
					StringBuilder assignments = new StringBuilder();
					while ( true ){
						assignments.append(lineElements[j]+" ");
						if ( lineElements[j].contains(";") ){
							break;
						}
						j++;
					}
					String assigningVariable = new String(assignments).split("<=")[0].trim();
					String assigningOperation = new String(assignments).split("<=")[1].trim().replace(";", "");
					Assignment assignment = new Assignment();
					for ( Variable variable : envVars ){
						if ( variable.getName().equals(assigningVariable) ){
							assignment.setAssigningVariable(variable);
							break;
						}
					}
					assignment.setAssignedOperation(new Operation(assigningOperation));
					currentCodeBlock.addInstruction(assignment);
					
					if ( !checkForExistenceOfRelatedConditionalBlocks(moduleBodyScopeLines, i+1) ){
						combinationalBlock.addInstruction(root);
						return i - lineBegin;
					}
					
					if ( relatedConditions.trim().isEmpty() ){
						relatedConditions = new String(condition);
					}
					else{
						relatedConditions = "( "+relatedConditions+" ) || ( "+new String(condition)+" )";
					}
				} else {
					i++;
				}
			}
			else if (moduleBodyScopeLines[i].indexOf("else") != -1){

				condition = new StringBuilder();
				condition.append("(!( "+relatedConditions+"))");
				
				if ( !(moduleBodyScopeLines[i].contains("begin")) ){
					
					currentCodeBlock = new CodeBlock(new Condition(new Operation(new String(condition)), 1));					
					root.addInstruction(currentCodeBlock);
					
					StringBuilder assignments = new StringBuilder();
					assignments.append(moduleBodyScopeLines[i].replace("else", ""));					
					String assigningVariable = new String(assignments).split("<=")[0].trim();
					String assigningOperation = new String(assignments).split("<=")[1].trim();
					Assignment assignment = new Assignment();
					for ( Variable variable : envVars ){
						if ( variable.getName().equals(assigningVariable) ){
							assignment.setAssigningVariable(variable);
							break;
						}
					}
					assignment.setAssignedOperation(new Operation(assigningOperation));
					currentCodeBlock.addInstruction(assignment);
					combinationalBlock.addInstruction(root);
					return i - lineBegin;
				}
				else{
					i++;
				}
			}
			else if (moduleBodyScopeLines[i].trim().isEmpty()){
				i++;
			}
			else if (moduleBodyScopeLines[i].trim().equals("end") ){
				combinationalBlock.addInstruction(root);
				return i - lineBegin;
			}
			else { //may expand to include for, while and do constructs

				StringBuilder assignments = new StringBuilder();
				assignments.append(moduleBodyScopeLines[i].trim().replace(";", ""));
				String assigningVariable = new String(assignments).split("<=")[0].trim();
				String assigningOperation = new String(assignments).split("<=")[1].trim();
				Assignment assignment = new Assignment();
				for ( Variable variable : envVars ){
					if ( variable.getName().equals(assigningVariable) ){
						assignment.setAssigningVariable(variable);
						break;
					}
				}
				assignment.setAssignedOperation(new Operation(assigningOperation));
				currentCodeBlock.addInstruction(assignment);
			}
		}
		combinationalBlock.addInstruction(root);
		return moduleBodyScopeLines.length - lineBegin;
	}

	private boolean checkForExistenceOfRelatedConditionalBlocks(
			String[] moduleBodyScopeLines, int lineBegin) {
		boolean relatedConditionalConstructFound = false;
		for ( int i=lineBegin; i<moduleBodyScopeLines.length; i++ ){
			if ( moduleBodyScopeLines[i].indexOf("if") != -1 && moduleBodyScopeLines[i].indexOf("else") == -1 ){
				return relatedConditionalConstructFound;
			}
			else if ( moduleBodyScopeLines[i].indexOf("else") != -1 ){
				relatedConditionalConstructFound = true;
			}
		}
		return relatedConditionalConstructFound;
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