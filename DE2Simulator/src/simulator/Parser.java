package simulator;

import java.util.Set;

import hardware_model.HardwareModel;
import hardware_model.ModuleDefinition;
import hdl_binding.ParsingException;

public interface Parser {

	public HardwareModel parseHDLCode(Set<String> modules) throws ParsingException;
	public ModuleDefinition createModule(String moduleCode) throws ParsingException;
	public String findModuleName(String moduleCode) throws ParsingException;
	public Set<String> findInputAndOutputVariables(String moduleCode);
	public String retrieveInputAndOutputVariablesScope(String moduleCode);
	public String retrieveModuleBodyScope(String moduleCode);
	public HardwareModel getGeneratedHardwareModel();
	
}
