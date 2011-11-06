package hdl_binding;

import hardware_model.Assignment;
import hardware_model.Combinational;
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

		Combinational combinationalBlock = new Combinational();
		
		for ( String line : moduleBodyScope.split("\n") ){
			
			line = line.trim();
			if ( line.isEmpty() ){
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
				
			}
			else if ( line.indexOf("always_comb") != -1 ){
				
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
				}
				module.addCombinationalBlock(combinationalBlock);
			}
		}
		return module;
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